package com.andb.apps.corners;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jaredrummler.android.colorpicker.ColorPanelView;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import static com.andb.apps.corners.MainActivityFragment.DIALOG_ID;

public class MainActivity extends AppCompatActivity implements ColorPickerDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.getOverflowIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorAccent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tutorial_from_menu) {
            MainActivityFragment.showTutorial();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case MainActivityFragment.DIALOG_ID:
                Log.d("colorSelected", Integer.toHexString(color));
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                MainActivityFragment.cornerColor = color;
                ColorPanelView colorPanelView = (ColorPanelView) findViewById(R.id.tagColorPreview);
                colorPanelView.setColor(color);
                /*Toast.makeText(CreateTag.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setBackgroundTintList(ColorStateList.valueOf(tagColor));
                tagNameEdit.clearFocus();
                setInputTextLayoutColor(tagColor, tagNameEdit);

                if (subFolderSwitch.isChecked()) {
                    subFolderSwitch.getThumbDrawable().setColorFilter(tagColor, PorterDuff.Mode.MULTIPLY);
                    subFolderSwitch.getTrackDrawable().setColorFilter(tagColor, PorterDuff.Mode.MULTIPLY);
                */

                CornerService.setColor(MainActivityFragment.cornerColor);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}