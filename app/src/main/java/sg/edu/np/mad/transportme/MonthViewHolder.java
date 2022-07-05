package sg.edu.np.mad.transportme;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MonthViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    private MonthAdapter.ItemListener itemListener;
    public TextView dayInMonth;
    public MonthViewHolder(@NonNull View itemView, MonthAdapter.ItemListener itemListener) {
        super(itemView);
        dayInMonth = itemView.findViewById(R.id.Day);
        this.itemListener = itemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemListener.onItemClick(getAdapterPosition(), (String) dayInMonth.getText()); //casting the day into string
    }
}
