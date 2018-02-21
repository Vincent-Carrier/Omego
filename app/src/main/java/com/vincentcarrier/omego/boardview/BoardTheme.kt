package com.vincentcarrier.omego.boardview

import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.DKGRAY
import android.graphics.Color.GRAY
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG


internal data class BoardTheme(
    val drawBackground: Canvas.() -> Unit = fun Canvas.() = this.drawColor(GRAY),
    val boardPaint: Paint = Paint().apply { color = GRAY },
    val blackStonePaint: Paint = Paint().apply { color = BLACK; flags = ANTI_ALIAS_FLAG },
    val whiteStonePaint: Paint = Paint().apply { color = WHITE; flags = ANTI_ALIAS_FLAG },
    val gridPaint: Paint? = Paint().apply { color = DKGRAY; flags = ANTI_ALIAS_FLAG; strokeWidth = 2f },
    val coordinatesPaint: Paint? = null
)