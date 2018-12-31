package com.andb.apps.corners

import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.jaredrummler.android.colorpicker.ColorPanelView
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {

    var individualCollapse = true
    private val REQUEST_CODE: Int = 34387

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadValues()
        setupWindow()
        setupContent()
    }

    private fun setupWindow() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.overflowIcon?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        window.navigationBarColor = resources.getColor(R.color.colorAccent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = resources.getColor(R.color.colorAccent)
        }

    }

    private fun loadValues() {
        checkDrawOverlayPermission()
        Values.size = Persist.getSavedCornerSize(this)
        Values.toggleState = Persist.getSavedToggleState(this)
        Persist.getIndividualState(this)
        Values.cornerColor = Persist.getSavedCornerColor(this)
        Values.firstRun = Persist.getSavedFirstRun(this)
    }

    private fun setupContent() {
        overlay_toggle.isChecked = Values.toggleState
        currentVal.text = Values.size.toString()
        seekBar.progress = Values.size

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Values.size = progress
                CornerService.size = Values.size
                currentVal.text = Values.size.toString()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this@MainActivity)) {
                        Log.d("change size", "change size")
                        CornerService.setSize(this@MainActivity)
                    } else {
                        Toast.makeText(this@MainActivity, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    CornerService.setSize(this@MainActivity)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        overlay_toggle.setOnCheckedChangeListener { _, isChecked ->
            val serviceIntent = Intent(this, CornerService::class.java)
            Values.toggleState = isChecked
            when(isChecked){
                true->{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(this)) {
                            Log.d("serviceStart", "service started")
                            startService(serviceIntent)
                            if(Values.firstRun){
                                showTutorial()
                                Values.firstRun = false
                            }
                        } else {
                            checkDrawOverlayPermission()
                            overlay_toggle.isChecked = false
                        }
                    } else {
                        startService(serviceIntent)
                    }
                }
                false->{
                    stopService(serviceIntent)
                }
            }
        }


        val scale = resources.displayMetrics.density
        val pixels = (52 * scale + 0.5f).toInt()
        val params = individual_card.layoutParams
        params.height = pixels
        collapseToggleSpace.setOnClickListener {
            if (individualCollapse) {

                TransitionManager.beginDelayedTransition(individual_card, TransitionSet()
                        .addTransition(ChangeBounds()))
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                individual_card.layoutParams = params

                collapseButton.animate().setDuration(100).rotation(0f)

            } else {
                TransitionManager.beginDelayedTransition(individual_card, TransitionSet()
                        .addTransition(ChangeBounds()))
                params.height = pixels
                individual_card.layoutParams = params
                collapseButton.animate().setDuration(100).rotation(180f)
            }
            individualCollapse = !individualCollapse
        }


        switchTopL.setOnCheckedChangeListener { buttonView, isChecked ->
            Values.cornerStates[0] = isChecked
        }
        switchTopR.setOnCheckedChangeListener { buttonView, isChecked ->
            Values.cornerStates[1] = isChecked
        }
        switchBottomL.setOnCheckedChangeListener{buttonView, isChecked ->
            Values.cornerStates[2] = isChecked
        }
        switchBottomR.setOnCheckedChangeListener { buttonView, isChecked ->
            Values.cornerStates[3] = isChecked
        }
        //TODO: Set in cornerservice
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.tutorial_from_menu) {
            showTutorial()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun showTutorial() {
        Log.d("popupWindow", "clicked")

        val inflater = LayoutInflater.from(this)
        val tutorialView = inflater.inflate(R.layout.remove_notif_tutorial, null, false)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(tutorialView)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        val ok = tutorialView.findViewById<TextView>(R.id.killText)
        ok.setOnClickListener { alertDialog.hide() }
    }


    //Request screenOverlay permission
    @TargetApi(Build.VERSION_CODES.M)
    fun checkDrawOverlayPermission() {
        /* check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(applicationContext)) {
            /* if not construct intent to request permission */
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            /* request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /* if so check once again if we have permission */
            if (!Settings.canDrawOverlays(applicationContext)) {
                Toast.makeText(this, "Overlay permision not granted!", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        when (dialogId) {
            DIALOG_ID -> {
                Log.d("colorSelected", Integer.toHexString(color))
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Values.cornerColor = color
                val colorPanelView = findViewById<View>(R.id.tagColorPreview) as ColorPanelView
                colorPanelView.color = color
                CornerService.setColor(Values.cornerColor)
            }
        }
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun onPause() {
        super.onPause()
        Persist.saveCornerSize(this, Values.size)
        Persist.saveToggleState(this, Values.toggleState)
        Persist.saveIndivdualState(this, Values.cornerStates[0], Values.cornerStates[1], Values.cornerStates[2], Values.cornerStates[3])
        Persist.saveCornerColor(this, Values.cornerColor)
        Persist.saveFirstRun(this, Values.firstRun)
    }

    companion object {
        val DIALOG_ID = 0

        fun setIndividualVisibility() {
            if (CornerService.mView != null) {
                val topLeft = CornerService.mView!!.findViewById<View>(R.id.topLeft) as TextView
                val topRight = CornerService.mView!!.findViewById<View>(R.id.topRight) as TextView
                val bottomLeft = CornerService.mView!!.findViewById<View>(R.id.bottomLeft) as TextView
                val bottomRight = CornerService.mView!!.findViewById<View>(R.id.bottomRight) as TextView

                val views = ArrayList(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight))

                for (i in views.indices) {
                    if (Values.cornerStates[i]) {
                        views[i].visibility = View.VISIBLE
                    } else {
                        views[i].visibility = View.GONE
                    }
                }
            }
        }
    }
}