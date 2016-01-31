package pro.devapp.notifi.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.squareup.okhttp.OkHttpClient;

import pro.devapp.notifi.R;

/**
 * Created by arseniy on 31/01/16.
 */
public class Alarm extends BroadcastReceiver {

    OkHttpClient client = new OkHttpClient();

    @Override
    public void onReceive(Context context, Intent intent) {

        String login = PreferenceHelper.get(context.getApplicationContext(), "login", "");
        if(!login.equals("")){
            // Выбираем таблетки по которм нужно отправить уведомления
            Notification notification = new Notification(context);
            String title = context.getResources().getString(R.string.app_name);
            notification.startNotification(title, "test");
        }
    }

    /**
     * Запускает проверку для уведомлений
     * @param context
     */
    public void SetAlarm(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        long s = 60000l; // 1m
        long cm = System.currentTimeMillis() + 1000l; // add 10s
        cm = cm + (s - cm%s);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cm, 1000 * 60, pi); // Millisec * Second * Minute
    }

    /**
     * Останавливает проверку для уведомлений
     * @param context
     */
    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
