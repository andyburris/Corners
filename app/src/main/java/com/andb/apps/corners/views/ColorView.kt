package com.andb.apps.corners.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.andb.apps.corners.dpToPx

class ColorView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint().also {
        it.color = Color.BLACK
        it.isAntiAlias = true
    }
    private val borderPaint = Paint().also {
        it.color = Color.BLACK
        it.isAntiAlias = true
    }
    private val borderRadius = dpToPx(1)

    var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(), (measuredWidth / 2).toFloat(), borderPaint)
        canvas.drawCircle((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(), (measuredWidth / 2).toFloat() - borderRadius, paint)
    }
}