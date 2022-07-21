package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.user.LoginPage.globalName;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class WeekActivity extends AppCompatActivity implements WeekAdapter.ItemListener {
    private RecyclerView calendarRV;
    private ListView weekListView;
    private TextView weekText, ttl;
    private Button weekBefore, weekAfter, log;
    public static Boolean arraySet = false;

    FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        calendarRV = findViewById(R.id.calendarRV);
        weekListView = findViewById(R.id.weekListView);
        weekText = findViewById(R.id.yearMonth);
        weekBefore = findViewById(R.id.weekBefore);
        weekAfter = findViewById(R.id.weekAfter);
        log = findViewById(R.id.log);
        ttl = findViewById(R.id.total);
        Intent intent = new Intent(this, AddExpenseActivity.class);
        WeekUtils.dateSelected = LocalDate.now();
        setExpenseArray();
        setWeek();



        weekBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeekUtils.dateSelected = WeekUtils.dateSelected.minusWeeks(1);
                setWeek();
            }
        });

        weekAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeekUtils.dateSelected = WeekUtils.dateSelected.plusWeeks(1);
                setWeek();
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intent);
            }
        });
    }
    private void setWeek() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        weekText.setText(WeekUtils.dateSelected.format(formatter)); //sets date in MMMM yyyy
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>();
        LocalDate chosenDate = weeksSunday(WeekUtils.dateSelected);
        LocalDate endDate = chosenDate.plusWeeks(1);
        while (chosenDate.isBefore(endDate))
        {
            daysInWeekArray.add(chosenDate); //ensures it starts from a sunday every time
            chosenDate = chosenDate.plusDays(1);
        }

        WeekAdapter monthAdapter = new WeekAdapter(daysInWeekArray, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRV.setLayoutManager(layoutManager);
        calendarRV.setAdapter(monthAdapter);
    }
    private static LocalDate weeksSunday(LocalDate current) //ensures it starts from a sunday every time
    {
        LocalDate prevWeek = current.minusWeeks(1);

        while (current.isAfter(prevWeek))
        {
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
    }

    @Override
    public void onItemClick(int position, LocalDate day) {
        WeekUtils.dateSelected = day;
        setWeek();
        setEventAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Expense> daysExpense = Expense.expensePerDate(WeekUtils.dateSelected);
        ExpenseAdapter expenseAdapter = new ExpenseAdapter(getApplicationContext(), daysExpense);
        weekListView.setAdapter(expenseAdapter);
        int totalcost = 0;
        for (Expense expense : daysExpense){
            int cost = Integer.parseInt(expense.getCost());
            totalcost += cost;
        }
        ttl.setText("Total: $"+totalcost);

    }

    private void setExpenseArray() {
        DatabaseReference reference = db.getReference() //Database Reference expense
                .child("User")
                .child(globalName)
                .child("Expenses");
        Log.w("check","check");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!arraySet){
                    for (DataSnapshot expenses: snapshot.getChildren()) {
                        Log.w("date", expenses.child("Date").getValue().toString());
                        Log.w("cost", expenses.child("Cost").getValue().toString());
                        Log.w("selected", expenses.child("Selected").getValue().toString());
                        LocalDate date = LocalDate.parse(expenses.child("Date").getValue().toString());
                        Expense expense = new Expense(date, expenses.child("Cost").getValue().toString(), expenses.child("Selected").getValue().toString());
                        Expense.expenseArrayList.add(expense);
                    }
                }
                SharedPreferences prefs =  getSharedPreferences("ExpenseData", MODE_PRIVATE);
                arraySet = prefs.getString("arraySet", "").equals("");
                Log.w("s", arraySet.toString());
                setEventAdapter();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


    }
}