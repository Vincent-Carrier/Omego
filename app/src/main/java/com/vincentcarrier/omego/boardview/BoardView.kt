package com.vincentcarrier.omego.boardview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import com.vincentcarrier.model.Coordinate
import com.vincentcarrier.model.Game
import com.vincentcarrier.model.isBlack
import com.vincentcarrier.model.isNotEmpty
import kotlin.math.roundToInt


class BoardView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

  lateinit var game: Game
  private var theme = Theme()

  private lateinit var boardRect: Rect
  private lateinit var gridRect: Rect
  private var gridSpace = 0
  private var gridPadding = 0


  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    boardRect = square(left, top, (0.9 * width).toInt())
    gridPadding = boardRect.width() / 10
    gridRect = square(left + gridPadding, top + gridPadding, boardRect.width() - gridPadding * 2)
    gridSpace = gridRect.width() / game.board.grid.size
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      ACTION_UP -> {
        if (game.playTurn(pixelToCoordinate(x, y))) postInvalidate()
      }
    }
    return true
  }

  override fun onDraw(canvas: Canvas) {
    with(canvas) {
      theme.drawBackground(this)
      drawRect(boardRect, theme.boardPaint)

      if (theme.gridPaint != null) {
        // Draw the grid
        (0 until game.board.size).forEach { position ->
          drawHorizontalLine( // draw
              gridRect.left,
              gridRect.top + gridSpace * position,
              gridRect.width(),
              theme.gridPaint!!
          )
          drawVerticalLine(
              gridRect.left + gridSpace * position,
              gridRect.top,
              gridRect.width(),
              theme.gridPaint!!
          )
        }
      }

      game.board.grid.forEachIndexed { y, row ->
        row.forEachIndexed { x, maybeStone ->
          if (maybeStone.isNotEmpty()) drawCircle(
              coordinateToPixelX(x),
              coordinateToPixelY(y),
              0.4f * gridSpace,
              if (maybeStone.isBlack()) theme.blackStonePaint else theme.whiteStonePaint
          )
        }
      }
    }
  }

  private fun coordinateToPixelX(x: Int) = (boardRect.left + gridPadding + x * gridSpace).toFloat()

  private fun coordinateToPixelY(y: Int) = (boardRect.top + gridPadding + y * gridSpace).toFloat()

  private fun pixelToCoordinate(x: Float, y: Float): Coordinate {
    return Coordinate(((x - boardRect.left - gridPadding)/gridSpace).roundToInt(),
                      ((y - boardRect.top - gridPadding)/gridSpace).roundToInt())
  }
}

fun square(l: Int, t: Int, w: Int) = Rect(l, t, l+w, t+w)

fun Canvas.drawHorizontalLine(l: Int, t: Int, width: Int, paint: Paint) {
  drawLine(l.toFloat(), t.toFloat(), (l + width).toFloat(), t.toFloat(), paint)
}

fun Canvas.drawVerticalLine(l: Int, t: Int, height: Int, paint: Paint) {
  drawLine(l.toFloat(), t.toFloat(), l.toFloat(), (t + height).toFloat(), paint)
}

private fun View.dp(px: Float) = px / (resources.displayMetrics.densityDpi / 160f)

private fun View.px(dp: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)