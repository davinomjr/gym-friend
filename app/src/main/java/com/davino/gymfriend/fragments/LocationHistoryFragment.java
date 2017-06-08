package com.davino.gymfriend.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.davino.gymfriend.R;
import com.davino.gymfriend.adapters.LocationsHistoryAdapter;
import com.davino.gymfriend.code.GymFriendApplication;
import com.davino.gymfriend.model.LocationHistory;
import com.davino.gymfriend.util.Constants;
import com.google.firebase.database.Query;

/**
 * Created by davin on 04/06/2017.
 */

public class LocationHistoryFragment extends BaseListFragment<LocationHistory> {

    private final static String TAG = LocationHistoryFragment.class.getSimpleName();
    private Query mQuery;

    public static LocationHistoryFragment newInstance() {
        return new LocationHistoryFragment();
    }

    private void setupFirebase() {
        mQuery = GymFriendApplication.getDatabase().getReference(Constants.FIREBASE_LOCATION_REFERENCE);
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

        mAdapter = new LocationsHistoryAdapter(mQuery, mAdapterItems, mAdapterKeys);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_history, container, false);
        handleInstanceState(savedInstanceState);
        setupFirebase();
        setupRecyclerview(view.findViewById(R.id.rv));
        Log.i(TAG, "Finished loading");
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }
}
