package com.vincentcarrier.omego

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import org.jetbrains.anko.toast


internal fun square(l: Int, t: Int, w: Int) = Rect(l, t, l+w, t+w)

internal fun View.dp(px: Float) = px / (resources.displayMetrics.densityDpi / 160f)

internal fun View.px(dp: Float): Float {
  return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

internal fun Canvas.drawHorizontalLine(l: Int, t: Int, width: Int, paint: Paint) {
  drawLine(l.toFloat(), t.toFloat(), (l + width).toFloat(), t.toFloat(), paint)
}

internal fun Canvas.drawVerticalLine(l: Int, t: Int, height: Int, paint: Paint) {
  drawLine(l.toFloat(), t.toFloat(), l.toFloat(), (t + height).toFloat(), paint)
}

internal inline fun Canvas.handleScaling(scaleFactor: Float, drawCalls: () -> Any) {
  save()
  scale(scaleFactor, scaleFactor)
  drawCalls()
  restore()
}

internal fun Fragment.toast(message: String) = activity?.toast(message)

internal fun Fragment.transaction(transactionBody: FragmentTransaction.() -> FragmentTransaction) {
  fragmentManager?.beginTransaction()?.transactionBody()?.commit()
}

internal fun AppCompatActivity.transaction(transactionBody: FragmentTransaction.() -> FragmentTransaction) {
  supportFragmentManager.beginTransaction().transactionBody().commit()
}

internal val Fragment.mainVm get() = (activity as MainActivity).vm