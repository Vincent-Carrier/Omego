package com.vincentcarrier.model

sealed class Turn

data class Move(val c: Coordinate, val color: @Stone Byte) : Turn() {
  constructor(x: Int, y: Int, color: @Stone Byte) : this(Coordinate(x,y), color)

  override fun toString() = "$c - ${if (color == BLACK) "BLACK" else "WHITE"}"
}

object Pass : Turn()

object Resign : Turn()