package sg.edu.np.mad.transportme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class LoadingScreen extends AppCompatActivity {
    public static ArrayList<BusStop> globalBusStops = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

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
                globalBusStops = busStops;
                Intent goToLoginPage = new Intent(LoadingScreen.this, MainActivity.class);
                startActivity(goToLoginPage);
            }
        });
    }
}