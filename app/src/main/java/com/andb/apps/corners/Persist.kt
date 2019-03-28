package com.andb.apps.corners

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

object Persist {

    fun saveCornerSizes(ctxt: Context, list: ArrayList<Int>) {
        saveCornerSizes(ctxt, list[0], list[1], list[2], list[3])
    }

    fun saveCornerSizes(ctxt: Context, topL: Int, topR: Int, botL: Int, botR: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        val editor = prefs.edit()
        editor.putInt("topLSize", topL)
        editor.putInt("topRSize", topR)
        editor.putInt("botLSize", botL)
        editor.putInt("botRSize", botR)
        editor.apply()
    }

    fun getIndividualSizes(ctxt: Context): ArrayList<Int> {
        val sizes = ArrayList<Int>()
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)

        prefs.apply {
            sizes.add(if (contains("topL")) getInt("topLSize", getOldSavedCornerSize(ctxt)) else DEFAULT_SIZE)
            sizes.add(if (contains("topR")) getInt("topRSize", getOldSavedCornerSize(ctxt)) else DEFAULT_SIZE)
            sizes.add(if (contains("botL")) getInt("botLSize", getOldSavedCornerSize(ctxt)) else DEFAULT_SIZE)
            sizes.add(if (contains("botR")) getInt("botRSize", getOldSavedCornerSize(ctxt)) else DEFAULT_SIZE)
        }

        Log.d("loadSizes", "sizes.size = ${sizes.size}")

        return sizes
    }

    fun getOldSavedCornerSize(ctxt: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        return if (prefs.contains("corner_size")) {
            prefs.getInt("corner_size", DEFAULT_SIZE)
        } else {
            DEFAULT_SIZE
        }
    }

    internal fun saveToggleState(ctxt: Context, toggleState: Boolean) {
        Log.d("saveToggle", "Saving as " + java.lang.Boolean.toString(toggleState))
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        val editor = prefs.edit()
        editor.putBoolean("toggle_state", toggleState)
        editor.apply()
    }

    fun getSavedToggleState(ctxt: Context): Boolean {
        Log.d("saveToggle", "Loading toggle state")
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        return if (prefs.contains("toggle_state"))
            prefs.getBoolean("toggle_state", true)
        else
            false
    }

    internal fun saveIndivdualState(ctxt: Context, topL: Boolean, topR: Boolean, botL: Boolean, botR: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        val editor = prefs.edit()

        editor.putBoolean("topL", topL)
        editor.putBoolean("topR", topR)
        editor.putBoolean("botL", botL)
        editor.putBoolean("botR", botR)


        editor.apply()

    }

    fun getIndividualState(ctxt: Context): ArrayList<Boolean> {
        val cornerStates = ArrayList<Boolean>()
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        cornerStates.add(if (prefs.contains("topL")) prefs.getBoolean("topL", true) else DEFAULT_TOGGLE)
        cornerStates.add(if (prefs.contains("topR")) prefs.getBoolean("topR", true) else DEFAULT_TOGGLE)
        cornerStates.add(if (prefs.contains("botL")) prefs.getBoolean("botL", true) else DEFAULT_TOGGLE)
        cornerStates.add(if (prefs.contains("botR")) prefs.getBoolean("botR", true) else DEFAULT_TOGGLE)
        return cornerStates
    }

    internal fun saveCornerColor(ctxt: Context, cornerColor: Int) {
        Log.d("saveColor", "Saving as " + Integer.toHexString(cornerColor))
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        val editor = prefs.edit()
        editor.putInt("corner_color", cornerColor)
        editor.apply()
    }

    fun getSavedCornerColor(ctxt: Context): Int {
        Log.d("loadColor", "Loading color")
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        return if (prefs.contains("corner_color"))
            prefs.getInt("corner_color", -16777216)
        else
            -16777216
    }

    fun saveFirstRun(ctxt: Context, firstRun: Boolean){
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        val editor = prefs.edit()
        editor.putBoolean("first_run", firstRun)
        editor.apply()
    }
    fun getSavedFirstRun(ctxt: Context): Boolean{
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        return if (prefs.contains("first_run"))
            prefs.getBoolean("first_run", true)
        else
            true
    }
}
