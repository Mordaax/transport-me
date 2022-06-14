package sg.edu.np.mad.transportme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusServiceAdapter
        extends RecyclerView.Adapter<BusServiceViewHolder>        //just like list, need declare <data type>
{
    ArrayList<BusService> data;
    public BusServiceAdapter(ArrayList<BusService> data)
    {
        this.data = data;                                       //this = this class / object
    }
    /* Remove This????? */
    @Override
    public int getItemViewType(int position){
        return (position%5==0)?0:1;
    }

    @NonNull
    @Override
    public BusServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_service_layout, null,false);


        return new BusServiceViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BusServiceViewHolder holder, int position) {
        BusService content = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.ServiceNumber.setText(content.ServiceNumber);
        holder.NextBus1.setText(content.NextBuses.get(0).EstimatedArrival);
        holder.NextBus2.setText(content.NextBuses.get(1).EstimatedArrival);
        holder.NextBus3.setText(content.NextBuses.get(2).EstimatedArrival);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}