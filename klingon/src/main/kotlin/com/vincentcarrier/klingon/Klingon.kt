package com.vincentcarrier.klingon

import com.vincentcarrier.model.Game
import com.vincentcarrier.model.GameState.ONGOING
import java.util.NoSuchElementException
import java.util.Scanner

fun main(args: Array<String>) {
  val game = Game()
  System.out.print(game.board)
  val sc = Scanner(System.`in`)
  while (game.state == ONGOING) {
    try {
      val legality = game.submitMove(sc.nextLine())
      println(legality.name ?: game.board)
    } catch (e: NoSuchElementException) {  }
  }
}