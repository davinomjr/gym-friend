package com.davino.gymfriend.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by davin on 04/06/2017.
 */

public class DateHelper {

    public static String getCurrentDateTimeHour(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date currentDateTime = Calendar.getInstance().getTime();
        return dateFormat.format(currentDateTime);
    }
}
