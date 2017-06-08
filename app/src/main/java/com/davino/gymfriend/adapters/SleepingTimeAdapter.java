package com.davino.gymfriend.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davino.gymfriend.R;
import com.davino.gymfriend.model.SleepingTime;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by davin on 08/06/2017.
 */


public class SleepingTimeAdapter extends FirebaseRecyclerAdapter<SleepingTimeAdapter.ViewHolder, SleepingTime> {

    private static final String TAG = SleepingTimeAdapter.class.getSimpleName();

    public SleepingTimeAdapter(Query query, @Nullable ArrayList<SleepingTime> items, @Nullable ArrayList<String> keys){
        super(query, items, keys);
    }

    @Override
    public SleepingTimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleeping_time, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SleepingTime item = getItem(position);
        holder.textViewSleepingTime.setText(item.getSleepingTime());
    }

    @Override
    protected void itemAdded(SleepingTime item, String key, int position) {
        Log.d(TAG, "Added a new item to the adapter.");
    }

    @Override
    protected void itemChanged(SleepingTime oldItem, SleepingTime newItem, String key, int position) {
        Log.d(TAG, "Changed an item.");
    }

    @Override
    protected void itemRemoved(SleepingTime item, String key, int position) {
        Log.d(TAG, "Removed an item from the adapter.");
    }

    @Override
    protected void itemMoved(SleepingTime item, String key, int oldPosition, int newPosition) {
        Log.d(TAG, "Moved an item.");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewSleepingTime;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textViewSleepingTime = (TextView) view.findViewById(R.id.sleeping_time);
        }
    }
}
