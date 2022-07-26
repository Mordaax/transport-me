package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.ReminderApplication.CHANNEL_ID;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.security.Provider;

import sg.edu.np.mad.transportme.views.MainActivity;

public class ReminderService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Reminder")
                .setContentText("TransportMe will notify you closer to the destination")
                .setSmallIcon(R.drawable.app_logo_vector)
                .setContentIntent(pendingIntent)
                .build();



        startForeground(1, notification);

        //do stopSelf() once reaching destn

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
