package com.andb.apps.corners

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast


class CornerService : Service() {


    private var notifManager: NotificationManager? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notification: Notification

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        if (notifManager == null) {
            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelDescription = "Corners"
            //Check if notification channel exists and if not create one
            var notificationChannel: NotificationChannel? = notifManager!!.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                notificationChannel = NotificationChannel(channelId, channelDescription, importance)
                notificationChannel.lightColor = Color.BLACK
                notificationChannel.enableVibration(true)
                notifManager!!.createNotificationChannel(notificationChannel)
            }


            notification = Notification.Builder(this, channelId)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_text))
                    .setSmallIcon(R.drawable.ic_notif)
                    .setContentIntent(pendingIntent)
                    .build()
        } else {
            notification = Notification.Builder(this)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_text))
                    .setContentIntent(pendingIntent)
                    .build()
        }


        startForeground(NOTIFICATION_ID, notification)

        Log.d("serviceStart", "service started")

        sizes = Values.sizes


        /*WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;*/


        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val inflater = LayoutInflater.from(this@CornerService)

        mView = inflater.inflate(R.layout.overlay, null)
        setSize(applicationContext)
        setColor(Values.cornerColor)

        val screenSize = getRealScreenSize(this)


        val height = screenSize.y
        val width = screenSize.x


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(applicationContext)) {
                startOverlay(height, width)
            } else {
                Toast.makeText(applicationContext, R.string.permission_service_sync_error_text, Toast.LENGTH_LONG).show()
            }
        } else {
            startOverlay(height, width)
        }

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val screenSize = getRealScreenSize(this)

        try {
            windowManager.removeViewImmediate(mView)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, R.string.permission_service_sync_error_text, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }


        val height = screenSize.y
        val width = screenSize.x

        Log.d("configchange", "config changed, restarting overlay")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(applicationContext)) {
                startOverlay(height, width)
            }
        } else {
            startOverlay(height, width)
        }

    }


    override fun onDestroy() {

        //bad code but prevents settings errors that I think might happen

        try {
            windowManager.removeViewImmediate(mView)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        var mView: View? = null
        lateinit var windowManager: WindowManager
        lateinit var params: WindowManager.LayoutParams

        var sizes = arrayListOf(12, 12, 12, 12)

        const val NOTIFICATION_ID = 1
        const val channelId = "default_channel_id"

        fun startOverlay(height: Int, width: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params = WindowManager.LayoutParams(width, height, 0, 0,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                or WindowManager.LayoutParams.FLAG_FULLSCREEN
                                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT)
            } else {
                params = WindowManager.LayoutParams(width,
                        height, 0, 0, WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                or WindowManager.LayoutParams.FLAG_FULLSCREEN
                                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT)
            }

            params.gravity = Gravity.TOP or Gravity.START
            params.x = 0
            params.y = 0

            windowManager.addView(mView, params)

            MainActivity.setIndividualVisibility()
            setColor(Values.cornerColor)
        }


        fun getRealScreenSize(context: Context): Point {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getRealSize(size)
            return size
        }

        fun setSize(context: Context) {
            if (mView != null) {
                val scale = context.resources.displayMetrics.density

                val dpAdjustTopL = (sizes[0] * scale + 0.5f).toInt()
                val dpAdjustTopR = (sizes[1] * scale + 0.5f).toInt()
                val dpAdjustBotL = (sizes[2] * scale + 0.5f).toInt()
                val dpAdjustBotR = (sizes[3] * scale + 0.5f).toInt()


                val topLeft = mView!!.findViewById<TextView>(R.id.topLeft)
                val topRight = mView!!.findViewById<TextView>(R.id.topRight)
                val bottomLeft = mView!!.findViewById<TextView>(R.id.bottomLeft)
                val bottomRight = mView!!.findViewById<TextView>(R.id.bottomRight)

                topLeft.width = dpAdjustTopL
                topLeft.height = dpAdjustTopL
                topRight.width = dpAdjustTopR
                topRight.height = dpAdjustTopR
                bottomLeft.width = dpAdjustBotL
                bottomLeft.height = dpAdjustBotL
                bottomRight.width = dpAdjustBotR
                bottomRight.height = dpAdjustBotR
            }

        }

        fun setColor(color: Int) {

            Log.d("setColor", "setting color: " + Integer.toHexString(color))
            if (mView != null) {

                val topLeft = mView!!.findViewById<TextView>(R.id.topLeft)
                val topRight = mView!!.findViewById<TextView>(R.id.topRight)
                val bottomLeft = mView!!.findViewById<TextView>(R.id.bottomLeft)
                val bottomRight = mView!!.findViewById<TextView>(R.id.bottomRight)


                topLeft.background.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
                topRight.background.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
                bottomLeft.background.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
                bottomRight.background.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)


                Log.d("setColor", "done setting color")
            }

        }
    }
}


