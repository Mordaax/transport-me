package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

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

import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.api.MySingleton;

public class StepAdapter extends RecyclerView.Adapter<StepViewHolder> {
    ArrayList<RouteStep> data;
    Context c;
    public StepAdapter(Context c, ArrayList<RouteStep> data){
        this.data = data;
        this.c = c;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || position+1 == data.size()){ //Set layout based on travel type, changes ICON and data displayed
            return 4;
        }
        if (data.get(position).TravelMode.equals("Walk")){
            return 1;
        }
        else if(data.get(position).TravelMode.equals("Bus")){
            return 2;
        }
        else if(data.get(position).TravelMode.equals("Drive")){
            return 5;
        }
        else{
            return 3;
        }

    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//Set layout based on travel type, changes ICON and data displayed
        View item = null;
        if (viewType ==1)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_walk_layout,parent,false);
        else if(viewType ==2)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_bus_layout,parent,false);
        else if(viewType == 3)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_mrt_layout,parent,false);
        else if(viewType == 4)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_startend_layout,parent,false);
        else if(viewType == 5)
            item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_car_layout,parent,false);
        return new StepViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        RouteStep content = data.get(position);
        ArrayList<BusStop> globallol = globalBusStops;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.stepcoordinates.size() > 0) {
                    ((RouteActivity) c).moveMapCamera(content.stepcoordinates);
                }
                if (content.TravelMode.equals("Bus")){ //Used to get bus stop timing for the routing
                    String BusNumber = content.LineName;
                    for (int i=0;i<globallol.size();i++){
                        BusStop currentstop = globallol.get(i);

                        if (currentstop.getDescription().equalsIgnoreCase(content.PreviousLocation) || currentstop.getRoadName().equalsIgnoreCase(content.PreviousLocation) ){

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                    (Request.Method.GET, "https://mad-assignment-backend.herokuapp.com/BusCodes?codes="+currentstop.getBusStopCode(), null, new Response.Listener<JSONObject>() {

                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try{
                                                JSONArray jsonArrayBusStops = response.getJSONArray("Results");
                                                List<String> nextBusString = Arrays.asList("NextBus","NextBus2","NextBus3");
                                                ArrayList<BusStop> nearBusStopLoaded = new ArrayList<>();
                                                LocalDateTime current = LocalDateTime.now(); //Get Current time to compare with estimated arrival time

                                                DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssXXXXX");
                                                //Load API data into objects

                                                JSONObject jsonBusStop = (JSONObject) jsonArrayBusStops.get(0);

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
                                                currentstop.setBusServices(busServices);
                                                holder.ServiceNumber.setText(BusNumber);       //Set the Text of Bus Service Number
                                                //Populating the list with each attribute of the next bus (Bus, Type, Feature)
                                                ArrayList<TextView> nextBusList= new ArrayList<TextView>(Arrays.asList(holder.NextBus1, holder.NextBus2, holder.NextBus3));
                                                ArrayList<TextView> nextBusTypeList = new ArrayList<TextView>(Arrays.asList(holder.NextBus1Type, holder.NextBus2Type, holder.NextBus3Type));
                                                ArrayList<ImageView> nextBusFeatureList = new ArrayList<>(Arrays.asList(holder.NextBus1Feature, holder.NextBus2Feature, holder.NextBus3Feature));
                                                for (int i = 0; i < nextBusList.size(); i++)        //Loops through the 3 next busses
                                                {
                                                    //Declaring the next bus
                                                    NextBus nb = currentstop.getBusServices().get(0).getNextBuses().get(i);
                                                    //Assigning variables to their corresponding holder views
                                                    TextView busTV = nextBusList.get(i);
                                                    TextView busTypeTV = nextBusTypeList.get(i);
                                                    ImageView busFeatureIV = nextBusFeatureList.get(i);
                                                    if (nb.getEstimatedArrival() == "Null")                 //Checks if the bus exists (Null == non-existent)
                                                    {
                                                        busTV.setTextColor(Color.parseColor("#000000"));    //Sets the text to 一 in black to indicate no bus
                                                        busTV.setText("一");
                                                        busTypeTV.setText("");
                                                        busFeatureIV.setVisibility(View.GONE);
                                                        continue;
                                                    }
                                                    else if (Integer.parseInt(nb.getEstimatedArrival()) == 0)       //When 0 minutes is left to arrival, set to Arr
                                                    {
                                                        busTV.setText("Arr");
                                                    }
                                                    else if (Integer.parseInt(nb.getEstimatedArrival()) < 0)        //If estimated arrival is negative, set to Left
                                                    {
                                                        busTV.setText("Left");
                                                    }
                                                    else
                                                    {
                                                        nextBusList.get(i).setText(nb.getEstimatedArrival());       //Else set it to estimated arrival time in mins
                                                    }

                                                    if (nb.getFeature().equals("none"))                             //Checks if bus is WAB (Wheel chair accessible)
                                                    {
                                                        busFeatureIV.setVisibility(View.GONE);                      //Does not display WAB icon if none
                                                    }

                                                    if (nb.getLoad().equals("SEA"))     //SEA = Seats Available
                                                    {
                                                        busTV.setTextColor(Color.parseColor("#90a959"));           //Sets text color to green (Green = Not Full)
                                                    }
                                                    else if (nb.getLoad().equals("SDA"))    //SDA = Standing Available
                                                    {
                                                        busTV.setTextColor(Color.parseColor("#e9b872"));           //Sets text color to yellow (Yellow = medium fullness)
                                                        busFeatureIV.setImageResource(R.drawable.wheelchair_yellow);        //Sets WAB icon to yellow
                                                    }
                                                    else        //Else = LSD = Limited Standing
                                                    {
                                                        busTV.setTextColor(Color.parseColor("#a63d40"));          //Sets text color to red (Red = Extremely full)
                                                        busFeatureIV.setImageResource(R.drawable.wheelchair_red);          //sets WAB icon to red
                                                    }

                                                    if (nb.getType().equals("SD"))      //SD = Single Deck
                                                    {
                                                        busTypeTV.setText("Single");    //Displays bus type as Single
                                                    }
                                                    else if (nb.getType().equals("DD")) //DD = Double Deck
                                                    {
                                                        busTypeTV.setText("Double");    //Displays bus type as Double
                                                    }
                                                    else        //Else = BD = Bendy
                                                    {
                                                        busTypeTV.setText("Bendy");     //Displays bus type as Bendy
                                                    }
                                                }
                                                if (holder.linearlayout_timing.getVisibility() == View.VISIBLE){
                                                    holder.linearlayout_timing.setVisibility(View.GONE);
                                                    TransitionManager.beginDelayedTransition(holder.buscardview,new AutoTransition());
                                                }
                                                else{
                                                    holder.linearlayout_timing.setVisibility(View.VISIBLE);
                                                    TransitionManager.beginDelayedTransition(holder.buscardview,new AutoTransition());
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO: Handle error

                                        }
                                    });

// Access the RequestQueue through your singleton class.
                            MySingleton.getInstance(c).addToRequestQueue(jsonObjectRequest);



                            ApiBusStopService apiBusStopService = new ApiBusStopService(c);
                            /*apiBusStopService.getBusService(currentStop, new ApiBusStopService.VolleyResponseListener2() { //call api to get bus services
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(c, "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {
                                    BusStop thisStop = busStopsLoaded.get(0);

                                    for (BusService currentService: thisStop.getBusServices()){
                                        if (currentService.getServiceNumber().equals(BusNumber)){

                                            h

                                        }
                                    }
                                }
                            });*/
                        }
                    }

                }
            }
        });
        if (position == 0 || position+1 == data.size()){
            holder.instructions.setText(content.Instructions);
        }
        else{
            holder.instructions.setText(content.Instructions);
            holder.duration.setText(content.Duration);
            holder.distance.setText(content.Distance);
            if (!content.TravelMode.equals("Drive")){
                holder.previouslocation.setText(content.PreviousLocation);
                holder.nextlocation.setText(content.NextLocation);
            }

            if (!content.TravelMode.equals("Walk") && !content.TravelMode.equals("Drive")){
                holder.line.setText(content.LineName);
                holder.stopcount.setText(content.NumStops.toString());
                if (content.TravelMode.equals("Subway")){
                    holder.linearlayout_mrt.setBackgroundColor(Color.parseColor(content.LineColor)); //Set background color based on
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
