package com.vincentcarrier.klingon

import com.vincentcarrier.model.Game
import com.vincentcarrier.model.GameState.ONGOING
import com.vincentcarrier.model.Legality.LEGAL
import java.util.Scanner

fun main(args: Array<String>) {
  val game = Game()
  System.out.print(game.board)
  val scanner = Scanner(System.`in`)
  var userInput = ""
  while (game.state == ONGOING) {
    try {
      userInput = scanner.nextLine()
    } catch (e: Exception) { }
    if (userInput.matches("""\w\d""".toRegex())) {
      val legality = game.submitMove(userInput)
      if (legality == LEGAL) println(game.board) else println(legality.name)
    }
  }
}