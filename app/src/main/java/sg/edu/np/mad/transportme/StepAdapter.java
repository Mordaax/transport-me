package sg.edu.np.mad.transportme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {
    ArrayList<RouteStep> data;
    Context c;
    public StepAdapter(Context c, ArrayList<RouteStep> data){
        this.data = data;
        this.c = c;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || position+1 == data.size()){
            return 4;
        }
        if (data.get(position).TravelMode.equals("Walk")){
            return 1;
        }
        else if(data.get(position).TravelMode.equals("Bus")){
            return 2;
        }
        else{
            return 3;
        }

    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if (viewType ==1)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_walk_layout,parent,false);
        else if(viewType ==2)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_bus_layout,parent,false);
        else if(viewType == 3)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_mrt_layout,parent,false);
        else if(viewType == 4)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_startend_layout,parent,false);
        return new StepViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        RouteStep content = data.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.stepcoordinates.size() > 0) {
                    ((Route) c).moveMapCamera(content.stepcoordinates);
                }
            }
        });
        if (position == 0 || position+1 == data.size()){
            holder.instructions.setText(content.Instructions);
        }
        else{
            holder.instructions.setText(content.Instructions);
            holder.duration.setText(content.Duration);
            holder.distance.setText(content.Distance);
            holder.previouslocation.setText(content.PreviousLocation);
            holder.nextlocation.setText(content.NextLocation);
            if (!content.TravelMode.equals("Walk")){
                holder.line.setText(content.LineName);
                holder.stopcount.setText(content.NumStops.toString());
                if (content.TravelMode.equals("Subway")){
                    holder.linearlayout_mrt.setBackgroundColor(Color.parseColor(content.LineColor));
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
