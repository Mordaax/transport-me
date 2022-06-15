package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class BusStopViewHolder extends RecyclerView.ViewHolder{
    TextView Description;
    TextView BusStopCode;
    TextView RoadName;
    RecyclerView RecyclerView2;
    public BusStopViewHolder(View item){
        super(item);
        Description = item.findViewById(R.id.Description);
        BusStopCode = item.findViewById(R.id.BusStopCode);
        RoadName = item.findViewById(R.id.RoadName);
        RecyclerView2 = item.findViewById(R.id.recyclerView2);
    }

}
