package com.andb.apps.corners

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log


object Persist {
    private val cornerNames = listOf("topL", "topR", "botL", "botR")

    private lateinit var prefs: SharedPreferences

    fun init(ctxt: Context) {
        if (!::prefs.isInitialized) {
            prefs = PreferenceManager.getDefaultSharedPreferences(ctxt)
        }
    }

    fun listen(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unListen(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun saveCorners(list: List<Corner>) {
        val cornerPairs = list.take(4).mapIndexed { i, corner -> Pair(corner, cornerNames[i]) }
        val editor = prefs.edit()
        cornerPairs.forEachFlat { corner, name ->
            editor.putInt("${name}Size", corner.size)
            editor.putBoolean("${name}State", corner.visible)
            editor.putInt("${name}Color", corner.color)
            editor.apply()
        }

    }

    fun getCorners(): ArrayList<Corner> {
        val corners = ArrayList<Corner>()
        for (i in 0 until 4) {
            val name = cornerNames[i]
            val corner = with(prefs) {
                val size = getInt("${name}Size", getOldSavedCornerSize())
                val visible = getBoolean("${name}State", getOldCornerState(i))
                val color = getInt("${name}Color", getOldColor())
                Corner(size, visible, color)
            }
            corners.add(corner)
        }
        return corners
    }


    private fun getOldSavedCornerSize(): Int {
        return when {
            prefs.contains("corner_size") -> prefs.getInt("corner_size", DEFAULT_SIZE)
            else -> DEFAULT_SIZE
        }
    }


    private fun getOldCornerState(i: Int): Boolean {
        val name = cornerNames[i]
        return prefs.getBoolean(name, DEFAULT_TOGGLE)
    }


    private fun getOldColor(): Int {
        Log.d("loadColor", "Loading color")
        return prefs.getInt("corner_color", -16777216)
    }

    fun saveToggleState(toggleState: Boolean) {
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

    fun saveFirstRun(firstRun: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean("first_run", firstRun)
        editor.apply()
    }

    fun getSavedFirstRun(): Boolean {
        return if (prefs.contains("first_run"))
            prefs.getBoolean("first_run", true)
        else
            true
    }

    fun saveLandscapeFix(landscapeFix: Boolean){
        val editor = prefs.edit()
        editor.putBoolean("landscape_fix", landscapeFix)
        editor.apply()
    }

    fun getSavedLandscapeFix(): Boolean{
        return if(prefs.contains("landscape_fix"))
            prefs.getBoolean("landscape_fix", false)
        else
            false
    }

}
