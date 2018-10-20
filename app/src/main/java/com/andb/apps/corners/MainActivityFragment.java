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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static int size = 12;
    public boolean toggleState = false;

    public static boolean topLState = true;
    public static boolean topRState = true;
    public static boolean bottomLState = true;
    public static boolean bottomRState = true;


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
        getIndividualState(getActivity().getApplicationContext());


        super.onViewCreated(view, savedInstanceState);
        final TextView position = (TextView) view.findViewById(R.id.currentVal);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        final Switch overlayToggle = (Switch) view.findViewById(R.id.overlay_toggle);
        //final TextView tutorial = (TextView) view.findViewById(R.id.nideNotifTutorialLaunch);

        overlayToggle.setChecked(toggleState);
        seekBar.setProgress(size);
        position.setText(Integer.toString(size));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position.setText(Integer.toString(progress));
                size = progress;
                CornerService.size = size;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(getActivity().getApplicationContext())) {
                        Log.d("change size", "change size");
                        CornerService.setSize(getActivity().getApplicationContext());
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Overlay permission not granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    CornerService.setSize(getActivity().getApplicationContext());
                }
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
                            toggleState = isChecked;
                        } else {
                            checkDrawOverlayPermission();
                            overlayToggle.setChecked(false);
                        }
                    } else {
                        getActivity().startService(serviceIntent);
                        toggleState = isChecked;
                    }
                } else {
                    getActivity().stopService(serviceIntent);
                    toggleState = isChecked;
                }
                Log.d("popupWindow", Boolean.toString(CornerService.first));
                Log.d("popupWindow", Boolean.toString(isChecked));


            }


        });

        individualToggleCheck(view);


    }

    public void individualToggleCheck(View view) {
        CheckBox topL = view.findViewById(R.id.switchTopL);
        CheckBox topR = view.findViewById(R.id.switchTopR);
        CheckBox bottomL = view.findViewById(R.id.switchBottomL);
        CheckBox bottomR = view.findViewById(R.id.switchBottomR);

        topL.setChecked(topLState);
        topR.setChecked(topRState);
        bottomL.setChecked(bottomLState);
        bottomR.setChecked(bottomRState);





        topL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                topLState = isChecked;
                setIndividualVisibility();

            }
        });
        topR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("CheckChange", "topR");
                topRState = isChecked;
                setIndividualVisibility();

            }
        });
        bottomL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("CheckChange", "botL");
                bottomLState = isChecked;
                setIndividualVisibility();
            }
        });
        bottomR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("CheckChange", "botR");
                bottomRState = isChecked;
                setIndividualVisibility();

            }
        });


    }

    public static void setIndividualVisibility(){
        if(CornerService.mView!=null) {
            final TextView topLeft = (TextView) CornerService.mView.findViewById(R.id.topLeft);
            final TextView topRight = (TextView) CornerService.mView.findViewById(R.id.topRight);
            final TextView bottomLeft = (TextView) CornerService.mView.findViewById(R.id.bottomLeft);
            final TextView bottomRight = (TextView) CornerService.mView.findViewById(R.id.bottomRight);

            if(topLState){
                topLeft.setVisibility(View.VISIBLE);
            }else {
                topLeft.setVisibility(View.INVISIBLE);
            }

            if(topRState){
                topRight.setVisibility(View.VISIBLE);
            }else {
                topRight.setVisibility(View.INVISIBLE);
            }

            if(bottomLState){
                bottomLeft.setVisibility(View.VISIBLE);
            }else {
                bottomLeft.setVisibility(View.INVISIBLE);
            }

            if(bottomRState){
                bottomRight.setVisibility(View.VISIBLE);
            }else {
                bottomRight.setVisibility(View.INVISIBLE);
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity().getApplicationContext())) {
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
        Log.d("saveToggle", "onPause");
        saveCornerSize(getActivity().getApplicationContext(), size);
        saveToggleState(getActivity().getApplicationContext(), toggleState);
        saveIndivdualState(getActivity().getApplicationContext(), topLState, topRState, bottomLState, bottomRState);
    }

    public void saveCornerSize(Context ctxt, int size) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("corner_size", size);
        editor.apply();
    }

    public static int getSavedCornerSize(Context ctxt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if (prefs.contains("corner_size"))
            return prefs.getInt("corner_size", 12);
        else return 12;
    }

    public void saveToggleState(Context ctxt, boolean toggleState) {
        Log.d("saveToggle", "Saving as " + Boolean.toString(toggleState));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("toggle_state", toggleState);
        editor.apply();
    }

    public static boolean getSavedToggleState(Context ctxt) {
        Log.d("saveToggle", "Loading toggle state");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if (prefs.contains("toggle_state"))
            return prefs.getBoolean("toggle_state", true);
        else return false;
    }

    public void saveIndivdualState(Context ctxt, boolean topL, boolean topR, boolean botL, boolean botR){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("topL", topL);
        editor.putBoolean("topR", topR);
        editor.putBoolean("botL", botL);
        editor.putBoolean("botR", botR);


        editor.apply();

    }

    public static void getIndividualState(Context ctxt){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if(prefs.contains("topL")){
            topLState = prefs.getBoolean("topL", true);
        }
        if(prefs.contains("topR")){
            topRState = prefs.getBoolean("topR", true);
        }
        if(prefs.contains("botL")){
            bottomLState = prefs.getBoolean("botL", true);
        }
        if(prefs.contains("botR")){
            bottomRState = prefs.getBoolean("botR", true);
        }
    }
}
