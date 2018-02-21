package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Companion.BLACK
import com.vincentcarrier.model.Legality.KO_RULE_BROKEN
import com.vincentcarrier.model.Legality.LEGAL
import org.amshove.kluent.shouldBe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object GameSpec : SubjectSpek<Game> ({
  subject { Game(
      Board("""
      WWOBWO
      WOOOBW
      OOOOOB
      OWBOOO
      WOWBOB
      OWBOBO
      """.trimIndent())
  ) }

  with(subject) {
    it("should be black's turn to play at the start of a game") {
      activePlayer.color shouldBe BLACK
    }

    on("a move at B5") {
      val legality = submitMove("B5")

      it("should accept legal moves") {
        legality shouldBe LEGAL
      }

      it("should execute legal moves") {
        board.isEmptyAt(board.Coordinate("B5")) shouldBe false
      }
    }

    it("should not break the Ko rule") {
      /* It seems Spek doesn't run tests in parallel when nested into a with() statement, so we must play
      * a turn as white just to be able to play as black on the next */
      submitMove("A4") shouldBe LEGAL
      submitMove("B2") shouldBe LEGAL
      submitMove("C2") shouldBe LEGAL
      submitMove("B2") shouldBe KO_RULE_BROKEN
    }
  }
})