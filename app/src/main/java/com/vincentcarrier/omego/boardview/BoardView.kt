package com.vincentcarrier.omego.boardview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import com.vincentcarrier.model.Board
import com.vincentcarrier.model.Board.Companion.BLACK
import com.vincentcarrier.model.Board.Companion.WHITE
import com.vincentcarrier.model.Board.Coordinate
import com.vincentcarrier.model.Stone
import org.jetbrains.anko.AnkoLogger
import kotlin.math.roundToInt

typealias Valid = Boolean

class BoardView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

  private val logger = AnkoLogger<BoardView>()

  internal lateinit var board: Board
  internal lateinit var onCellTouched: (Coordinate) -> Valid

  private var theme = BoardTheme()
  private lateinit var boardRect: Rect
  private lateinit var gridRect: Rect
  private var gridSpace = 0
  private var gridPadding = 0

  private var scaleFactor = 1f

  private val tapDetector = object : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent) = true
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {

      return true
    }
  }

  private val scaleDetector by lazy {
    ScaleGestureDetector(
        context,
        object : SimpleOnScaleGestureListener() {
          override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            Math.max(0.5f, Math.min(scaleFactor, 5.0f))
            invalidate()
            return true
          }
        }
    ) }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    boardRect = square(left, top, (0.9 * height).toInt())
    gridPadding = boardRect.width() / 10
    gridRect = square(left + gridPadding, top + gridPadding, boardRect.width() - gridPadding * 2)
    gridSpace = gridRect.width() / board.size
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    return when (event.action) {
      ACTION_DOWN -> return true
      ACTION_UP -> onCellTouched(coordinateFromPixel(event.x, event.y))
      ACTION_MOVE -> scaleDetector.onTouchEvent(event)
      else -> false
    }
  }

  override fun onDraw(canvas: Canvas) {
    fun drawStone(x: Int, y: Int, @Stone color: Byte) {
      canvas.drawCircle(
          coordinateToPixelX(x),
          coordinateToPixelY(y),
          0.4f * gridSpace,
          if (color == BLACK) theme.blackStonePaint else theme.whiteStonePaint
      )
    }

    scaleX = scaleFactor
    scaleY = scaleFactor

    with(canvas) {
      theme.drawBackground(this)

      // Draw the board
      drawRect(boardRect, theme.boardPaint)

      // Draw the board
      if (theme.gridPaint != null) {
        (0 .. board.size).forEach { position ->
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
      board.forEach { x, y, stone ->
        when (stone) {
          BLACK -> drawStone(x, y, BLACK)
          WHITE -> drawStone(x, y, WHITE)
        }
      }
    }
  }

  private fun coordinateToPixelX(x: Int) = (boardRect.left + gridPadding + x * gridSpace).toFloat()

  private fun coordinateToPixelY(y: Int) = (boardRect.top + gridPadding + y * gridSpace).toFloat()

  internal fun coordinateFromPixel(x: Float, y: Float): Coordinate {
    return board.c(((x - boardRect.left - gridPadding)/gridSpace).roundToInt(),
                   ((y - boardRect.top - gridPadding)/gridSpace).roundToInt())
  }
}