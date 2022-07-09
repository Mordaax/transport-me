package sg.edu.np.mad.transportme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Route extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routemap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        direction();
        LatLng sydney = new LatLng(1.3199637,103.7743615);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void direction(){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        ArrayList<RouteStep> routeSteps = new ArrayList<>();

        String url = "https://mad-assignment-backend.herokuapp.com/routetest";
        /*String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination","The Tampines Trilliant")
                .appendQueryParameter("origin","Ngee Ann Polytechnic")
                .appendQueryParameter("mode","transit")
                .appendQueryParameter("key","AIzaSyC5TLFoQWmsorYN0--un6BieG6VI2STONE")
                .toString();*/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray routes = response.getJSONArray("routes");
                        ArrayList<LatLng> points;

                        Log.d("kek", routes.toString());
                        for (int i = 0; i < routes.length(); i++) {

                            PolylineOptions polylineOptions = null;
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            Log.d("kek", legs.toString());
                            for (int j = 0; j < legs.length(); j++) {
                                String start_address = legs.getJSONObject(j).getString("start_address");
                                String end_address = legs.getJSONObject(j).getString("end_address");
                                RouteStep firstRouteStep = new RouteStep();
                                firstRouteStep.Instructions = "From "+ start_address;
                                routeSteps.add(firstRouteStep);
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                                Log.d("kek", steps.toString());
                                for (int k = 0; k < steps.length(); k++) {

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

                                        /*List<PatternItem> pattern = Arrays.asList(
                                                new Dot(), new Gap(20), new Dash(30), new Gap(20));
                                        polylineOptions.setPattern(pattern);*/
                                        travelMode = "Walk";
                                        previousLocation = k==0?start_address: steps.getJSONObject(k-1).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name");
                                        nextLocation = k==steps.length()-1? end_address: steps.getJSONObject(k+1).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");

                                    }
                                    else {/*if (travelmode.equals("TRANSIT")){*/
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

                                        marker = new MarkerOptions().position(latlongend);
                                        marker.icon(bitmapDescriptorFromVector(Route.this,R.drawable.ic_baseline_lens_24));
                                        mMap.addMarker(marker);

                                        previousLocation = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name");
                                        nextLocation = steps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name");

                                    }


                                    RouteStep currentStep = new RouteStep(latlongstart,latlongend,travelMode,instructions, distance,duration,previousLocation,nextLocation);
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

                        mMap.addMarker(new MarkerOptions().position(new LatLng(1.3595533, 103.94306)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(1.3212432, 103.7743509)));

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(1.3595533, 103.94306))
                                .include(new LatLng(1.3212432, 103.7743509)).build();
                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    /*private List<LatLng> decodePoly(String encoded){
        List<LatLng>poly=new ArrayList<>();
        int index=0,len=encoded.length();
        int lat = 0,lng=0;
        while(index<len){
            int b,shift = 0,result=0;
            do {
                b = encoded.charAt(index++) - 63;
                result = (b & 0x1f) << shift;
                shift += 5;
            }while(b>=0x20);
                int dlat = ((result&1) != 0 ?~(result >>1) : (result >>1));
                lat+=dlat;
                shift=0;
                result=0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while(b > 0x20);
                int dlng=((result&1)!=0?~(result >>1):(result >>1));
                lng+=dlng;
                LatLng p =new LatLng((((double) lat/ 1E5)),
                        (((double) lng /1E5)));
                poly.add(p);
            }
            return poly;
    }*/
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
}