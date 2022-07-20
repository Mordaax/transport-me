package sg.edu.np.mad.transportme.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.transportme.Carpark;

public class ApiCarparkService {

    Context context;
    public ApiCarparkService(Context c) {this.context = c;}

    public void getCarpark(){
        String url = "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("AccountKey", "MlHNjn00RVKWi4Z1R5zR+A==");
                params.put("accept", "application/json");
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }



}
