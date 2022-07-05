package sg.edu.np.mad.transportme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MonthAdapter extends RecyclerView.Adapter<MonthViewHolder> {

    private ArrayList<String> dayInMonth;
    private ItemListener itemListener;

    public MonthAdapter(ArrayList<String> dayInMonth, ItemListener itemListener) {
        this.dayInMonth = dayInMonth;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_layout, parent, false);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.16666667); //each cell is 1/6 of full view
        return new MonthViewHolder(v, itemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        holder.dayInMonth.setText(dayInMonth.get(position));
    }

    @Override
    public int getItemCount() {
        return dayInMonth.size();
    }

    public interface ItemListener {
        void onItemClick(int position, String dayInText);
    }


}
