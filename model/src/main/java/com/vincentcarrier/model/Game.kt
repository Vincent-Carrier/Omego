package com.vincentcarrier.model

import com.vincentcarrier.model.GameState.BLACK_WIN
import com.vincentcarrier.model.GameState.ONGOING
import com.vincentcarrier.model.GameState.WHITE_WIN
import com.vincentcarrier.model.PlayerType.HUMAN


class Game(
    size: Int = 19,
    blackPlayerType: PlayerType = HUMAN,
    whitePlayerType: PlayerType = HUMAN,
    private val komi: Float = 6.5f,
    private val handicap: Int = 0
) {
  val board = Board(size)

  var gameState = ONGOING
    private set

  private val history = History()

  val blackPlayer = Player(blackPlayerType, BLACK)
  val whitePlayer = Player(whitePlayerType, WHITE)
  val activePlayer get() = if (history.size % 2 == 0) blackPlayer else whitePlayer

  var blackScore = board.territory(BLACK).size.toFloat()
  var whiteScore = board.territory(WHITE).size + komi

  fun playTurn(c: Coordinate) = playTurn(Move(c, activePlayer.color))

  /**
  * @return true if turn was played or false if move was illegal
  */
  private fun playTurn(turn: Turn): Boolean {

    when (turn) {
      is Move -> if (isMoveLegal(turn)) board.placeStone(turn) else return false
      Pass -> if (history.peek().first == Pass) gameOver() // The game ends when both players are out of good moves
      Resign -> {
        gameState = when (activePlayer.color) {
          BLACK -> WHITE_WIN
          WHITE -> BLACK_WIN
          else -> throw IllegalStateException("Resigned when game was already over")
        }
        return true
      }
    }

    history.push(turn to board.grid)
    return true
  }

  private fun isMoveLegal(move: Move): Boolean {
    fun koRuleIsRespected(move: Move): Boolean {
      // A player is note allowed to play a move that would continue an infinite back and forth
      return !(history.last().second.contentEquals(board.simulateMove(move).grid))
    }

    return  with(board) { isWithinBoard(move.c)
                       && isEmptyAt(move.c)
                       && !isSuicide(move) }
                       && koRuleIsRespected(move)
  }

  private fun gameOver() {
    gameState = if (blackScore > whiteScore) BLACK_WIN else WHITE_WIN
  }
}


enum class GameState {
  ONGOING, BLACK_WIN, WHITE_WIN
}