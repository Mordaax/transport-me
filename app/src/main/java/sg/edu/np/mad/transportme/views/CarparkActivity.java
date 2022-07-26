package sg.edu.np.mad.transportme.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.Carpark;
import sg.edu.np.mad.transportme.CarparkAdapter;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.Route;
import sg.edu.np.mad.transportme.StepAdapter;
import sg.edu.np.mad.transportme.api.ApiCarparkService;
import sg.edu.np.mad.transportme.api.MySingleton;

public class CarparkActivity extends AppCompatActivity {

    ArrayList<Carpark> carparkArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark);

        RecyclerView recyclerView = findViewById(R.id.carparkRecyclerView);

        ApiCarparkService apiCarparkService = new ApiCarparkService(this);
        apiCarparkService.getCarparkAvailability(carparkArrayList, new ApiCarparkService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(CarparkActivity.this,"Cannot get Carpark Availability, Try again later",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(ArrayList<Carpark> Carparks) {
                Log.d("Hello",Carparks.toString());
                CarparkAdapter adapter = new CarparkAdapter(CarparkActivity.this, carparkArrayList);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CarparkActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                recyclerView.setAdapter(adapter);

            }
        });
    }


}