package ac.whitireia.ourmoments;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BaseApplication extends Application {

    private static final String CHANNEL_ID = "OurMomentsChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "OurMoments Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("You are in OurMoments channel notification");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
