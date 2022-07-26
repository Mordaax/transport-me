package sg.edu.np.mad.transportme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.views.MainActivity;

public class BusServiceAdapter
        extends RecyclerView.Adapter<BusServiceViewHolder> {
    ArrayList<BusService> data;
    Context c;
    List<Marker> mList = new ArrayList<>();
    List<LatLng> lList = new ArrayList<>();
    Polyline line = null;
    public BusServiceAdapter(ArrayList<BusService> data, Context c) {
        this.c = c;
        this.data = data;
    }

    @NonNull
    @Override
    public BusServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_service_layout, null, false);

        return new BusServiceViewHolder(item);
    }
    @Override
    public void onBindViewHolder(@NonNull BusServiceViewHolder holder, int position) {
        BusService content = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Yes", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                builder.setTitle("Show bus routes for " + content.getServiceNumber() + "?");
                builder.setIcon(R.drawable.appsplashicon);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {

                        if (options[item].equals("Yes")) {
                            ((MainActivity) c).removemarker(mList, line);
                            line = null;
                            lList.clear();
                            mList.clear();
                            ApiBusStopService apiBusStopService = new ApiBusStopService(c);
                            apiBusStopService.getBusRoute(content.getServiceNumber(),new ApiBusStopService.VolleyResponseListener3() { //Call API for nearby bus stops
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(c,"Cannot Get Bus Route, Check Location and Connection",Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                    apiBusStopService.getBusService(busStopRouteLoaded, new ApiBusStopService.VolleyResponseListener2() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(c, "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopsLoaded) {
                                            /*RecyclerView rv = view.findViewById(R.id.searchrecyclerView);
                                            BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,c);
                                            LinearLayoutManager layout = new LinearLayoutManager(c);
                                            rv.setAdapter(adapter);
                                            rv.setLayoutManager(layout);*/
                                            ((MainActivity) c).busrouteview(busStopsLoaded);
                                            /*progressDialog.dismiss();*/
                                        }
                                    });

                                    for(BusStop busStop : busStopRouteLoaded) {
                                        ((MainActivity) c).busroute(busStop.getLatitude(), busStop.getLongitude(), busStop, mList, lList);
                                    }
                                    ((MainActivity) c).camerazoom(mList);
                                    line = ((MainActivity) c).polyline(lList);
                                    Snackbar snackbar = Snackbar.make(view.getRootView(), "Showing route for "+content.getServiceNumber(), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("cancel", new View.OnClickListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.M)
                                                @Override
                                                public void onClick(View view) {
                                                    ((MainActivity) c).removemarker(mList, line);
                                                    line = null;
                                                    lList.clear();
                                                    mList.clear();
                                                }
                                            });
                                    View snackbarLayout = snackbar.getView();
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    lp.setMargins(200, 1000, 0, 0);
                                    snackbarLayout.setLayoutParams(lp);
                                    snackbar.show();
                                }
                            });
                        }
                        if (options[item].equals("Cancel")) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        holder.ServiceNumber.setText(content.getServiceNumber());       //Set the Text of Bus Service Number
        //Populating the list with each attribute of the next bus (Bus, Type, Feature)
        ArrayList<TextView> nextBusList= new ArrayList<TextView>(Arrays.asList(holder.NextBus1, holder.NextBus2, holder.NextBus3));
        ArrayList<TextView> nextBusTypeList = new ArrayList<TextView>(Arrays.asList(holder.NextBus1Type, holder.NextBus2Type, holder.NextBus3Type));
        ArrayList<ImageView> nextBusFeatureList = new ArrayList<>(Arrays.asList(holder.NextBus1Feature, holder.NextBus2Feature, holder.NextBus3Feature));
        for (int i = 0; i < nextBusList.size(); i++)        //Loops through the 3 next busses
        {
            //Declaring the next bus
            NextBus nb = content.getNextBuses().get(i);
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

        /*
        Load: SEA (Seats available), SDA (Standing available), LSD (Limited Standing)
        Feature: WAB (wheel Chair accessible), none (Not wheel chat accessible)
        Type: SD (Single Deck), DD (Double Deck), BD (Bendy) */
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}