package sg.edu.np.mad.transportme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.format.DateTimeFormatter;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;
import sg.edu.np.mad.transportme.user.LoginPage;
import sg.edu.np.mad.transportme.views.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExpenseAdapter extends ArrayAdapter<Expense> {
    public ExpenseAdapter(@NonNull Context context, List<Expense> expenseList) {
        super(context, 0, expenseList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Expense expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_layout, parent, false);
        }
        TextView expenseTransport = convertView.findViewById(R.id.expenseTransport);
        TextView expenseType = convertView.findViewById(R.id.expenseType);

        expenseTransport.setText(expense.getSelected());
        expenseType.setText("$" + expense.getCost());
        return convertView;



    }
}