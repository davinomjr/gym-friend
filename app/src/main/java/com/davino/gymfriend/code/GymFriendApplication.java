package com.davino.gymfriend.code;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.davino.gymfriend.services.AppBackgroundService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by davin on 31/05/2017.
 */

public class GymFriendApplication extends Application {
    private static final String TAG = GymFriendApplication.class.getSimpleName();
    private static GymFriendApplication mInstance;
    private AppGoogleAPIHelper googleAPIHelper;
    private FirebaseDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        startGoogleAPIClient();
        Log.i(TAG, "initializing firebase");
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance();
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

    public AppGoogleAPIHelper getGoogleAPIHelperInstance() {
        return this.googleAPIHelper;
    }

    public static AppGoogleAPIHelper getGoogleAPIHelper() {
        return getInstance().googleAPIHelper;
    }
}
