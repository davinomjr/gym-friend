package com.davino.gymfriend.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import android.os.Process;

/**
 * Created by davin on 31/05/2017.
 */

public class BackgroundService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("TutorialService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();

        mServiceHandler = new ServiceHandler(mServiceLooper);
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);


        startForeground(1, b.setOngoing(true)
                .setContentTitle("teste")
                .setContentText("teste content")
                .setSmallIcon(android.R.drawable.stat_sys_download).build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);
        return START_STICKY;
    }

    protected void showToast(final String msg){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            while(true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                showToast("Finishing TutorialService, id: " + msg.arg1);
                //stopSelf(msg.arg1);

            }
        }
    }

}
