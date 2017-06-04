package com.davino.gymfriend.util;

import android.util.Log;

import com.davino.gymfriend.interfaces.IContextAwerenessCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

/**
 * Created by davin on 03/06/2017.
 */

public class LocationHelper {

    private static final String TAG = "LocationHelper";
    private IContextAwerenessCallback mContext;
    private GoogleApiClient mGoogleApiClient;

    public LocationHelper(IContextAwerenessCallback context, GoogleApiClient apiClient){
        mContext = context;
        mGoogleApiClient = apiClient;
    }

    public void checkIfNearGym() {
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        PendingResult<PlaceLikelihoodBuffer> result = com.google.android.gms.location.places.Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
         result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                Log.i(TAG, "RESULTED COMINGG");
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                            if(placeLikelihood.getLikelihood() > Constants.MINIUM_LIKELYHOOD_CONFIDENCE
                                && placeLikelihood.getPlace().getPlaceTypes().indexOf(Place.TYPE_GYM) != -1){

                                mContext.notifyDeviceNearGym();
                                break;
                            }
                }

                likelyPlaces.release();
            }
        });
    }

}
