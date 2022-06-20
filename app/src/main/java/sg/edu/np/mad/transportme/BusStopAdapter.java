package sg.edu.np.mad.transportme;

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
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
    public BusStopAdapter(ArrayList<BusStop> data, Context c)
    {
        this.c = c;
        this.data = data;
        //this = this class / object
    }
    /* Remove This????? */
    @Override
    public int getItemViewType(int position){
        return (position%5==0)?0:1;
    }

    @NonNull
    @Override
    public BusStopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_layout, parent,false);

        return new BusStopViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BusStopViewHolder holder, int position) {
        BusStop content = data.get(position);

        if (c.getClass().getSimpleName().equals("FavouritesFragment"))
        {
            String busStopCode = content.BusStopCode;
            //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = db.getReference()
                    .child("User")
                    //.child(firebaseUser.getUid())
                    .child(globalName)
                    .child("Favourited");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child(busStopCode).exists())
                    {
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return;
        }

        ViewGroup cardView = holder.itemView.findViewById(R.id.base_cardview);
        View hiddenView = holder.itemView.findViewById(R.id.recyclerView2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (c.getClass().getSimpleName().equals("MainActivity"))
                {
                    ((MainActivity)c).moveMapsCamera(content.Latitude,content.Longitude);
                }

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

        holder.Description.setText(content.Description);
        holder.BusStopCode.setText(content.BusStopCode);
        holder.RoadName.setText(content.RoadName);

        isFavourited(content.BusStopCode, holder.Favourite);
        holder.Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                {
                    DatabaseReference reference = db.getReference()
                            .child("User")
                            //.child(firebaseUser.getUid())
                            .child(globalName)
                            .child("Favourited")
                            .child(content.BusStopCode);

                    if (holder.Favourite.getTag() == "Favourite")
                    {
                        holder.Favourite.setImageResource(R.drawable.filled_favourite);
                        holder.Favourite.setTag("Favourited");
                        reference.setValue(true);
                    }
                    else
                    {
                        holder.Favourite.setImageResource(R.drawable.favourite);
                        holder.Favourite.setTag("Favourite");
                        reference.setValue(null);
                    }
                }
                else
                {
                    Toast.makeText(c, "Wifi is OFF, favourites may not update.", Toast.LENGTH_SHORT).show();
                    if (holder.Favourite.getTag() == "Favourite")
                    {
                        holder.Favourite.setImageResource(R.drawable.filled_favourite);
                        holder.Favourite.setTag("Favourited");
                    }
                    else
                    {
                        holder.Favourite.setImageResource(R.drawable.favourite);
                        holder.Favourite.setTag("Favourite");
                    }
                }
            }
        });

        /*RecyclerView rv = c.findViewById(R.id.recyclerView2);*/
        BusServiceAdapter adapterMember = new BusServiceAdapter(content.busServices);
        LinearLayoutManager layout = new LinearLayoutManager(c);
        /*rv.setAdapter(adapter);
        rv.setLayoutManager(layout);*/
        holder.RecyclerView2.setLayoutManager(layout);
        holder.RecyclerView2.setAdapter(adapterMember);
    }


    private void isFavourited(String busStopCode, ImageView favouritedView)
    {
        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = db.getReference()
                .child("User")
                //.child(firebaseUser.getUid())
                .child(globalName)
                .child("Favourited")
                .child(busStopCode);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                {
                    favouritedView.setImageResource(R.drawable.filled_favourite);
                    favouritedView.setTag("Favourited");
                }
                else
                {
                    favouritedView.setImageResource(R.drawable.favourite);
                    favouritedView.setTag("Favourite");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isNetworkAvailable()
    {
        try{
            ConnectivityManager manager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if(manager != null){
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }
        catch(NullPointerException e){
            return false;
        }


    }
}