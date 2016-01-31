package pro.devapp.notifi.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;

import pro.devapp.notifi.MainActivity;
import pro.devapp.notifi.R;

/**
 * Создание уведомлений
 */
public class Notification {

    Context context;

    public Notification(Context context){
        this.context = context;
    }

    public void startNotification(String title, String content){
        scheduleNotification(getNotification(title, content), 1000);
    }

    private android.app.Notification getNotification(String title, String content) {
        android.app.Notification.Builder builder = new android.app.Notification.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.icon);
        builder.setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.build().flags |= android.app.Notification.FLAG_AUTO_CANCEL;
        } else {
            builder.getNotification().flags |= android.app.Notification.FLAG_AUTO_CANCEL;
        }
        builder.setTicker(title);
        builder.setNumber(1);
        // вибрация
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        //LED
        builder.setLights(Color.RED, 3000, 3000);
        //Ton
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        // Open activity after click
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("extra", "232");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(contentIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else {
            return  builder.getNotification();
        }
    }

    private void scheduleNotification(android.app.Notification notification, int delay) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

        // alarmManager.cancel(pendingIntent);
    }
}

