package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.LoginPage.globalName;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        BusServiceAdapter adapterMember = new BusServiceAdapter(content.getBusServices());  //Create the RecyclerView for BusServices
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
}