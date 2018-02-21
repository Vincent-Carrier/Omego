package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Coordinate
import java.util.Arrays


typealias Grid = Array<ByteArray>

typealias Coordinates = List<Coordinate>

data class Board(
    val size: Int = 19,
    private val grid: Grid = Array(size) { ByteArray(size) { EMPTY } }
) {

  constructor(string: String) :
      this(Math.sqrt(string.trim().length.toDouble()).toInt()) {
    val charGrid = string.split('\n')
    charGrid.forEachIndexed { y, s ->
      s.forEachIndexed { x, char ->
        when (char) {
          'W' -> placeStone(x, y, WHITE)
          'B' -> placeStone(x, y, BLACK)
        }
      }
    }
  }

  init {
    if (grid.size != size) throw IllegalArgumentException("Constructed board must be same size as given size")
    grid.forEach { row ->
      if (row.size != grid.size) throw IllegalStateException("Board must be a square")
    }
  }

  companion object {
    const val EMPTY: Byte = 0
    const val BLACK: Byte = 1
    const val WHITE: Byte = 2
  }

  fun forEach(func: (Int, Int, @StoneOrEmpty Byte) -> Unit) {
    grid.forEachIndexed { y, row ->
      row.forEachIndexed { x, stone ->
          func(x, y, stone)
      }
    }
  }

  fun group(c: Coordinate): Coordinates {
    fun sameColorAdjacent(c: Coordinate): Coordinates = adjacentCoordinates(c).filter { c isSameColorAs it }

    if (isEmptyAt(c)) throw IllegalArgumentException("This coordinate is empty")
    val group = mutableListOf(c)
    var newCoordinates = sameColorAdjacent(c)
    while (newCoordinates.isNotEmpty())  {
      group += newCoordinates
      newCoordinates = newCoordinates.flatMap { sameColorAdjacent(it) }
                                     .filter { it !in group }
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

  internal fun simulateMove(move: Move, checkCondition: (Board) -> Boolean): Boolean {
    val virtualBoard = copy().apply { move.execute() }
    return checkCondition(virtualBoard)
  }

  internal fun territory(player: @Stone Byte): Coordinates {
    TODO()
  }

  internal fun isEmptyAt(c: Coordinate) = get(c) == EMPTY

  private fun get(c: Coordinate): @StoneOrEmpty Byte = grid[c.y][c.x]

  private fun placeStone(c: Coordinate, color: @Stone Byte) {
    grid[c.y][c.x] = color
  }

  private fun placeStone(x: Int, y: Int, color: @Stone Byte) = placeStone(c(x,y), color)

  private fun placeStone(move: Move) = placeStone(move.c, move.color)

  private fun adjacentCoordinates(c: Coordinate): Coordinates {
    // top, right, down, left
    return listOf(Coordinate(c.x, c.y + 1), Coordinate(c.x + 1, c.y),
                  Coordinate(c.x, c.y - 1), Coordinate(c.x - 1, c.y)).filter { it.isWithinBoard }
  }

  private infix fun Coordinate.isSameColorAs(c: Coordinate) = get(this) == get(c)

  private infix fun Coordinate.isOppositeColorOf(c: Coordinate) = get(this) == get(c).opposite

  private fun oppositeColorAdjacent(c: Coordinate): Coordinates {
    return adjacentCoordinates(c).filter { c isOppositeColorOf it }
  }

  private fun surroundingCoordinates(group: Coordinates): Coordinates {
    val oppositeColor = get(group[0]).opposite
    return group.flatMap {
      adjacentCoordinates(it)
          .filter { get(it) == oppositeColor }
          .distinct()
    }
  }

  private fun Coordinates.isSurrounded() = liberties(this).isEmpty()

  override fun toString(): String {
    operator fun Char.times(n: Int): String {
      return StringBuilder().apply {
        (0 until n).forEach { append(this@times) }
      }.toString()
    }

    return StringBuilder().apply {
      appendln()
      // Vertical coordinate i.e. ABCDE...
      val leftPadding = "$size".length + 1
      append(' ' * leftPadding)
      (0 until size).forEach { append(Character.toChars(65 + it)) }
      appendln()

      grid.forEachIndexed { y, row ->
        // Horizontal coordinate i.e. 19 18 17...
        val number = "${size - y}"
        val spaces = leftPadding - number.length
        append(number + ' ' * spaces)

        row.forEach {
          when (it) {
            WHITE -> append('W')
            BLACK -> append('B')
            EMPTY -> append('O')
          }
        }
        appendln()
      }
    }.toString()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Board

    if (size != other.size || !grid.contentEquals(other.grid)) return false

    return true
  }

  override fun hashCode(): Int {
    return Arrays.hashCode(grid)
  }

  inner class Coordinate internal constructor(val x: Int, val y: Int) {

    constructor(string: String) : this(
        string[0].toUpperCase() - 'A',
        size - Integer.parseInt(string.substring(1))
    )

    val isWithinBoard: Boolean
      get() = this.x >= 0 && this.y >= 0 && this.x < size && this.y < size

    override fun toString(): String = "${'A' + x}${size - y}"

    override fun equals(other: Any?): Boolean {
      return other is Coordinate && other.x == this.x && other.y == this.y
    }

    override fun hashCode(): Int {
      var result = x
      result = 31 * result + y
      return result
    }
  }

  fun c(x: Int, y: Int) = Coordinate(x,y)

  fun coordinates(vararg xy: Int): Coordinates {
    val xs = xy.filterIndexed { index, _ -> index % 2 == 0 }
    val ys = xy.filterIndexed { index, _ -> index % 2 == 1 }
    if (xs.size != ys.size) throw IllegalArgumentException("Must enter an even number of Ints")
    return xs.zip(ys) { x, y ->
      c(x,y)
    }
  }

  inner class Move(val c: Coordinate, val color: @Stone Byte) {
    fun execute(): Coordinates {
      fun removeStones(coordinates: Coordinates) {
        coordinates.forEach {
          grid[it.y][it.x] = EMPTY
        }
      }

      placeStone(this)
      // Remove any captured stones
      val removedStones = mutableListOf<Coordinate>()
      oppositeColorAdjacent(this.c).forEach {
        val group = group(it)
        if (group.isSurrounded()) {
          removeStones(group)
          removedStones.addAll(group)
        }
      }

      return removedStones
    }

    internal val isSuicide: Boolean
      get() {
        fun simulateMove(move: Move, checkCondition: (Board) -> Boolean): Boolean {
          val virtualBoard = copy().apply { move.execute() }
          return checkCondition(virtualBoard)
        }

        return simulateMove(this) {
          val group = group(this.c)

          if (!group.isSurrounded()) false
          // If the group surrounding the inner group is itself surrounded, then the move is not suicide
          else !(surroundingCoordinates(group).isSurrounded())
        }
      }

    override fun toString() = "$c - ${if (color == BLACK) "BLACK" else "WHITE"}"
  }
}