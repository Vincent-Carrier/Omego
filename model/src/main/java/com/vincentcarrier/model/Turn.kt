package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Move

sealed class Turn {
  class Play(val move: Move) : Turn()
  object Pass : Turn()
  object Resign : Turn()

  override fun toString(): String {
    return when (this) {
      is Play -> "$move"
      Pass -> "PASS"
      Resign -> "RESIGN"
    }
  }
}