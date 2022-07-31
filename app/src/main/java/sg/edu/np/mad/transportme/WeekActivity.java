package sg.edu.np.mad.transportme;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static sg.edu.np.mad.transportme.user.LoginPage.globalName;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import sg.edu.np.mad.transportme.views.CarparkActivity;
import sg.edu.np.mad.transportme.views.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class WeekActivity extends AppCompatActivity implements WeekAdapter.ItemListener, NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView calendarRV;
    private ListView weekListView;
    private TextView weekText, ttl;
    private Button weekBefore, weekAfter, log, insights;
    public static Boolean arraySet = false;
    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    LinearLayout contentView;
    NavigationView navigationView;
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.weekContentView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_fares);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();

        calendarRV = findViewById(R.id.calendarRV);
        weekListView = findViewById(R.id.weekListView);
        weekText = findViewById(R.id.yearMonth);
        weekBefore = findViewById(R.id.weekBefore);
        weekAfter = findViewById(R.id.weekAfter);
        log = findViewById(R.id.log);
        insights = findViewById(R.id.insights);
        ttl = findViewById(R.id.total);
        Intent toAddExpense = new Intent(this, AddExpenseActivity.class);
        Intent toInsights = new Intent(this, InsightsActivity.class);
        WeekUtils.dateSelected = LocalDate.now();
        setExpenseArray();
        setWeek();



        weekBefore.setOnClickListener(new View.OnClickListener() { //displays the previous week
            @Override
            public void onClick(View view) {
                WeekUtils.dateSelected = WeekUtils.dateSelected.minusWeeks(1);
                setWeek();
            }
        });

        weekAfter.setOnClickListener(new View.OnClickListener() { //displays the next week
            @Override
            public void onClick(View view) {
                WeekUtils.dateSelected = WeekUtils.dateSelected.plusWeeks(1);
                setWeek();
            }
        });
        log.setOnClickListener(new View.OnClickListener() { //brings user to log expense
            @Override
            public void onClick(View view) {

                startActivity(toAddExpense);
                overridePendingTransition(R.anim.right_slidein, R.anim.left_slideout);
            }
        });

        insights.setOnClickListener(new View.OnClickListener() { //allows user to check expense insight
            @Override
            public void onClick(View view) {

                startActivity(toInsights);
                overridePendingTransition(R.anim.right_slidein, R.anim.left_slideout);
            }
        });
    }
    private void setWeek() { //ensures the week from the calendar starts from a sunday every time

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        weekText.setText(WeekUtils.dateSelected.format(formatter)); //sets date in MMMM yyyy (eg. July 2022)
        ArrayList<LocalDate> daysInWeekArray = new ArrayList<>(); //the days in the week to be set by the adapter to the viewholder
        LocalDate chosenDate = weeksSunday(WeekUtils.dateSelected); //the sunday of the selected date
        LocalDate endDate = chosenDate.plusWeeks(1); //the next sunday after the selected date
        //populates the daysinweekarray with the week's dates and nothing more
        while (chosenDate.isBefore(endDate))
        {
            daysInWeekArray.add(chosenDate);
            chosenDate = chosenDate.plusDays(1);
        }
        //sets the calendar to match the week and dates set in daysInWeekArray
        WeekAdapter weekAdapter = new WeekAdapter(daysInWeekArray, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRV.setLayoutManager(layoutManager);
        calendarRV.setAdapter(weekAdapter);
    }
    //ensures it starts from a sunday every time by selecting the sunday within the selected date's week
    private static LocalDate weeksSunday(LocalDate current)
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
    public void onItemClick(int position, LocalDate day) { //set the day to what the user clicked, display expenses accordingly
        WeekUtils.dateSelected = day;
        setWeek();
        setExpenseAdapter();
    }

    @Override
    protected void onResume() { //set expense adapter if activity is restarted
        super.onResume();
        setExpenseAdapter();
    }

    //populates the listview with expenses in the array, showing the expenses for the day and the total expenses for the day
    private void setExpenseAdapter() {
        ArrayList<Expense> daysExpense = Expense.expensePerDate(WeekUtils.dateSelected); //gets the arraylist of expenses for the date selected
        ExpenseAdapter expenseAdapter = new ExpenseAdapter(getApplicationContext(), daysExpense); //passes it into ExpenseAdapter to set the expense listview
        weekListView.setAdapter(expenseAdapter);
        double totalcost = 0;
        for (Expense expense : daysExpense){
            double cost = Double.parseDouble(expense.getCost());
            totalcost += cost;
        }
        ttl.setText("Total: $"+totalcost);

    }
    //retrieves expense data from database to populate the expense array which is to be used in setting the expense adapter
    private void setExpenseArray() {
        DatabaseReference reference = db.getReference() //Database Reference expense
                .child("User")
                .child(globalName)
                .child("Expenses");
        Log.w("check","check");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!arraySet){ //ensures the array is only set once per session
                    //retrieving all expense data under the current user from firebase, respectively creating Expenses and adding it to the array
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
                setExpenseAdapter(); //populate adapter with populated array
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    private void animateNavigationDrawer(){
        /*drawerLayout.setScrimColor(getResources().getColor(com.google.android.material.R.color.));*/
        drawerLayout.setScrimColor(Color.parseColor("#e8c490"));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:

                finish();

                /*fragmentlayout.setVisibility(View.INVISIBLE); //Set fragment to invisible, show map and main recycler view to help with loading times
                mapandrv.setVisibility(View.VISIBLE);
                favourite = false;*/
                break;
            case R.id.nav_carpark:
                Intent intentcarpark = new Intent(WeekActivity.this, CarparkActivity.class);
                intentcarpark.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentcarpark);
                finish();
                break;
            case R.id.nav_profile:
                Intent intentMainActivity = new Intent(WeekActivity.this, MainActivity.class);
                intentMainActivity.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                intentMainActivity.putExtra("Profile", "Yes");

                startActivity(intentMainActivity);
                finish();
                /*mapandrv.setVisibility(View.INVISIBLE);
                fragmentlayout.setVisibility(View.VISIBLE);
                replaceFragment(new ProfileFragment());*/
                break;
            case R.id.nav_route:
                Intent routeintent = new Intent(WeekActivity.this, RouteActivity.class);
                routeintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(routeintent);
                finish();
                break;
            case R.id.nav_fares:
                break;
            case R.id.nav_rate:
                Uri uri = Uri.parse("market://details?id=sg.edu.np.mad.transportme");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=sg.edu.np.mad.transportme")));
                    break;
                }
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the Best Bus App In Singapore! \n\n https://play.google.com/store/apps/details?id=sg.edu.np.mad.transportme");
                startActivity(Intent.createChooser(sendIntent,"Share With"));
                break;
            case R.id.nav_privacy:
                Intent privacyintent = new Intent(WeekActivity.this, PrivacyPolicyActivty.class);
                privacyintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(privacyintent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}