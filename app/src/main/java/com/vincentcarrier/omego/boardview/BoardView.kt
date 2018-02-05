package com.vincentcarrier.omego.boardview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import com.vincentcarrier.model.BLACK
import com.vincentcarrier.model.Board
import com.vincentcarrier.model.Coordinate
import com.vincentcarrier.model.isNotEmpty
import org.jetbrains.anko.AnkoLogger
import kotlin.math.roundToInt


class BoardView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

  private val logger = AnkoLogger<BoardView>()

  lateinit var board: Board

  private var theme = BoardTheme()
  private lateinit var boardRect: Rect
  private lateinit var gridRect: Rect
  private var gridSpace = 0
  private var gridPadding = 0

  private val tapDetector = object : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent) = true
    override fun onSingleTapUp(e: MotionEvent) = true
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    boardRect = square(left, top, (0.9 * height).toInt())
    gridPadding = boardRect.width() / 10
    gridRect = square(left + gridPadding, top + gridPadding, boardRect.width() - gridPadding * 2)
    gridSpace = gridRect.width() / board.size
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    tapDetector.onSingleTapUp(event)
    return super.onTouchEvent(event)
  }

  override fun onDraw(canvas: Canvas) {
    with(canvas) {
      theme.drawBackground(this)

      // Draw the board
      drawRect(boardRect, theme.boardPaint)

      // Draw the board
      if (theme.gridPaint != null) {
        (0 until board.size).forEach { position ->
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

      // Draw the stones
      board.grid().forEachIndexed { y, row ->
        row.forEachIndexed { x, maybeStone ->
          if (maybeStone.isNotEmpty())
            drawCircle(
              coordinateToPixelX(x),
              coordinateToPixelY(y),
              0.4f * gridSpace,
              if (maybeStone == BLACK) theme.blackStonePaint else theme.whiteStonePaint
            )
        }
      }
    }
  }

  private fun coordinateToPixelX(x: Int) = (boardRect.left + gridPadding + x * gridSpace).toFloat()

  private fun coordinateToPixelY(y: Int) = (boardRect.top + gridPadding + y * gridSpace).toFloat()

  internal fun pixelToCoordinate(x: Float, y: Float): Coordinate {
    return Coordinate(((x - boardRect.left - gridPadding)/gridSpace).roundToInt(),
                      ((y - boardRect.top - gridPadding)/gridSpace).roundToInt())
  }
}