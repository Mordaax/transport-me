package sg.edu.np.mad.transportme.views;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import static android.graphics.BitmapFactory.decodeResource;

import static sg.edu.np.mad.transportme.BitmapResize.getResizedBitmap;
import static sg.edu.np.mad.transportme.ReminderApplication.CHANNEL_ID_2;
import static sg.edu.np.mad.transportme.ReminderService.reached;
import static sg.edu.np.mad.transportme.user.LoginPage.globalCloseness;
import static sg.edu.np.mad.transportme.user.LoginPage.globalName;
import static sg.edu.np.mad.transportme.user.LoginPage.globalRemindCloseness;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminder;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminderBusService;
import static sg.edu.np.mad.transportme.user.LoginPage.grbsChange;
import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import sg.edu.np.mad.transportme.BusService;
import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.BusStopAdapter;
import sg.edu.np.mad.transportme.BusStopDBHandler;
import sg.edu.np.mad.transportme.DistanceCalculator;
import sg.edu.np.mad.transportme.NextBus;
import sg.edu.np.mad.transportme.PrivacyPolicyActivty;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.ReminderService;
import sg.edu.np.mad.transportme.Route;
import sg.edu.np.mad.transportme.WeekActivity;
import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.user.ProfileFragment;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    public static String networkprovider = LocationManager.GPS_PROVIDER;
    public static LatLng currentLocation = null;
    public static ArrayList<Marker> mlistlocation;
    LinearLayout mapandrv;
    FrameLayout fragmentlayout;
    LinearLayout reminderView;      //CHANGE TO SCROLLVIEW LATER
    Button reminderButton;
    Button cancelReminderButton;
    LinearLayout noReminderLayout;
    SwipeRefreshLayout swipeLayoutRemind;
    ConstraintLayout remindInfoLayout;
    GoogleMap map;
    Uri image_uri;
    LocationManager locationManager;
    DrawerLayout drawerLayout;
    FloatingActionButton cameraSearch;
    SwipeRefreshLayout swipeRefreshLayout;
    BottomNavigationView bottomNavigationView;
    static final float END_SCALE = 0.7f;
    ConstraintLayout contentView;
    public static Boolean favourite = false;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int LOCATION_REQUEST = 1337;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(globalBusStops.isEmpty()){
            BusStopDBHandler busStopDBHandler = new BusStopDBHandler(MainActivity.this,null,null,1);
            globalBusStops = busStopDBHandler.getBusStops();
        }
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle); //Show Loading icon when the user first loads
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        contentView = findViewById(R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView menuIcon = findViewById(R.id.menu_icon);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();

        cameraSearch = findViewById(R.id.fab);
        cameraSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent cameraIntent = new Intent(MainActivity.this, CameraSearch.class);
                startActivity(cameraIntent);*/
                selectImage();
            }
        });

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");     //Initialise database instance
        DatabaseReference reminderReference = db.getReference()
                .child("User")
                //.child(firebaseUser.getUid())
                .child(globalName)
                .child("Reminder");

        Log.e("build",""+Build.VERSION.SDK_INT );


        mapandrv = findViewById(R.id.MapAndRV);
        fragmentlayout = findViewById(R.id.frame_layout);
        reminderView = findViewById(R.id.reminderView);
        reminderButton = findViewById(R.id.reminderButton);
        cancelReminderButton = findViewById(R.id.cancelReminderButton);
        noReminderLayout = findViewById(R.id.noReminderLayout);
        swipeLayoutRemind = findViewById(R.id.swipeLayoutRemind);
        remindInfoLayout = findViewById(R.id.remindInfoLayout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); // load botttom navigation bar
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    fragmentlayout.setVisibility(View.INVISIBLE); //Set fragment to invisible, show map and main recycler view to help with loading times
                    reminderView.setVisibility(View.GONE);
                    cameraSearch.setVisibility(View.VISIBLE);
                    mapandrv.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    favourite = false;
                    break;
                case R.id.favourites:
                    mapandrv.setVisibility(View.INVISIBLE);
                    cameraSearch.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new FavouritesFragment());
                    favourite = true;
                    break;
                case R.id.search:
                    mapandrv.setVisibility(View.INVISIBLE);
                    cameraSearch.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.mrtmap:
                    mapandrv.setVisibility(View.INVISIBLE);
                    cameraSearch.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new MrtMapFragment());
                    break;
                case R.id.notify:
                    reminderView.setVisibility(View.VISIBLE);
                    mapandrv.setVisibility(View.VISIBLE);
                    fragmentlayout.setVisibility(View.INVISIBLE);
                    cameraSearch.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    findViewById(R.id.busrouterecyclerView).setVisibility(View.GONE);
                    if(globalReminder == null)
                    {
                        noReminderLayout.setVisibility(View.VISIBLE);
                        swipeLayoutRemind.setVisibility(View.GONE);
                        remindInfoLayout.setVisibility(View.GONE);
                    } else {
                        noReminderLayout.setVisibility(View.GONE);
                        swipeLayoutRemind.setVisibility(View.VISIBLE);
                        remindInfoLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.nav_carpark:
                    Intent intent = new Intent(MainActivity.this, CarparkActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        Intent recievingEnd = getIntent();
        String gotoprofile = recievingEnd.getStringExtra("Profile");
        if (gotoprofile != null) {
            mapandrv.setVisibility(View.INVISIBLE);
            fragmentlayout.setVisibility(View.VISIBLE);
            replaceFragment(new ProfileFragment());
            navigationView.setCheckedItem(R.id.nav_profile);
            progressDialog.dismiss();
        }

        cancelReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mrtmap);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ArrayList<BusStop> busStops = globalBusStops;

        // If location permission is rejected, send toast message to user
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
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            final int LOCATION_REQUEST = 1337;

            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            Toast.makeText(MainActivity.this, "Check Location and Connection Settings", Toast.LENGTH_LONG).show();

            return;
        } else {

            if (locationManager.isProviderEnabled(networkprovider)) { //Comments in this section is the same as the one in the LocationManager.NETWORK_PROVIDER
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        locationManager.requestLocationUpdates(networkprovider, 60000, 10, new LocationListener() { //Every 60 seconds or 10m change, run code
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                Double Latitude = location.getLatitude(); //Get latitude and logitude
                                Double Longitude = location.getLongitude();


                                LatLng latLng = new LatLng(Latitude, Longitude);
                                Geocoder geocoder = new Geocoder(getApplicationContext());


                                ArrayList<BusStop> closeBusStops = new ArrayList<>();
                                map.clear(); //Clear all existing markers on the map
                                for (int i = 0; i < busStops.size(); i++) { //Get all bus stop given the radius
                                    BusStop busStop = busStops.get(i);
                                    busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                                    if (busStop.getDistanceToLocation() <= globalCloseness) {
                                        closeBusStops.add(busStop);
                                        LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                        map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                                    }
                                }
                                Collections.sort(closeBusStops);
                                if (closeBusStops.size() > 0) { // If close bus stops > 0 run API and load recycler view
                                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                    apiBusStopService.getBusService(closeBusStops, new ApiBusStopService.VolleyResponseListener2() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(MainActivity.this, "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                            RecyclerView rv = findViewById(R.id.recyclerView); //Load recyclerview when they onresponse is recieved
                                            BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded, MainActivity.this);
                                            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                            rv.setAdapter(adapter);
                                            rv.setLayoutManager(layout);
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                                swipeRefreshLayout.setRefreshing(false); //Close refreshing Icon
                                if (closeBusStops.size() == 0) { // If there are no nearby bus stop, show toast message
                                    Toast.makeText(MainActivity.this, "No nearby bus stops", Toast.LENGTH_LONG).show();
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });

                swipeLayoutRemind.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        locationManager.requestLocationUpdates(networkprovider, 6000, 10, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                if (globalReminder != null) {
                                    Double Latitude = location.getLatitude(); //Get latitude and logitude
                                    Double Longitude = location.getLongitude();


                                    LatLng latLng = new LatLng(Latitude, Longitude);
                                    currentLocation = latLng;

                                    Geocoder geocoder = new Geocoder(getApplicationContext());

                                    ArrayList<BusStop> remindBusStop = new ArrayList<>();
                                    remindBusStop.add(globalReminder);
                                    RecyclerView rv = findViewById(R.id.recyclerViewRemind); //Load recyclerview when they onresponse is recieved
                                    BusStopAdapter adapter = new BusStopAdapter(remindBusStop, MainActivity.this);
                                    LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);

                                    LatLng destnLL = new LatLng(globalReminder.getLatitude(), globalReminder.getLongitude());
                                    Double destnDist = SphericalUtil.computeDistanceBetween(latLng, destnLL);
                                    TextView remindDestnDist = findViewById(R.id.remindDestnDist);
                                    String display = String.format("%.2f", destnDist / 1000) + "km\nLeft to " + globalReminder.getDescription();
                                    remindDestnDist.setText(display);

                                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                    apiBusStopService.getBusRoute(globalReminderBusService, new ApiBusStopService.VolleyResponseListener3() { //Call API for bus route
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(MainActivity.this, "Cannot Get Bus Route, Check Location and Connection", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                            Integer index = busStopRouteLoaded.lastIndexOf(globalReminder);
                                            if (destnDist <= globalRemindCloseness) {
                                                ArrayList<BusStop> busStopDist = new ArrayList<>();
                                                for (BusStop bs : busStopRouteLoaded) {
                                                    bs.setDistanceToLocation(SphericalUtil.computeDistanceBetween(latLng, new LatLng(bs.getLatitude(), bs.getLongitude())));
                                                    busStopDist.add(bs);
                                                }
                                                Collections.sort(busStopDist);

                                                Integer closestBusStopIndex = busStopRouteLoaded.indexOf(busStopDist.get(0));
                                                if (index - closestBusStopIndex < 2) {
                                                    Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID_2)
                                                            .setSmallIcon(R.drawable.app_logo_vector)
                                                            .setContentTitle("Reminder to Alight")
                                                            .setContentText("You are arriving " + globalReminder.getDescription() + "!")
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                            .build();

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                                    notificationManager.notify(1, notification);
                                                    reminderReference.setValue(null);
                                                }
                                            }
                                        }
                                    });
                                }
                                swipeLayoutRemind.setRefreshing(false); //Close refreshing Icon
                            }
                        });
                    }
                });

                // Main location request when the app first loads
                locationManager.requestLocationUpdates(networkprovider, 60000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Double Latitude = location.getLatitude();
                        Double Longitude = location.getLongitude();


                        LatLng latLng = new LatLng(Latitude, Longitude);
                        currentLocation = latLng;

                        Geocoder geocoder = new Geocoder(getApplicationContext());

                        if (globalReminder != null) {
                            ArrayList<BusStop> remindBusStop = new ArrayList<>();
                            remindBusStop.add(globalReminder);
                            RecyclerView rv = findViewById(R.id.recyclerViewRemind); //Load recyclerview when they onresponse is recieved
                            BusStopAdapter adapter = new BusStopAdapter(remindBusStop, MainActivity.this);
                            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                            rv.setAdapter(adapter);
                            rv.setLayoutManager(layout);

                            LatLng destnLL = new LatLng(globalReminder.getLatitude(), globalReminder.getLongitude());
                            Double destnDist = SphericalUtil.computeDistanceBetween(latLng, destnLL);
                            TextView remindDestnDist = findViewById(R.id.remindDestnDist);
                            String display = String.format("%.2f", destnDist / 1000) + "km\nLeft to " + globalReminder.getDescription();
                            remindDestnDist.setText(display);


                            ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                            apiBusStopService.getBusRoute(globalReminderBusService, new ApiBusStopService.VolleyResponseListener3() { //Call API for bus route
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(MainActivity.this, "Cannot Get Bus Route, Check Location and Connection", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                    Integer index = busStopRouteLoaded.lastIndexOf(globalReminder);

                                    if (destnDist <= globalRemindCloseness) {
                                        ArrayList<BusStop> busStopDist = new ArrayList<>();
                                        for (BusStop bs : busStopRouteLoaded) {
                                            bs.setDistanceToLocation(SphericalUtil.computeDistanceBetween(latLng, new LatLng(bs.getLatitude(), bs.getLongitude())));
                                            busStopDist.add(bs);
                                        }
                                        Collections.sort(busStopDist);

                                        Integer closestBusStopIndex = busStopRouteLoaded.indexOf(busStopDist.get(0));
                                        if (index - closestBusStopIndex < 2 && reached != true) {
                                            Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID_2)
                                                    .setSmallIcon(R.drawable.app_logo_vector)
                                                    .setContentTitle("Reminder to Alight")
                                                    .setContentText("You are arriving " + globalReminder.getDescription() + "!")
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                    .build();

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                            notificationManager.notify(1, notification);

                                            reminderReference.setValue(null);
                                        }
                                    }
                                }
                            });
                        }

                        ArrayList<BusStop> closeBusStops = new ArrayList<>();
                        for (int i = 0; i < busStops.size(); i++) { //Get bus stops nearby
                            BusStop busStop = busStops.get(i);
                            busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                            if (busStop.getDistanceToLocation() <= globalCloseness) {
                                closeBusStops.add(busStop);
                                LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                            }
                        }
                        Collections.sort(closeBusStops);
                        if (closeBusStops.size() > 0) { //Call API if there nearby bus stops, if there arent, send toast message
                            ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                            apiBusStopService.getBusService(closeBusStops, new ApiBusStopService.VolleyResponseListener2() { //call api to get bus services
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(MainActivity.this, "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                    RecyclerView rv = findViewById(R.id.recyclerView); //Load recyclerview on response from API
                                    BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded, MainActivity.this);
                                    LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        if (closeBusStops.size() == 0) {
                            Toast.makeText(MainActivity.this, "No nearby bus stops", Toast.LENGTH_LONG).show();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                            progressDialog.dismiss();
                        }


                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f)); //Move camera to here the user is

                    }
                });

            }/* else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) { //This section is similar to the LocationManager.GPS_PROVIDER section above
                //For users to refresh the recyclerview, runs the location reqeust updates
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() { //Whene user refresh run code
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                Double Latitude = location.getLatitude();
                                Double Longitude = location.getLongitude();


                                LatLng latLng = new LatLng(Latitude, Longitude);
                                Geocoder geocoder = new Geocoder(getApplicationContext());


                                ArrayList<BusStop> closeBusStops = new ArrayList<>();
                                map.clear();
                                for (int i = 0; i < busStops.size(); i++) {
                                    BusStop busStop = busStops.get(i);
                                    busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                                    if (busStop.getDistanceToLocation() <= globalCloseness) {
                                        closeBusStops.add(busStop);
                                        LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                        map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                                    }
                                }
                                Collections.sort(closeBusStops);
                                if (closeBusStops.size() > 0) { // If close bus stops > 0 run API and load recycler view
                                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                    apiBusStopService.getBusService(closeBusStops, new ApiBusStopService.VolleyResponseListener2() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(MainActivity.this, "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                            RecyclerView rv = findViewById(R.id.recyclerView);
                                            BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded, MainActivity.this);
                                            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                            rv.setAdapter(adapter);
                                            rv.setLayoutManager(layout);
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                                swipeRefreshLayout.setRefreshing(false); //Close refreshing Icon
                                if (closeBusStops.size() == 0) { // If there are no nearby bus stop, show toast message
                                    Toast.makeText(MainActivity.this, "No nearby bus stops", Toast.LENGTH_LONG).show();
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                swipeLayoutRemind.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                if (globalReminder != null)
                                {
                                    Double Latitude = location.getLatitude(); //Get latitude and logitude
                                    Double Longitude = location.getLongitude();


                                    LatLng latLng = new LatLng(Latitude, Longitude);
                                    Geocoder geocoder = new Geocoder(getApplicationContext());


                                    ArrayList<BusStop> remindBusStop = new ArrayList<>();
                                    remindBusStop.add(globalReminder);
                                    RecyclerView rv = findViewById(R.id.recyclerViewRemind); //Load recyclerview when they onresponse is recieved
                                    BusStopAdapter adapter = new BusStopAdapter(remindBusStop, MainActivity.this);
                                    LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);

                                    LatLng destnLL = new LatLng(globalReminder.getLatitude(),globalReminder.getLongitude());
                                    Double destnDist = SphericalUtil.computeDistanceBetween(latLng,destnLL);
                                    TextView remindDestnDist = findViewById(R.id.remindDestnDist);
                                    String display = String.format("%.2f", destnDist / 1000) + "km\nLeft to " + globalReminder.getDescription();
                                    remindDestnDist.setText(display);


                                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                    apiBusStopService.getBusRoute(globalReminderBusService,new ApiBusStopService.VolleyResponseListener3() { //Call API for bus route
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(MainActivity.this,"Cannot Get Bus Route, Check Location and Connection",Toast.LENGTH_LONG).show();
                                        }
                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                            Integer index = busStopRouteLoaded.lastIndexOf(globalReminder);
                                            if(destnDist <= globalRemindCloseness)
                                            {
                                                ArrayList<BusStop> busStopDist = new ArrayList<>();
                                                for (BusStop bs : busStopRouteLoaded)
                                                {
                                                    bs.setDistanceToLocation(SphericalUtil.computeDistanceBetween(latLng, new LatLng(bs.getLatitude(),bs.getLongitude())));
                                                    busStopDist.add(bs);
                                                }
                                                Collections.sort(busStopDist);

                                                Integer closestBusStopIndex = busStopRouteLoaded.indexOf(busStopDist.get(0));
                                                if(index - closestBusStopIndex < 2)
                                                {
                                                    Notification notification = new NotificationCompat.Builder(MainActivity.this,CHANNEL_ID_2)
                                                            .setSmallIcon(R.drawable.app_logo_vector)
                                                            .setContentTitle("Reminder to Alight")
                                                            .setContentText("You are arriving "+ globalReminder.getDescription() + "!")
                                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                            .build();

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                                    notificationManager.notify(1,notification);

                                                    reminderReference.setValue(null);
                                                }
                                            }
                                        }
                                    });
                                }
                                swipeLayoutRemind.setRefreshing(false); //Close refreshing Icon
                            }
                        });
                    }
                });

                // Main location request when the app first loads
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Double Latitude = location.getLatitude();
                        Double Longitude = location.getLongitude();


                        LatLng latLng = new LatLng(Latitude, Longitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());

                        if(globalReminder != null)
                        {
                            ArrayList<BusStop> remindBusStop = new ArrayList<>();
                            remindBusStop.add(globalReminder);
                            RecyclerView rv = findViewById(R.id.recyclerViewRemind); //Load recyclerview when they onresponse is recieved
                            BusStopAdapter adapter = new BusStopAdapter(remindBusStop, MainActivity.this);
                            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                            rv.setAdapter(adapter);
                            rv.setLayoutManager(layout);

                            LatLng destnLL = new LatLng(globalReminder.getLatitude(),globalReminder.getLongitude());
                            Double destnDist = SphericalUtil.computeDistanceBetween(latLng,destnLL);
                            TextView remindDestnDist = findViewById(R.id.remindDestnDist);
                            String display = String.format("%.2f", destnDist / 1000) + "km\nLeft to " + globalReminder.getDescription();
                            remindDestnDist.setText(display);

                            ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                            apiBusStopService.getBusRoute(globalReminderBusService,new ApiBusStopService.VolleyResponseListener3() { //Call API for bus route
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(MainActivity.this,"Cannot Get Bus Route, Check Location and Connection",Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                    Integer index = busStopRouteLoaded.lastIndexOf(globalReminder);
                                    if(destnDist <= globalRemindCloseness)
                                    {
                                        ArrayList<BusStop> busStopDist = new ArrayList<>();
                                        for (BusStop bs : busStopRouteLoaded)
                                        {
                                            bs.setDistanceToLocation(SphericalUtil.computeDistanceBetween(latLng, new LatLng(bs.getLatitude(),bs.getLongitude())));
                                            busStopDist.add(bs);
                                        }
                                        Collections.sort(busStopDist);

                                        Integer closestBusStopIndex = busStopRouteLoaded.indexOf(busStopDist.get(0));
                                        if(index - closestBusStopIndex < 2)
                                        {
                                            Notification notification = new NotificationCompat.Builder(MainActivity.this,CHANNEL_ID_2)
                                                    .setSmallIcon(R.drawable.app_logo_vector)
                                                    .setContentTitle("Reminder to Alight")
                                                    .setContentText("You are arriving "+ globalReminder.getDescription() + "!")
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                    .build();

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                            notificationManager.notify(1,notification);

                                            reminderReference.setValue(null);
                                        }
                                    }
                                }
                            });
                        }

                        ArrayList<BusStop> closeBusStops = new ArrayList<>();
                        for (int i = 0; i < busStops.size(); i++) { //Get nearby bus stops
                            BusStop busStop = busStops.get(i);
                            busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                            if (busStop.getDistanceToLocation() <= globalCloseness) {
                                closeBusStops.add(busStop);
                                LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                            }
                        }
                        Collections.sort(closeBusStops);
                        if (closeBusStops.size() > 0) {
                            ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                            apiBusStopService.getBusService(closeBusStops, new ApiBusStopService.VolleyResponseListener2() { //Call API for nearby bus stops
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(MainActivity.this, "Cannot Get Bus Stops, Check Location and Connection", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                    RecyclerView rv = findViewById(R.id.recyclerView);
                                    BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded, MainActivity.this);
                                    LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);
                                    progressDialog.dismiss();
                                }
                            });

                        }
                        if (closeBusStops.size() == 0) {
                            Toast.makeText(MainActivity.this, "No nearby bus stops", Toast.LENGTH_LONG).show();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                            progressDialog.dismiss();
                        }

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));

                    }
                });
            }*/
        }
        grbsChange.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String changedValue) {
                reminderUpdate(reminderButton, findViewById(R.id.recyclerViewRemind));

            }
        });
        new Handler().postDelayed(new Runnable() {      //Gives app time to load global variables from Login Page before setting value
            @Override
            public void run() {
                grbsChange.setValue(globalReminderBusService);
            }
        }, 6500);
        cancelReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeLayoutRemind.setVisibility(View.GONE);
                remindInfoLayout.setVisibility(View.GONE);
                noReminderLayout.setVisibility(View.VISIBLE);
                reminderReference.setValue(null);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (globalReminder != null)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)   //if no bg perms granted and foreground tracking not activated
            {
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        0, notificationIntent, 0);
                Notification noPermNotif = new NotificationCompat.Builder(this,CHANNEL_ID_2)
                        .setSmallIcon(R.drawable.app_logo_vector)
                        .setContentTitle("TransportMe Cannot Track Your Location")
                        .setContentIntent(pendingIntent)
                        .setContentText("Please set Location Permissions to 'Allow all the time' so we can notify you even when the app is in the background!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(1,noPermNotif);
            }
            else
            {
                startReminderService();
                reached = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopReminderService();
        if (reached == true)
        {
            FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");     //Initialise database instance
            DatabaseReference reminderReference = db.getReference()
                    .child("User")
                    //.child(firebaseUser.getUid())
                    .child(globalName)
                    .child("Reminder");
            reminderReference.setValue(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopReminderService();
    }

    public void startReminderService()
    {
        Intent serviceIntent = new Intent(this, ReminderService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopReminderService()
    {
        Intent serviceIntent = new Intent(this, ReminderService.class);
        stopService(serviceIntent);
    }

    public void reminderUpdate(Button reminderButton, RecyclerView rv) {
        if (!(globalReminder == null)) {
            reminderButton.setText("Alight at " + globalReminder.getDescription());
            reminderButton.setVisibility(View.VISIBLE);
        } else {
            reminderButton.setVisibility(View.INVISIBLE);
        }
    }

    public void moveMapsCamera(Double latitude, Double longitude) { //Function to enable move camera from other classes
        LatLng latlongmove = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlongmove)
                .zoom(17f)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cu);

    }

    public void busrouteview(ArrayList<BusStop> busStopList) {
        SwipeRefreshLayout orv = findViewById(R.id.swipeLayout);
        RecyclerView rv = findViewById(R.id.busrouterecyclerView);
        if (busStopList.size() > 0) {
            BusStopAdapter adapter = new BusStopAdapter(busStopList, MainActivity.this);
            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
            rv.setAdapter(adapter);
            rv.setLayoutManager(layout);
            orv.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
        fragmentlayout.setVisibility(View.INVISIBLE); //Set fragment to invisible, show map and main recycler view to help with loading times
        reminderView.setVisibility(View.GONE);
        cameraSearch.setVisibility(View.VISIBLE);
        mapandrv.setVisibility(View.VISIBLE);
        orv.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        /*swipeRefreshLayout.setVisibility(View.VISIBLE);*/
        favourite = false;
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

    }

    public void busroute(Double latitude, Double longitude, BusStop currentStop, List<Marker> mList, List<LatLng> lList) {
        LatLng latlongmarker = new LatLng(latitude, longitude);
        Bitmap icon = Bitmap.createBitmap(15, 15, Bitmap.Config.ARGB_8888);
        Drawable shape = getResources().getDrawable(R.drawable.marker_icon);
        Canvas canvas = new Canvas(icon);
        shape.setBounds(0, 0, icon.getWidth(), icon.getHeight());
        shape.draw(canvas);
        Marker marker = map.addMarker(new MarkerOptions().position(latlongmarker).title(currentStop.getDescription()).icon(BitmapDescriptorFactory.fromBitmap(icon)));
        lList.add(latlongmarker);
        mList.add(marker);
    }

    public Polyline polyline(List<LatLng> lList) {
        Polyline polyline = map.addPolyline(new PolylineOptions().addAll(lList).color(Color.RED));
        return polyline;
    }

    public void camerazoom(List<Marker> mList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker m : mList) {
            builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 30);
        map.animateCamera(cu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void removemarker(List<Marker> mList, Polyline line) {
        if (line != null) {
            line.remove();
        }
        for (Marker m : mList) {

            m.remove();
        }
        SwipeRefreshLayout orv = findViewById(R.id.swipeLayout);
        RecyclerView rv = findViewById(R.id.busrouterecyclerView);
        rv.setVisibility(View.GONE);
        orv.setVisibility(View.VISIBLE);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            final int LOCATION_REQUEST = 1337;

            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17f)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cu);
    }

    private void replaceFragment(Fragment fragment) { //Replace fragment for nav bar
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
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
        map.setMyLocationEnabled(true);

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
                fragmentlayout.setVisibility(View.INVISIBLE); //Set fragment to invisible, show map and main recycler view to help with loading times
                mapandrv.setVisibility(View.VISIBLE);
                favourite = false;
                break;
            case R.id.nav_carpark:
                Intent intentcarpark = new Intent(MainActivity.this, CarparkActivity.class);
                intentcarpark.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentcarpark);
                break;
            case R.id.nav_profile:
                mapandrv.setVisibility(View.INVISIBLE);
                fragmentlayout.setVisibility(View.VISIBLE);
                replaceFragment(new ProfileFragment());
                break;
            case R.id.nav_route:
                Intent routeintent = new Intent(MainActivity.this, Route.class);
                routeintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(routeintent);
                break;
            case R.id.nav_fares:
                Intent fareintent = new Intent(MainActivity.this, WeekActivity.class);
                fareintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(fareintent);
                break;
            case R.id.nav_privacy:
                Intent privacyintent = new Intent(MainActivity.this, PrivacyPolicyActivty.class);
                privacyintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(privacyintent);
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

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    public void addBusLocations(BusService currentService){
        ArrayList<NextBus> nextbuses = currentService.getNextBuses();
        mlistlocation = new ArrayList<>();

        for (NextBus nextbus : nextbuses){
            LatLng latlongbus = new LatLng(Double.valueOf(nextbus.getLatitude()), Double.valueOf(nextbus.getLongitude()));
            MarkerOptions marker = new MarkerOptions().position(latlongbus).title(currentService.getServiceNumber());
            Bitmap icon = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            Drawable shape = getResources().getDrawable(R.drawable.ic_baseline_directions_bus_yellow_24);
            Canvas canvas = new Canvas(icon);
            shape.setBounds(0, 0, icon.getWidth(), icon.getHeight());
            shape.draw(canvas);
            Marker busmarker = map.addMarker(new MarkerOptions().position(latlongbus).title(currentService.getServiceNumber()).icon(BitmapDescriptorFactory.fromBitmap(icon)));
            mlistlocation.add(busmarker);
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(nextbuses.get(0).getLatitude()), Double.valueOf(nextbuses.get(0).getLongitude())), 16.2f));
    }
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
    private void selectImage() {
        final CharSequence[] options = {"Choose from Gallery","Cancel" };
        /*final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };*/
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.bus_stop_next_to_pond);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.cameraalertdialog,null);
        ImageView helloimage = view.findViewById(R.id.busstopimageview);
        helloimage.setImageResource(R.drawable.bus_stop_next_to_pond);

        Button selectImageButton = view.findViewById(R.id.buttonselectimage);
        Button cancelImageButton = view.findViewById(R.id.buttoncancel);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).create();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
                alertDialog.cancel();
            }
        });
        cancelImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
        /*builder.setTitle("Choose Image to Scan");
        builder.setIcon(R.drawable.appsplashicon);
        builder.setView(image);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                    image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                    Log.d("Hell", image_uri.toString());
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    *//*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);*//*
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(takePicture, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
                    *//*takePicture.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);*//*
         *//*startActivityForResult(takePicture,0);*//*

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK /*&& data != null*/) {
                        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
                        {
                            //Get our saved file into a bitmap object:

                            File file = new File(Environment.getExternalStorageDirectory()+File.separator +
                                    "image.jpg");
                            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
                            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                            Frame frameImage = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> textBlockSpaceArray = textRecognizer.detect(frameImage);
                        }
                        /*try{
                            Bitmap b = (Bitmap)data.getExtras().get("data");
                            Log.d("Hell", b.toString());

                            ArrayList<BusStop> cameraBusStops = new ArrayList<>();
                            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                            *//*Bitmap selectedImage = (Bitmap) data.getExtras().get("data");*//*
                            selectedImage = getResizedBitmap(selectedImage,1000);
                            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                            Frame frameImage = new Frame.Builder().setBitmap(b).build();
                            SparseArray<TextBlock> textBlockSpaceArray = textRecognizer.detect(frameImage);


                            for (int i =0; i<textBlockSpaceArray.size();i++){
                                TextBlock textBlock = textBlockSpaceArray.get(textBlockSpaceArray.keyAt(i));
                                Log.d("Hello",textBlock.getValue());
                                for (int x=0; i< globalBusStops.size(); i++){
                                    BusStop currentStop = globalBusStops.get(i);
                                    if (textBlock.getValue().equalsIgnoreCase(currentStop.getDescription()) ||
                                            textBlock.getValue().equals(currentStop.getBusStopCode()) ||
                                            textBlock.getValue().equalsIgnoreCase(currentStop.getRoadName())){
                                        cameraBusStops.add(currentStop);
                                        LatLng latlongmarker = new LatLng(currentStop.getLatitude(), currentStop.getLongitude());
                                        map.addMarker(new MarkerOptions().position(latlongmarker).title(currentStop.getDescription()));
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlongmarker, 16.2f));
                                    }
                                }

                            }
                            if(cameraBusStops.size() > 0){
                                Toast.makeText(MainActivity.this,"Bus Stop Recognized, loading Bus Stop",Toast.LENGTH_LONG).show();
                                ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                apiBusStopService.getBusService(cameraBusStops,new ApiBusStopService.VolleyResponseListener2() { //Call API for nearby bus stops
                                    @Override
                                    public void onError(String message) {
                                        Toast.makeText(MainActivity.this,"Cannot Get Bus Stop, Check Location and Connection Settings",Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onResponse(ArrayList<BusStop> busStopsLoaded) {
                                        RecyclerView rv = findViewById(R.id.recyclerView);
                                        BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,MainActivity.this);
                                        LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                        rv.setAdapter(adapter);
                                        rv.setLayoutManager(layout);
                                    }
                                });
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Cannot Recognize Text Choose Another Photo",Toast.LENGTH_LONG).show();

                            }

                        }
                        catch(Exception e){
                        }*/

                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap selectedPhoneImage = BitmapFactory.decodeFile(picturePath);

                                try{
                                    ArrayList<BusStop> cameraBusStops = new ArrayList<>();
                                    TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                                    Frame frameImage = new Frame.Builder().setBitmap(selectedPhoneImage).build();
                                    SparseArray<TextBlock> textBlockSpaceArray = textRecognizer.detect(frameImage);
                                    for (int i =0; i<textBlockSpaceArray.size();i++){
                                        TextBlock textBlock = textBlockSpaceArray.get(textBlockSpaceArray.keyAt(i));
                                        for (int x=0; i< globalBusStops.size(); i++){
                                            BusStop currentStop = globalBusStops.get(i);
                                            if (textBlock.getValue().equalsIgnoreCase(currentStop.getDescription()) ||
                                                    textBlock.getValue().equals(currentStop.getBusStopCode()) ||
                                                    textBlock.getValue().equalsIgnoreCase(currentStop.getRoadName())){
                                                cameraBusStops.add(currentStop);
                                                LatLng latlongmarker = new LatLng(currentStop.getLatitude(), currentStop.getLongitude());
                                                map.addMarker(new MarkerOptions().position(latlongmarker).title(currentStop.getDescription()));
                                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlongmarker, 16.2f));
                                            }
                                        }
                                    }
                                    if(cameraBusStops.size() > 0){
                                        Toast.makeText(MainActivity.this,"Bus Stop Recognized, loading Bus Stop",Toast.LENGTH_LONG).show();
                                        ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                        apiBusStopService.getBusService(cameraBusStops,new ApiBusStopService.VolleyResponseListener2() { //Call API for nearby bus stops
                                            @Override
                                            public void onError(String message) {
                                                Toast.makeText(MainActivity.this,"Cannot Get Bus Stop, Check Location and Connection Settings",Toast.LENGTH_LONG).show();
                                            }
                                            @Override
                                            public void onResponse(ArrayList<BusStop> busStopsLoaded) {
                                                RecyclerView rv = findViewById(R.id.recyclerView);
                                                BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,MainActivity.this);
                                                LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                                rv.setAdapter(adapter);
                                                rv.setLayoutManager(layout);
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this,"Cannot Recognize Text Choose Another Photo",Toast.LENGTH_LONG).show();

                                    }

                                }
                                catch(Exception e){
                                    Log.d("HAHA", "failed");
                                }
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
}
