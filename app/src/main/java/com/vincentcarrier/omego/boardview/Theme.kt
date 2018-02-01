package com.vincentcarrier.omego.boardview

import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.DKGRAY
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG


internal data class Theme(
    val drawBackground: Canvas.() -> Unit = fun Canvas.() = this.drawColor(BLACK),
    val boardPaint: Paint = Paint().apply { color = BLACK },
    val blackStonePaint: Paint = Paint().apply { color = DKGRAY; flags = ANTI_ALIAS_FLAG },
    val whiteStonePaint: Paint = Paint().apply { color = WHITE; flags = ANTI_ALIAS_FLAG },
    val gridPaint: Paint? = null,
    val coordinatesPaint: Paint? = null
)