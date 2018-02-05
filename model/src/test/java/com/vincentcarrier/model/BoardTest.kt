package com.vincentcarrier.model

import io.kotlintest.matchers.shouldBe
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

    "executeMove() should remove captured stones" {
      board2.executeMove(Move(4,0, BLACK))
      (board2.grid()[0][4] == BLACK) shouldBe true
      board2.isEmptyAt(3 to 0) shouldBe true
      board2.isEmptyAt(4 to 1) shouldBe true
    }

    "isSuicide() should return true for suicide move, false otherwise" {
      val table = table(
          headers("board", "move", "result"),
          row(board1, Move(0 to 2, WHITE), true),
          row(board2, Move(4 to 0, BLACK), false)
      )

      forAll(table) { board, move, result ->
        board.isSuicide(move) shouldEqual result
      }
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



    "simulateMove() should create a new board and execute the given move" {
//      val virtualBoard = board1.simulateMove(Move(2 to 0, WHITE))
//      virtualBoard shouldNotBe board1
//      println(virtualBoard)
//      println(board1)
    }
  }
}