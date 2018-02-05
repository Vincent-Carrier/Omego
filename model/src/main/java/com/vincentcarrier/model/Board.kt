package com.vincentcarrier.model

import java.util.Arrays


typealias Grid = Array<ByteArray>

data class Board(
    val size: Int = 19,
    private val grid: Grid = Array(size) { ByteArray(size) { EMPTY } }
) {
  constructor(string: String) :
      this(Math.sqrt(string.trim().length.toDouble()).toInt()) {
    val charGrid = string.split('\n')
    charGrid.forEachIndexed { y, s ->
      s.forEachIndexed { x, c ->
        when (c) {
          'W' -> placeStone(x to y, WHITE)
          'B' -> placeStone(x to y, BLACK)
        }
      }
    }
  }

  constructor(size: Int, moves: List<Move>) : this(size) {
    moves.forEach { placeStone(it) }
  }

  init {
    if (grid.size != size) throw IllegalArgumentException("Constructed board must be same size as given size")
    grid.forEach { row ->
      if (row.size != grid.size) throw IllegalStateException("Board must be a square")
    }
  }

  fun grid() = grid.clone() // Prevent other modules from modifying the grid's content

  fun group(c: Coordinate): Coordinates {
    fun sameColorAdjacent(c: Coordinate): Coordinates {
      return adjacentCoordinates(c).filter { c isSameColorAs it }
    }

    if (isEmptyAt(c)) throw IllegalArgumentException("This coordinate is empty")
    var group = setOf(c)
    var prevSize = 0
    while (group.size > prevSize) {
      prevSize = group.size
      group.forEach { group = group + sameColorAdjacent(it) }
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

  fun atari(group: Coordinates) = liberties(group).size == 1

  internal fun isWithinBoard(c: Coordinate): Boolean {
    return c.x >= 0 && c.y >= 0 && c.x < size && c.y < size
  }

  internal fun isEmptyAt(c: Coordinate) = get(c) == EMPTY

  internal fun executeMove(move: Move): Coordinates {
    fun removeStones(coordinates: Coordinates) {
      coordinates.forEach {
        grid[it.y][it.x] = EMPTY
      }
    }

    placeStone(move)
    // Remove any captured stones
    val removedStones = mutableListOf<Coordinate>()
    oppositeColorAdjacent(move.c).forEach {
      val group = group(it)
      if (group.isSurrounded()) removeStones(group); removedStones += group
    }

    return removedStones
  }

  internal fun simulateMove(move: Move, checkCondition: (Board) -> Boolean): Boolean {
    val virtualBoard = copy(size, grid).apply { placeStone(move) }

    return checkCondition(virtualBoard)
  }

  internal fun isSuicide(move: Move): Boolean {
    return simulateMove(move) {
      val group = group(move.c)
      // If the group surrounding the inner group is itself surrounded, then the move is not suicide
      if (!group.isSurrounded()) false else !(surroundingGroups(group).isSurrounded())
    }
  }

  internal fun territory(player: @Stone Byte): Coordinates {
    TODO()
  }

  private fun get(c: Coordinate): Byte = grid[c.y][c.x]

  private fun placeStone(c: Coordinate, color: @Stone Byte) {
    if (isEmptyAt(c)) grid[c.y][c.x] = color
    else throw IllegalStateException("There's already a stone there")
  }

  private fun placeStone(move: Move) = placeStone(move.c, move.color)

  private fun adjacentCoordinates(c: Coordinate): Coordinates {
    fun onlyWithinBoard(coordinates: Coordinates): Coordinates {
      return coordinates.filter { isWithinBoard(it) }
    }
    // top, right, down, left
    return onlyWithinBoard(listOf(
        c.x to c.y - 1,
        c.x + 1 to c.y,
        c.x to c.y + 1,
        c.x - 1 to c.y
    ))
  }

  private infix fun Coordinate.isAdjacentTo(c: Coordinate) = adjacentCoordinates(this).contains(c)

  private infix fun Coordinate.isSameColorAs(c: Coordinate) = get(this) == get(c)

  private infix fun Coordinate.isOppositeColorOf(c: Coordinate) = get(this) == get(c).opposite

  private fun oppositeColorAdjacent(c: Coordinate): Coordinates {
    return adjacentCoordinates(c).filter { c isOppositeColorOf it }
  }

  private fun surroundingGroups(group: Coordinates): Coordinates {
    val oppositeColor = get(group[0]).opposite
    return group.flatMap {
      adjacentCoordinates(it)
          .filter { get(it) == oppositeColor }
    }
  }

  private fun Coordinates.isSurrounded() = liberties(this).isEmpty()

  private fun Coordinate.isSurrounded() = group(this).isSurrounded()

  private fun isContiguous(group: Coordinates) = group(group[0]) == group

  override fun toString(): String {
    val sb = StringBuilder()
    sb.appendln()
    grid.forEach {
      it.forEach {
        when (it) {
          WHITE -> sb.append('W')
          BLACK -> sb.append('B')
          EMPTY -> sb.append('O')
        }
      }
      sb.appendln()
    }
    return sb.toString()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Board

    if (size != other.size) return false
    if (!Arrays.equals(grid, other.grid)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = size
    result = 31 * result + Arrays.hashCode(grid)
    return result
  }
}