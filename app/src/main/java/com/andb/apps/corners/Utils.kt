package com.andb.apps.corners

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView

fun getNavigationBarSize(context: Context): Point {
    val appUsableSize = getAppUsableScreenSize(context)
    val realScreenSize = getRealScreenSize(context)

    // navigation bar on the right
    if (appUsableSize.x < realScreenSize.x) {
        return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
    }

    // navigation bar at the bottom
    return if (appUsableSize.y < realScreenSize.y) {
        Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
    } else Point()

    // navigation bar is not present
}

fun getAppUsableScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}


fun getRealScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getRealSize(size)
    Log.d("screenSize", "x: ${size.x}, y: ${size.y}")
    return size
}

const val channelId = "corners_channel_id"
const val channelDescription = "Corners"

@TargetApi(26)
fun createNotificationChannel(): NotificationChannel {
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val notificationChannel = NotificationChannel(channelId, channelDescription, importance)
    notificationChannel.lightColor = Color.BLACK
    notificationChannel.enableVibration(true)
    return notificationChannel
}

fun <A, B> List<Pair<A, B>>.forEachFlat(action: (first: A, second: B) -> Unit) {
    this.forEach {
        action.invoke(it.first, it.second)
    }
}

fun ImageView.setColor(color: Int){
    this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun dpToPx(dp: Int): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dp * scale).toInt()
}