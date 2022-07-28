package sg.edu.np.mad.transportme.views;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.Carpark;
import sg.edu.np.mad.transportme.CarparkAdapter;
import sg.edu.np.mad.transportme.PrivacyPolicyActivty;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.Route;
import sg.edu.np.mad.transportme.StepAdapter;
import sg.edu.np.mad.transportme.WeekActivity;
import sg.edu.np.mad.transportme.api.ApiCarparkService;
import sg.edu.np.mad.transportme.api.MySingleton;

public class CarparkActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final float END_SCALE = 0.7f;
    ArrayList<Carpark> carparkArrayList = new ArrayList<>();
    DrawerLayout drawerLayout;
    LinearLayout contentView;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.carparkContentView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_carpark);
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

        RecyclerView recyclerView = findViewById(R.id.carparkRecyclerView);

        ApiCarparkService apiCarparkService = new ApiCarparkService(this);
        apiCarparkService.getCarparkAvailability(carparkArrayList, new ApiCarparkService.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(CarparkActivity.this,"Cannot get Carparks, Try again later",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(ArrayList<Carpark> Carparks) {
                Log.d("Hello",Carparks.toString());
                CarparkAdapter adapter = new CarparkAdapter(CarparkActivity.this, carparkArrayList);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CarparkActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                recyclerView.setAdapter(adapter);

            }
        });
        //Search feature in carpark availability
        EditText carparkSearch = findViewById(R.id.carparkSearch);
        carparkSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Carpark> searchResult = new ArrayList<>(); //Reset ArrayList
                //Log.d("Initial", carparkArrayList.toString());
                //Log.d("searchString", carparkSearch.getText().toString());
                for(Carpark c : carparkArrayList){
                    //Log.d("carpark", c.Development);
                    if(c.Development.toLowerCase().contains(carparkSearch.getText().toString().toLowerCase())){
                        searchResult.add(c);
                        //Toast.makeText(CarparkActivity.this, "Found it", Toast.LENGTH_LONG).show();
                    }

                }
                if(searchResult.isEmpty()){
                    Toast.makeText(CarparkActivity.this, "No result, check spelling",Toast.LENGTH_LONG).show();
                }
                else {
                    CarparkAdapter adapter = new CarparkAdapter(CarparkActivity.this, searchResult);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CarparkActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.setAdapter(adapter);
                }

                /*
                ApiCarparkService apiCarparkService = new ApiCarparkService(CarparkActivity.this);
                apiCarparkService.getCarparkAvailability(carparkArrayList, new ApiCarparkService.VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Toast.makeText(CarparkActivity.this,"Cannot get Carparks, Try again later",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(ArrayList<Carpark> Carparks) {
                        Log.d("a", Carparks.toString());
                        for(int i = 0; i < Carparks.size(); i++){
                            if (Carparks.get(i).Development.toLowerCase(Locale.ROOT).equals(carparkSearch.toString().toLowerCase(Locale.ROOT))){
                                searchResult.add(Carparks.get(i));
                                Toast.makeText(CarparkActivity.this, "Found it", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(CarparkActivity.this, "No such place", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
                 */
            }
        });
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
                break;
            case R.id.nav_profile:
                Intent intentMainActivity = new Intent(CarparkActivity.this, MainActivity.class);
                intentMainActivity.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                intentMainActivity.putExtra("Profile", "Yes");

                startActivity(intentMainActivity);
                finish();
                /*mapandrv.setVisibility(View.INVISIBLE);
                fragmentlayout.setVisibility(View.VISIBLE);
                replaceFragment(new ProfileFragment());*/
                break;
            case R.id.nav_route:
                Intent routeintent = new Intent(CarparkActivity.this, Route.class);
                routeintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(routeintent);
                finish();
                break;
            case R.id.nav_fares:
                Intent fareintent = new Intent(CarparkActivity.this, WeekActivity.class);
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
                Intent privacyintent = new Intent(CarparkActivity.this, PrivacyPolicyActivty.class);
                privacyintent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(privacyintent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}