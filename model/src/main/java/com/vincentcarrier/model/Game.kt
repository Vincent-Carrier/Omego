package com.vincentcarrier.model

import com.vincentcarrier.model.GameState.BLACK_WIN
import com.vincentcarrier.model.GameState.ONGOING
import com.vincentcarrier.model.GameState.WHITE_WIN
import com.vincentcarrier.model.PlayerType.HUMAN
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class Game(
    val board: Board = Board(),
    blackPlayerType: PlayerType = HUMAN,
    whitePlayerType: PlayerType = HUMAN,
    private val komi: Float = 6.5f, // White's compensation for playing second
    private val handicap: Int = 0 // Number of stones black can place at the beginning
) {

  private val logger = AnkoLogger<Board>()

  var gameState = ONGOING
    private set

  val blackPlayer = Player(blackPlayerType, BLACK)

  val whitePlayer = Player(whitePlayerType, WHITE)

  val activePlayer get() = if (history.size % 2 == 0) blackPlayer else whitePlayer

  private val history = History()

  fun isHumansTurn() = activePlayer.type == HUMAN

  fun submitMove(c: Coordinate) = submitTurn(Move(c, activePlayer.color))

  fun submitTurn(turn: Turn): Legality {
    when (turn) {
      is Move -> {
        val legality = turn.isLegal()
        if (legality.isLegal) {
          activePlayer.prisoners += board.executeMove(turn).size
        } else println(legality.explanation); return legality
      }
      Pass -> if (history.peek().turn == Pass) gameOver() // The game ends when both players are out of good moves
      Resign -> {
        gameState = when (activePlayer.color) {
          BLACK -> WHITE_WIN
          WHITE -> BLACK_WIN
          else -> throw IllegalStateException("Resigned when game was already over")
        }
      }
    }
    history.push(Moment(turn, board))
    logger.info("$turn was played")
    return Legality(true, null)
  }

  private fun Move.isLegal(): Legality {
    fun koRuleIsRespected(move: Move): Boolean {
      // A player is note allowed to play a move that would continue an infinite back and forth
      return board.simulateMove(move) {
        it != history.last?.board
      }
    }

    val withinBoard = board.isWithinBoard(this.c)
    val emptySpot = board.isEmptyAt(this.c)
    val isNotSuicide = !board.isSuicide(this)
    val koRuleIsRespected = koRuleIsRespected(this)

    val isLegal = withinBoard && emptySpot && isNotSuicide && koRuleIsRespected

    val explanation = when {
      isLegal -> null
      !withinBoard -> "Cannot play a stone outside the board"
      !emptySpot -> "Cannot play on an occupied spot"
      !isNotSuicide -> "Cannot play a move that would involve suicide"
      else -> "Cannot play a move that would repeat the previous state"
    }

    return Legality(isLegal, explanation)
  }

  private fun gameOver() {
    blackPlayer.score = board.territory(BLACK).size.toFloat()
    whitePlayer.score = board.territory(WHITE).size.toFloat() + komi
    gameState = if (blackPlayer.score > whitePlayer.score) BLACK_WIN else WHITE_WIN
  }
}


enum class GameState {
  ONGOING, BLACK_WIN, WHITE_WIN
}

data class Legality(val isLegal: Boolean, val explanation: String?)