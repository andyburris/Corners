package com.andb.apps.corners

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class Autostart : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        when(intent.action){
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED->{

                Persist.init(context)

                Values.corners = Persist.getCorners()

                if (Persist.getSavedToggleState()) {

                    val serviceIntent = Intent(context, CornerService::class.java)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                    Log.i("Autostart", "started")
                }
            }
        }

    }
}
