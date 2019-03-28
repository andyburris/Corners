package com.andb.apps.corners

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object ModifyWallpaper {

    const val REQUEST_CODE = 1578

    fun applyToLockscreenWallpaper(activity: Activity, context: Context, size: Int, color: Int): Bitmap? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                val wallpaperInput: Drawable =
                        //if (WallpaperManager.getInstance(context).getBuiltInDrawable(WallpaperManager.FLAG_LOCK) == null) {
                            WallpaperManager.getInstance(context).drawable
                        //} else {
                            //WallpaperManager.getInstance(context).getBuiltInDrawable(WallpaperManager.FLAG_LOCK)
                        //}

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SET_WALLPAPER) == PackageManager.PERMISSION_GRANTED) {
                    val wpToSet = overlayAll(wallpaperInput, context, dpToPx(size), Color.BLACK)
                    WallpaperManager.getInstance(context).setBitmap(wpToSet, wallpaperInput.bounds, true, WallpaperManager.FLAG_LOCK)
                    return wpToSet
                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SET_WALLPAPER), REQUEST_CODE)
                }

            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            }
        }

        return null
    }

    fun overlayAll(wallpaper: Drawable, context: Context, size: Int, color: Int): Bitmap {
        val topLeft: Drawable = context.getDrawable(R.drawable.ic_top_left).mutate()
        val bottomLeft: Drawable = context.getDrawable(R.drawable.ic_bottom_left).mutate()
        val topRight: Drawable = context.getDrawable(R.drawable.ic_top_right).mutate()
        val bottomRight: Drawable = context.getDrawable(R.drawable.ic_bottom_right).mutate()

        topLeft.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        topRight.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        bottomLeft.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        bottomRight.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)


        var toSet = drawableToBitmap(wallpaper)
        toSet = putOverlay(toSet, drawableToBitmap(topLeft, size, size), 0f, 0f)
        toSet = putOverlay(toSet, drawableToBitmap(topRight, size, size), (toSet.width - size).toFloat(), 0f)
        toSet = putOverlay(toSet, drawableToBitmap(bottomLeft, size, size), 0f, (toSet.height - size).toFloat())
        toSet = putOverlay(toSet, drawableToBitmap(bottomRight, size, size), (toSet.width - size).toFloat(), (toSet.height - size).toFloat())

        return toSet
    }

    fun drawableToBitmap(drawable: Drawable, width: Int = width(), height: Int = height()): Bitmap {
        var bitmap: Bitmap =
                if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
                } else {
                    Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                }

        bitmap = cropBitmap(bitmap, width, height)

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun cropBitmap(originalImage: Bitmap, width: Int, height: Int): Bitmap{
        Log.d("widths", "imageWidth: ${originalImage.width}, screenWidth: $width")
        val matrix = Matrix()
        //matrix.setRectToRect(RectF(0f, 0f, originalImage.width.toFloat(), originalImage.height.toFloat()), RectF(0f, 0f, width.toFloat(), height.toFloat()), Matrix.ScaleToFit.)
        return Bitmap.createBitmap(originalImage, 0, 0, Math.min(originalImage.width, width), Math.min(originalImage.height, height), matrix, true)
    }

    fun putOverlay(bitmap: Bitmap, overlay: Bitmap, x: Float, y: Float): Bitmap {
        //val overlayed: Bitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(bitmap)
        //canvas.drawBitmap(bitmap, Matrix(), null)
        //canvas.drawBitmap(overlay, Matrix(), null)
        //val paint = Paint(FILTER_BITMAP_FLAG)
        canvas.drawBitmap(overlay, x, y, null)
        return bitmap
    }

    fun width(): Int{
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun height(): Int{
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun dpToPx(dp: Int): Int{
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale).toInt()
    }
}