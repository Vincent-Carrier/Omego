package com.vincentcarrier.omego.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
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
import com.vincentcarrier.omego.get
import com.vincentcarrier.omego.square
import kotlin.math.min
import kotlin.math.roundToInt


internal class BoardView : View {
  constructor(ctx: Context) : super(ctx)
  constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet)

  companion object {
    private const val MIN_ZOOM = 1f
    private const val MAX_ZOOM = 2f
    private const val ZOOMED_IN = MIN_ZOOM + (MAX_ZOOM - MIN_ZOOM) * 0.2
  }

  internal lateinit var board: Board
  internal lateinit var onBoardTouched: (Board.Coordinate) -> IsValidMove

  internal var theme = LIGHT_THEME
    set(value) {
      field = value
      invalidate()
    }

  private var boardRect = RectF()
  private var gridRect = RectF()
  private var gridPadding = 0f
  private var gridSpacing = 0f

  private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent) = true

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
      fun PointF.toCoordinate(): Board.Coordinate {
        fun Float.toCoordinateComponent(i: Int) = ((this - boardRect[i] - gridPadding) / gridSpacing).roundToInt()

        return board.c(x.toCoordinateComponent(0), y.toCoordinateComponent(1))
      }

      if (onBoardTouched(PointF(e.x, e.y).toCoordinate())) invalidate()
      return true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
      if (scaleX >= ZOOMED_IN) setZoomLevel(MIN_ZOOM) else setZoomLevel(MAX_ZOOM)
      return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, dx: Float, dy: Float): Boolean {
      translationX -= dx
      translationY -= dy
      return true
    }
  })

  private val scaleDetector = ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
    override fun onScale(detector: ScaleGestureDetector): Boolean {
      fun adjustScaleFactor(prev: Float) = (prev * detector.scaleFactor).coerceIn(MIN_ZOOM, MAX_ZOOM)

      setZoomLevel(adjustScaleFactor(scaleX))
      pivotX = detector.focusX
      pivotY = detector.focusY
      return true
    }
  })

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    boardRect = square(left, top, min(width, height))
    gridPadding = boardRect.width() * 0.03f
    gridRect = square(left + gridPadding, top + gridPadding, boardRect.width() - gridPadding * 2)
    gridSpacing = gridRect.width() / board.size
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(e: MotionEvent): Boolean {
    return gestureDetector.onTouchEvent(e) || scaleDetector.onTouchEvent(e)
  }

  override fun onDraw(canvas: Canvas) {
    with(canvas) {
      fun drawBackground() = theme.drawBackground(this)
      fun drawBoard() = drawRect(boardRect, theme.boardPaint)
      fun drawGrid() {
        if (theme.gridPaint != null) {
          (0..board.size).forEach { position ->
            drawHorizontalLine( // draw
                gridRect.left,
                gridRect.top + gridSpacing * position,
                gridRect.width(),
                theme.gridPaint!!
            )
            drawVerticalLine(
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
          fun Coordinate.toPointFX() = boardRect.left + gridPadding + x * gridSpacing
          fun Coordinate.toPointFY() = boardRect.top + gridPadding + y * gridSpacing

          drawCircle(c.toPointFX(), c.toPointFY(), 0.4f * gridSpacing, paint)
        }

        board.forEach { c, stone ->
          when (stone) {
            BLACK -> drawStone(c, theme.blackStonePaint)
            WHITE -> drawStone(c, theme.whiteStonePaint)
          }
        }
      }

      drawBackground()
      drawBoard()
      drawGrid()
      drawStones()
    }
  }

  private fun setZoomLevel(scaleFactor: Float) {
    scaleX = scaleFactor
    scaleY = scaleFactor
  }
}

private typealias IsValidMove = Boolean
