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
import com.vincentcarrier.model.Validity
import kotlin.math.roundToInt

class BoardView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

  internal lateinit var board: Board
  internal lateinit var onCellTouched: (Coordinate) -> Validity

  private var theme = BoardTheme()
  private lateinit var boardRect: Rect
  private lateinit var gridRect: Rect
  private var gridSpace = 0
  private var gridPadding = 0

  private var scaleFactor = 1f

  private val tapDetector = object : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent) = true
    override fun onSingleTapUp(e: MotionEvent) = true
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
      ACTION_MOVE -> scaleDetector.onTouchEvent(event)
      ACTION_UP -> {
        if (tapDetector.onSingleTapUp(event)
            && onCellTouched(coordinateFromPixel(event.x, event.y))) {
          invalidate()
          true
        } else false
      }
      else -> false
    }
  }

  override fun onDraw(canvas: Canvas) {
    fun handleScaling(func: () -> Unit) {
      with(canvas) {
        super.onDraw(this)
        save()
        scale(scaleFactor, scaleFactor)
        func()
        restore()
      }
    }

    fun drawBackground() = theme.drawBackground(canvas)

    fun drawBoard() = canvas.drawRect(boardRect, theme.boardPaint)

    fun drawGrid() {
      if (theme.gridPaint != null) {
        (0 .. board.size).forEach { position ->
          canvas.drawHorizontalLine( // draw
              gridRect.left,
              gridRect.top + gridSpace * position,
              gridRect.width(),
              theme.gridPaint!!
          )
          canvas.drawVerticalLine(
              gridRect.left + gridSpace * position,
              gridRect.top,
              gridRect.width(),
              theme.gridPaint!!
          )
        }
      }
    }

    fun drawStones() {
      fun drawStone(x: Int, y: Int, @Stone color: Byte) {
        canvas.drawCircle(
            coordinateToPixelX(x),
            coordinateToPixelY(y),
            0.4f * gridSpace,
            if (color == BLACK) theme.blackStonePaint else theme.whiteStonePaint
        )
      }

      board.forEach { x, y, stone ->
        when (stone) {
          BLACK -> drawStone(x, y, BLACK)
          WHITE -> drawStone(x, y, WHITE)
        }
      }
    }

    handleScaling {
      drawBackground()
      drawBoard()
      drawGrid()
      drawStones()
    }
  }

  private fun coordinateToPixelX(x: Int) = (boardRect.left + gridPadding + x * gridSpace).toFloat()

  private fun coordinateToPixelY(y: Int) = (boardRect.top + gridPadding + y * gridSpace).toFloat()

  private fun coordinateFromPixel(x: Float, y: Float): Coordinate {
    return board.c(((x - boardRect.left - gridPadding)/gridSpace).roundToInt(),
                   ((y - boardRect.top - gridPadding)/gridSpace).roundToInt())
  }
}