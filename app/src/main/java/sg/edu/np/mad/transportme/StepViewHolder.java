package sg.edu.np.mad.transportme;

import android.view.View;
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
    }

}
