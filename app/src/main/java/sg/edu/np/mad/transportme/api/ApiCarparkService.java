package sg.edu.np.mad.transportme.api;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sg.edu.np.mad.transportme.Carpark;

public class ApiCarparkService {

    Context context;
    JsonObjectRequest jsonObjectRequestCarPark;

    //interface for retrieving carparks
    public interface VolleyResponseListener{
        void onError(String message);
        void onResponse(ArrayList<Carpark> Carparks);
    }

    public ApiCarparkService(Context c) {this.context = c;}

    public void getCarparkAvailability(ArrayList<Carpark> carparkArrayList, VolleyResponseListener volleyResponseListener){
        String url = "https://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2";
        //Get carparks using api
        jsonObjectRequestCarPark = new JsonObjectRequest( Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("value");
                            for (int i = 0; i < jsonArray.length(); i++){
                                //converting JSONArray to JSONObject
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int carparkID =  Integer.parseInt(jsonObject.get("CarParkID").toString());
                                String area = jsonObject.get("Area").toString();
                                String dev = jsonObject.get("Development").toString();
                                String[] coordinates = jsonObject.get("Location").toString().split(" ");
                                Location location = new Location(dev);
                                location.setLatitude(Double.parseDouble(coordinates[0]));
                                location.setLongitude(Double.parseDouble(coordinates[1]));
                                int available = Integer.parseInt(jsonObject.get("AvailableLots").toString());
                                String lotType = jsonObject.get("LotType").toString();
                                String agency = jsonObject.get("Agency").toString();
                                //Adding object to array
                                carparkArrayList.add(new Carpark(carparkID, area, dev, location, available, lotType, agency));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("TAG", response.toString());
                        volleyResponseListener.onResponse(carparkArrayList);
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
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequestCarPark);
    }



}
