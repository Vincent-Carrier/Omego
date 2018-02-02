package com.vincentcarrier.model

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS

@Retention(SOURCE)
@Target(ANNOTATION_CLASS)
annotation class ByteDef(vararg val value: Byte = [])

const val EMPTY: Byte = -1
const val BLACK: Byte = 0
const val WHITE: Byte = 1

@Target(AnnotationTarget.TYPE)
@ByteDef(EMPTY, BLACK, WHITE)
annotation class MaybeStone

@Target(AnnotationTarget.TYPE)
@ByteDef(BLACK, WHITE)
annotation class Stone

fun Byte.isBlack() = this == BLACK

fun Byte.isWhite() = this == WHITE

fun Byte.isNotEmpty() = this != EMPTY

