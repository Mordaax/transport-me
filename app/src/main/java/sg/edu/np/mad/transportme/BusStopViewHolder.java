package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class BusStopViewHolder extends RecyclerView.ViewHolder{
    TextView Description;
    TextView BusStopCode;
    TextView RoadName;
    ImageView Favourite;
    ImageView Reminder;
    RecyclerView RecyclerView2;
    public BusStopViewHolder(View item){
        super(item);
        Description = item.findViewById(R.id.Description);
        BusStopCode = item.findViewById(R.id.BusStopCode);
        RoadName = item.findViewById(R.id.RoadName);
        Favourite = item.findViewById(R.id.Favourite);
        Reminder = item.findViewById(R.id.Reminder);
        RecyclerView2 = item.findViewById(R.id.recyclerView2);
    }

}
