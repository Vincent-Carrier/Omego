package com.vincentcarrier.model


data class Coordinate(val x: Int, val y: Int) {
  override fun toString(): String = "($x,$y)"
}

infix fun Int.to(y: Int) = Coordinate(this, y)

typealias Coordinates = List<Coordinate>