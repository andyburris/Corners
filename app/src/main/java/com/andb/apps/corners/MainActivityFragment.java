package com.andb.apps.corners;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static int size = 12;
    public boolean toggleState = true;

    public int REQUEST_CODE = 34387;

    public static Context ctxt1;
    public static Context ctxt2;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);


    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        checkDrawOverlayPermission();

        ctxt1 = getActivity().getApplicationContext();
        ctxt2 = getContext();

        size = getSavedCornerSize(getActivity().getApplicationContext());
        toggleState = getSavedToggleState(getActivity().getApplicationContext());

        super.onViewCreated(view, savedInstanceState);
        final TextView position = (TextView) view.findViewById(R.id.currentVal);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        Switch overlayToggle = (Switch) view.findViewById(R.id.overlay_toggle);
        //final TextView tutorial = (TextView) view.findViewById(R.id.nideNotifTutorialLaunch);

        seekBar.setProgress(size);
        position.setText(Integer.toString(size));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position.setText(Integer.toString(progress));
                size = progress;
                CornerService.size = size;
                CornerService.setSize(getActivity().getApplicationContext());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        boolean start = false;

        overlayToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            Intent serviceIntent = new Intent(getActivity(), CornerService.class);


            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(getActivity().getApplicationContext())) {
                            Log.d("serviceStart", "service started");
                            getActivity().startService(serviceIntent);
                        } else {
                            checkDrawOverlayPermission();
                        }
                    } else {
                        getActivity().startService(serviceIntent);
                    }
                } else {
                    getActivity().stopService(serviceIntent);
                }
                Log.d("popupWindow", Boolean.toString(CornerService.first));
                Log.d("popupWindow", Boolean.toString(isChecked));


            }


        });


    }

    public static void showTutorial() {
        Log.d("popupWindow", "clicked");

        LayoutInflater inflater = LayoutInflater.from(ctxt1);


        View tutorialView = inflater.inflate(R.layout.remove_notif_tutorial, null, false);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctxt2);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(tutorialView);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        TextView ok = (TextView) tutorialView.findViewById(R.id.killText);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.hide();
            }
        });
    }


    //Request screenOverlay permission
    @TargetApi(Build.VERSION_CODES.M)
    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(getActivity().getApplicationContext())) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /** if so check once again if we have permission */
            if (Settings.canDrawOverlays(getActivity().getApplicationContext())) {
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        saveCornerSize(getActivity().getApplicationContext(), size);
        saveToggleState(getActivity().getApplicationContext(), toggleState);
    }

    public void saveCornerSize(Context ctxt, int size) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("corner_size", size);
        editor.apply();
    }

    public int getSavedCornerSize(Context ctxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if (prefs.contains("corner_size"))
            return prefs.getInt("corner_size", 12);
        else return 12;
    }

    public void saveToggleState(Context ctxt, boolean toggleState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("toggle_state", toggleState);
        editor.apply();
    }

    public boolean getSavedToggleState(Context ctxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if (prefs.contains("toggle_state"))
            return prefs.getBoolean("toggle_state", true);
        else return true;
    }
}
