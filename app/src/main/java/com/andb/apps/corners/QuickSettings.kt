package com.andb.apps.corners

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettings : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        Persist.init(applicationContext)
        update()
    }

    override fun onClick() {
        super.onClick()
        val serviceIntent = Intent(this, CornerService::class.java)
        Log.d("qsTile", "clicked")
        if(checkDrawOverlayPermission()) {
            Log.d("qsTile", "can draw")
            Values.toggleState = !Values.toggleState
            if (Values.toggleState) {
                startService(serviceIntent)
            } else {
                stopService(serviceIntent)
            }
            Persist.saveToggleState(Values.toggleState)
            update()
        }else{
            Log.d("qsTile", "can't draw")
            Toast.makeText(applicationContext, R.string.permission_not_granted, Toast.LENGTH_LONG).show()
        }

    }

    fun update(){
        Values.toggleState = Persist.getSavedToggleState()
        val state = if (Values.toggleState && checkDrawOverlayPermission()){
            Tile.STATE_ACTIVE
        }else{
            Tile.STATE_INACTIVE
        }
        val tile = qsTile
        tile.state = state
        tile.updateTile()
    }

    fun checkDrawOverlayPermission() : Boolean{
        /* check if we already  have permission to draw over other apps */
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(applicationContext)

    }


}