package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Companion.BLACK
import com.vincentcarrier.model.Board.Companion.EMPTY
import com.vincentcarrier.model.Board.Companion.WHITE
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS

@Retention(SOURCE)
@Target(ANNOTATION_CLASS)
annotation class ByteDef(vararg val value: Byte = [])

@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
@ByteDef(EMPTY, BLACK, WHITE)
annotation class StoneOrEmpty

@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
@ByteDef(BLACK, WHITE)
annotation class Stone

fun @StoneOrEmpty Byte.isNotEmpty() = this != EMPTY

internal val @Stone Byte.opposite: Byte
  get() = when (this) {
    WHITE -> BLACK
    BLACK -> WHITE
    else -> EMPTY
  }