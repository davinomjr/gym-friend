package com.davino.gymfriend.model;

import org.parceler.Parcel;

/**
 * Created by davin on 07/06/2017.
 */

@Parcel
public class SleepingTime extends BaseEntity {

    private String sleepingTime;

    public SleepingTime(){}

    public SleepingTime(String sleepingTime){
        this.sleepingTime = sleepingTime;
    }


    public String getSleepingTime() {
        return sleepingTime;
    }

    public void setSleepingTime(String sleepingTime) {
        this.sleepingTime = sleepingTime;
    }
}
