package sg.edu.np.mad.transportme.views;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import static sg.edu.np.mad.transportme.user.LoginPage.globalCloseness;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.BusStopAdapter;
import sg.edu.np.mad.transportme.DistanceCalculator;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.user.ProfileFragment;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    GoogleMap map;
    LocationManager locationManager;
    DrawerLayout drawerLayout;
    static final float END_SCALE = 0.7f;
    ConstraintLayout contentView;

    public static Boolean favourite = false;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int LOCATION_REQUEST=1337;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeLayout);
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,R.style.MyAlertDialogStyle); //Show Loading icon when the user first loads
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
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView); // load botttom navigation bar
        bottomNavigationView.setOnItemSelectedListener(item ->{
            LinearLayout mapandrv = findViewById(R.id.MapAndRV);
            FrameLayout fragmentlayout = findViewById(R.id.frame_layout);
            switch(item.getItemId()){
                case R.id.home:
                    fragmentlayout.setVisibility(View.INVISIBLE); //Set fragment to invisible, show map and main recycler view to help with loading times
                    mapandrv.setVisibility(View.VISIBLE);
                    favourite = false;
                    break;
                case R.id.favourites:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new FavouritesFragment());
                    favourite = true;
                    break;
                case R.id.search:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.mrtmap:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new MrtMapFragment());
                    break;
                case R.id.profile:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new ProfileFragment());
                    break;

            }
            return true;
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mrtmap);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ArrayList<BusStop> busStops = LoadingScreen.globalBusStops;

        // If location permission is rejected, send toast message to user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(MainActivity.this,"Check Location and Connection Settings",Toast.LENGTH_LONG).show();

            return;
        }
        else{ 
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { //Comments in this section is the same as the one in the LocationManager.NETWORK_PROVIDER
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, new LocationListener() { //Every 60 seconds or 10m change, run code
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                Double Latitude = location.getLatitude(); //Get latitude and logitude
                                Double Longitude = location.getLongitude();


                                LatLng latLng = new LatLng(Latitude, Longitude);
                                Geocoder geocoder = new Geocoder(getApplicationContext());


                                ArrayList<BusStop> closeBusStops = new ArrayList<>();
                                map.clear(); //Clear all existing markers on the map
                                for (int i = 0; i < busStops.size(); i++){ //Get all bus stop given the radius
                                    BusStop busStop = busStops.get(i);
                                    busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                                    if (busStop.getDistanceToLocation() <= globalCloseness){
                                        closeBusStops.add(busStop);
                                        LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                        map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                                    }
                                }
                                if(closeBusStops.size() > 0){ // If close bus stops > 0 run API and load recycler view
                                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                    apiBusStopService.getBusService(closeBusStops,new ApiBusStopService.VolleyResponseListener2() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(MainActivity.this,"Cannot Get Bus Stops, Check Location and Connection",Toast.LENGTH_LONG).show();
                                        }
                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                            RecyclerView rv = findViewById(R.id.recyclerView); //Load recyclerview when they onresponse is recieved
                                            BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,MainActivity.this);
                                            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                            rv.setAdapter(adapter);
                                            rv.setLayoutManager(layout);
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                                swipeRefreshLayout.setRefreshing(false); //Close refreshing Icon
                                if(closeBusStops.size() == 0){ // If there are no nearby bus stop, show toast message
                                    Toast.makeText(MainActivity.this,"No nearby bus stops",Toast.LENGTH_LONG).show();
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                // Main location request when the app first loads
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Double Latitude = location.getLatitude();
                        Double Longitude = location.getLongitude();


                        LatLng latLng = new LatLng(Latitude, Longitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());



                        ArrayList<BusStop> closeBusStops = new ArrayList<>();
                        for (int i = 0; i < busStops.size(); i++){ //Get bus stops nearby
                            BusStop busStop = busStops.get(i);
                            busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                            if (busStop.getDistanceToLocation() <= globalCloseness){
                                closeBusStops.add(busStop);
                                LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                            }
                        }
                        if(closeBusStops.size() > 0){ //Call API if there nearby bus stops, if there arent, send toast message
                            ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                            apiBusStopService.getBusService(closeBusStops,new ApiBusStopService.VolleyResponseListener2() { //call api to get bus services
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(MainActivity.this,"Cannot Get Bus Stops, Check Location and Connection",Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                    RecyclerView rv = findViewById(R.id.recyclerView); //Load recyclerview on response from API
                                    BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,MainActivity.this);
                                    LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        if(closeBusStops.size() == 0){
                            Toast.makeText(MainActivity.this,"No nearby bus stops",Toast.LENGTH_LONG).show();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                            progressDialog.dismiss();
                        }



                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f)); //Move camera to here the user is

                    }
                });
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){ //This section is similar to the LocationManager.GPS_PROVIDER section above
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
                                for (int i = 0; i < busStops.size(); i++){
                                    BusStop busStop = busStops.get(i);
                                    busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                                    if (busStop.getDistanceToLocation() <= globalCloseness){
                                        closeBusStops.add(busStop);
                                        LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                        map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                                    }
                                }
                                if(closeBusStops.size() > 0){ // If close bus stops > 0 run API and load recycler view
                                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                                    apiBusStopService.getBusService(closeBusStops,new ApiBusStopService.VolleyResponseListener2() {
                                        @Override
                                        public void onError(String message) {
                                            Toast.makeText(MainActivity.this,"Cannot Get Bus Stops, Check Location and Connection",Toast.LENGTH_LONG).show();
                                        }
                                        @Override
                                        public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                            RecyclerView rv = findViewById(R.id.recyclerView);
                                            BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,MainActivity.this);
                                            LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                            rv.setAdapter(adapter);
                                            rv.setLayoutManager(layout);
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                                swipeRefreshLayout.setRefreshing(false); //Close refreshing Icon
                                if(closeBusStops.size() == 0){ // If there are no nearby bus stop, show toast message
                                    Toast.makeText(MainActivity.this,"No nearby bus stops",Toast.LENGTH_LONG).show();
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                                    progressDialog.dismiss();
                                }
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



                        ArrayList<BusStop> closeBusStops = new ArrayList<>();
                        for (int i = 0; i < busStops.size(); i++){ //Get nearby bus stops
                            BusStop busStop = busStops.get(i);
                            busStop.setDistanceToLocation(DistanceCalculator.distanceBetween(busStop.getLatitude(), busStop.getLongitude(), Latitude, Longitude));

                            if (busStop.getDistanceToLocation() <= globalCloseness){
                                closeBusStops.add(busStop);
                                LatLng latlongmarker = new LatLng(busStop.getLatitude(), busStop.getLongitude());
                                map.addMarker(new MarkerOptions().position(latlongmarker).title(busStop.getDescription()));
                            }
                        }
                        if(closeBusStops.size() > 0){
                            ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                            apiBusStopService.getBusService(closeBusStops,new ApiBusStopService.VolleyResponseListener2() { //Call API for nearby bus stops
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(MainActivity.this,"Cannot Get Bus Stops, Check Location and Connection",Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                    RecyclerView rv = findViewById(R.id.recyclerView);
                                    BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,MainActivity.this);
                                    LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        if(closeBusStops.size() == 0){
                            Toast.makeText(MainActivity.this,"No nearby bus stops",Toast.LENGTH_LONG).show();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                            progressDialog.dismiss();
                        }



                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));

                    }
                });
            }






        }



    }
    public void moveMapsCamera(Double latitude, Double longitude){ //Function to enable move camera from other classes
        LatLng latlongmove = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlongmove)
                .zoom(17f)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cu);

    }
    private void replaceFragment(Fragment fragment){ //Replace fragment for nav bar
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_train:
                /*Intent intent = new Intent(MainActivity.this, Bus.class);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);*/
                break;
        }
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
}
