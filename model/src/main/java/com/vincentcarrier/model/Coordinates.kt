package com.vincentcarrier.model


data class Coordinate(val x: Int, val y: Int) {
  override fun toString(): String = "($x,$y)"
}

infix fun Int.to(y: Int) = Coordinate(this, y)

typealias Coordinates = List<Coordinate>

internal fun Coordinates.toString(size: Int): String {
  val sb = StringBuilder()
  (0..size).forEach { y ->
    (0..size).forEach { x ->
      if (this.contains(x to y)) sb.append('X') else sb.append('O')
    }
    sb.appendln()
  }

  return sb.toString()
}