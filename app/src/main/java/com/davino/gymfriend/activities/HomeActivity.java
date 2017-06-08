package com.davino.gymfriend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;


import com.afollestad.materialdialogs.MaterialDialog;
import com.davino.gymfriend.R;
import com.davino.gymfriend.code.GymFriendApplication;
import com.davino.gymfriend.fragments.LocationHistoryFragment;
import com.davino.gymfriend.fragments.SleepingTimesFragment;
import com.davino.gymfriend.model.BaseEntity;
import com.davino.gymfriend.model.LocationHistory;
import com.davino.gymfriend.model.SleepingTime;
import com.davino.gymfriend.services.AppBackgroundService;
import com.davino.gymfriend.util.Constants;
import com.google.firebase.database.DatabaseReference;


import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
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
            treatIntent(getIntent());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
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

        LocationHistoryFragment locationsFragment = LocationHistoryFragment.newInstance();
        SleepingTimesFragment sleepingTimesFragment = SleepingTimesFragment.newInstance();

        adapter.addFragment(locationsFragment, getString(R.string.title_locations_fragment));
        adapter.addFragment(sleepingTimesFragment, getString(R.string.title_sleepingtimes_fragment));
        viewPager.setAdapter(adapter);
    }

    private void treatIntent(Intent intent) {
        if(intent.getBooleanExtra("alert", false)){
            new MaterialDialog.Builder(this)
                    .title(R.string.gym_Friend_title)
                    .content(R.string.alert)
                    .positiveText("OK")
                    .show();
        }
        else if(intent.getExtras() != null && intent.getExtras().containsKey("sleepingTime")){
            SleepingTime sleepingTime = Parcels.unwrap(intent.getExtras().getParcelable("sleepingTime"));
            addFirebaseItem(sleepingTime, Constants.FIREBASE_SLEEPING_TIME_REFERENCE);
        }

        if (intent.getBooleanExtra(Constants.KILL_COMMAND, false)) {
            Log.i(TAG, "Killing app");
            stopService(new Intent(this, AppBackgroundService.class));
            GymFriendApplication.getGoogleAPIHelper().disconnect();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        else if(intent.getExtras() != null && intent.getExtras().containsKey("location")){
            LocationHistory location = Parcels.unwrap(intent.getExtras().getParcelable("location"));
            addFirebaseItem(location, Constants.FIREBASE_LOCATION_REFERENCE);

            if(!mOnGym) {
                new MaterialDialog.Builder(this)
                        .title(R.string.gym_Friend_title)
                        .content(R.string.have_a_good_workout)
                        .positiveText("OK")
                        .show();

            }

            mOnGym = true;
        }
    }

    private void addFirebaseItem(BaseEntity entity, String firebaseReference){
        DatabaseReference reference = GymFriendApplication.getDatabase().getReference(firebaseReference);
        String key = reference.push().getKey();
        entity.setId(key);
        reference.child(key).setValue(entity);
    }

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
