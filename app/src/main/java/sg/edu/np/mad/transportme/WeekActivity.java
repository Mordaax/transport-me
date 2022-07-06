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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class WeekActivity extends AppCompatActivity implements WeekAdapter.ItemListener {
    private RecyclerView calendarRV;
    private TextView weekText;
    public LocalDate dateSelected;
    private Button weekBefore, weekAfter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        calendarRV = findViewById(R.id.calendarRV);
        weekText = findViewById(R.id.yearMonth);
        weekBefore = findViewById(R.id.weekBefore);
        weekAfter = findViewById(R.id.weekAfter);
        dateSelected = LocalDate.now();
        setWeek();

        weekBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSelected = dateSelected.minusWeeks(1);
                setWeek();
            }
        });

        weekAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateSelected = dateSelected.plusWeeks(1);
                setWeek();
            }
        });
    }
    private void setWeek() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        weekText.setText(dateSelected.format(formatter)); //sets date in MMMM yyyy
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>();
        LocalDate chosenDate = weeksSunday(dateSelected);
        LocalDate endDate = chosenDate.plusWeeks(1);
        while (chosenDate.isBefore(endDate))
        {
            daysInWeekArray.add(chosenDate); //ensures it starts from a sunday every time
            chosenDate = chosenDate.plusDays(1);
        }

        WeekAdapter monthAdapter = new WeekAdapter(daysInWeekArray, this, dateSelected);
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
        dateSelected = day;
        setWeek();
    }
}