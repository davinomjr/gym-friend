package com.davino.gymfriend.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davino.gymfriend.R;
import com.davino.gymfriend.adapters.LocationsHistoryAdapter;
import com.davino.gymfriend.adapters.SleepingTimeAdapter;
import com.davino.gymfriend.code.GymFriendApplication;
import com.davino.gymfriend.model.LocationHistory;
import com.davino.gymfriend.model.SleepingTime;
import com.davino.gymfriend.services.AppBackgroundService;
import com.davino.gymfriend.util.Constants;
import com.google.firebase.database.Query;

public class SleepingTimesFragment extends BaseListFragment<SleepingTime> {

    private static final String TAG = SleepingTimesFragment.class.getSimpleName();
    private Query mQuery;

    public SleepingTimesFragment() {
        // Required empty public constructor
    }

    private void setupFirebase() {
        mQuery = GymFriendApplication.getDatabase().getReference(Constants.FIREBASE_SLEEPING_TIME_REFERENCE);
    }


    public static SleepingTimesFragment newInstance() {
        return new SleepingTimesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupRecyclerview(View recycView) {
        RecyclerView recyclerView = (RecyclerView) recycView;
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.i(TAG, "Clicked item");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        mAdapter = new SleepingTimeAdapter(mQuery, mAdapterItems, mAdapterKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleeping_times, container, false);
        handleInstanceState(savedInstanceState);
        setupFirebase();
        setupRecyclerview(view.findViewById(R.id.rv));
        Button button = (Button) view.findViewById(R.id.btn_foward_time);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GymFriendApplication.getBackgroundService().setTimeToSleep();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }
}
