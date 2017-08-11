package com.jogato.climate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by asapkota on 8/10/17.
 */

public class NotificationService extends Service {

    private static final String USER_CITY_KEY = "city";
    private static final String USER_STATE_KEY = "state";
    private static final String USER_DAY_KEY = "day";
    private static final String USER_MONTH_KEY = "month";
    private static final String USER_YEAR_KEY = "year";

    PowerManager pm;
    PowerManager.WakeLock wl;

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(periodicUpdate, 60 * 1000 - SystemClock.elapsedRealtime()%1000);
            queryDatabase();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        handler.post(periodicUpdate);
        //super.onStartCommand(intent, flags, startId);
        //int hour_of_day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //queryDatabase();
        return Service.START_STICKY;
    }

    @Override
    public void onCreate(){
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GpsTrackerWakelock");
        wl.acquire();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wl.release();
    }

    private void queryDatabase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newqueryReference = databaseReference.child("forecasts");
        Query queryReference = newqueryReference.orderByChild("Date");
        queryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<QueryObject> dataList = new ArrayList<QueryObject>();
                Iterable<DataSnapshot> dataSnap = dataSnapshot.getChildren();
                for (DataSnapshot snap : dataSnap){
                    Log.i("AS",snap.child("userId").getValue() + "");
                    if ((snap.child("userId").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                        QueryObject queryObject = new QueryObject(snap);
                        Log.i("CLi", "Data incoming");
                        showNotification(queryObject);
                        dataList.add(queryObject);
                        break;
                    }else{
                        Log.i("Cli", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(QueryObject queryObject){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        String year = calendar.get(Calendar.YEAR) + "";
        String month = calendar.get(Calendar.MONTH) + "";
        int currentDay = calendar.get(Calendar.DATE);
        int day = Integer.parseInt(queryObject.getDay());
        Log.i("showNotifications" , year);
        if (queryObject.getYear().equals(year)){
            if (queryObject.getMonth().equals(month)){
                if (day  >= currentDay + 3){
                    Log.i("AS", "dispalyNotification");
                    displayNotification(queryObject);
                }
            }
        }
    }

    private void displayNotification(QueryObject queryObject){
        //Log.i()
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER_CITY_KEY, queryObject.getCity());
        intent.putExtra(USER_STATE_KEY, queryObject.getState());
//        intent.putExtra(USER_DAY_KEY, queryObject.getDay());
//        intent.putExtra(USER_MONTH_KEY, queryObject.getMonth());
//        intent.putExtra(USER_YEAR_KEY, queryObject.getYear());
        PendingIntent intent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Get your CliMate Suggestions")
                .setContentText("See the suggestions")
                .setSmallIcon(android.R.drawable.btn_dropdown)
                .setContentIntent(intent1)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, n);

    }

}
