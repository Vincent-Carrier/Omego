package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Companion.BLACK
import com.vincentcarrier.model.Board.Companion.WHITE
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeIn
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

object BoardSpec : SubjectSpek<Board>({

  subject {
    Board("""
      WWOBWO
      WOOOBW
      OOOOOB
      OWBOOO
      WOWBOB
      OWBOBO
      """.trimIndent())
  }

  with(subject) {
    it("should have Coordinate.toString() return the proper output") {
      c(0,0).toString() shouldBeEqualTo "A6"
    }

    it("should have Coordinate() take a string and return the appropriate coordinate") {
      Coordinate("A6") shouldEqual c(0,0)
    }

    it("should have coordinates() group pairs of Ints correctly into new coordinates") {
      coordinates(0,0, 1,1) shouldContainAll listOf(c(0,0), c(1,1))
    }

    it("should have isEmptyAt() return true if coordinate is empty") {
      isEmptyAt(c(2,0)) shouldBe true
    }

    it("should have isEmptyAt() return false if coordinate is occupied") {
      isEmptyAt(c(1,0)) shouldBe false
    }

    it("should group stones properly") {
      group(c(0,0)) shouldContainAll coordinates(0,0, 1,0, 0,1)
    }

    it("should have atari() return true when a group is in atari") {
      atari(coordinates(4,0)) shouldBe true
    }

    it("should have liberties() return the correct liberties") {
      liberties(coordinates(0,0, 1,0, 0,1)) shouldContainAll coordinates(0,2, 1,1, 2,0)
    }

    it("should have isSuicide() return true if a move is suicide") {
      Move(c(5,5), WHITE).isSuicide() shouldBe true
    }

    it("should have isSuicide() return false if a move is not suicide") {
      Move(c(5,0), BLACK).isSuicide() shouldBe false
    }

    it("should remove stones when executeMove() captures them") {
      Move(c(5,0), BLACK).execute() shouldBeIn coordinates(4,0, 5,1)
//      isEmptyAt(c(5,0)) shouldBe false
//      isEmptyAt(c(4,0)) shouldBe true
//      isEmptyAt(c(5,1)) shouldBe true
    }
  }
})