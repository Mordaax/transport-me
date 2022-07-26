package sg.edu.np.mad.transportme;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static sg.edu.np.mad.transportme.ReminderApplication.CHANNEL_ID;
import static sg.edu.np.mad.transportme.ReminderApplication.CHANNEL_ID_2;
import static sg.edu.np.mad.transportme.user.LoginPage.globalName;
import static sg.edu.np.mad.transportme.user.LoginPage.globalRemindCloseness;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminder;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminderBusService;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import sg.edu.np.mad.transportme.api.ApiBusStopService;
import sg.edu.np.mad.transportme.views.MainActivity;

public class ReminderService extends Service {

    LocationManager locationManager;
    LocationListener locationListener;
    private Context context;
    public static Boolean reached = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        context = ReminderApplication.getContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");     //Initialise database instance
        DatabaseReference reminderReference = db.getReference()
                .child("User")
                //.child(firebaseUser.getUid())
                .child(globalName)
                .child("Reminder");


        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Reminder")
                .setContentText("TransportMe will notify you closer to the destination")
                .setSmallIcon(R.drawable.app_logo_vector)
                .setOngoing(true)
                .build();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.e("no i am not","no");
                    if (globalReminder != null)
                    {
                        Double Latitude = location.getLatitude(); //Get latitude and logitude
                        Double Longitude = location.getLongitude();


                        LatLng latLng = new LatLng(Latitude, Longitude);
                        Geocoder geocoder = new Geocoder(getApplicationContext());


                        ArrayList<BusStop> remindBusStop = new ArrayList<>();
                        remindBusStop.add(globalReminder);
                        ApiBusStopService apiBusStopService = new ApiBusStopService(context);
                        apiBusStopService.getBusRoute(globalReminderBusService,new ApiBusStopService.VolleyResponseListener3() { //Call API for bus route
                            @Override
                            public void onError(String message) {

                            }
                            @Override
                            public void onResponse(ArrayList<BusStop> busStopRouteLoaded) {
                                Integer index = busStopRouteLoaded.lastIndexOf(globalReminder);
                                LatLng destnLL = new LatLng(globalReminder.getLatitude(),globalReminder.getLongitude());
                                Double destnDist = SphericalUtil.computeDistanceBetween(latLng,destnLL);
                                Log.e("destndist",""+destnDist);
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
                                        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID_2)
                                                .setSmallIcon(R.drawable.app_logo_vector)
                                                .setContentTitle("Reminder to Alight")
                                                .setContentIntent(pendingIntent)
                                                .setContentText("You are arriving "+ globalReminder.getDescription() + "!")
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                .build();

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                        notificationManager.notify(1,notification);
                                        reached = true;
                                        reminderReference.setValue(null);
                                        stopSelf();
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }

        startForeground(1, notification);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);    // Stop getting updates from the location manager
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
