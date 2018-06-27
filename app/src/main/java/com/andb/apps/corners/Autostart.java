package com.andb.apps.corners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Autostart extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent arg1) {

        MainActivityFragment.size = MainActivityFragment.getSavedCornerSize(context);

        if(MainActivityFragment.getSavedToggleState(context)) {

            Intent intent = new Intent(context, CornerService.class);
            context.startService(intent);
            Log.i("Autostart", "started");
        }
    }
}
