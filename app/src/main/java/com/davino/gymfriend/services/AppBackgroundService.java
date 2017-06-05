package com.davino.gymfriend.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Process;

import com.davino.gymfriend.activities.HomeActivity;
import com.davino.gymfriend.interfaces.IGymLocationListener;
import com.davino.gymfriend.model.LocationHistory;
import com.davino.gymfriend.util.Constants;
import com.davino.gymfriend.util.LocationHelper;
import com.davino.gymfriend.util.NotificationHelper;

import org.parceler.Parcels;

/**
 * Created by davin on 31/05/2017.
 */

public class AppBackgroundService extends Service implements IGymLocationListener {

    private static final String TAG = AppBackgroundService.class.getSimpleName();
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final IBinder mBinder = new GymBinder();
    private IGymLocationListener mCallback;
    private LocationHelper mLocationHelper;
    private NotificationHelper mNotificationHelper = new NotificationHelper();
    private boolean userOnGym = false;

    @Override
    public void notifyDeviceNearGym(LocationHistory locationHistory) {
        userOnGym = locationHistory != null;
        if(userOnGym) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("location", Parcels.wrap(locationHistory));
            startActivity(intent);
        }
    }

    // Class used for the client Binder.
    public class GymBinder extends Binder {
        public AppBackgroundService getService() {
            // Return this instance of MyService so clients can call public methods
            return AppBackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"SERVICE STARTED") ;
        HandlerThread thread = new HandlerThread("AppBackgroundService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        startKillSwitchNotification();
    }

    public void configureMLocationHelper(){
        mLocationHelper = LocationHelper.getInstance();
        mLocationHelper.setOnEventListener(this);
    }

    private void startKillSwitchNotification(){
        Log.i(TAG, "NOTIFICATION STARTED");
        Intent killIntent = new Intent(getApplicationContext(), HomeActivity.class);
        killIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        killIntent.putExtra(Constants.KILL_COMMAND, true);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        startForeground(Constants.KILL_SWITCH_CODE, notification.setOngoing(true)
                .setContentTitle("CLIQUE PARA MATAR O PROCESSO")
                .setContentText("")
                .setContentIntent(PendingIntent.getActivity(this.getApplicationContext(), 0, killIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(android.R.drawable.ic_dialog_info).build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Log.i(TAG, "onStartCommand");
            Message message = mServiceHandler.obtainMessage();
            message.arg1 = startId;
            mServiceHandler.sendMessage(message);
        }

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            while(true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if(!userOnGym) {
                    mLocationHelper.checkIfNearGym();
                }

                Log.i(TAG, "Service on");
            }
        }
    }



}
