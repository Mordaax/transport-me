package sg.edu.np.mad.transportme;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import static sg.edu.np.mad.transportme.LoginPage.globalCloseness;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback  {

    GoogleMap map;
    LocationManager locationManager;


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
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,R.style.MyAlertDialogStyle);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item ->{
            LinearLayout mapandrv = findViewById(R.id.MapAndRV);
            FrameLayout fragmentlayout = findViewById(R.id.frame_layout);
            switch(item.getItemId()){
                case R.id.home:
                    fragmentlayout.setVisibility(View.INVISIBLE);
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
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, new LocationListener() {
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Double Latitude = location.getLatitude();
                        Double Longitude = location.getLongitude();


                        LatLng latLng = new LatLng(Latitude, Longitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());



                        ArrayList<BusStop> closeBusStops = new ArrayList<>();
                        for (int i = 0; i < busStops.size(); i++){
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
                        if(closeBusStops.size() == 0){
                            Toast.makeText(MainActivity.this,"No nearby bus stops",Toast.LENGTH_LONG).show();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));
                            progressDialog.dismiss();
                        }



                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.2f));

                    }
                });
            }
            else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                //For users to refresh the recyclerview, runs the location reqeust updates
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
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
                        for (int i = 0; i < busStops.size(); i++){
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
    public void moveMapsCamera(Double latitude, Double longitude){
        LatLng latlongmove = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlongmove)
                .zoom(17f)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cu);

    }
    private void replaceFragment(Fragment fragment){
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

}