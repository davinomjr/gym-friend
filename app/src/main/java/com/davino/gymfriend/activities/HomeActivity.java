package com.davino.gymfriend.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.davino.gymfriend.R;
import com.davino.gymfriend.code.GymFriendApplication;
import com.davino.gymfriend.fragments.LocationHistoryFragment;
import com.davino.gymfriend.fragments.SleepingTimesFragment;
import com.davino.gymfriend.interfaces.IGymLocationListener;
import com.davino.gymfriend.model.LocationHistory;
import com.davino.gymfriend.services.AppBackgroundService;
import com.davino.gymfriend.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;


import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, IGymLocationListener {

    private static final String TAG = "HomeActivity";
    private AppBackgroundService mService;
    private boolean mServiceBound = false;
    private boolean mOnGym = false;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.i(TAG, "Started HomeActivity");

        loadTabs();
        if (getIntent() != null) {
            dealWithIntent(getIntent());
        }
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
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }


    private void loadTabs(){
        toolbar = (Toolbar) findViewById(R.id.tabBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        LocationHistoryFragment locationsFragment = new LocationHistoryFragment();
        SleepingTimesFragment sleepingTimesFragment = new SleepingTimesFragment();

        adapter.addFragment(locationsFragment, getString(R.string.title_locations_fragment));
        adapter.addFragment(sleepingTimesFragment, getString(R.string.title_sleepingtimes_fragment));
        viewPager.setAdapter(adapter);
    }

    private void dealWithIntent(Intent intent) {
        if (intent.getBooleanExtra(Constants.KILL_COMMAND, false)) {
            Log.i(TAG, "Killing app");
            stopService(new Intent(this, AppBackgroundService.class));
            GymFriendApplication.getGoogleAPIHelper().disconnect();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        else if(intent.getExtras() != null && intent.getExtras().containsKey("location")){
            LocationHistory location = Parcels.unwrap(intent.getExtras().getParcelable("location"));
            addLocation(location);

            if(!mOnGym) {
                new MaterialDialog.Builder(this)
                        .title(R.string.gym_Friend_title)
                        .content(R.string.have_a_good_workout)
                        .positiveText("OK")
                        .show();
            }

            mOnGym = true;
            Log.i(TAG, "TREAT LIKE ON GYM");
        }
    }

    //TODO: Change location?
    private void addLocation(LocationHistory location){
        DatabaseReference reference = GymFriendApplication.getDatabase().getReference("locations");
        String key = reference.push().getKey();
        location.setId(key);
        reference.child(key).setValue(location);
    }

    @Override
    public void notifyDeviceNearGym(LocationHistory location) {
        Log.i(TAG, "notifyDeviceNearGym: HomeActivity");
        mOnGym = location != null;
        if(mOnGym){
            Log.i(TAG, "still on GYMM");
        }
        else{
            Log.i(TAG, "LEFT GYMM");
        }
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
