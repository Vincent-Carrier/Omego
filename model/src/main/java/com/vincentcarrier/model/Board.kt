package com.vincentcarrier.model

import com.google.common.annotations.VisibleForTesting


typealias Grid = Array<ByteArray>

data class Board(val size: Int) {
  val grid: Grid = Array(size) { ByteArray(size) { EMPTY } }

  internal fun isWithinBoard(c: Coordinate): Boolean {
    return c.x >= 0 && c.y >= 0 && c.x < size && c.y < size
  }

  private fun onlyWithinBoard(coordinates: Coordinates): Coordinates {
    return coordinates.filter { isWithinBoard(it) }
  }

  private fun get(c: Coordinate): Byte = grid[c.y][c.x]

  internal fun isEmptyAt(c: Coordinate) = get(c) == EMPTY

  private fun isSameColor(c1: Coordinate, c2: Coordinate) = get(c1) == get(c2)

  fun placeStone(c: Coordinate, color: @Stone Byte) {
    grid[c.y][c.x] = color
  }

  internal fun placeStone(move: Move) = placeStone(move.c, move.color)

  internal fun simulateMove(move: Move) = copy().apply { placeStone(move) }

  private fun removeStone(c: Coordinate) {
    grid[c.y][c.x] = EMPTY
  }

  internal fun removeStones(coordinates: Coordinates) {
    coordinates.forEach {
      removeStone(it)
    }
  }

  internal fun adjacentCoordinates(c: Coordinate): Coordinates {
    // top, right, down, left
    return onlyWithinBoard(listOf(
        c.x to c.y - 1,
        c.x + 1 to c.y,
        c.x to c.y + 1,
        c.x - 1 to c.y
    ))
  }

  fun areAdjacent(c1: Coordinate, c2: Coordinate) = adjacentCoordinates(c1).contains(c2)

  fun sameColorAdjacent(c: Coordinate): Coordinates {
    return adjacentCoordinates(c).filter { isSameColor(c, it) }
  }

  fun group(c: Coordinate): Coordinates {
    if (isEmptyAt(c)) throw IllegalArgumentException("This coordinate is empty")
    val group = mutableSetOf(c)
    var prevSize = 0
    while (group.size > prevSize) {
      prevSize = group.size
      group.forEach { group.addAll(sameColorAdjacent(it)) }
    }

    return group.toList()
  }

  fun liberties(group: Coordinates): Coordinates {
    return group.flatMap {
      adjacentCoordinates(it)
          .filter { isEmptyAt(it) }
          .distinct()
    }
  }

  private fun surroundingGroups(group: Coordinates): Coordinates {
    val oppositeColor = if (get(group[0]) == BLACK) WHITE else BLACK
    return group.flatMap {
      adjacentCoordinates(it)
          .filter { get(it) == oppositeColor }
    }
  }

  fun atari(group: Coordinates) = liberties(group).size == 1

  private fun isSurrounded(c: Coordinate) = liberties(group(c)).isEmpty()

  private fun isSurrounded(c: Coordinates) = liberties(c).isEmpty()

  private fun isContiguous(group: Coordinates) = group(group[0]) == group

  internal fun isSuicide(move: Move): Boolean {
    with(simulateMove(move)) {
      val group = group(move.c)
      if (isSurrounded(group)) {
        if (isSurrounded(surroundingGroups(group))) {
          // If the group surrounding the inner group is itself surrounded, then the move is not suicide
          return false
        }
        return true
      }
      return false
    }
  }

  fun territory(player: @Stone Byte): Coordinates {
    TODO()
  }

  @VisibleForTesting
  internal constructor(
      string: String
  ) : this(Math.sqrt(string.trim().length.toDouble()).toInt()) {
    val charGrid = string.split('\n')
    charGrid.forEachIndexed { y, s ->
      s.forEachIndexed { x, c ->
        when(c) {
          'W' -> placeStone(x to y, WHITE)
          'B' -> placeStone(x to y, WHITE)
        }
      }
    }
  }

  override fun toString(): String {
    val s = StringBuilder()
    s.appendln()
    grid.forEach {
      it.forEach {
        when(it) {
          WHITE -> s.append('W')
          BLACK -> s.append('B')
          EMPTY -> s.append('O')
        }
      }
      s.appendln()
    }
    return s.toString()
  }
}
