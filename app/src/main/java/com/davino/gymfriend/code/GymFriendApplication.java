package com.davino.gymfriend.code;

import android.app.Application;
import android.content.Intent;

import com.davino.gymfriend.services.BackgroundService;

/**
 * Created by davin on 31/05/2017.
 */

public class GymFriendApplication extends Application{

   @Override
    public void onCreate(){
       super.onCreate();
       startService(new Intent(this, BackgroundService.class));
   }
}
