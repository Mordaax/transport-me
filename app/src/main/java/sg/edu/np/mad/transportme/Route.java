package sg.edu.np.mad.transportme;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static sg.edu.np.mad.transportme.ReminderApplication.CHANNEL_ID_2;
import static sg.edu.np.mad.transportme.ReminderApplication.getContext;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminder;
import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;
import static sg.edu.np.mad.transportme.views.MainActivity.networkprovider;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import sg.edu.np.mad.transportme.user.ProfileFragment;
import sg.edu.np.mad.transportme.views.CarparkActivity;
import sg.edu.np.mad.transportme.views.MainActivity;

public class Route extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    DrawerLayout drawerLayout;
    LinearLayout contentView;
    NavigationView navigationView;
    static final float END_SCALE = 0.7f;
    private GoogleMap mMap;
    String currentLocation = "";
    Spinner traveloption;
    String travelmode = "transit";
    Boolean notificationchoice = false;
    ArrayList<RouteStep> routestepsreminder;
    TextToSpeech t1;
    Boolean drivingchoice;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routemap);
        mapFragment.getMapAsync(this);
        ImageView notificationbellimage = findViewById(R.id.notificationbellimage);
        notificationbellimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notificationchoice == false){
                    notificationbellimage.setImageResource(R.drawable.filled_bell);
                    notificationchoice = true;
                }
                else if(notificationchoice == true){
                    notificationbellimage.setImageResource(R.drawable.grey_bell);
                    notificationchoice = false;
                }
            }
        });

        ImageView DropdownMenu = findViewById(R.id.dropdown_icon);
        LinearLayout dropdown = findViewById(R.id.dropdownmenu);
        DropdownMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dropdown.getVisibility() == View.VISIBLE) {

                    dropdown.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(contentView, new AutoTransition());
                } else {
                    dropdown.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(contentView, new AutoTransition());
                }
            }
        });

        traveloption = findViewById(R.id.spinnerTransportType);
        ArrayAdapter<CharSequence> travelmodeadapter = ArrayAdapter.createFromResource(this, R.array.travelmodes, android.R.layout.simple_spinner_item);
        travelmodeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        traveloption.setAdapter(travelmodeadapter);

        traveloption.setOnItemSelectedListener(this);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.contentView);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_route);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();


        AutoCompleteTextView atcfrom = findViewById(R.id.actfrom);
        AutoCompleteTextView actto = findViewById(R.id.actto);

        ArrayList<String> searchStrings = new ArrayList<String>();
        for (int i = 0; i < globalBusStops.size(); i++) {
            searchStrings.add(globalBusStops.get(i).getDescription());
            searchStrings.add(globalBusStops.get(i).getRoadName());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchStrings);
        atcfrom.setAdapter(adapter);
        actto.setAdapter(adapter);

        atcfrom.setText("Current Location");
        //makes it such that when user clicks on edit textbox, textbox is not automatically filled with "CHANGE PASSWORD"
        atcfrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atcfrom.setText("");
                } else /*if(b == false && password.getEditableText().equals(""))*/ {
                    if (!(atcfrom.getEditableText().length() > 0)) {
                        atcfrom.setText("Current Location");
                    }
                }
            }
        });
        TextView placeholder = findViewById(R.id.textviewplaceholder);
        Button routeButton = findViewById(R.id.buttonRoute);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            final String[] LOCATION_PERMS = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };

            final int LOCATION_REQUEST = 1337;

            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            Toast.makeText(Route.this, "Check Location and Connection Settings", Toast.LENGTH_LONG).show();

            return;
        } else {
            if (locationManager.isProviderEnabled(networkprovider)) {
                locationManager.requestLocationUpdates(networkprovider, 2000, 2, new LocationListener() { //Every 60 seconds or 10m change, run code
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.d("location", "location changed");
                        Double Latitude = location.getLatitude(); //Get latitude and logitude
                        Double Longitude = location.getLongitude();
                        currentLocation = Latitude.toString() + "," + Longitude.toString();
                        routeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                mMap.clear();
                                String from = String.valueOf(atcfrom.getText());
                                String to = String.valueOf(actto.getText());
                                if (to.equals("")) {
                                    Toast.makeText(Route.this, "Please Add a location to route to", Toast.LENGTH_LONG).show();
                                } else {
                                    direction(from, to);
                                }
                                placeholder.setVisibility(View.GONE);
                            }
                        });
                        LatLng latLng = new LatLng(Latitude, Longitude);
                        if (notificationchoice){
                            try{
                                for (int i = 0;i<routestepsreminder.size();i++){
                                    RouteStep routestep = routestepsreminder.get(i);
                                    Double destnDist = SphericalUtil.computeDistanceBetween(latLng,routestep.Latlongend);
                                    if (destnDist<=100.0){
                                        if(routestep.TravelMode!="Drive"){
                                            Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID_2)
                                                    .setSmallIcon(R.drawable.app_logo_vector)
                                                    .setContentTitle(!routestep.TravelMode.equals("Walk")? "Remember to alight":"You are close!")
                                                    .setContentText("You are arriving at "+ routestep.NextLocation + "!")
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                    .build();

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                            notificationManager.notify(1,notification);
                                            routestepsreminder.remove(routestep);
                                            Log.d("YEs","Yes");
                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            /*if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, new LocationListener() { //Every 60 seconds or 10m change, run code
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.d("location", "location changed");

                        Double Latitude = location.getLatitude(); //Get latitude and logitude
                        Double Longitude = location.getLongitude();
                        currentLocation = Latitude.toString() + "," + Longitude.toString();
                        routeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mMap.clear();
                                String from = String.valueOf(atcfrom.getText());
                                String to = String.valueOf(actto.getText());
                                direction(from, to);
                                placeholder.setVisibility(View.GONE);
                            }
                        });
                        LatLng latLng = new LatLng(Latitude, Longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                });
            }*/


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng singapore = new LatLng(1.3521, 103.8198);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 10f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            final String[] LOCATION_PERMS = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            final int LOCATION_REQUEST = 1337;

            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void direction(String From, String Destination){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        ArrayList<RouteStep> routeSteps = new ArrayList<>();
        if (From.equals("Current Location")){
            if (!currentLocation.isEmpty()){
                From = currentLocation;
            }
            else{
                Toast.makeText(Route.this, "Cannot Get Current location", Toast.LENGTH_SHORT).show();
            }
        }
        /*String url = "https://mad-assignment-backend.herokuapp.com/routetest";*/
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination",Destination)
                .appendQueryParameter("origin",From)
                .appendQueryParameter("mode",travelmode)
                .appendQueryParameter("key","AIzaSyC5TLFoQWmsorYN0--un6BieG6VI2STONE")
                .toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        LatLngBounds.Builder bounds = LatLngBounds.builder();
                        if(routes.length()<1){
                            Toast.makeText(Route.this, "Cannot find route, be more specific", Toast.LENGTH_LONG).show();
                        }

                        for (int i = 0; i < routes.length(); i++) {

                            PolylineOptions polylineOptions = null;
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j = 0; j < legs.length(); j++) {
                                String start_address = legs.getJSONObject(j).getString("start_address");
                                String end_address = legs.getJSONObject(j).getString("end_address");
                                RouteStep firstRouteStep = new RouteStep();
                                firstRouteStep.Instructions = "From "+ start_address;
                                routeSteps.add(firstRouteStep);
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                                Log.d("kek", steps.toString());
                                for (int k = 0; k < steps.length(); k++) {
                                    ArrayList<LatLng> stepcoordinates = new ArrayList<>();
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    String instructions = Jsoup.parse(steps.getJSONObject(k).getString("html_instructions")).text();
                                    String travelmode = steps.getJSONObject(k).getString("travel_mode");

                                    Double startlat = steps.getJSONObject(k).getJSONObject("start_location").getDouble("lat");
                                    Double startlong = steps.getJSONObject(k).getJSONObject("start_location").getDouble("lng");
                                    Double endlat = steps.getJSONObject(k).getJSONObject("end_location").getDouble("lat");
                                    Double endlong = steps.getJSONObject(k).getJSONObject("end_location").getDouble("lng");

                                    LatLng latlongstart = new LatLng(startlat,startlong);
                                    LatLng latlongend = new LatLng(endlat,endlong);

                                    List<LatLng> list = decodePoly(polyline);

                                    points = new ArrayList<>();
                                    polylineOptions = new PolylineOptions();
                                    for (int l = 0; l < list.size(); l++) {
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                        bounds.include(position);
                                        stepcoordinates.add(position);
                                    }
                                    polylineOptions.addAll(points);
                                    polylineOptions.width(24);

                                    String travelMode;
                                    String distance = steps.getJSONObject(k).getJSONObject("distance").getString("text");
                                    String duration = steps.getJSONObject(k).getJSONObject("duration").getString("text");
                                    String previousLocation;
                                    String nextLocation;
                                    if (travelmode.equals("WALKING")){
                                        /*polylineOptions.color(ContextCompat.getColor(Route.this, R.color.purple_500));*/
                                        polylineOptions.color(Color.parseColor("#62d431"));
                                        MarkerOptions marker = new MarkerOptions().position(latlongstart);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_24));
                                        mMap.addMarker(marker);
                                        marker = new MarkerOptions().position(latlongend);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_24));
                                        mMap.addMarker(marker);
                                        /*List<PatternItem> pattern = Arrays.asList(
                                                new Dot(), new Gap(20), new Dash(30), new Gap(20));
                                        polylineOptions.setPattern(pattern);*/
                                        travelMode = "Walk";
                                        previousLocation = k==0?start_address: steps.getJSONObject(k-1).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name");
                                        nextLocation = k==steps.length()-1? end_address: steps.getJSONObject(k+1).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");

                                    }
                                    else if (travelmode.equals("TRANSIT")){
                                        JSONObject transitline = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line");
                                        String transitlinecolor = transitline.getString("color");
                                        travelMode = transitline.getJSONObject("vehicle").getString("name");
                                        if (travelMode.equals("Bus")){
                                            transitlinecolor = "#c62020";
                                        }
                                        polylineOptions.color(Color.parseColor(transitlinecolor));

                                        MarkerOptions marker = new MarkerOptions().position(latlongstart);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_24));
                                        mMap.addMarker(marker);

                                        bounds.include(latlongstart);
                                        stepcoordinates.add(latlongstart);

                                        marker = new MarkerOptions().position(latlongend);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_24));
                                        mMap.addMarker(marker);

                                        bounds.include(latlongend);
                                        stepcoordinates.add(latlongend);

                                        previousLocation = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");
                                        nextLocation = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name");

                                    }
                                    else{
                                        travelMode = "Drive";
                                        previousLocation = "Driving Previous Location";
                                        nextLocation = "Driving Next Location";
                                        polylineOptions.color(Color.parseColor("#305978"));
                                        bounds.include(latlongstart);
                                        stepcoordinates.add(latlongstart);
                                        bounds.include(latlongend);
                                        stepcoordinates.add(latlongend);
                                        MarkerOptions marker = new MarkerOptions().position(latlongstart);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_blue_24));
                                        mMap.addMarker(marker);

                                        marker = new MarkerOptions().position(latlongend);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_blue_24));
                                        mMap.addMarker(marker);

                                    }

                                    RouteStep currentStep = new RouteStep(latlongstart,latlongend,travelMode,instructions, distance,duration,previousLocation,nextLocation);
                                    currentStep.stepcoordinates = stepcoordinates;
                                    if (travelmode.equals("TRANSIT")) {
                                        currentStep.LineName = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line").getString("name");
                                        currentStep.NumStops = steps.getJSONObject(k).getJSONObject("transit_details").getInt("num_stops");
                                        if (travelMode.equals("Subway")){
                                            currentStep.LineColor = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line").getString("color");
                                        }
                                    }

                                    routeSteps.add(currentStep);
                                    polylineOptions.geodesic(true);
                                    mMap.addPolyline(polylineOptions);
                                }
                                RouteStep lastRouteStep = new RouteStep();
                                lastRouteStep.Instructions = "Arrive at "+ end_address;
                                routeSteps.add(lastRouteStep);
                            }

                        }
                        RecyclerView routerv = findViewById(R.id.routeRecyclerView);
                        StepAdapter adapter = new StepAdapter(Route.this, routeSteps);
                        LinearLayoutManager layout = new LinearLayoutManager(Route.this);

                        routerv.setAdapter(adapter);
                        routerv.setLayoutManager(layout);

                        /*mMap.addMarker(new MarkerOptions().position(new LatLng(1.3595533, 103.94306)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(1.3212432, 103.7743509)));*/
                        routestepsreminder = new ArrayList<>();
                        if (notificationchoice){
                            for (int i=1; i<routeSteps.size()-1; i++){
                                routestepsreminder.add(routeSteps.get(i));
                            }
                        }


                        final LatLngBounds boundsbuilt = bounds.build();
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsbuilt, 30));
                    }
                } catch(JSONException e){
                    Toast.makeText(Route.this, "Check Location and Connection Settings", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(Route.this, "Check Location and Connection Settings", Toast.LENGTH_LONG).show();
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }
    public void moveMapCamera(ArrayList<LatLng> latlngbounds){
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        for (LatLng latlongobject : latlngbounds){
            bounds.include(latlongobject);
        }
        final LatLngBounds boundsbuilt = bounds.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsbuilt, 30));
    }
    public static List<LatLng> decodePoly(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private void animateNavigationDrawer(){
        /*drawerLayout.setScrimColor(getResources().getColor(com.google.android.material.R.color.));*/
        drawerLayout.setScrimColor(Color.parseColor("#e8c490"));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                finish();

                /*fragmentlayout.setVisibility(View.INVISIBLE); //Set fragment to invisible, show map and main recycler view to help with loading times
                mapandrv.setVisibility(View.VISIBLE);
                favourite = false;*/
                break;
            case R.id.nav_carpark:
                Intent intentcarpark = new Intent(Route.this, CarparkActivity.class);
                intentcarpark.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentcarpark);
                finish();
                break;
            case R.id.nav_profile:
                Intent intentMainActivity = new Intent(Route.this, MainActivity.class);
                intentMainActivity.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                intentMainActivity.putExtra("Profile", "Yes");

                startActivity(intentMainActivity);
                finish();
                /*mapandrv.setVisibility(View.INVISIBLE);
                fragmentlayout.setVisibility(View.VISIBLE);
                replaceFragment(new ProfileFragment());*/
                break;
            case R.id.nav_route:
                break;
            case R.id.nav_fares:
                Intent fareintent = new Intent(Route.this, WeekActivity.class);
                fareintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(fareintent);
                finish();
                break;
            case R.id.nav_rate:
                Uri uri = Uri.parse("market://details?id=sg.edu.np.mad.transportme");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=sg.edu.np.mad.transportme")));
                    break;
                }
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the Best Bus App In Singapore! \n\n https://play.google.com/store/apps/details?id=sg.edu.np.mad.transportme");
                startActivity(Intent.createChooser(sendIntent,"Share With"));
                break;
            case R.id.nav_privacy:
                Intent privacyintent = new Intent(Route.this, PrivacyPolicyActivty.class);
                privacyintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(privacyintent);

                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        Log.d("travechoice", choice);
        if (choice.equals("Public Transport")){
            travelmode = "transit";
        }
        else{
            travelmode="driving";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}