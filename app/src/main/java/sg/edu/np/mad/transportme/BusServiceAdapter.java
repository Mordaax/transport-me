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
        extends RecyclerView.Adapter<BusServiceViewHolder>
{
    ArrayList<BusService> data;
    public BusServiceAdapter(ArrayList<BusService> data)
    {
        this.data = data;
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

        holder.ServiceNumber.setText(content.getServiceNumber());       //Set the Text of Bus Service Number
        //Populating the list with each attribute of the next bus (Bus, Type, Feature)
        ArrayList<TextView> nextBusList= new ArrayList<TextView>(Arrays.asList(holder.NextBus1, holder.NextBus2, holder.NextBus3));
        ArrayList<TextView> nextBusTypeList = new ArrayList<TextView>(Arrays.asList(holder.NextBus1Type, holder.NextBus2Type, holder.NextBus3Type));
        ArrayList<ImageView> nextBusFeatureList = new ArrayList<>(Arrays.asList(holder.NextBus1Feature, holder.NextBus2Feature, holder.NextBus3Feature));
        for (int i = 0; i < nextBusList.size(); i++)        //Loops through the 3 next busses
        {
            //Declaring the next bus
            NextBus nb = content.getNextBuses().get(i);
            //Assigning variables to their corresponding holder views
            TextView busTV = nextBusList.get(i);
            TextView busTypeTV = nextBusTypeList.get(i);
            ImageView busFeatureIV = nextBusFeatureList.get(i);
            if (nb.getEstimatedArrival() == "Null")                 //Checks if the bus exists (Null == non-existent)
            {
                busTV.setTextColor(Color.parseColor("#000000"));    //Sets the text to 一 in black to indicate no bus
                busTV.setText("一");
                busTypeTV.setText("");
                busFeatureIV.setVisibility(View.GONE);
                continue;
            }
            else if (Integer.parseInt(nb.getEstimatedArrival()) == 0)       //When 0 minutes is left to arrival, set to Arr
            {
                busTV.setText("Arr");
            }
            else if (Integer.parseInt(nb.getEstimatedArrival()) < 0)        //If estimated arrival is negative, set to Left
            {
                busTV.setText("Left");
            }
            else
            {
                nextBusList.get(i).setText(nb.getEstimatedArrival());       //Else set it to estimated arrival time in mins
            }

            if (nb.getFeature().equals("none"))                             //Checks if bus is WAB (Wheel chair accessible)
            {
                busFeatureIV.setVisibility(View.GONE);                      //Does not display WAB icon if none
            }

            if (nb.getLoad().equals("SEA"))     //SEA = Seats Available
            {
                busTV.setTextColor(Color.parseColor("#90a959"));           //Sets text color to green (Green = Not Full)
            }
            else if (nb.getLoad().equals("SDA"))    //SDA = Standing Available
            {
                busTV.setTextColor(Color.parseColor("#e9b872"));           //Sets text color to yellow (Yellow = medium fullness)
                busFeatureIV.setImageResource(R.drawable.wheelchair_yellow);        //Sets WAB icon to yellow
            }
            else        //Else = LSD = Limited Standing
            {
                busTV.setTextColor(Color.parseColor("#a63d40"));          //Sets text color to red (Red = Extremely full)
                busFeatureIV.setImageResource(R.drawable.wheelchair_red);          //sets WAB icon to red
            }

            if (nb.getType().equals("SD"))      //SD = Single Deck
            {
                busTypeTV.setText("Single");    //Displays bus type as Single
            }
            else if (nb.getType().equals("DD")) //DD = Double Deck
            {
                busTypeTV.setText("Double");    //Displays bus type as Double
            }
            else        //Else = BD = Bendy
            {
                busTypeTV.setText("Bendy");     //Displays bus type as Bendy
            }


        }

        /*
        Load: SEA (Seats available), SDA (Standing available), LSD (Limited Standing)
        Feature: WAB (wheel Chair accessible), none (Not wheel chat accessible)
        Type: SD (Single Deck), DD (Double Deck), BD (Bendy) */
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}