package com.andb.apps.corners;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;


public class CornerService extends JobIntentService {

    static View mView;

    public LayoutInflater inflater;
    public static WindowManager windowManager;
    static WindowManager.LayoutParams params;

    public static int size;

    Notification notification;
    private NotificationManager notifManager;

    static boolean first;

    public static int NOTIFICATION_ID = 1;
    public static String channelId = "default_channel_id";


    @Override
    protected void onHandleWork(@NonNull Intent intent){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        first = false;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelDescription = "Corners";
            //Check if notification channel exists and if not create one
            NotificationChannel notificationChannel = notifManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.BLACK);
                notificationChannel.enableVibration(true);
                notifManager.createNotificationChannel(notificationChannel);
                first = true;
            }


            notification = new Notification.Builder(this, channelId)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_text))
                    .setSmallIcon(R.drawable.ic_notif)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_text))
                    .setContentIntent(pendingIntent)
                    .build();
        }


        startForeground(NOTIFICATION_ID, notification);

        Log.d("serviceStart", "service started");

        size = MainActivityFragment.size;




        /*WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;*/


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        inflater = LayoutInflater.from(CornerService.this);

        mView = inflater.inflate(R.layout.overlay, null);
        setSize(getApplicationContext());

        Point screenSize = getRealScreenSize(this);


        int height = screenSize.y;
        int width = screenSize.x;

        startOverlay(height, width);


    }

    public static void startOverlay(int height, int width) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(width, height

                    /*ViewGroup.LayoutParams.MATCH_PARENT*/, 0, 0,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(width,
                    height, 0, 0
                    , WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;

        windowManager.addView(mView, params);

        Log.d("popupWindowService", Boolean.toString(first));

        if (first) {
            MainActivityFragment.showTutorial();



        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Point screenSize = getRealScreenSize(this);

        windowManager.removeViewImmediate(mView);


        int height = screenSize.y;
        int width = screenSize.x;

        startOverlay(height, width);

    }


    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

            display.getRealSize(size);


        return size;
    }

    public static void setSize(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;

        int dpAdjust = (int) (size * scale + 0.5f);

        TextView topLeft = (TextView) mView.findViewById(R.id.topLeft);
        TextView topRight = (TextView) mView.findViewById(R.id.topRight);
        TextView bottomLeft = (TextView) mView.findViewById(R.id.bottomLeft);
        TextView bottomRight = (TextView) mView.findViewById(R.id.bottomRight);

        topLeft.setWidth(dpAdjust);
        topLeft.setHeight(dpAdjust);
        topRight.setWidth(dpAdjust);
        topRight.setHeight(dpAdjust);
        bottomLeft.setWidth(dpAdjust);
        bottomLeft.setHeight(dpAdjust);
        bottomRight.setWidth(dpAdjust);
        bottomRight.setHeight(dpAdjust);

    }

    @Override
    public void onDestroy() {
        windowManager.removeViewImmediate(mView);
    }
}


