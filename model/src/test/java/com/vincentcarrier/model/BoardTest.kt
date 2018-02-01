package com.vincentcarrier.model

import com.vincentcarrier.model.TestCase.blackGroup
import com.vincentcarrier.model.TestCase.whiteGroup
import com.vincentcarrier.model.TestCase.whiteLiberties
import io.kotlintest.matchers.shouldEqual
import org.junit.Test

class BoardTest {


  @Test
  fun adjacent() {
    TestCase.board.adjacentCoordinates(0 to 0) shouldEqual setOf(0 to 1, 1 to 0)
  }

  @Test
  fun sameColorAdjacent() {
    TestCase.board.sameColorAdjacent(4 to 0) shouldEqual setOf(3 to 0, 4 to 1)
  }

  @Test
  fun group() {
    TestCase.board.group(blackGroup.first()) shouldEqual blackGroup
  }

  @Test
  fun liberties() {
    TestCase.board.liberties(whiteGroup) shouldEqual whiteLiberties
  }
}