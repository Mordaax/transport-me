package sg.edu.np.mad.transportme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
@RequiresApi(api = Build.VERSION_CODES.O)
public class AddExpenseActivity extends AppCompatActivity {

    String[] items = {"Bus", "Train", "Taxi", "Others"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String selected;
    TextView expenseDate, expenseTime;
    TextInputEditText expenseCost;
    DateTimeFormatter dateFormatter, timeFormatter;
    LocalTime time;
    Button addExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_edit);
        expenseDate = findViewById(R.id.expenseDate);
        expenseTime = findViewById(R.id.expenseTime);
        expenseCost = findViewById(R.id.expenseCost);
        addExpense = findViewById(R.id.addExpense);
        dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        autoCompleteTextView = findViewById(R.id.auto_complete);
        time = LocalTime.now();
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item_layout, items);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected = adapterView.getItemAtPosition(i).toString();
            }
        });
        expenseDate.setText("DATE: " + WeekUtils.dateSelected.format(dateFormatter));
        expenseTime.setText("TIME: " + time.format(timeFormatter));
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cost = expenseCost.getText().toString();
                Expense expense = new Expense(WeekUtils.dateSelected, time, cost, selected);
                Expense.expenseArrayList.add(expense);
                finish();
            }
        });
    }


}