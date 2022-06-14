package sg.edu.np.mad.transportme;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
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

        holder.ServiceNumber.setText(content.ServiceNumber);
        ArrayList<TextView> nextBusList= new ArrayList<TextView>(Arrays.asList(holder.NextBus1, holder.NextBus2, holder.NextBus3));
        ArrayList<TextView> nextBusTypeList = new ArrayList<TextView>(Arrays.asList(holder.NextBus1Type, holder.NextBus2Type, holder.NextBus3Type));
        for (int i = 0; i < nextBusList.size(); i++)
        {
            NextBus nb = content.NextBuses.get(i);
            TextView busTV = nextBusList.get(i);
            TextView busTypeTV = nextBusTypeList.get(i);
            if (nb.Load.equals("SEA"))
            {
                busTV.setTextColor(Color.parseColor("#90a959"));
            }
            else if (nb.Load.equals("SDA"))
            {
                busTV.setTextColor(Color.parseColor("#e9b872"));
            }
            else
            {
                busTV.setTextColor(Color.parseColor("#a63d40"));
            }

            if (nb.Type.equals("SD"))
            {
                busTypeTV.setText("Single");
            }
            else if (nb.Type.equals("DD"))
            {
                busTypeTV.setText("Double");
            }
            else
            {
                busTypeTV.setText("Bendy");
            }

            if (nb.EstimatedArrival == "Null")
            {
                busTV.setTextColor(Color.parseColor("#000000"));
                busTV.setText("一");
                busTypeTV.setText("");
            }
            else if (Integer.parseInt(nb.EstimatedArrival) == 0)
            {
                busTV.setText("Arr");
            }
            else if (Integer.parseInt(nb.EstimatedArrival) < 0)
            {
                busTV.setText("Left");
            }
            else
            {
                nextBusList.get(i).setText(nb.EstimatedArrival);
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