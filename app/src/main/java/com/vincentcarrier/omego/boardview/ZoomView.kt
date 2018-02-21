package com.vincentcarrier.omego.boardview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout

/**
 * Zooming view.
 */
class ZoomView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

  // zooming
  var zoom = 2.0f
    internal set
  internal var maxZoom = 2.0f
  internal var smoothZoom = 1.0f
  internal var zoomX: Float = 0.toFloat()
  internal var zoomY: Float = 0.toFloat()
  internal var smoothZoomX: Float = 0.toFloat()
  internal var smoothZoomY: Float = 0.toFloat()
  private var scrolling: Boolean = false // NOPMD by karooolek on 29.06.11 11:45

  // minimap variables
  var isMiniMapEnabled = false
  var miniMapColor = Color.BLACK
  private var miniMapHeight = -1
  var miniMapCaption: String? = null
  var miniMapCaptionSize = 10.0f
  var miniMapCaptionColor = Color.WHITE

  // touching variables
  private var lastTapTime: Long = 0
  private var touchStartX: Float = 0f
  private var touchStartY: Float = 0f
  private var touchLastX: Float = 0f
  private var touchLastY: Float = 0f
  private var startd: Float = 0f
  private var pinching: Boolean = false
  private var lastd: Float = 0f
  private var lastdx1: Float = 0f
  private var lastdy1: Float = 0f
  private var lastdx2: Float = 0f
  private var lastdy2: Float = 0f

  private val tapDetector = object : ScaleGestureDetector.OnScaleGestureListener {
    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
  }

  // drawing
  private val m = Matrix()
  private val p = Paint()

  // listener
  var listener: ZoomViewListener? = null

  private var ch: Bitmap? = null

  val zoomFocusX: Float
    get() = zoomX * zoom

  val zoomFocusY: Float
    get() = zoomY * zoom

  /**
   * Zooming view listener interface.
   *
   * @author karooolek
   */
  interface ZoomViewListener {

    fun onZoomStarted(zoom: Float, zoomx: Float, zoomy: Float)

    fun onZooming(zoom: Float, zoomx: Float, zoomy: Float)

    fun onZoomEnded(zoom: Float, zoomx: Float, zoomy: Float)
  }

  fun getMaxZoom(): Float {
    return maxZoom
  }

  fun setMaxZoom(maxZoom: Float) {
    if (maxZoom < 1.0f) {
      return
    }

    this.maxZoom = maxZoom
  }

  fun setMiniMapHeight(miniMapHeight: Int) {
    if (miniMapHeight < 0) {
      return
    }
    this.miniMapHeight = miniMapHeight
  }

  fun getMiniMapHeight(): Int {
    return miniMapHeight
  }

  fun zoomTo(zoom: Float, x: Float, y: Float) {
    this.zoom = Math.min(zoom, maxZoom)
    zoomX = x
    zoomY = y
    smoothZoomTo(this.zoom, x, y)
  }

  fun smoothZoomTo(zoom: Float, x: Float, y: Float) {
    smoothZoom = clamp(1.0f, zoom, maxZoom)
    smoothZoomX = x
    smoothZoomY = y
    if (listener != null) {
      listener!!.onZoomStarted(smoothZoom, x, y)
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    // single touch
    if (ev.pointerCount == 1) {
      processSingleTouchEvent(ev)
    }

    // // double touch
    if (ev.pointerCount == 2) {
      return false
    }

    // redraw
    rootView.invalidate()
    invalidate()

    return true
  }

  private fun processSingleTouchEvent(ev: MotionEvent) {

    val x = ev.x
    val y = ev.y

    val w = miniMapHeight * width.toFloat() / height
    val h = miniMapHeight.toFloat()
    val touchingMiniMap = x >= 10.0f && x <= 10.0f + w && y >= 10.0f && y <= 10.0f + h

    if (isMiniMapEnabled && smoothZoom > 1.0f && touchingMiniMap) {
      processSingleTouchOnMinimap(ev)
    } else {
      processSingleTouchOutsideMinimap(ev)
    }
  }

  private fun processSingleTouchOnMinimap(ev: MotionEvent) {
    val x = ev.x
    val y = ev.y

    val w = miniMapHeight * width.toFloat() / height
    val h = miniMapHeight.toFloat()
    val zx = (x - 10.0f) / w * width
    val zy = (y - 10.0f) / h * height
    smoothZoomTo(smoothZoom, zx, zy)
  }

  private fun processSingleTouchOutsideMinimap(ev: MotionEvent) {
    val x = ev.x
    val y = ev.y
    var lx = x - touchStartX
    var ly = y - touchStartY
    val l = Math.hypot(lx.toDouble(), ly.toDouble()).toFloat()
    var dx = x - touchLastX
    var dy = y - touchLastY
    touchLastX = x
    touchLastY = y

    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        touchStartX = x
        touchStartY = y
        touchLastX = x
        touchLastY = y
        dx = 0f
        dy = 0f
        lx = 0f
        ly = 0f
        scrolling = false
      }

      MotionEvent.ACTION_MOVE -> if (scrolling || smoothZoom > 1.0f && l > 30.0f) {
        if (!scrolling) {
          scrolling = true
          ev.action = MotionEvent.ACTION_CANCEL
          super.dispatchTouchEvent(ev)
        }
        smoothZoomX -= dx / zoom
        smoothZoomY -= dy / zoom
        return
      }

      MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_UP ->

        // tap
        if (l < 30.0f) {
          // check double tap
          if (System.currentTimeMillis() - lastTapTime < 500) {
            if (smoothZoom == 1.0f) {
              smoothZoomTo(maxZoom, x, y)
            } else {
              smoothZoomTo(1.0f, width / 2.0f, height / 2.0f)
            }
            lastTapTime = 0
            ev.action = MotionEvent.ACTION_CANCEL
            super.dispatchTouchEvent(ev)
            return
          }

          lastTapTime = System.currentTimeMillis()

          performClick()
        }

      else -> {
      }
    }

    ev.setLocation(zoomX + (x - 0.5f * width) / zoom, zoomY + (y - 0.5f * height) / zoom)

    ev.x
    ev.y

    super.dispatchTouchEvent(ev)
  }

  private fun processDoubleTouchEvent(ev: MotionEvent) {
    val x1 = ev.getX(0)
    val dx1 = x1 - lastdx1
    lastdx1 = x1
    val y1 = ev.getY(0)
    val dy1 = y1 - lastdy1
    lastdy1 = y1
    val x2 = ev.getX(1)
    val dx2 = x2 - lastdx2
    lastdx2 = x2
    val y2 = ev.getY(1)
    val dy2 = y2 - lastdy2
    lastdy2 = y2

    // pointers distance
    val d = Math.hypot((x2 - x1).toDouble(), (y2 - y1).toDouble()).toFloat()
    val dd = d - lastd
    lastd = d
    val ld = Math.abs(d - startd)

    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        startd = d
        pinching = false
      }

      MotionEvent.ACTION_MOVE -> if (pinching || ld > 30.0f) {
        pinching = true
        val dxk = 0.5f * (dx1 + dx2)
        val dyk = 0.5f * (dy1 + dy2)
        smoothZoomTo(Math.max(1.0f, zoom * d / (d - dd)), zoomX - dxk / zoom, zoomY - dyk / zoom)
      }

      MotionEvent.ACTION_UP -> pinching = false
      else -> pinching = false
    }

    ev.action = MotionEvent.ACTION_CANCEL
    super.dispatchTouchEvent(ev)
  }

  private fun clamp(min: Float, value: Float, max: Float): Float {
    return Math.max(min, Math.min(value, max))
  }

  private fun lerp(a: Float, b: Float, k: Float): Float {
    return a + (b - a) * k
  }

  private fun bias(a: Float, b: Float, k: Float): Float {
    return if (Math.abs(b - a) >= k) a + k * Math.signum(b - a) else b
  }

  override fun dispatchDraw(canvas: Canvas) {
    // do zoom
    zoom = lerp(bias(zoom, smoothZoom, 0.05f), smoothZoom, 0.2f)
    smoothZoomX = clamp(0.5f * width / smoothZoom, smoothZoomX, width - 0.5f * width / smoothZoom)
    smoothZoomY = clamp(0.5f * height / smoothZoom, smoothZoomY, height - 0.5f * height / smoothZoom)

    zoomX = lerp(bias(zoomX, smoothZoomX, 0.1f), smoothZoomX, 0.35f)
    zoomY = lerp(bias(zoomY, smoothZoomY, 0.1f), smoothZoomY, 0.35f)
    if (zoom != smoothZoom && listener != null) {
      listener!!.onZooming(zoom, zoomX, zoomY)
    }

    val animating = (Math.abs(zoom - smoothZoom) > 0.0000001f
        || Math.abs(zoomX - smoothZoomX) > 0.0000001f || Math.abs(zoomY - smoothZoomY) > 0.0000001f)

    // nothing to draw
    if (childCount == 0) {
      return
    }

    // prepare matrix
    m.setTranslate(0.5f * width, 0.5f * height)
    m.preScale(zoom, zoom)
    m.preTranslate(-clamp(0.5f * width / zoom, zoomX, width - 0.5f * width / zoom),
        -clamp(0.5f * height / zoom, zoomY, height - 0.5f * height / zoom))

    // get view
    val v = getChildAt(0)
    m.preTranslate(v.left.toFloat(), v.top.toFloat())

    // get drawing cache if available
    if (animating && ch == null && isAnimationCacheEnabled) {
      v.isDrawingCacheEnabled = true
      ch = v.drawingCache
    }

    // draw using cache while animating
    if (animating && isAnimationCacheEnabled && ch != null) {
      p.color = -0x1
      canvas.drawBitmap(ch!!, m, p)
    } else { // zoomed or cache unavailable
      ch = null
      canvas.save()
      canvas.concat(m)
      v.draw(canvas)
      canvas.restore()
    }

    // draw minimap
    if (isMiniMapEnabled) {
      if (miniMapHeight < 0) {
        miniMapHeight = height / 4
      }

      canvas.translate(10.0f, 10.0f)

      p.color = -0x80000000 or (0x00ffffff and miniMapColor)
      val w = miniMapHeight * width.toFloat() / height
      val h = miniMapHeight.toFloat()
      canvas.drawRect(0.0f, 0.0f, w, h, p)

      if (miniMapCaption != null && miniMapCaption!!.length > 0) {
        p.textSize = miniMapCaptionSize
        p.color = miniMapCaptionColor
        p.isAntiAlias = true
        canvas.drawText(miniMapCaption!!, 10.0f, 10.0f + miniMapCaptionSize, p)
        p.isAntiAlias = false
      }

      p.color = -0x80000000 or (0x00ffffff and miniMapColor)
      val dx = w * zoomX / width
      val dy = h * zoomY / height
      canvas.drawRect(dx - 0.5f * w / zoom, dy - 0.5f * h / zoom, dx + 0.5f * w / zoom, dy + 0.5f * h / zoom, p)

      canvas.translate(-10.0f, -10.0f)
    }

    // redraw
    // if (animating) {
    rootView.invalidate()
    invalidate()
    // }
  }
}
