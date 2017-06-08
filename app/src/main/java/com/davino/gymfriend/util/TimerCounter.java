package com.davino.gymfriend.util;

/**
 * Created by davin on 05/06/2017.
 */
public class TimerCounter
{
    private static final String TAG = TimerCounter.class.getName();

    private long timer;

    public TimerCounter(){
    }

    public void startCounting(){
        timer = 0;
        timer = System.currentTimeMillis();
    }

    public void stop(){
        timer = 0;
    }

    public int checkHowManySecondsPassed(){
        return timer > 0 ? (int) (System.currentTimeMillis() - timer) / 1000 : 0;
    }

    public boolean isStopped(){
        return timer == 0;
    }
}
