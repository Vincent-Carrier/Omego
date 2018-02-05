package com.vincentcarrier.model


typealias Grid = Array<ByteArray>

class Board(
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

  fun grid() = grid.clone() // Prevent other modules from modifying the board

  fun group(c: Coordinate): Coordinates {
    if (isEmptyAt(c)) throw IllegalArgumentException("This coordinate is empty")
    var group = setOf(c)
    var prevSize = 0
    while (group.size > prevSize) {
      prevSize = group.size
      group.forEach { group = group.plus(sameColorAdjacent(it)) }
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

  internal fun executeMove(move: Move) {
    placeStone(move)
    // Remove any captured stones
    oppositeColorAdjacent(move.c).forEach {
      if (it.isSurrounded()) removeStones(group(it))
    }
  }

  internal fun simulateMove(move: Move) = Board(size, grid).apply { placeStone(move) }

  internal fun isSuicide(move: Move): Boolean {
    with(simulateMove(move)) {
      val group = group(move.c)
      // If the group surrounding the inner group is itself surrounded, then the move is not suicide
      return if (group.isSurrounded()) !(surroundingGroups(group).isSurrounded()) else false
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

  private fun removeStone(c: Coordinate) {
    grid[c.y][c.x] = EMPTY
  }

  private fun removeStones(coordinates: Coordinates) {
    coordinates.forEach {
      removeStone(it)
    }
  }

  private fun onlyWithinBoard(coordinates: Coordinates): Coordinates {
    return coordinates.filter { isWithinBoard(it) }
  }

  private fun adjacentCoordinates(c: Coordinate): Coordinates {
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

  private fun sameColorAdjacent(c: Coordinate): Coordinates {
    return adjacentCoordinates(c).filter { c isSameColorAs it }
  }

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
}
