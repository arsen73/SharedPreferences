package pro.devapp.notifi.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by arseniy on 31/01/16.
 */
public class Alarm extends BroadcastReceiver {

    OkHttpClient client = new OkHttpClient();
    Context context;
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String login = PreferenceHelper.get(context, "login", "empty");
        Log.d(Alarm.class.getName(), login);
        if(!login.equals("empty")){
            TeskPush teskPush = new TeskPush();
            teskPush.execute(login);
        }
    }


    class TeskPush extends AsyncTask<String, String, String> {

        String result = "";

        @Override
        protected String doInBackground(String... params) {
            String url = PreferenceHelper.get(context, "server", "http://notpush.azurewebsites.net");
            Request request = new Request.Builder()
                    .url(url + "/api/PushMessages?employerLogin=" + params[0])
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                result = response.body().string();
            } catch (Exception e){
                Log.d(Alarm.class.getName(), e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.d(Alarm.class.getName(), result);
                JSONObject resObj = new JSONObject(result);
                Iterator itr = resObj.keys();
                while (itr.hasNext()){
                    String key = itr.next().toString();
                    JSONArray item = resObj.getJSONArray(key);
                    Notification notification = new Notification(context);
                    //String title = context.getResources().getString(R.string.app_name);
                    notification.startNotification(item.getString(1), item.getString(2));
                }
            } catch (Exception e){
                Log.d(Alarm.class.getName(), e.toString());
            }
        }
    };

    /**
     * Запускает проверку для уведомлений
     * @param context
     */
    public void SetAlarm(Context context)
    {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        long s = 10000l; // 1m
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
