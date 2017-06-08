package com.davino.gymfriend.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.os.Process;

import com.davino.gymfriend.activities.HomeActivity;
import com.davino.gymfriend.interfaces.IGymLocationListener;
import com.davino.gymfriend.model.LocationHistory;
import com.davino.gymfriend.model.SleepingTime;
import com.davino.gymfriend.sensors.LocationSensor;
import com.davino.gymfriend.util.DateHelper;
import com.davino.gymfriend.util.TimerCounter;
import com.davino.gymfriend.util.Constants;
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
    private LocationSensor mLocationHelper;
    private NotificationHelper mNotificationHelper = new NotificationHelper();
    private boolean userOnGym = false;

    private TimerCounter timerOn;
    private TimerCounter timerSinceLastAlert;
    private TimerCounter timerPossiblySleeping;
    private String lastDateTimeSleep;

    // TODO: PLACEHOLDER
    private boolean timeToSleep;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service Created");
        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        startKillSwitchNotification();

        timerOn = new TimerCounter();
        timerSinceLastAlert = new TimerCounter();
        timerPossiblySleeping = new TimerCounter();
        new ScreenReceiver();
    }


    @Override
    public void notifyDeviceNearGym(LocationHistory gymLocationHistory) {
        Log.i(TAG, "notifyDeviceNearGym: SERVICE");
        if (gymLocationHistory == null) {
            Log.i(TAG, "Usuario saiu da academia");
            timerOn.stop();
            userOnGym = false;
            return;
        }

        if (!userOnGym) { // Nao estava na academia
            userOnGym = true;
            Log.i(TAG, "Usuario chegou à academia");
            Intent intent = getNotificationIntent();
            intent.putExtra("location", Parcels.wrap(gymLocationHistory));
            startActivity(intent);
            timerOn.startCounting();
        }


        Log.i(TAG, "Tempo usando o telefone= " + timerOn.checkHowManySecondsPassed());
        if ((timerSinceLastAlert.isStopped() || timerSinceLastAlert.checkHowManySecondsPassed() > 30)
                && timerOn.checkHowManySecondsPassed() > Constants.MAX_SECONDS_INTERACTION_ON_GYM) {
            Log.i(TAG, "Tempo ultrapassado, irá chamar outra activity");
            Intent intent = getNotificationIntent();
            intent.putExtra("alert", true);
            timerSinceLastAlert.startCounting();
            startActivity(intent);
        }
    }

    public class GymBinder extends Binder {
        public AppBackgroundService getService() {
            return AppBackgroundService.this;
        }
    }


    public void configureMLocationHelper() {
        mLocationHelper = LocationSensor.getInstance();
        mLocationHelper.setOnEventListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
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


    private Intent getNotificationIntent() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    private void startKillSwitchNotification() {
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

    public void setTimeToSleep(){
        Log.i(TAG, "Time to sleep (FAKE) = TRUE");
        timeToSleep = true;
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Log.i(TAG, "Service on");
                mLocationHelper.checkIfNearGym();
                if (timerPossiblySleeping.checkHowManySecondsPassed() > Constants.MIN_SECONDS_TO_CONSIDER_SLEEPING) {
                    timerPossiblySleeping.stop();
                    Intent intent = getNotificationIntent();
                    intent.putExtra("sleepingTime", Parcels.wrap(new SleepingTime(lastDateTimeSleep)));
                    startActivity(intent);
                }
            }
        }
    }

    private boolean isOnSleepingTime() {
        int currentHour = DateHelper.getCurrentHour();
        return timeToSleep || currentHour > 22 || currentHour < 4;
    }

    private void treatUserOnGym(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i(TAG, "Screen OFF (ON GYM), timer stopping");
            timerOn.stop();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                && timerOn.isStopped()) {
            Log.i(TAG, "Screen ON (ON GYM), timer starting");
            timerOn.startCounting();
        }
    }

    private void treatUserPossiblySleeping(Intent intent) {
        if (!isOnSleepingTime()) {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i(TAG, "Screen OFF (POSSIBLY SLEEPING), timer starting");
            timerPossiblySleeping.startCounting();
            lastDateTimeSleep = DateHelper.getFormattedCurrentDateTime();
        } else {
            Log.i(TAG, "Screen ON (NOT SLEEPING), timer stopping");
            timerPossiblySleeping.startCounting();
        }
    }


    private class ScreenReceiver extends BroadcastReceiver {

        protected ScreenReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (userOnGym) {
                treatUserOnGym(intent);
            } else {
                treatUserPossiblySleeping(intent);
            }
        }
    }
}