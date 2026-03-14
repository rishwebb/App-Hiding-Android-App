package com.launcherx.icons

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IconPackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pm: PackageManager = context.packageManager
    private val iconSize = (60 * context.resources.displayMetrics.density).toInt() // Standard size

    fun getIconForPackage(packageName: String, appInfo: ApplicationInfo? = null): Bitmap {
        return try {
            val drawable = if (appInfo != null) {
                pm.getApplicationIcon(appInfo)
            } else {
                pm.getApplicationIcon(packageName)
            }
            drawableToBitmap(drawable)
        } catch (e: PackageManager.NameNotFoundException) {
            createFallbackBitmap()
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val src = drawable.bitmap
            if (src.width == iconSize && src.height == iconSize) {
                return src
            }
            return Bitmap.createScaledBitmap(src, iconSize, iconSize, true)
        }

        val bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun createFallbackBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.DKGRAY)
        return bitmap
    }
}
