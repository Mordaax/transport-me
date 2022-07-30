package sg.edu.np.mad.transportme;

import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WeekViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    private WeekAdapter.ItemListener itemListener;
    public View parent;
    public TextView dayInMonth;
    private ArrayList<LocalDate> daysInWeek;
    public WeekViewHolder(@NonNull View itemView, WeekAdapter.ItemListener itemListener, ArrayList<LocalDate> daysInWeek) {
        super(itemView);
        parent = itemView.findViewById(R.id.ConstraintLayout);
        dayInMonth = itemView.findViewById(R.id.Day);
        this.itemListener = itemListener;
        itemView.setOnClickListener(this);
        this.daysInWeek = daysInWeek;
    }

    @Override
    public void onClick(View view) {
        itemListener.onItemClick(getAdapterPosition(), daysInWeek.get(getAdapterPosition()));
    }
}

