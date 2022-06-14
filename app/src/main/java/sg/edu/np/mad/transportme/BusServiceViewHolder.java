package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class BusServiceViewHolder extends RecyclerView.ViewHolder{
    TextView ServiceNumber;
    TextView NextBus1;
    TextView NextBus2;
    TextView NextBus3;
    TextView NextBus1Type;
    TextView NextBus2Type;
    TextView NextBus3Type;

    public BusServiceViewHolder(View item){
        super(item);
        ServiceNumber = item.findViewById(R.id.ServiceNumber);
        NextBus1 = item.findViewById(R.id.NextBus1);
        NextBus2 = item.findViewById(R.id.NextBus2);
        NextBus3 = item.findViewById(R.id.NextBus3);
        NextBus1Type = item.findViewById(R.id.NextBus1Type);
        NextBus2Type = item.findViewById(R.id.NextBus2Type);
        NextBus3Type = item.findViewById(R.id.NextBus3Type);

    }
}
