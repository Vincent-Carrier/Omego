package com.vincentcarrier.omego.board

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.DKGRAY
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG

private val ALMOST_BLACK = Color.rgb(30, 30, 30)

private val ALMOST_WHITE = Color.rgb(240, 240, 220)

internal data class BoardTheme(
    val drawBackground: Canvas.() -> Unit = fun Canvas.() {
      drawColor(BLACK)
    },
    val boardPaint: Paint = Paint().apply { color = BLACK },
    val gridPaint: Paint? = Paint().apply {
      color = ALMOST_BLACK
      flags = ANTI_ALIAS_FLAG
      strokeWidth = 2f
    },
    val coordinatesPaint: Paint? = null,
    val blackStonePaint: Paint = Paint().apply {
      color = DKGRAY
      flags = ANTI_ALIAS_FLAG
    },
    val whiteStonePaint: Paint = Paint().apply {
      color = WHITE
      flags = ANTI_ALIAS_FLAG
    }
) {
  init {
    gridPaint?.color = BLACK
    gridPaint?.flags = ANTI_ALIAS_FLAG
    gridPaint?.strokeWidth = 2f
  }
}

internal val DARK_THEME = BoardTheme()

internal val LIGHT_THEME = BoardTheme(
    drawBackground = fun Canvas.() = drawColor(WHITE),
    boardPaint = Paint().apply { color = ALMOST_WHITE }
)
