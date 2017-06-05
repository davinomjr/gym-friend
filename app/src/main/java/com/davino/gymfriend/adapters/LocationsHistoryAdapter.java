package com.davino.gymfriend.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davino.gymfriend.R;
import com.davino.gymfriend.model.LocationHistory;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by davin on 04/06/2017.
 */

public class LocationsHistoryAdapter extends FirebaseRecyclerAdapter<LocationsHistoryAdapter.ViewHolder, LocationHistory> {

    private static final String TAG = "LocationsHistoryAdapter";

    public LocationsHistoryAdapter(Query query, @Nullable ArrayList<LocationHistory> items, @Nullable ArrayList<String> keys){
        super(query, items, keys);
    }

    @Override
    public LocationsHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_history, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationHistory item = getItem(position);
        holder.textViewLocationName.setText(item.getLocationName());
        holder.textViewDate.setText(item.getLocationHistoryDate());
    }

    @Override
    protected void itemAdded(LocationHistory item, String key, int position) {
        Log.d(TAG, "Added a new item to the adapter.");
    }

    @Override
    protected void itemChanged(LocationHistory oldItem, LocationHistory newItem, String key, int position) {
        Log.d(TAG, "Changed an item.");
    }

    @Override
    protected void itemRemoved(LocationHistory item, String key, int position) {
        Log.d(TAG, "Removed an item from the adapter.");
    }

    @Override
    protected void itemMoved(LocationHistory item, String key, int oldPosition, int newPosition) {
        Log.d(TAG, "Moved an item.");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewLocationName;
        TextView textViewDate;
        View view;

        public ViewHolder(View view) {
            super(view);
            textViewLocationName = (TextView) view.findViewById(R.id.location_name);
            textViewDate = (TextView) view.findViewById(R.id.location_history_date);
            this.view = view;
        }
    }
}