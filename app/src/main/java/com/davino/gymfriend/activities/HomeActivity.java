package com.davino.gymfriend.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.davino.gymfriend.R;
import com.davino.gymfriend.services.AppBackgroundService;
import com.davino.gymfriend.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

public class HomeActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HomeActivity";
    private AppBackgroundService mService;
    private boolean mServiceBound = false;
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getIntent().getBooleanExtra(Constants.KILL_COMMAND, false)) {
            Log.i(TAG, "Killing app");
            stopService(new Intent(this, AppBackgroundService.class));
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        mApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AppBackgroundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        mApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPuase");
        super.onPause();
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
            mService.configureMLocationHelper(mApiClient);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    };
}
