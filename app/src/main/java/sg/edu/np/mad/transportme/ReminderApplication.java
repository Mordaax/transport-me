package sg.edu.np.mad.transportme;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class ReminderApplication extends Application {
    public static final String CHANNEL_ID = "reminderServiceChannel";
    public static final String CHANNEL_ID_2 = "reminderAlightChannel";
    private static Context context;

    public static Context getContext() {
        return context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ReminderApplication.context = getApplicationContext();
        createNotificationChannels();
    }

    private void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)      //If API level higher than Oreo
        {
            NotificationChannel serviceChannel = new NotificationChannel(       //Create first notification channel for foreground services
                    CHANNEL_ID,
                    "Reminder Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationChannel serviceChannel2 = new NotificationChannel(      //Create second notification channel for when notifying to alight
                    CHANNEL_ID_2,
                    "Reminder Alight Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);  //Create notification manager
            manager.createNotificationChannel(serviceChannel);      //Create both notification channels
            manager.createNotificationChannel(serviceChannel2);
        }
    }
}
