package com.andb.apps.corners

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.overlay.view.*


const val NOTIFICATION_ID = 1

class CornerService : Service() {

    private var notifManager: NotificationManager? = null

    private lateinit var mView: View
    private lateinit var windowManager: WindowManager


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun onCreate() {
        super.onCreate()
        cornerService = this

        if (notifManager == null) {
            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        val notifBuilder: Notification.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Check if notification channel exists and if not create one
            var notificationChannel: NotificationChannel? = notifManager!!.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                notificationChannel = createNotificationChannel()
                notifManager!!.createNotificationChannel(notificationChannel)
            }
            notifBuilder = Notification.Builder(this, channelId)

        } else {
            @Suppress("DEPRECATION")
            notifBuilder = Notification.Builder(this)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = notifBuilder.setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_text))
            .setSmallIcon(R.drawable.app_icon_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        Log.d("serviceStart", "service started")


        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val inflater = LayoutInflater.from(this@CornerService)

        mView = inflater.inflate(R.layout.overlay, null)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(applicationContext)) {
                refreshOverlay()
            } else {
                Toast.makeText(applicationContext, R.string.permission_service_sync_error_text, Toast.LENGTH_LONG).show()
            }
        } else {
            refreshOverlay()
        }

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)


        try {
            windowManager.removeViewImmediate(mView)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, R.string.permission_service_sync_error_text, Toast.LENGTH_LONG)
                .show()
            e.printStackTrace()
        }




        Log.d("configChange", "config changed, restarting overlay")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(applicationContext)) {
                refreshOverlay()
            }
        } else {
            refreshOverlay()
        }

    }


    override fun onDestroy() {

        //bad code but prevents settings errors that I think might happen
        try {
            windowManager.removeViewImmediate(mView)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cornerService = null
    }


    fun refreshOverlay() {
        val screenSize = getRealScreenSize(this)

        val height = screenSize.y
        val width = screenSize.x

        Log.d("cwNavBar", "${(landscapeFixDefault() xor Values.landscapeFix) && windowManager.defaultDisplay.rotation == Surface.ROTATION_270}")

        val params = getParams(width, height)

        params.gravity = Gravity.TOP or Gravity.START

        //when the navigation bar is on the left, the x is at the edge of the bar, not the edge of the screen, so it needs to be moved back
        params.x = if ((landscapeFixDefault() xor Values.landscapeFix) && windowManager.defaultDisplay.rotation == Surface.ROTATION_270) -getNavigationBarSize(applicationContext).x else 0
        params.y = 0

        setCorners()
        if(mView.parent == null){
            windowManager.addView(mView, params)
        }else{
            windowManager.updateViewLayout(mView, params)
        }

    }

    private fun setCorners() {
        val cornerViews: List<TextView> = with(mView) {
            listOf(topLeft, topRight, bottomLeft, bottomRight)
        }
        cornerViews.forEachIndexed { i, view ->
            val corner = Values.corners[i]

            val scale = view.context.resources.displayMetrics.density
            val size = (corner.size * scale + 0.5f).toInt()

            view.width = size
            view.height = size
            view.visibility = if (corner.visible) View.VISIBLE else View.GONE
            view.background.mutate().setColorFilter(corner.color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun getParams(width: Int, height: Int): WindowManager.LayoutParams {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams(
                width, height, 0, 0,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                PixelFormat.TRANSLUCENT
            )
        } else {
            return WindowManager.LayoutParams(
                width, height, 0, 0, WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
        }
    }

    companion object {
        var cornerService: CornerService? = null
    }

}


