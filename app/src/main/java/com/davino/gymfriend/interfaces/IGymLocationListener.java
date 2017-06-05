package com.davino.gymfriend.interfaces;

import com.davino.gymfriend.model.LocationHistory;

/**
 * Created by davin on 04/06/2017.
 */

public interface IGymLocationListener {
    void notifyDeviceNearGym(LocationHistory location);
}
