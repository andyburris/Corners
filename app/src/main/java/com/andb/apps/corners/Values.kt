package com.andb.apps.corners

const val DEFAULT_SIZE = 12
const val DEFAULT_TOGGLE = true

object Values {
    var toggleState = false
    var sizes = arrayListOf(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE)
    var cornerStates: ArrayList<Boolean> = ArrayList(listOf(DEFAULT_TOGGLE, DEFAULT_TOGGLE, DEFAULT_TOGGLE, DEFAULT_TOGGLE))
    var cornerColor = -16777216  /*black*/

    var firstRun = true

    fun listFromSize(size: Int): ArrayList<Int>{
        return arrayListOf(size, size, size, size)
    }

    fun commonSize(): Int{
        return sizes.groupingBy { it }.eachCount().maxBy { it.value }?.key ?: DEFAULT_SIZE
    }

}