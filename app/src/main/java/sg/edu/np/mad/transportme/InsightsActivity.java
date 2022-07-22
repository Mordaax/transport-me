package sg.edu.np.mad.transportme;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;


@RequiresApi(api = Build.VERSION_CODES.O)
public class InsightsActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    String[] transportMode = {"Bus", "Train", "Taxi", "Others"};

    LocalDate date = WeekUtils.dateSelected;
    TextView expenseTV, yearMonth;
    Button monthBefore, monthAfter;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);
        AnyChartView anyChartView = findViewById(R.id.expenseChart);
        yearMonth = findViewById(R.id.yearMonth);
        monthBefore = findViewById(R.id.monthBefore);
        monthAfter = findViewById(R.id.monthAfter);
        anyChartView = findViewById(R.id.expenseChart);
        expenseTV = findViewById(R.id.expenseTextView);

        yearMonth.setText(date.format(formatter));


        final Pie pie = AnyChart.pie();  //creating the pie chart with values from each expense in the expensearraylist that corresponds to the chosen date
        List<DataEntry> dataEntryList = new ArrayList<>();
        double Bus = 0, Train = 0, Taxi = 0, Others = 0;
        for (Expense expense : Expense.expenseArrayList){
            if(expense.getDate().getMonthValue() == date.getMonthValue() && expense.getDate().getYear() == date.getYear()){
                if(expense.getSelected().equals("Bus")) {
                    Bus += Double.parseDouble(expense.getCost());

                }
                else if(expense.getSelected().equals("Train")) {
                    Train += Double.parseDouble(expense.getCost());
                }
                else if(expense.getSelected().equals("Taxi")) {
                    Taxi += Double.parseDouble(expense.getCost());
                }
                else {
                    Others += Double.parseDouble(expense.getCost());
                }
            }
        }

        double[] transportCosts = {Bus,Train,Taxi,Others};
        for (int i = 0; i < transportMode.length; i++) {
            dataEntryList.add(new ValueDataEntry(transportMode[i], transportCosts[i]));
        }
        pie.data(dataEntryList);
        anyChartView.setChart(pie);
        if (DoubleStream.of(transportCosts).sum() > 0){
            expenseTV.setText("Total expenditure for "+date.format(formatter));
        }
        else{
            expenseTV.setText("No expenses logged for "+date.format(formatter));
        }


        monthBefore.setOnClickListener(new View.OnClickListener() { //changing the chosen date and recreating the pie to match the correct values
            @Override
            public void onClick(View view) {
                date = date.minusMonths(1);
                yearMonth.setText(date.format(formatter));
                initChart(pie);

            }
        });

        monthAfter.setOnClickListener(new View.OnClickListener() { //changing the chosen date and recreating the pie to match the correct values
            @Override
            public void onClick(View view) {
                date = date.plusMonths(1);
                yearMonth.setText(date.format(formatter));
                initChart(pie);

            }
        });

    }


    public void initChart(Pie pie) { //used to update the pie when new date is chosen
        List<DataEntry> dataEntryList = new ArrayList<>();
        double Bus = 0, Train = 0, Taxi = 0, Others = 0;
        for (Expense expense : Expense.expenseArrayList){
            if(expense.getDate().getMonthValue() == date.getMonthValue() && expense.getDate().getYear() == date.getYear()){
                if(expense.getSelected().equals("Bus")) {
                    Bus += Double.parseDouble(expense.getCost());

                }
                else if(expense.getSelected().equals("Train")) {
                    Train += Double.parseDouble(expense.getCost());
                }
                else if(expense.getSelected().equals("Taxi")) {
                    Taxi += Double.parseDouble(expense.getCost());
                }
                else {
                    Others += Double.parseDouble(expense.getCost());
                }
            }
        }

        double[] transportCosts = {Bus,Train,Taxi,Others};
        for (int i = 0; i < transportMode.length; i++) {
            dataEntryList.add(new ValueDataEntry(transportMode[i], transportCosts[i]));
        }
        pie.data(dataEntryList);
        if (DoubleStream.of(transportCosts).sum() > 0){
            expenseTV.setText("Total expenditure for "+date.format(formatter));
        }
        else{
            expenseTV.setText("No expenses logged for "+date.format(formatter));
        }

    }
}