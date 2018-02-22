package com.vincentcarrier.omego.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
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
import com.vincentcarrier.omego.drawHorizontalLine
import com.vincentcarrier.omego.drawVerticalLine
import com.vincentcarrier.omego.square
import java.lang.Math.max
import java.lang.Math.min
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
  private var viewport = RectF()

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
            max(0.5f, min(scaleFactor, 5.0f))
            invalidate()
            return true
          }
        }
    ) }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    boardRect = square(left, top, min(width, height))
    gridPadding = boardRect.width() / 10
    gridRect = square(left + gridPadding, top + gridPadding, boardRect.width() - gridPadding * 2)
    gridSpace = gridRect.width() / board.size
    scaleFactor = max(width, height) / boardRect.height().toFloat()
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    fun coordinateFromPixel(x: Float, y: Float): Coordinate {
      return board.c(((x - boardRect.left - gridPadding)/gridSpace).roundToInt(),
          ((y - boardRect.top - gridPadding)/gridSpace).roundToInt())
    }

    return when (event.actionMasked) {
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
    fun handleScaling(body: () -> Unit) {
      with(canvas) {
        super.onDraw(this)
        save()
        scale(scaleFactor, scaleFactor)
        body()
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
      fun drawStone(c: Coordinate, @Stone color: Byte) {
        fun Coordinate.toPixelX() = (boardRect.left + gridPadding + x * gridSpace).toFloat()
        fun Coordinate.toPixelY() = (boardRect.top + gridPadding + y * gridSpace).toFloat()

              canvas.drawCircle(
                  c.toPixelX(),
                  c.toPixelY(),
                  0.4f * gridSpace,
                  if (color == BLACK) theme.blackStonePaint else theme.whiteStonePaint
              )
        }

      board.forEach { c, stone ->
        when (stone) {
          BLACK -> drawStone(c, BLACK)
          WHITE -> drawStone(c, WHITE)
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
}