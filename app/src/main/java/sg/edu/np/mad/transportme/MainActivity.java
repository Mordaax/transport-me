package sg.edu.np.mad.transportme;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback  {

    GoogleMap map;
    LocationManager locationManager;

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item ->{
            LinearLayout mapandrv = findViewById(R.id.MapAndRV);
            FrameLayout fragmentlayout = findViewById(R.id.frame_layout);
            switch(item.getItemId()){
                case R.id.home:
                    fragmentlayout.setVisibility(View.INVISIBLE);
                    mapandrv.setVisibility(View.VISIBLE);
                    break;
                case R.id.favourites:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new FavouritesFragment());
                    break;
                case R.id.search:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.profile:
                    mapandrv.setVisibility(View.INVISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });

        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Double Latitude = location.getLatitude();
                    Double Longitude = location.getLongitude();

                    /* For testing purposes, Remove at the end */
                    Latitude = 1.332346;
                    Longitude = 103.777561;

                    LatLng latLng = new LatLng(Latitude, Longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    /* +-0.00904 lat and long per km */
                    Double Closeness = 0.00504;

                    ArrayList<BusStop> closeBusStops = new ArrayList<>();
                    for (int i = 0; i < busStops.size(); i++){
                        BusStop busStop = busStops.get(i);
                        if (busStop.Longitude < Longitude+Closeness && busStop.Longitude > Longitude-Closeness
                                && busStop.Latitude < Latitude+Closeness && busStop.Latitude > Latitude-Closeness){
                            closeBusStops.add(busStop);
                        }
                    }

                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                    apiBusStopService.getBusService(closeBusStops,new ApiBusStopService.VolleyResponseListener2() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(MainActivity.this,"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
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

                    try {
                        List<Address> addressList = geocoder.getFromLocation(Latitude, Longitude, 1);
                        String str = addressList.get(0).getLocality()+", ";
                        str += addressList.get(0).getCountryName();

                        map.addMarker(new MarkerOptions().position(latLng).title(str));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.2f));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Double Latitude = location.getLatitude();
                    Double Longitude = location.getLongitude();
                    LatLng latLng = new LatLng(Latitude, Longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    /* For testing purposes, Remove at the end */
                    Latitude = 1.332346;
                    Longitude = 103.777561;

                    Double Closeness = 0.00504;
                    ArrayList<BusStop> closeBusStops = new ArrayList<>();
                    for (int i = 0; i < busStops.size(); i++){
                        BusStop busStop = busStops.get(i);
                        if (busStop.Longitude < Longitude+Closeness && busStop.Longitude > Longitude-Closeness
                                && busStop.Latitude < Latitude+Closeness && busStop.Latitude > Latitude-Closeness){
                            closeBusStops.add(busStop);
                        }
                    }
                    ApiBusStopService apiBusStopService = new ApiBusStopService(MainActivity.this);
                    apiBusStopService.getBusService(closeBusStops,new ApiBusStopService.VolleyResponseListener2() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(MainActivity.this,"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
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
                    try {
                        List<Address> addressList = geocoder.getFromLocation(Latitude, Longitude, 1);
                        String str = addressList.get(0).getLocality()+", ";
                        str += addressList.get(0).getCountryName();
                        map.addMarker(new MarkerOptions().position(latLng).title(str));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.2f));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        }




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