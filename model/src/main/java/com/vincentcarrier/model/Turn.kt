package com.vincentcarrier.model

sealed class Turn

data class Move(val c: Coordinate, val color: @Stone Byte) : Turn()

object Pass : Turn()

object Resign : Turn()