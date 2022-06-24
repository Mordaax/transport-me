package sg.edu.np.mad.transportme;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

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

        holder.ServiceNumber.setText(content.getServiceNumber());
        ArrayList<TextView> nextBusList= new ArrayList<TextView>(Arrays.asList(holder.NextBus1, holder.NextBus2, holder.NextBus3));
        ArrayList<TextView> nextBusTypeList = new ArrayList<TextView>(Arrays.asList(holder.NextBus1Type, holder.NextBus2Type, holder.NextBus3Type));
        ArrayList<ImageView> nextBusFeatureList = new ArrayList<>(Arrays.asList(holder.NextBus1Feature, holder.NextBus2Feature, holder.NextBus3Feature));
        for (int i = 0; i < nextBusList.size(); i++)
        {
            NextBus nb = content.getNextBuses().get(i);
            TextView busTV = nextBusList.get(i);
            TextView busTypeTV = nextBusTypeList.get(i);
            ImageView busFeatureIV = nextBusFeatureList.get(i);
            if (nb.getEstimatedArrival() == "Null")
            {
                busTV.setTextColor(Color.parseColor("#000000"));
                busTV.setText("一");
                busTypeTV.setText("");
                busFeatureIV.setVisibility(View.GONE);
                continue;
            }
            else if (Integer.parseInt(nb.getEstimatedArrival()) == 0)
            {
                busTV.setText("Arr");
            }
            else if (Integer.parseInt(nb.getEstimatedArrival()) < 0)
            {
                busTV.setText("Left");
            }
            else
            {
                nextBusList.get(i).setText(nb.getEstimatedArrival());
            }

            if (nb.getFeature().equals("none"))
            {
                busFeatureIV.setVisibility(View.GONE);
            }

            if (nb.getLoad().equals("SEA"))
            {
                busTV.setTextColor(Color.parseColor("#90a959"));
            }
            else if (nb.getLoad().equals("SDA"))
            {
                busTV.setTextColor(Color.parseColor("#e9b872"));
                busFeatureIV.setImageResource(R.drawable.wheelchair_yellow);
            }
            else
            {
                busTV.setTextColor(Color.parseColor("#a63d40"));
                busFeatureIV.setImageResource(R.drawable.wheelchair_red);
            }

            if (nb.getType().equals("SD"))
            {
                busTypeTV.setText("Single");
            }
            else if (nb.getType().equals("DD"))
            {
                busTypeTV.setText("Double");
            }
            else
            {
                busTypeTV.setText("Bendy");
            }


        }

        //holder.NextBus1.setText(content.NextBuses.get(0).EstimatedArrival);
        //holder.NextBus2.setText(content.NextBuses.get(1).EstimatedArrival);
        //holder.NextBus3.setText(content.NextBuses.get(2).EstimatedArrival);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}