package sg.edu.np.mad.transportme.api;
import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
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

import sg.edu.np.mad.transportme.BusService;
import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.NextBus;

public class ApiBusStopService {

    Context context;
    public ApiBusStopService(Context c) {
        this.context = c;
    } //Constructor, passes in context

    // Interface for API service that gets the bus stops
    public interface VolleyResponseListener{
        void onError(String message);
        void onResponse(ArrayList<BusStop> busStops);
    }
    // Interface for API service that get the bus services timings for each bus stop
    public interface VolleyResponseListener2{
        void onError(String message);
        void onResponse(ArrayList<BusStop> busStopsService);
    }
    public interface VolleyResponseListener3{
        void onError(String message);
        void onResponse(ArrayList<BusStop> busStopRoute);
    }

    public void getBusStop(VolleyResponseListener volleyResponseListener){

        String busStopsUrl = "https://mad-assignment-backend.herokuapp.com/BusStops"; //Call all bus stops from heroku backend
        ArrayList<BusStop> busStops = new ArrayList<>();
        // Calls API using JsonObjectRequest
        JsonObjectRequest jsonObjectRequestBusStops = new JsonObjectRequest(Request.Method.GET, busStopsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Results");
                            for(int i = 0; i<jsonArray.length();i++) { //Convert JsonArray object to objects
                                JSONObject jsonBusStop = jsonArray.getJSONObject(i);
                                String busStopCode = jsonBusStop.get("BusStopCode").toString();
                                String roadName = jsonBusStop.get("RoadName").toString();
                                String description = jsonBusStop.get("Description").toString();
                                Double latitude = Double.parseDouble(jsonBusStop.get("Latitude").toString());
                                Double longitude = Double.parseDouble(jsonBusStop.get("Longitude").toString());
                                busStops.add(new BusStop(busStopCode, roadName, description, latitude, longitude));
                            }
                            volleyResponseListener.onResponse(busStops); //Triggers on response callback in MainActivity, returns the busStops
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

    //Another API to call the Bus Services(Bus Timings) for each bus stop
    public void getBusService(ArrayList<BusStop> nearBusStops, VolleyResponseListener2 volleyResponseListener2){
        ArrayList<BusStop> busStopsService = new ArrayList<>();
        String busStopsUrl = "https://mad-assignment-backend.herokuapp.com/BusCodes?codes=";
        for (int i = 0; i < nearBusStops.size();i++){
            String code = nearBusStops.get(i).getBusStopCode();
            if (i !=0){
                busStopsUrl = busStopsUrl+"," +code; //Formats url of bus stop codes for API
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
                        Log.d("Yes", response.toString());
                        try {
                            //Load api Data into objects
                            JSONArray jsonArrayBusStops = response.getJSONArray("Results");
                            List<String> nextBusString = Arrays.asList("NextBus","NextBus2","NextBus3");
                            ArrayList<BusStop> nearBusStopLoaded = new ArrayList<>();
                            LocalDateTime current = LocalDateTime.now(); //Get Current time to compare with estimated arrival time

                            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXXXX");
                            //Load API data into objects
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
                                        // Load next bus data to object
                                        String estArrival = nextbus.getString("EstimatedArrival");
                                        String arrivalMinutes = "Null";
                                        if (!estArrival.equals("")){  //Get difference between time for Arrival Time
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
                                nearBusStop.setBusServices(busServices);
                                nearBusStopLoaded.add(nearBusStop);
                            }
                            volleyResponseListener2.onResponse(nearBusStopLoaded); //Call the onResponse callback in MainActivity

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                volleyResponseListener2.onError("Cannot Get data"); //Call onError Callback in main Activity
            }
        });
        jsonObjectRequestBusStop.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequestBusStop); //Add to request queue

        volleyResponseListener2.onResponse(busStopsService);
    }
    public void getBusRoute(String busServiceNumber, VolleyResponseListener3 volleyResponseListener3){

        String busRouteUrl = "https://mad-assignment-backend.herokuapp.com/BusRoutes?services=" + busServiceNumber; //Call all bus stops from heroku backend
        ArrayList<BusStop> busStopRoute = new ArrayList<>();
        // Calls API using JsonObjectRequest
        JsonObjectRequest jsonObjectRequestBusStops = new JsonObjectRequest(Request.Method.GET, busRouteUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray(busServiceNumber);
                            for(int i = 0; i<jsonArray.length();i++) { //Convert JsonArray object to objects
                                for (int x =0; x<globalBusStops.size(); x++){
                                    if(((String) jsonArray.get(i)).equals(globalBusStops.get(x).getBusStopCode())){
                                        busStopRoute.add(globalBusStops.get(x));
                                    }
                                }

                            }
                            volleyResponseListener3.onResponse(busStopRoute); //Triggers on response callback in MainActivity, returns the busStops
                        } catch (JSONException e) {
                            e.printStackTrace();
                            volleyResponseListener3.onError("Cannot Get Bus Route");
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
}
