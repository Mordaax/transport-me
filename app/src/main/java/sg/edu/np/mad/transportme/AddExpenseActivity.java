package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.user.LoginPage.globalName;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
@RequiresApi(api = Build.VERSION_CODES.O)
public class AddExpenseActivity extends AppCompatActivity {
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
    String[] items = {"Bus", "Train", "Taxi", "Others"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String selected;
    TextView expenseDate, expenseTime;
    TextInputEditText expenseCost;
    DateTimeFormatter dateFormatter, timeFormatter;
    Button addExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_edit);
        expenseDate = findViewById(R.id.expenseDate);
        expenseCost = findViewById(R.id.expenseCost);
        addExpense = findViewById(R.id.addExpense);
        dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        autoCompleteTextView = findViewById(R.id.auto_complete);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item_layout, items);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected = adapterView.getItemAtPosition(i).toString();
            }
        });
        expenseDate.setText("DATE: " + WeekUtils.dateSelected.format(dateFormatter));
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cost = expenseCost.getText().toString();
                if(cost.isEmpty()){
                    expenseCost.setError("Please enter a cost");
                    expenseCost.requestFocus();
                    return;
                }
                if(selected == null){
                    autoCompleteTextView.setError("Please enter a mode of transport");
                    autoCompleteTextView.requestFocus();
                    return;
                }
                Expense expense = new Expense(WeekUtils.dateSelected, cost, selected);
                Expense.expenseArrayList.add(expense);
                DatabaseReference reference = db.getReference()
                        .child("User")
                        .child(globalName)
                        .child("Expenses");
                String id = reference.push().getKey();
                reference.child(id).child("Date").setValue(WeekUtils.dateSelected.toString());
                reference.child(id).child("Cost").setValue(cost);
                reference.child(id).child("Selected").setValue(selected);
                finish();
            }
        });
    }


}