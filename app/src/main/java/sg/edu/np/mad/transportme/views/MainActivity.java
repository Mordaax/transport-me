package sg.edu.np.mad.transportme.views;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import static sg.edu.np.mad.transportme.BitmapResize.getResizedBitmap;
import static sg.edu.np.mad.transportme.user.LoginPage.globalCloseness;
import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import sg.edu.np.mad.transportme.BusServiceAdapter;
import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.BusStopAdapter;
import sg.edu.np.mad.transportme.DistanceCalculator;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.Route;
import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.user.ProfileFragment;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    LinearLayout mapandrv;
    FrameLayout fragmentlayout;
    GoogleMap map;
    Uri image_uri;
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

        FloatingActionButton cameraSearch = findViewById(R.id.fab);
        cameraSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent cameraIntent = new Intent(MainActivity.this, CameraSearch.class);
                startActivity(cameraIntent);*/
                selectImage();
            }
        });


        mapandrv = findViewById(R.id.MapAndRV);
        fragmentlayout = findViewById(R.id.frame_layout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView); // load botttom navigation bar
        bottomNavigationView.setOnItemSelectedListener(item ->{

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
                case R.id.notify:
                    mapandrv.setVisibility(View.VISIBLE);
                    fragmentlayout.setVisibility(View.VISIBLE);
                    replaceFragment(new NotifyFragment());
                    break;

            }
            return true;
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

    public void busroute(Double latitude, Double longitude, BusStop currentStop, List<Marker> mList, List<LatLng> lList){
        LatLng latlongmarker = new LatLng(latitude, longitude);
        Marker marker = map.addMarker(new MarkerOptions().position(latlongmarker).title(currentStop.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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

    public void removemarker(List<Marker> mList, Polyline line) {
        if (line != null) {
            line.remove();
        }
        for (Marker m : mList) {

            m.remove();
        }
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
                Intent intent = new Intent(MainActivity.this, Route.class);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
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
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose Image to Scan");
        builder.setIcon(R.drawable.appsplashicon);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                    image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                    startActivityForResult(takePicture,0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK /*&& data != null*/) {
                        try{
                            ArrayList<BusStop> cameraBusStops = new ArrayList<>();
                            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                            /*Bitmap selectedImage = (Bitmap) data.getExtras().get("data");*/
                            selectedImage = getResizedBitmap(selectedImage,1000);
                            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                            Frame frameImage = new Frame.Builder().setBitmap(selectedImage).build();
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
                        }

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


}
