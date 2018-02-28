package com.vincentcarrier.omego

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import androidx.graphics.toRectF
import org.jetbrains.anko.toast


fun square(l: Float, t: Float, w: Float) = RectF(l, t, l+w, t+w)

fun square(l: Int, t: Int, w: Int) = Rect(l, t, l+w, t+w).toRectF()

fun View.dp(px: Float) = px / (resources.displayMetrics.densityDpi / 160f)

fun View.px(dp: Float): Float {
  return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Canvas.drawHorizontalLine(l: Float, t: Float, width: Float, paint: Paint) {
  drawLine(l, t, (l + width), t, paint)
}

fun Canvas.drawVerticalLine(l: Float, t: Float, height: Float, paint: Paint) {
  drawLine(l, t, l, (t + height), paint)
}

operator fun PointF.div(dividend: Float): PointF {
  x / dividend
  y / dividend
  return this
}

operator fun PointF.times(multiplier: Float): PointF {
  x * multiplier
  y * multiplier
  return this
}

operator fun RectF.get(i: Int): Float {
  return when (i) {
    0 -> left
    1 -> top
    2 -> right
    3 -> bottom
    else -> throw IllegalArgumentException()
  }
}

fun Fragment.toast(message: String) = activity?.toast(message)

fun Fragment.transaction(transactionBody: FragmentTransaction.() -> FragmentTransaction) {
  fragmentManager!!.beginTransaction().transactionBody().commit()
}

fun AppCompatActivity.transaction(transactionBody: FragmentTransaction.() -> FragmentTransaction) {
  supportFragmentManager.beginTransaction().transactionBody().commit()
}

internal val Fragment.mainVm get() = (activity as MainActivity).vm