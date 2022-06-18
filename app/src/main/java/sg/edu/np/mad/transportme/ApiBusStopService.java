package sg.edu.np.mad.transportme;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiBusStopService {

    Context context;
    public ApiBusStopService(Context c) {
        this.context = c;
    }

    public interface VolleyResponseListener{
        void onError(String message);
        void onResponse(ArrayList<BusStop> busStops);
    }

    public interface VolleyResponseListener2{
        void onError(String message);
        void onResponse(ArrayList<BusStop> busStopsService);
    }


    public void getBusStop(VolleyResponseListener volleyResponseListener){

        String busStopsUrl = "https://mad-assignment-backend.herokuapp.com/BusStops";
        ArrayList<BusStop> busStops = new ArrayList<>();

        JsonObjectRequest jsonObjectRequestBusStops = new JsonObjectRequest(Request.Method.GET, busStopsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            Log.d("results", String.valueOf(jsonArray.length()));
                            for(int i = 0; i<jsonArray.length();i++) {
                                JSONObject jsonBusStop = jsonArray.getJSONObject(i);
                                String busStopCode = jsonBusStop.get("BusStopCode").toString();
                                String roadName = jsonBusStop.get("RoadName").toString();
                                String description = jsonBusStop.get("Description").toString();
                                Double latitude = Double.parseDouble(jsonBusStop.get("Latitude").toString());
                                Double longitude = Double.parseDouble(jsonBusStop.get("Longitude").toString());
                                busStops.add(new BusStop(busStopCode, roadName, description, latitude, longitude));
                            }
                            volleyResponseListener.onResponse(busStops);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            volleyResponseListener.onError("Cannot Get Bus Stops");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequestBusStops);
    }

    public void getBusService(ArrayList<BusStop> nearBusStops, VolleyResponseListener2 volleyResponseListener2){
        ArrayList<BusStop> busStopsService = new ArrayList<>();
        String busStopsUrl = "https://mad-assignment-backend.herokuapp.com/BusCodes?codes=";
        for (int i = 0; i < nearBusStops.size();i++){
            String code = nearBusStops.get(i).BusStopCode;
            if (i !=0){
                busStopsUrl = busStopsUrl+"," +code;
            }
            else {
                busStopsUrl = busStopsUrl+code;
            }
        }

        JsonObjectRequest jsonObjectRequestBusStop = new JsonObjectRequest(Request.Method.GET, busStopsUrl, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArrayBusStops = response.getJSONArray("Results");
                            List<String> nextBusString = Arrays.asList("NextBus","NextBus2","NextBus3");
                            ArrayList<BusStop> nearBusStopLoaded = new ArrayList<>();
                            Log.d("before", jsonArrayBusStops.toString());
                            LocalDateTime current = LocalDateTime.now();

                            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXXXX");

                            for (int i=0; i<jsonArrayBusStops.length(); i++){
                                JSONObject jsonBusStop = (JSONObject) jsonArrayBusStops.get(i);
                                BusStop nearBusStop = nearBusStops.get(i);
                                ArrayList<BusService> busServices = new ArrayList<>();
                                JSONArray jsonArrayBusServices = jsonBusStop.getJSONArray("Services");
                                for (int j=0;j<jsonArrayBusServices.length();j++){
                                    JSONObject jsonBusService = jsonArrayBusServices.getJSONObject(j);
                                    String serviceNumber = jsonBusService.getString("ServiceNo");
                                    ArrayList<NextBus> nextBuses = new ArrayList<>();

                                    for (String nextbusstring: nextBusString){
                                        JSONObject nextbus = jsonBusService.getJSONObject(nextbusstring);

                                        String estArrival = nextbus.getString("EstimatedArrival");
                                        String arrivalMinutes = "Null";
                                        if (!estArrival.equals("")){
                                            LocalDateTime parsed = LocalDateTime.parse(estArrival,DATE_TIME_FORMATTER);
                                            Duration duration = Duration.between(current,parsed);
                                            arrivalMinutes = String.valueOf(duration.toMinutes());
                                        }

                                        String Latitude =  nextbus.getString("Latitude");
                                        String Longitude = nextbus.getString("Longitude");
                                        String Load = nextbus.getString("Load");
                                        String Feature = nextbus.getString("Feature");
                                        String Type = nextbus.getString("Type");
                                        nextBuses.add(new NextBus(arrivalMinutes,Latitude,Longitude,Load,Feature,Type));
                                    }

                                    busServices.add(new BusService(serviceNumber,nextBuses));
                                }
                                nearBusStop.busServices = busServices;
                                nearBusStopLoaded.add(nearBusStop);
                            }
                            volleyResponseListener2.onResponse(nearBusStopLoaded);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                volleyResponseListener2.onError("Cannot Get data");
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequestBusStop);

        volleyResponseListener2.onResponse(busStopsService);
    }

}
