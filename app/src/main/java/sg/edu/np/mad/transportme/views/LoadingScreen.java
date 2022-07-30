package sg.edu.np.mad.transportme.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.BusStopDBHandler;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.user.LoginPage;

public class LoadingScreen extends AppCompatActivity {

    //Global bus stop list, holds data for all bus stops
    public static ArrayList<BusStop> globalBusStops = new ArrayList<>();
    int i = 0;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        final String[] LOCATION_PERMS={
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        final int LOCATION_REQUEST=1337;

        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST); //Request permissions from user

        ProgressBar progressBar = findViewById(R.id.progress_bar); //Progress Bar for loading screen to downlaod all bus stops
        TextView progressText = findViewById(R.id.progress_text);

        // Initialize local SQL database to hold all bus stop data (Bus Stop code, Lat Long, Description, Roadname)
        BusStopDBHandler busStopDBHandler = new BusStopDBHandler(LoadingScreen.this,null,null,1);
        globalBusStops = busStopDBHandler.getBusStops(); //Grab existing bus stop data

        //Check if its the first time the app runs, if it is it will download the bus stops
        if(globalBusStops.size() == 0){
            ApiBusStopService apiBusStopService = new ApiBusStopService(LoadingScreen.this); //Initialize API
            apiBusStopService.getBusStop(new ApiBusStopService.VolleyResponseListener() { //Call API asynchronously
                @Override
                public void onError(String message) {
                    Toast.makeText(LoadingScreen.this,"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onResponse(ArrayList<BusStop> busStops) {
                    busStopDBHandler.addBusStops(busStops);
                    progressBar.setProgress(100);
                    globalBusStops = busStops; // Load called bus stop data to globalBusStop to be access by all classes

                    Intent goToLoginPage = new Intent(LoadingScreen.this, LoginPage.class); //Go to login page
                    goToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToLoginPage);
                }
            });
            progressText.setText("Downloading Bus Stops, Only Happens Once");
            final Handler handler = new Handler();  //Handles the progress bar, progress bar is based on time
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // set the limitations for the numeric
                    // text under the progress bar
                    if (i <= 85) {
                        progressBar.setProgress(i);
                        i+=1;
                        handler.postDelayed(this, 200);
                    } else {
                        handler.removeCallbacks(this);
                    }
                }
            }, 200);
        }
        else{ // Progress bar for when database is already downloaded, just need to grab data from local SQL database
            progressText.setText("Loading Bus Stops");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // set the limitations for the numeric
                    // text under the progress bar
                    if (i <= 85) {
                        progressBar.setProgress(i);
                        i+=5;
                        handler.postDelayed(this, 200);
                    } else {
                        handler.removeCallbacks(this);  
                    }
                }
            }, 200);

            Intent goToLoginPage = new Intent(LoadingScreen.this, LoginPage.class);
            goToLoginPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goToLoginPage);

        }

    }
}