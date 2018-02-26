package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Coordinate
import kotlin.math.sqrt


typealias Grid = Array<ByteArray>

typealias Coordinates = List<Coordinate>

data class Board (val size: Int = 19) {

  companion object {
    const val EMPTY: Byte = 0
    const val BLACK: Byte = 1
    const val WHITE: Byte = 2
  }

  // For testing
  internal constructor(string: String) : this(sqrt(string.length.toFloat()).toInt()) {
    val charGrid = string.split('\n')
    charGrid.forEachIndexed { y, s ->
      s.forEachIndexed { x, char ->
        when (char) {
          'W' -> placeStone(x, y, WHITE)
          'B' -> placeStone(x, y, BLACK)
          'O' -> { }
          else -> throw IllegalArgumentException("Did not expect character $char")
        }
      }
    }
  }

  private val grid: Grid = Array(size) { ByteArray(size) { EMPTY } }

  init {
    if (grid.size != size) throw IllegalArgumentException("Constructed board must be same size as given size")
    grid.forEach { row ->
      if (row.size != grid.size) throw IllegalStateException("Board must be a square")
    }
  }

  fun c(x: Int, y: Int) = Coordinate(x,y)

  fun forEach(func: (c: Coordinate, @StoneOrEmpty Byte) -> Unit) {
    grid.forEachIndexed { y, row ->
      row.forEachIndexed { x, stone ->
          func(c(x,y), stone)
      }
    }
  }

  fun group(c: Coordinate): Coordinates {
    fun sameColorAdjacent(c: Coordinate): Coordinates {
      infix fun Coordinate.isSameColorAs(c: Coordinate) = grid[this] == grid[c]

      return adjacentCoordinates(c).filter { c isSameColorAs it }
    }

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

  internal fun territory(player: @Stone Byte): Coordinates {
    TODO()
  }

  internal fun isEmptyAt(c: Coordinate) = grid[c] == EMPTY

  private operator fun Grid.get(c: Coordinate): @StoneOrEmpty Byte = this[c.y][c.x]

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

  private fun Coordinates.isSurrounded() = liberties(this).isEmpty()

  inner class Coordinate internal constructor(val x: Int, val y: Int) {
    constructor(string: String) : this(
        string[0].toUpperCase() - 'A',
        size - Integer.parseInt(string.substring(1))
    )

    val isWithinBoard get() = x >= 0 && y >= 0 && x < size && y < size

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

  inner class Move(val c: Coordinate, val color: @Stone Byte) {
    fun execute(): Coordinates {
      // TODO: Fix bug where a stone may reappear after the move was refused
      fun removeStones(coordinates: Coordinates) {
        coordinates.forEach {
          grid[it.y][it.x] = EMPTY
        }
      }
      fun oppositeColorAdjacent(c: Coordinate): Coordinates {
        infix fun Coordinate.isOppositeColorOf(c: Coordinate) = grid[this] == grid[c].opposite

        return adjacentCoordinates(c).filter { c isOppositeColorOf it }
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

    internal fun simulate(checkCondition: (Board) -> Boolean): Boolean {
      val virtualBoard = copy().apply { this@Move.execute() }
      return checkCondition(virtualBoard)
    }

    internal fun isSuicide(): Boolean {
      fun surroundingCoordinates(group: Coordinates): Coordinates {
        val oppositeColor = grid[group[0]].opposite
        return group.flatMap {
          adjacentCoordinates(it)
              .filter { grid[it] == oppositeColor }
              .distinct()
        }
      }

      return simulate {
        val group = group(this.c)

        if (!group.isSurrounded()) false
        // If the group surrounding the inner group is itself surrounded, then the move is not suicide
        else !(surroundingCoordinates(group).isSurrounded())
      }
    }

    override fun toString() = "$c - ${if (color == BLACK) "BLACK" else "WHITE"}"
  }

  // For testing
  internal fun coordinates(vararg xy: Int): Coordinates {
    val xs = xy.filterIndexed { index, _ -> index % 2 == 0 }
    val ys = xy.filterIndexed { index, _ -> index % 2 == 1 }
    if (xs.size != ys.size) throw IllegalArgumentException("Must enter an even number of Ints")
    return xs.zip(ys) { x, y ->
      c(x,y)
    }
  }

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
}