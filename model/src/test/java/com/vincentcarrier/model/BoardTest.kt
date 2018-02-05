package com.vincentcarrier.model

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec

class BoardTest : StringSpec() {
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
      board1.atari(listOf(0 to 3)) shouldEqual true
      board2.atari(listOf(4 to 0)) shouldEqual true
      board2.atari(listOf(2 to 0)) shouldEqual false
    }

    "executeMove() should remove captured stones" {
      board2.executeMove(Move(4,0, BLACK))
      board2.isEmptyAt(3 to 0) shouldEqual true
      board2.isEmptyAt(4 to 1) shouldEqual true
    }
  }
}