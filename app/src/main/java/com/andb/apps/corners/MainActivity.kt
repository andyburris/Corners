package com.andb.apps.corners

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.color_preview_layout.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_custom_value.view.*

class MainActivity : AppCompatActivity() {

    private var moreCollapse = true
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        Log.d("pref changed", "key: $key")
        when (key) {
            "toggle_state" -> overlay_toggle.isChecked = prefs.getBoolean(key, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Persist.init(this)
        setContentView(R.layout.activity_main)
        loadValues()
        setupWindow()
        setupContent()
        setupTile()
    }

    private fun setupWindow() {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.overflowIcon?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

    }

    private fun loadValues() {
        checkDrawOverlayPermission()
        Values.toggleState = Persist.getSavedToggleState()
        Values.corners = Persist.getCorners()
        Values.firstRun = Persist.getSavedFirstRun()
    }

    private fun setupContent() {
        overlay_toggle.isChecked = Values.toggleState
        currentVal.text = Values.commonSize().toString()
        currentVal.setOnClickListener { sizeDialog(-1) }
        sizeBar.progress = Values.commonSize()

        sizeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    Values.corners.forEach {
                        it.size = progress
                    }
                    updateService()
                    save()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        overlay_toggle.setOnCheckedChangeListener { _, isChecked ->
            val serviceIntent = Intent(this, CornerService::class.java)
            Values.toggleState = isChecked
            if (isChecked) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        Log.d("serviceStart", "service started")
                        startService(serviceIntent)
                        if (Values.firstRun) {
                            showHelp()
                            Values.firstRun = false
                            Persist.saveFirstRun(Values.firstRun)
                        }
                    } else {
                        checkDrawOverlayPermission()
                        overlay_toggle.isChecked = false
                    }
                } else {
                    startService(serviceIntent)
                }
            } else {
                stopService(serviceIntent)
            }
            saveToggle()
        }


        val pixels = dpToPx(52)
        val params = individual_card.layoutParams
        params.height = pixels
        collapseToggleSpace.setOnClickListener {
            TransitionManager.beginDelayedTransition(individual_card, TransitionSet().addTransition(ChangeBounds()))
            if (moreCollapse) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                individual_card.layoutParams = params

                collapseButton.animate().setDuration(100).rotation(0f)
            } else {
                params.height = pixels
                individual_card.layoutParams = params
                collapseButton.animate().setDuration(100).rotation(180f)
            }
            moreCollapse = !moreCollapse
        }

        allCornersColorLayout.colorPreview.color = Values.commonColor()
        allCornersColorLayout.setOnClickListener {
            colorDialog(Values.commonColor()) { color ->
                Values.corners.forEach {
                    it.color = color
                }
                allCornersColorLayout.colorPreview.color = color
                updateService()
            }
        }

        landscapeFixSwitch.isChecked = Values.landscapeFix
        landscapeFixSwitch.setOnCheckedChangeListener { _, isChecked ->
            Values.landscapeFix = isChecked
            Persist.saveLandscapeFix(Values.landscapeFix)
            updateService()
        }

        setupIndividual()

    }

    private fun setupIndividual() {
        switchTopL.isChecked = Values.corners[0].visible
        switchTopR.isChecked = Values.corners[1].visible
        switchBottomL.isChecked = Values.corners[2].visible
        switchBottomR.isChecked = Values.corners[3].visible

        switchTopL.setOnCheckedChangeListener { _, isChecked ->
            Values.corners[0].visible = isChecked
            updateService()
            save()
        }
        switchTopR.setOnCheckedChangeListener { _, isChecked ->
            Values.corners[1].visible = isChecked
            updateService()
            save()
        }
        switchBottomL.setOnCheckedChangeListener { _, isChecked ->
            Values.corners[2].visible = isChecked
            updateService()
            save()
        }
        switchBottomR.setOnCheckedChangeListener { _, isChecked ->
            Values.corners[3].visible = isChecked
            updateService()
            save()
        }

        switchTopL.setOnLongClickListener { sizeDialog(0); true }
        switchTopR.setOnLongClickListener { sizeDialog(1); true }
        switchBottomL.setOnLongClickListener { sizeDialog(2); true }
        switchBottomR.setOnLongClickListener { sizeDialog(3); true }

    }

    @SuppressLint("InflateParams")//alert dialog
    private fun sizeDialog(index: Int) {

        val specific = index != -1
        val currentSize = if (specific) Values.corners[index].size else Values.commonSize()
        val name = when (index) {
            0 -> getString(R.string.top_left_toggle)
            1 -> getString(R.string.top_right_toggle)
            2 -> getString(R.string.bottom_left_toggle)
            3 -> getString(R.string.bottom_right_toggle)
            else -> getString(R.string.all_corners)
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_value, null)
        dialogView.customSizeEditText.setText(currentSize.toString())

        if (specific) {
            dialogView.dialogColorPreview.colorPreview.color = Values.corners[index].color
            dialogView.dialogColorPreview.visibility = View.VISIBLE
            dialogView.dialogColorPreview.setOnClickListener {
                colorDialog(Values.corners[index].color) { color ->
                    Values.corners[index].color = color
                    dialogView.dialogColorPreview.colorPreview.color = Values.corners[index].color
                    allCornersColorLayout.colorPreview.color = Values.commonColor()
                    updateService()
                }
            }
        } else {
            dialogView.dialogColorPreview.visibility = View.GONE
        }

        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = name)
            customView(view = dialogView, scrollable = true)
            positiveButton(R.string.dialog_ok) {
                val newSize = dialogView.customSizeEditText.text.toString().toIntOrNull()
                    ?: return@positiveButton
                if (specific) {
                    Values.corners[index].size = newSize
                } else {
                    Values.corners.forEach {
                        it.size = newSize
                    }
                }
                this@MainActivity.sizeBar.progress = Values.commonSize()

                updateService()
                save()
            }
            cornerRadius(Values.commonSize().toFloat())
        }
    }

    private fun colorDialog(currentColor: Int, callback: ((Int) -> Unit)) {

        val primaryPaletteWithBlack = ColorPalette.Primary
            .toMutableList()
            .also { it.add(0, Color.parseColor("#000000")) }
            .toIntArray()

        val subPaletteWithBlack = ColorPalette.PrimarySub
            .toMutableList()
            .also {
                it.add(
                    0, intArrayOf(
                        Color.parseColor("#FFFFFF"), Color.parseColor("#DDDDDD"), Color.parseColor("#BBBBBB"),
                        Color.parseColor("#999999"), Color.parseColor("#777777"), Color.parseColor("#555555"),
                        Color.parseColor("#333333"), Color.parseColor("#111111"), Color.parseColor("#000000")
                    )
                )
            }
            .toTypedArray()

        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.corner_color_string)
            colorChooser(
                primaryPaletteWithBlack,
                subPaletteWithBlack,
                initialSelection = currentColor,
                waitForPositiveButton = false,
                allowCustomArgb = true
            ) { _, color ->
                callback.invoke(color)
            }
            positiveButton(R.string.dialog_ok)
            cornerRadius(Values.commonSize().toFloat())
        }
    }

    private fun setupTile() {
        Persist.listen(prefsListener)
    }

    fun updateService() {
        currentVal.text = Values.commonSize().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this@MainActivity)) {
                CornerService.cornerService?.refreshOverlay()
            } else {
                Toast.makeText(this@MainActivity, R.string.permission_not_granted, Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            CornerService.cornerService?.refreshOverlay()
        }
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
        when (item.itemId) {
            R.id.menuItemHelp -> showHelp()
            R.id.menuItemAbout -> showAbout()
        }


        return super.onOptionsItemSelected(item)
    }

    private fun showHelp() {
        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(R.string.menu_item_help)
            customView(R.layout.help_dialog, scrollable = true)
            positiveButton(R.string.dialog_ok)
            cornerRadius(Values.commonSize().toFloat())
        }

    }

    private fun showAbout() {
        supportFragmentManager.beginTransaction()
            .add(R.id.aboutFragmentHolder, About(), "aboutScreen").addToBackStack("aboutScreen")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        aboutFragmentHolder.animate().alpha(1f)
        toolbar.setTitle(R.string.menu_item_about)
        toolbar.menu.setGroupVisible(R.id.main_menu_group, false)
    }

    private fun hideAbout(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            aboutFragmentHolder.animate().alpha(0f)
            toolbar.setTitle(R.string.app_name)
            toolbar.menu.setGroupVisible(R.id.main_menu_group, true)
            return true
        }
        return false
    }

    //Request screenOverlay permission
    @TargetApi(Build.VERSION_CODES.M)
    fun checkDrawOverlayPermission() {
        /* check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(applicationContext)) {
            /* if not construct intent to request permission */
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
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
                Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun save() {
        Persist.saveCorners(Values.corners)
    }

    private fun saveToggle() {
        Persist.saveToggleState(Values.toggleState)
    }

    override fun onBackPressed() {
        val close = !hideAbout()
        when {
            close -> super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        save()
        Persist.unListen(prefsListener)
    }

    companion object {
        const val REQUEST_CODE: Int = 34387
    }


}