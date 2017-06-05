package com.davino.gymfriend.fragments;

import android.os.Bundle;

import com.davino.gymfriend.adapters.FirebaseRecyclerAdapter;
import com.davino.gymfriend.model.LocationHistory;

import org.parceler.Parcels;

import java.util.ArrayList;
import android.support.v4.app.Fragment;

/**
 * Created by davin on 04/06/2017.
 */

public class BaseListFragment<BaseEntity> extends Fragment {

    protected ArrayList<BaseEntity> mAdapterItems;
    protected ArrayList<String> mAdapterKeys;
    protected FirebaseRecyclerAdapter mAdapter;

    private final static String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    private final static String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";

    // Restoring the item list and the keys of the items: they will be passed to the adapter
    protected void handleInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SAVED_ADAPTER_ITEMS) &&
                savedInstanceState.containsKey(SAVED_ADAPTER_KEYS)) {
            mAdapterItems = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_ADAPTER_ITEMS));
            mAdapterKeys = savedInstanceState.getStringArrayList(SAVED_ADAPTER_KEYS);
        } else {
            mAdapterItems = new ArrayList<>();
            mAdapterKeys = new ArrayList<>();
        }
    }

    // Saving the list of items and keys of the items on rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_ADAPTER_ITEMS, Parcels.wrap(mAdapter.getItems()));
        outState.putStringArrayList(SAVED_ADAPTER_KEYS, mAdapter.getKeys());
    }
}