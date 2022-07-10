package sg.edu.np.mad.transportme;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExpenseAdapter extends ArrayAdapter<Expense> {
    public ExpenseAdapter(@NonNull Context context, List<Expense> expenseList) {
        super(context, 0, expenseList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        Expense expense = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_layout, parent, false);
        }
        TextView expenseTransport = convertView.findViewById(R.id.expenseTransport);
        TextView expenseType = convertView.findViewById(R.id.expenseType);
        TextView expenseTime2 = convertView.findViewById(R.id.expenseTime2);
        //String setExpenseTransport = expense.getSelected();
        expenseTransport.setText(expense.getSelected());
        //String setExpenseType = " $" + expense.getCost();
        expenseType.setText("$" + expense.getCost());
        //String setExpenseTime = timeFormatter.format(expense.getTime());
        expenseTime2.setText(timeFormatter.format(expense.getTime()));
        return convertView;
    }
}