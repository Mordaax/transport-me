package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class BusServiceViewHolder extends RecyclerView.ViewHolder{
    TextView ServiceNumber;
    TextView NextBus1;
    TextView NextBus2;
    TextView NextBus3;

    public BusServiceViewHolder(View item){
        super(item);
        ServiceNumber = item.findViewById(R.id.ServiceNumber);
        NextBus1 = item.findViewById(R.id.NextBus1);
        NextBus2 = item.findViewById(R.id.NextBus2);
        NextBus3 = item.findViewById(R.id.NextBus3);

    }
}
