package com.davino.gymfriend.code;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.davino.gymfriend.R;
import com.davino.gymfriend.services.AppBackgroundService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by davin on 31/05/2017.
 */

public class GymFriendApplication extends Application implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = GymFriendApplication.class.getSimpleName();
    private static GymFriendApplication mInstance;
    private AppGoogleAPIHelper googleAPIHelper;
    private FirebaseDatabase mDatabase;
    private AppBackgroundService mService;
    private boolean mServiceBound = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        startGoogleAPIClient();
        Log.i(TAG, "initializing firebase");
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance();
        Intent intent = new Intent(this, AppBackgroundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void killService(){
        unbindService(mServiceConnection);
        mServiceBound = false;
    }

    public static FirebaseDatabase getDatabase() {
        return getInstance().mDatabase;
    }

    private void startGoogleAPIClient() {
        googleAPIHelper = new AppGoogleAPIHelper(mInstance);
    }

    public static synchronized GymFriendApplication getInstance() {
        return mInstance;
    }

    public static AppGoogleAPIHelper getGoogleAPIHelper() {
        return getInstance().googleAPIHelper;
    }

    public static AppBackgroundService getBackgroundService(){
        return getInstance().mService;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), getString(R.string.err_connection_failure), Toast.LENGTH_SHORT).show();
    }


    /**
     * Callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AppBackgroundService.GymBinder binder = (AppBackgroundService.GymBinder) service;
            mService = binder.getService();
            mServiceBound = true;
            mService.configureMLocationHelper();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    };

}
