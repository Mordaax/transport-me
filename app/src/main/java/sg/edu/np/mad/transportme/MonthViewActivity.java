package sg.edu.np.mad.transportme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class MonthViewActivity extends AppCompatActivity implements MonthAdapter.ItemListener{

    private RecyclerView calendarRV;
    private TextView monthInText;
    private LocalDate dateSelected;
    private Button monthBefore, monthAfter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        calendarRV = findViewById(R.id.calendarRV);
        monthInText = findViewById(R.id.yearMonth);
        monthBefore = findViewById(R.id.monthBefore);
        monthAfter = findViewById(R.id.monthAfter);
        dateSelected = LocalDate.now();
        setMonth();

        monthBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSelected = dateSelected.minusMonths(1);
                setMonth();
            }
        });

        monthAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSelected = dateSelected.plusMonths(1);
                setMonth();
            }
        });

    }

    @Override
    public void onItemClick(int position, String dayInText) {

    }

    private void setMonth() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        ArrayList<String> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(dateSelected);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = dateSelected.withDayOfMonth(1);
        int dayOfWeekNum = firstOfMonth.getDayOfWeek().getValue();
        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeekNum || i > daysInMonth + dayOfWeekNum) //if i is before first day of month or after last day of month (calendar dates overflow)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeekNum));
            }
        }
        monthInText.setText(dateSelected.format(formatter)); //sets date in MMMM yyyy
        MonthAdapter monthAdapter = new MonthAdapter(daysInMonthArray, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRV.setLayoutManager(layoutManager);
        calendarRV.setAdapter(monthAdapter);

    }
}