package com.davino.gymfriend.util;

import android.util.Log;

import com.davino.gymfriend.code.GymFriendApplication;
import com.davino.gymfriend.interfaces.IGymLocationListener;
import com.davino.gymfriend.model.LocationHistory;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

import java.util.ArrayList;

/**
 * Created by davin on 03/06/2017.
 */

public class LocationHelper {

    private static final String TAG = "LocationHelper";
    private IGymLocationListener mContext;
    private static LocationHelper mLocationHelper;
    private ArrayList<IGymLocationListener> listeners = new ArrayList<>();

    public void setOnEventListener(IGymLocationListener listener){
        listeners.add(listener);
    }

    public static LocationHelper getInstance(){
        if(mLocationHelper == null){
            mLocationHelper =  new LocationHelper();
        }

        return mLocationHelper;
    }

    public void checkIfNearGym() {
        if(!GymFriendApplication.getGoogleAPIHelper().isConnected()){
            GymFriendApplication.getGoogleAPIHelper().connect();
        }

        PendingResult<PlaceLikelihoodBuffer> result = com.google.android.gms.location.places.Places.PlaceDetectionApi.getCurrentPlace(GymFriendApplication.getGoogleAPIHelper().getApiClient(), null);
         result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                LocationHistory gymLocation = null;
                boolean onGym = false;
                Log.i(TAG, "RESULTED COMINGG");
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                            if(placeLikelihood.getLikelihood() > Constants.MINIUM_LIKELYHOOD_CONFIDENCE
                                && placeLikelihood.getPlace().getPlaceTypes().indexOf(Place.TYPE_GYM) != -1){
                                gymLocation = new LocationHistory(placeLikelihood.getPlace().getName().toString(), DateHelper.getCurrentDateTimeHour());
                                break;
                            }
                }

                for(IGymLocationListener listener : listeners){
                    listener.notifyDeviceNearGym(gymLocation);
                }

                likelyPlaces.release();
            }
        });
    }

}
