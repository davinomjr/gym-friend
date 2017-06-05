package com.davino.gymfriend.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davino.gymfriend.R;

public class SleepingTimesFragment extends Fragment {


    public SleepingTimesFragment() {
        // Required empty public constructor
    }


    public static SleepingTimesFragment newInstance(String param1, String param2) {
        SleepingTimesFragment fragment = new SleepingTimesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sleeping_times, container, false);
    }
}
