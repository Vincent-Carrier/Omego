package com.vincentcarrier.model

import com.vincentcarrier.model.GameState.BLACK_WIN
import com.vincentcarrier.model.GameState.ONGOING
import com.vincentcarrier.model.GameState.WHITE_WIN
import com.vincentcarrier.model.PlayerType.HUMAN


class Game(
    size: Int = 19,
    blackPlayerType: PlayerType = HUMAN,
    whitePlayerType: PlayerType = HUMAN,
    private val komi: Float = 6.5f, // White's compensation for playing second
    private val handicap: Int = 0 // Number of stones black can place at the beginning
) {

  val board = Board(size)

  var gameState = ONGOING
    private set

  val blackPlayer = Player(blackPlayerType, BLACK)

  val whitePlayer = Player(whitePlayerType, WHITE)

  val activePlayer get() = if (history.size % 2 == 0) blackPlayer else whitePlayer

  private val history = History()

  fun isHumansTurn() = activePlayer.type == HUMAN

  fun playTurn(c: Coordinate) = playTurn(Move(c, activePlayer.color))

  /**
  * @return true if turn was played or false if move was illegal
  */
  private fun playTurn(turn: Turn): Boolean {
    when (turn) {
      is Move -> if (isMoveLegal(turn)) board.placeStone(turn) else return false
      Pass -> if (history.peek().turn == Pass) gameOver() // The game ends when both players are out of good moves
      Resign -> {
        gameState = when (activePlayer.color) {
          BLACK -> WHITE_WIN
          WHITE -> BLACK_WIN
          else -> throw IllegalStateException("Resigned when game was already over")
        }
      }
    }

    history.push(Moment(turn, board.grid))
    return true
  }

  private fun isMoveLegal(move: Move): Boolean {
    fun koRuleIsRespected(move: Move): Boolean {
      // A player is note allowed to play a move that would continue an infinite back and forth
      return !(history.last.grid contentEquals board.simulateMove(move).grid)
    }

    return  with(board) { isWithinBoard(move.c)
                       && isEmptyAt(move.c)
                       && !isSuicide(move) }
                       && koRuleIsRespected(move)
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