package com.vincentcarrier.omego.boardview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View


internal fun AppCompatActivity.addFragment(@IdRes container: Int, fragment: Fragment) {
  supportFragmentManager.beginTransaction().add(container, fragment).commit()
}

internal fun View.square(l: Int, t: Int, w: Int) = Rect(l, t, l+w, t+w)

internal fun View.dp(px: Float) = px / (resources.displayMetrics.densityDpi / 160f)

internal fun View.px(dp: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

internal fun Canvas.drawHorizontalLine(l: Int, t: Int, width: Int, paint: Paint) {
  drawLine(l.toFloat(), t.toFloat(), (l + width).toFloat(), t.toFloat(), paint)
}

internal fun Canvas.drawVerticalLine(l: Int, t: Int, height: Int, paint: Paint) {
  drawLine(l.toFloat(), t.toFloat(), l.toFloat(), (t + height).toFloat(), paint)
}
