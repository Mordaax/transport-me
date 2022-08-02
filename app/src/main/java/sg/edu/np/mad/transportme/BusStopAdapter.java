package sg.edu.np.mad.transportme;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static sg.edu.np.mad.transportme.ReminderApplication.CHANNEL_ID_2;
import static sg.edu.np.mad.transportme.user.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.user.LoginPage.globalName;
import static sg.edu.np.mad.transportme.user.LoginPage.globalRemindCloseness;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminder;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminderBusService;
import static sg.edu.np.mad.transportme.user.LoginPage.grbsChange;
import static sg.edu.np.mad.transportme.views.MainActivity.currentLocation;
import static sg.edu.np.mad.transportme.views.MainActivity.networkprovider;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.user.LoginPage;
import sg.edu.np.mad.transportme.views.MainActivity;

public class BusStopAdapter
        extends RecyclerView.Adapter<BusStopViewHolder>        //just like list, need declare <data type>
{
    ArrayList<BusStop> data;
    Context c;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");     //Initialise database instance
    public BusStopAdapter(ArrayList<BusStop> data, Context c)
    {
        this.c = c;
        this.data = data;
    }
    private static Boolean tooClose = false;
    private static Boolean bgPermReq = false;
    @NonNull
    @Override
    public BusStopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_layout, parent,false);       //Creates layout declaring parent object
        return new BusStopViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopViewHolder holder, int position) {
        BusStop content = data.get(position);       //Using row id to retrieve data from list

        ViewGroup cardView = holder.itemView.findViewById(R.id.base_cardview);
        View hiddenView = holder.itemView.findViewById(R.id.recyclerView2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Moving the camera for the main Google Maps to the bus stop clicked
                ((MainActivity)c).moveMapsCamera(content.getLatitude(), content.getLongitude());

                //Smooth animation for drop down arrow when bus services are expanded
                if (holder.itemView.findViewById(R.id.recyclerView2).getVisibility() == View.VISIBLE){
                    RotateAnimation rotate = new RotateAnimation(-90, 0, Animation.RELATIVE_TO_SELF, 0.5f,          Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(250);
                    rotate.setInterpolator(new LinearInterpolator());
                    holder.itemView.findViewById(R.id.DropDownArrow).startAnimation(rotate);
                    holder.itemView.findViewById(R.id.DropDownArrow).setRotation(90);
                    TransitionManager.beginDelayedTransition(cardView,new AutoTransition());
                    holder.itemView.findViewById(R.id.recyclerView2).setVisibility(View.GONE);
                }
                else{
                    RotateAnimation rotate = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f,          Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(250);
                    rotate.setInterpolator(new LinearInterpolator());
                    holder.itemView.findViewById(R.id.DropDownArrow).startAnimation(rotate);
                    holder.itemView.findViewById(R.id.DropDownArrow).setRotation(0);
                    TransitionManager.beginDelayedTransition(cardView,new AutoTransition());
                    hiddenView.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.Description.setText(content.getDescription());       //Setting text for relevant fields in Bus Stop Layout
        holder.BusStopCode.setText(content.getBusStopCode());
        holder.RoadName.setText(content.getRoadName());

        isReminder(content.getBusStopCode(), holder.Reminder, content);      //Method to check whether bus stop is set to remind when reached
        isFavourited(content.getBusStopCode(), holder.Favourite);   //Method to check whether bus stop is favourited, sets heart icon to red if favourited
        holder.Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())                            //Method to check if Wi-Fi/Mobile data is on
                {
                    DatabaseReference reference = db.getReference()
                            .child("User")
                            //.child(firebaseUser.getUid())
                            .child(globalName)
                            .child("Favourited")
                            .child(content.getBusStopCode());       //Gets database reference of the child of the bus stop clicked on, under the current user's favourited list in RTDB
                    globalFavouriteBusStop.add(content);            //Adds bus stop to favourite list when clicked on
                    if (holder.Favourite.getTag() == "Favourite")
                    {
                        holder.Favourite.setImageResource(R.drawable.filled_favourite);     //Setting the heart icon to red if it is not favourited
                        holder.Favourite.setTag("Favourited");
                        reference.setValue(true);                                           //Adding the bus stop code of selected bus stop into the user's favourite list in RTDB
                    }
                    else
                    {
                        holder.Favourite.setImageResource(R.drawable.favourite);            //Setting the heart icon to empty if it is favourited
                        holder.Favourite.setTag("Favourite");
                        reference.setValue(null);                                           //Removing the bus stop code of selected bus stop from the user's favourite list in RTDB
                    }
                }
                else
                {
                    //Notifies user that Wi-Fi/Mobile data is off and updates are not sent to the database
                    Toast.makeText(c, "Wifi is OFF, favourites may not update.", Toast.LENGTH_SHORT).show();
                    if (holder.Favourite.getTag() == "Favourite")
                    {
                        holder.Favourite.setImageResource(R.drawable.filled_favourite);     //Setting the heart icon to red if it is not favourited
                        holder.Favourite.setTag("Favourited");
                    }
                    else
                    {
                        holder.Favourite.setImageResource(R.drawable.favourite);            //Setting the heart icon to empty if it is favourited
                        holder.Favourite.setTag("Favourite");
                    }
                }
            }
        });
        holder.Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                {
                    if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && !bgPermReq)
                    {
                        BackgroundAlert(((MainActivity)c));
                    }
                    DatabaseReference reference = db.getReference()
                            .child("User")
                            //.child(firebaseUser.getUid())
                            .child(globalName)
                            .child("Reminder");
                    if (holder.Reminder.getTag() == "False")        //If bell grey
                    {
                        if(!(globalReminder == null))               //If there is an existing bus stop for reminder
                        {
                            AlertDialog.Builder reminderBuilder = new AlertDialog.Builder(c);
                            reminderBuilder.setTitle("Marked bus stop for reminder exists!");
                            reminderBuilder.setIcon(R.drawable.appsplashicon);
                            reminderBuilder.setMessage("Do you wish to remove " + globalReminder.getDescription() + " and mark this bus stop?");
                            reminderBuilder.setCancelable(false);
                            reminderBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reference.setValue(null);
                                    Toast.makeText(c,content.getDescription()+" Reminder Removed!",Toast.LENGTH_LONG).show();
                                    isValidBusService(content, holder,reference);

                                }
                            });
                            reminderBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            AlertDialog alert = reminderBuilder.create();
                            alert.show();
                        }
                        else    //If there is no existing bus stop marked for reminder
                        {
                            isValidBusService(content, holder,reference);
                        }
                    }
                    else        //If bell is filled yellow
                    {
                        reference.setValue(null);
                        Toast.makeText(c,content.getDescription()+" Reminder Removed!",Toast.LENGTH_LONG).show();
                    }
                }
                else        //If no internet connection
                {
                    //do something like toast no wifi cannot set reminder
                    Toast.makeText(c,"Internet connection unavailable, reminder cannot be set",Toast.LENGTH_LONG).show();
                }
            }
        });

        BusServiceAdapter adapterMember = new BusServiceAdapter(content.getBusServices(), c);  //Create the RecyclerView for BusServices
        LinearLayoutManager layout = new LinearLayoutManager(c);                            //LayoutManager tells RecyclerView how to draw the list

        holder.RecyclerView2.setLayoutManager(layout);          //Pass in layout and adapter
        holder.RecyclerView2.setAdapter(adapterMember);
    }

    //Method that searches for the bus stop code when the Bus Stop card view is loaded in,
    //and sets the heart to red if it exists in the favourited list, and empty if it does not
    private void isFavourited(String busStopCode, ImageView favouritedView)
    {
        if (isNetworkAvailable())           //Checks whether Wi-Fi/Mobile data is on before making a reference in firebase
        {
            DatabaseReference reference = db.getReference()
                    .child("User")
                    //.child(firebaseUser.getUid())
                    .child(globalName)
                    .child("Favourited")
                    .child(busStopCode);    //Gets database reference of the child of the bus stop clicked on, under the current user's favourited list in RTDB

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null)                                    //Checks whether bus stop exists in favourited list
                    {
                        favouritedView.setImageResource(R.drawable.filled_favourite);   //Sets heart icon to red if favourited
                        favouritedView.setTag("Favourited");
                    }
                    else
                    {
                        favouritedView.setImageResource(R.drawable.favourite);          //Sets heart icon to empty if not favourited
                        favouritedView.setTag("Favourite");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            //Notifies user the Wi-Fi/Mobile data is OFF and list in RTDB will not be checked
            Toast.makeText(c, "Wifi is OFF, favourites may not be up to date.", Toast.LENGTH_SHORT).show();
        }
    }

    private void isReminder(String busStopCode, ImageView reminderView, BusStop busStop)
    {
        if (isNetworkAvailable())           //Checks whether Wi-Fi/Mobile data is on before making a reference in firebase
        {
            DatabaseReference reference = db.getReference()
                    .child("User")
                    //.child(firebaseUser.getUid())
                    .child(globalName)
                    .child("Reminder");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (busStopCode.equals(snapshot.child("BusStop").getValue()) && snapshot.getValue() != null && snapshot.child("BusService").getValue() != null)
                    {
                        reminderView.setImageResource(R.drawable.filled_bell);
                        reminderView.setTag("Remind");
                        globalReminder = busStop;
                        globalReminderBusService = snapshot.child("BusService").getValue().toString();
                        grbsChange.setValue(globalReminderBusService);
                    }
                    else
                    {
                        reminderView.setImageResource(R.drawable.grey_bell);
                        reminderView.setTag("False");
                    }
                    if (snapshot.getValue() == null && snapshot.child("BusService").getValue() == null)
                    {
                        globalReminder = null;
                        globalReminderBusService = "";
                        grbsChange.setValue(globalReminderBusService);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            //Notifies user the Wi-Fi/Mobile data is OFF and list in RTDB will not be checked
            Toast.makeText(c, "Wifi is OFF, Notification may not be up to date.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCloseNess(BusStop bs)
    {
        tooClose = false;

        //Log.e("dis",""+SphericalUtil.computeDistanceBetween(currentLocation,new LatLng(bs.getLatitude(),bs.getLongitude())));

        if(SphericalUtil.computeDistanceBetween(currentLocation,new LatLng(bs.getLatitude(),bs.getLongitude())) < globalRemindCloseness)
        {
            tooClose = true;
        }

    }

    private void isValidBusService(BusStop bs, BusStopViewHolder holder, DatabaseReference reference)
    {
        checkCloseNess(bs);
        if (tooClose)
        {
            Toast.makeText(c, "Destination is too near!", Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("What bus will you be riding?");
            final EditText input = new EditText(c) ;
            String inputBusService;
            input.setPadding(
                    c.getResources().getDimensionPixelOffset(R.dimen.dp_64),
                    c.getResources().getDimensionPixelOffset(R.dimen.dp_10),
                    c.getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    c.getResources().getDimensionPixelOffset(R.dimen.dp_10)
            );
            input.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
            input.setRawInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setIcon(R.drawable.appsplashicon);
            builder.setCancelable(false);
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String inputBusService = input.getText().toString().toUpperCase();
                    for(BusService busServ : bs.getBusServices())
                    {
                        if(inputBusService.equals(busServ.getServiceNumber()))
                        {
                            globalReminderBusService = inputBusService;
                        }
                    }
                    if (!globalReminderBusService.equals(""))
                    {
                        holder.Reminder.setImageResource(R.drawable.filled_bell);
                        holder.Reminder.setTag("Remind");
                        globalReminder = bs;
                        reference.child("BusStop").setValue(bs.getBusStopCode());
                        reference.child("BusService").setValue(inputBusService);
                        Toast.makeText(c, "Remind when arriving " + bs.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(c, "Invalid Bus Service!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isNetworkAvailable()     //Method to check whether Wi-Fi/Mobile data is OFF or ON
    {
        try{
            ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if(manager != null){
                networkInfo = manager.getActiveNetworkInfo();       //Check for active network connections
            }
            return networkInfo != null && networkInfo.isConnected();
        }
        catch(NullPointerException e){
            return false;
        }
    }
    private void BackgroundAlert(Activity context)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Background Location Use");
        String action = "";
        if (Build.VERSION.SDK_INT < 30)
        {
            action = "Request permissions for accessing background location.";
        }
        else
        {
            action = "redirect you to the settings page to set Location Permissions to 'Allow all the time'";
        }

        builder.setMessage("TransportMe collects location data to enable the remind to alight feature, even when the app is not in use." + //Inform user about location policy of TransportMe
                "\nThis enables us to send you a notification when you are near to your destination set." +
                "\n\nAccepting will " + action);        //Tell user what will happen when they choose to accept
        builder.setIcon(R.drawable.appsplashicon);
        builder.setCancelable(false);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT < 30)
                {
                    final String[] BACKGROUND_PERM={
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    };
                    final int BACKGROUND_REQUEST=1338;
                    requestPermissions(context,BACKGROUND_PERM,BACKGROUND_REQUEST);
                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivityForResult(intent, 0);
                }
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(c,"TransportMe will not be able to track location while app is in the background.", Toast.LENGTH_LONG).show();
            }
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
        bgPermReq = true;
    }
}