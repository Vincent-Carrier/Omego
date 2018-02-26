package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Companion.BLACK
import com.vincentcarrier.model.Board.Companion.WHITE
import com.vincentcarrier.model.Board.Coordinate
import com.vincentcarrier.model.Board.Move
import com.vincentcarrier.model.GameState.BLACK_WIN
import com.vincentcarrier.model.GameState.ONGOING
import com.vincentcarrier.model.GameState.WHITE_WIN
import com.vincentcarrier.model.Legality.KO_RULE_BROKEN
import com.vincentcarrier.model.Legality.LEGAL
import com.vincentcarrier.model.Legality.OCCUPIED
import com.vincentcarrier.model.Legality.OUTSIDE
import com.vincentcarrier.model.Legality.SUICIDE
import com.vincentcarrier.model.PlayerType.HUMAN
import com.vincentcarrier.model.Turn.Pass
import com.vincentcarrier.model.Turn.Play
import com.vincentcarrier.model.Turn.Resign
import java.util.Stack

typealias History = Stack<Moment>

class Game(
    board: Board = Board(),
    blackPlayerType: PlayerType = HUMAN,
    whitePlayerType: PlayerType = HUMAN,
    private val komi: Float = 6.5f, // White's compensation for playing second
    private val handicap: Int = 0 // Number of stones black can place at the beginning
) {
  var board = board
    private set

  var state = ONGOING
    private set

  val blackPlayer = Player(blackPlayerType, BLACK)
  val whitePlayer = Player(whitePlayerType, WHITE)
  val activePlayer get() = if (history.size % 2 == 0) blackPlayer else whitePlayer
  val isHumansTurn get() = activePlayer.type == HUMAN

  private val history = History()
  private val undoHistory = History()

  fun submitMove(c: Coordinate) = submitTurn(Play(board.Move(c, activePlayer.color)))

  fun submitMove(string: String) = submitMove(board.Coordinate(string))

  fun pass() = submitTurn(Pass)

  fun resign() = submitTurn(Resign)

  fun undo() {
    undoHistory.push(history.pop())
    board = history.peek().board
  }

  fun redo() {
    history.push(undoHistory.pop())
    board = history.peek().board
  }

  private fun submitTurn(turn: Turn): Legality {
    fun legality(move: Move): Legality {
      fun koRuleIsRespected(move: Move): Boolean {
        // A player is note allowed to play a move that would continue an infinite back and forth
        return move.simulate {
          history.search(Moment(Play(move), it)) == -1 // TODO: Optimize this
        }
      }

      return when {
        !move.c.isWithinBoard -> OUTSIDE
        !board.isEmptyAt(move.c) -> OCCUPIED
        move.isSuicide() -> SUICIDE
        !koRuleIsRespected(move) -> KO_RULE_BROKEN
        else -> LEGAL
      }
    }
    fun gameOver() {
      blackPlayer.score = board.territory(BLACK).size.toFloat()
      whitePlayer.score = board.territory(WHITE).size.toFloat() + komi
      state = if (blackPlayer.score > whitePlayer.score) BLACK_WIN else WHITE_WIN
    }

    when (turn) {
      is Play -> {
        val legality = legality(turn.move)
        if (legality == LEGAL) {
          activePlayer.prisoners += turn.move.execute().size
        } else {
          println("$turn was refused: $legality")
          return legality
        }
      }
      Pass -> if (history.peek().turn == Pass) gameOver()
      Resign -> {
        state = when (activePlayer.color) {
          BLACK -> WHITE_WIN
          WHITE -> BLACK_WIN
          else -> throw IllegalStateException("Resigned when game was already over")
        }
      }
    }
    history.push(Moment(turn, board))
    println("$turn was played")
    return LEGAL
  }
}

data class Moment(val turn: Turn, val board: Board)

enum class GameState {
  ONGOING, BLACK_WIN, WHITE_WIN
}

enum class Legality {
  LEGAL, OUTSIDE, OCCUPIED, SUICIDE, KO_RULE_BROKEN
}