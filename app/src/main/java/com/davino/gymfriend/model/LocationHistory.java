package com.davino.gymfriend.model;

import org.parceler.Parcel;

/**
 * Created by davin on 04/06/2017.
 */

@Parcel
public class LocationHistory extends BaseEntity {
    private String locationName;
    private String locationHistoryDate;

    public LocationHistory(){

    }

    public LocationHistory(String locationName, String locationHistoryDate){
        this.locationName = locationName;
        this.locationHistoryDate = locationHistoryDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationHistoryDate() {
        return locationHistoryDate;
    }

    public void setLocationHistoryDate(String locationHistoryDate) {
        this.locationHistoryDate = locationHistoryDate;
    }
}
