package com.andb.apps.corners

const val DEFAULT_SIZE = 12
const val DEFAULT_TOGGLE = true
const val DEFAULT_COLOR = -16777216  //black

object Values {
    var toggleState = false

    var corners = arrayListOf(Corner(), Corner(), Corner(), Corner())

    var firstRun = true

    var landscapeFix = false

    fun commonSize(): Int{
        return corners.map { it.size }
            .groupingBy { it }
            .eachCount()
            .maxBy { it.value }?.key
            ?: DEFAULT_SIZE
    }

    fun commonColor(): Int{
        return corners.map { it.color }
            .groupingBy { it }
            .eachCount()
            .maxBy { it.value }?.key
            ?: DEFAULT_COLOR
    }

}