package sg.edu.np.mad.transportme;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusStopAdapter
        extends RecyclerView.Adapter<BusStopViewHolder>        //just like list, need declare <data type>
{
    ArrayList<BusStop> data;
    Context c;
    public BusStopAdapter(ArrayList<BusStop> data, Context c)
    {
        this.c = c;
        this.data = data;                                       //this = this class / object
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
        ViewGroup cardView = holder.itemView.findViewById(R.id.base_cardview);
        View hiddenView = holder.itemView.findViewById(R.id.recyclerView2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        //isFavourited(content.BusStopCode, holder.Favourite);
        holder.Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.Favourite.getTag() == "Favourite")           //it might need to be 0 and 1s
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
        });

        /*RecyclerView rv = c.findViewById(R.id.recyclerView2);*/
        BusServiceAdapter adapterMember = new BusServiceAdapter(content.busServices);
        LinearLayoutManager layout = new LinearLayoutManager(c);
        /*rv.setAdapter(adapter);
        rv.setLayoutManager(layout);*/
        holder.RecyclerView2.setLayoutManager(layout);
        holder.RecyclerView2.setAdapter(adapterMember);
    }

    /*
    private void isFavourited(String busStopCode, ImageView favouritedView)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(firebaseUser.getUid())
                .child("Favourited");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(busStopCode).exists())
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
    }*/

    @Override
    public int getItemCount() {
        return data.size();
    }
}