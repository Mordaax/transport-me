package sg.edu.np.mad.transportme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
        /* Load Bus Stops API, can add persistent storage i guess?
         */
        ApiBusStopService apiBusStopService = new ApiBusStopService(LoadingScreen.this);
        apiBusStopService.getBusStop(new ApiBusStopService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(LoadingScreen.this,"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onResponse(ArrayList<BusStop> busStops) {
                progressBar.setProgress(100);
                globalBusStops = busStops;
                Intent goToLoginPage = new Intent(LoadingScreen.this, MainActivity.class);
                startActivity(goToLoginPage);
            }
        });
        progressText.setText("Getting Bus Stops");
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
    }
}