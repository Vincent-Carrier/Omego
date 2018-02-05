package com.vincentcarrier.model

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec


class GameTest : StringSpec() {
  val game = Game(
      board = Board("""
  OOOOO
  OBWBO
  BWOWO
  OBWBO
  OOOOO
  """.trimIndent())
  )

  init {
    "submitMove() should return false when Ko rule is broken" {
      println(game.board)
      println(listOf(2 to 2).toString(5))
      game.submitMove(2 to 2) shouldEqual true
//      game.submitMove(1 to 2) shouldEqual false
    }
  }
}