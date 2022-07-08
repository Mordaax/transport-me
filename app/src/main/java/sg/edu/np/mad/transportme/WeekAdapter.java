package sg.edu.np.mad.transportme;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class WeekAdapter extends RecyclerView.Adapter<WeekViewHolder> {

    private ArrayList<LocalDate> daysInMonth;
    private ItemListener itemListener;

    public WeekAdapter(ArrayList<LocalDate> dayInMonth, ItemListener itemListener) {
        this.daysInMonth = dayInMonth;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_layout, parent, false);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight());
        return new WeekViewHolder(v, itemListener, daysInMonth);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
        LocalDate date = daysInMonth.get(position);
        holder.dayInMonth.setText(String.valueOf(date.getDayOfMonth()));
        if(date.equals(WeekUtils.dateSelected)){
            holder.parent.setBackgroundColor(Color.LTGRAY);
        }

    }

    @Override
    public int getItemCount() {
        return daysInMonth.size();
    }

    public interface ItemListener {
        void onItemClick(int position, LocalDate day);
    }


}
