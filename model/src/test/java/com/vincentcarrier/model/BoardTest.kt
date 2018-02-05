package com.vincentcarrier.model

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.StringSpec

class BoardTest : StringSpec() {
  val board1 = Board("""
  OOOWW
  BBOOW
  OBBOW
  WBOOO
  BBOWW
  """.trimIndent())

  val topWhiteGroup = listOf(3 to 0, 4 to 0,
      4 to 1,
      4 to 2)

  val blackGroup = listOf(0 to 1, 1 to 1, 1 to 2, 2 to 2, 1 to 3, 0 to 4, 1 to 4)

  val topWhiteLiberties = listOf(2 to 0, 3 to 1, 3 to 2, 4 to 3)

  val board2 = Board("""
  OOBWO
  BBOBW
  OBBOB
  WBOOO
  BBOWW
  """.trimIndent())

  init {
    "group() should return all coordinates of a group" {
      board1.group(0 to 1).toSet() shouldEqual blackGroup.toSet()
    }

    "liberties() should return the correct coordinates" {
      board1.liberties(topWhiteGroup).toSet() shouldEqual topWhiteLiberties.toSet()
    }

    "isSuicide() should return true for suicide move" {
      board1.isSuicide(Move(0,2, WHITE)) shouldEqual true
    }

    "isSuicide() should return false if surrounding group is surrounded" {
      board2.isSuicide(Move(4,0, BLACK)) shouldEqual false
    }

    "atari() should return true if group is in atari" {
      val table = table(
          headers("board", "group", "result"),
          row(board1, listOf(0 to 3), true),
          row(board2, listOf(3 to 0), true),
          row(board2, listOf(2 to 0), false)
      )

      forAll(table) { board, group, result ->
        board.atari(group) shouldEqual result
      }
    }

    "executeMove() should remove captured stones" {
      board2.executeMove(Move(4,0, BLACK))
      board2.isEmptyAt(3 to 0) shouldEqual true
      board2.isEmptyAt(4 to 1) shouldEqual true
    }
  }
}