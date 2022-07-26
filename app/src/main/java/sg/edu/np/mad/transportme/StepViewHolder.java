package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class StepViewHolder extends RecyclerView.ViewHolder {
    TextView instructions;
    TextView duration;
    TextView distance;
    TextView line;
    TextView previouslocation;
    TextView nextlocation;
    TextView stopcount;
    LinearLayout linearlayout_mrt;
    CardView startend_cardview;
    View viewItem;
    LinearLayout linearlayout_timing;
    CardView buscardview;

    TextView ServiceNumber;
    TextView NextBus1;
    TextView NextBus2;
    TextView NextBus3;
    TextView NextBus1Type;
    TextView NextBus2Type;
    TextView NextBus3Type;
    ImageView NextBus1Feature;
    ImageView NextBus2Feature;
    ImageView NextBus3Feature;

    public StepViewHolder(View item){
        super(item);
        viewItem = item;
        instructions = item.findViewById(R.id.textView_Instructions);
        duration = item.findViewById(R.id.textView_Duration);
        distance = item.findViewById(R.id.textView_Distance);
        line = item.findViewById(R.id.textView_Line);
        previouslocation = item.findViewById(R.id.textView_PreviousLocation);
        nextlocation = item.findViewById(R.id.textView_NextLocation);
        stopcount = item.findViewById(R.id.textView_StopNumber);
        linearlayout_mrt = item.findViewById(R.id.linearLayout_mrt);
        startend_cardview = item.findViewById(R.id.cardView_startend);

        ServiceNumber = item.findViewById(R.id.ServiceNumber);
        NextBus1 = item.findViewById(R.id.NextBus1);
        NextBus2 = item.findViewById(R.id.NextBus2);
        NextBus3 = item.findViewById(R.id.NextBus3);
        NextBus1Type = item.findViewById(R.id.NextBus1Type);
        NextBus2Type = item.findViewById(R.id.NextBus2Type);
        NextBus3Type = item.findViewById(R.id.NextBus3Type);
        NextBus1Feature = item.findViewById(R.id.NextBus1Feature);
        NextBus2Feature = item.findViewById(R.id.NextBus2Feature);
        NextBus3Feature = item.findViewById(R.id.NextBus3Feature);

        linearlayout_timing = item.findViewById(R.id.bustimingview);
        buscardview = item.findViewById(R.id.buscardview);
    }

}
