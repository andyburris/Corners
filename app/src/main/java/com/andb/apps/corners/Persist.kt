package com.andb.apps.corners

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

object Persist {

    lateinit var prefs: SharedPreferences
    fun init(ctxt: Context){
        if(!::prefs.isInitialized) {
            prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        }
    }

    fun listen(listener: SharedPreferences.OnSharedPreferenceChangeListener){
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unListen(listener: SharedPreferences.OnSharedPreferenceChangeListener){
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun saveCornerSizes(list: ArrayList<Int>) {
        saveCornerSizes(list[0], list[1], list[2], list[3])
    }

    fun saveCornerSizes(topL: Int, topR: Int, botL: Int, botR: Int) {
        val editor = prefs.edit()
        editor.putInt("topLSize", topL)
        editor.putInt("topRSize", topR)
        editor.putInt("botLSize", botL)
        editor.putInt("botRSize", botR)
        editor.apply()
    }

    fun getIndividualSizes(): ArrayList<Int> {
        val sizes = ArrayList<Int>()

        prefs.apply {
            sizes.add(if (contains("topL")) getInt("topLSize", getOldSavedCornerSize()) else DEFAULT_SIZE)
            sizes.add(if (contains("topR")) getInt("topRSize", getOldSavedCornerSize()) else DEFAULT_SIZE)
            sizes.add(if (contains("botL")) getInt("botLSize", getOldSavedCornerSize()) else DEFAULT_SIZE)
            sizes.add(if (contains("botR")) getInt("botRSize", getOldSavedCornerSize()) else DEFAULT_SIZE)
        }

        Log.d("loadSizes", "sizes.size = ${sizes.size}")

        return sizes
    }

    fun getOldSavedCornerSize(): Int {
        return if (prefs.contains("corner_size")) {
            prefs.getInt("corner_size", DEFAULT_SIZE)
        } else {
            DEFAULT_SIZE
        }
    }

    internal fun saveToggleState(toggleState: Boolean) {
        Log.d("saveToggle", "Saving as " + java.lang.Boolean.toString(toggleState))
        val editor = prefs.edit()
        editor.putBoolean("toggle_state", toggleState)
        editor.apply()
    }

    fun getSavedToggleState(): Boolean {
        Log.d("saveToggle", "Loading toggle state")
        return if (prefs.contains("toggle_state"))
            prefs.getBoolean("toggle_state", true)
        else
            false
    }

    internal fun saveIndivdualState(topL: Boolean, topR: Boolean, botL: Boolean, botR: Boolean) {
        val editor = prefs.edit()

        editor.putBoolean("topL", topL)
        editor.putBoolean("topR", topR)
        editor.putBoolean("botL", botL)
        editor.putBoolean("botR", botR)


        editor.apply()

    }

    fun getIndividualState(): ArrayList<Boolean> {
        val cornerStates = ArrayList<Boolean>()
        cornerStates.add(if (prefs.contains("topL")) prefs.getBoolean("topL", true) else DEFAULT_TOGGLE)
        cornerStates.add(if (prefs.contains("topR")) prefs.getBoolean("topR", true) else DEFAULT_TOGGLE)
        cornerStates.add(if (prefs.contains("botL")) prefs.getBoolean("botL", true) else DEFAULT_TOGGLE)
        cornerStates.add(if (prefs.contains("botR")) prefs.getBoolean("botR", true) else DEFAULT_TOGGLE)
        return cornerStates
    }

    internal fun saveCornerColor(cornerColor: Int) {
        Log.d("saveColor", "Saving as " + Integer.toHexString(cornerColor))
        val editor = prefs.edit()
        editor.putInt("corner_color", cornerColor)
        editor.apply()
    }

    fun getSavedCornerColor(): Int {
        Log.d("loadColor", "Loading color")
        return if (prefs.contains("corner_color"))
            prefs.getInt("corner_color", -16777216)
        else
            -16777216
    }

    fun saveFirstRun(firstRun: Boolean){
        val editor = prefs.edit()
        editor.putBoolean("first_run", firstRun)
        editor.apply()
    }
    fun getSavedFirstRun(): Boolean{
        return if (prefs.contains("first_run"))
            prefs.getBoolean("first_run", true)
        else
            true
    }
}
