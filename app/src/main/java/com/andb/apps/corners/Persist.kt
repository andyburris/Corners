package com.andb.apps.corners

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

object Persist {

    internal fun saveCornerSize(ctxt: Context, size: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        val editor = prefs.edit()
        editor.putInt("corner_size", size)
        editor.apply()
    }

    fun getSavedCornerSize(ctxt: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        return if (prefs.contains("corner_size"))
            prefs.getInt("corner_size", 12)
        else
            12
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

    fun getIndividualState(ctxt: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        if (prefs.contains("topL")) {
            Values.cornerStates[0] = prefs.getBoolean("topL", true)
        }
        if (prefs.contains("topR")) {
            Values.cornerStates[1] = prefs.getBoolean("topR", true)
        }
        if (prefs.contains("botL")) {
            Values.cornerStates[2]  = prefs.getBoolean("botL", true)
        }
        if (prefs.contains("botR")) {
            Values.cornerStates[3]  = prefs.getBoolean("botR", true)
        }
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
