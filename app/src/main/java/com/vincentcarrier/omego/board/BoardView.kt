package com.vincentcarrier.omego.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import com.vincentcarrier.model.Board
import com.vincentcarrier.model.Board.Companion.BLACK
import com.vincentcarrier.model.Board.Companion.WHITE
import com.vincentcarrier.model.Board.Coordinate
import com.vincentcarrier.omego.drawHorizontalLine
import com.vincentcarrier.omego.drawVerticalLine
import com.vincentcarrier.omego.handleScaling
import com.vincentcarrier.omego.square
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private typealias IsValidMove = Boolean

internal class BoardView(ctx: Context, attributeSet: AttributeSet?) : View(ctx, attributeSet) {

  internal lateinit var board: Board
  internal lateinit var onBoardTouched: (Board.Coordinate) -> IsValidMove

  internal var theme = LIGHT_THEME
    set(value) {
      field = value
      invalidate()
    }

  private var boardRect = Rect()
  private var gridPadding = 0
  private var gridRect = Rect()
  private var gridSpacing = 0

  private var scaleFactor = 1f
  private val viewPort = Rect()

  private val tapDetector = GestureDetector(ctx, object : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent) = true
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
      fun coordinateFromPixel(x: Float, y: Float): Coordinate {
        return board.c(((x - boardRect.left - gridPadding) / gridSpacing).roundToInt(),
            ((y - boardRect.top - gridPadding) / gridSpacing).roundToInt())
      }

      if (onBoardTouched(coordinateFromPixel(e.x, e.y))) invalidate()
      return true
    }
  })
  private val scaleDetector = ScaleGestureDetector(ctx, object : SimpleOnScaleGestureListener() {
      override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        max(0.5f, min(scaleFactor, 5.0f))
        invalidate()
        return true
      }
    })

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    boardRect = square(left, top, min(width, height))
    gridPadding = boardRect.width() / 10
    gridRect = square(left + gridPadding, top + gridPadding, boardRect.width() - gridPadding * 2)
    gridSpacing = gridRect.width() / board.size
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    return tapDetector.onTouchEvent(event) || scaleDetector.onTouchEvent(event)
  }

  override fun onDraw(canvas: Canvas) {
    fun drawBackground() = theme.drawBackground(canvas)
    fun drawBoard() = canvas.drawRect(boardRect, theme.boardPaint)
    fun drawGrid() {
      if (theme.gridPaint != null) {
        (0..board.size).forEach { position ->
          canvas.drawHorizontalLine( // draw
              gridRect.left,
              gridRect.top + gridSpacing * position,
              gridRect.width(),
              theme.gridPaint!!
          )
          canvas.drawVerticalLine(
              gridRect.left + gridSpacing * position,
              gridRect.top,
              gridRect.width(),
              theme.gridPaint!!
          )
        }
      }
    }
    fun drawStones() {
      fun drawStone(c: Coordinate, paint: Paint) {
        fun Coordinate.toPixelX() = (boardRect.left + gridPadding + x * gridSpacing).toFloat()
        fun Coordinate.toPixelY() = (boardRect.top + gridPadding + y * gridSpacing).toFloat()

        canvas.drawCircle(c.toPixelX(), c.toPixelY(),0.4f * gridSpacing, paint)
      }

      board.forEach { c, stone ->
        when (stone) {
          BLACK -> drawStone(c, theme.blackStonePaint)
          WHITE -> drawStone(c, theme.whiteStonePaint)
        }
      }
    }

    canvas.handleScaling(scaleFactor) {
      drawBackground()
      drawBoard()
      drawGrid()
      drawStones()
    }
  }
}