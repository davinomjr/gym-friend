package com.davino.gymfriend.util;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by davin on 03/06/2017.
 */

public class NotificationHelper {

    public void setNotification(Context context, String title, String contentText){

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setOngoing(true)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_dialog_info).build();
    }

}
