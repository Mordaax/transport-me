package sg.edu.np.mad.transportme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoadingScreen extends AppCompatActivity {
    public static ArrayList<BusStop> globalBusStops = new ArrayList<>();
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        TextView progressText = findViewById(R.id.progress_text);

        BusStopDBHandler busStopDBHandler = new BusStopDBHandler(LoadingScreen.this,null,null,1);
        globalBusStops = busStopDBHandler.getBusStops();
        if(globalBusStops.size() == 0){
            ApiBusStopService apiBusStopService = new ApiBusStopService(LoadingScreen.this);
            apiBusStopService.getBusStop(new ApiBusStopService.VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Toast.makeText(LoadingScreen.this,"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onResponse(ArrayList<BusStop> busStops) {
                    busStopDBHandler.addBusStops(busStops);
                    progressBar.setProgress(100);
                    globalBusStops = busStops;
                    Intent goToLoginPage = new Intent(LoadingScreen.this, LoginPage.class);
                    startActivity(goToLoginPage);
                }
            });
            progressText.setText("Downloading Bus Stops, Only Happens Once");
            final Handler handler = new Handler();
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
        else{
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
            startActivity(goToLoginPage);

        }

    }
}