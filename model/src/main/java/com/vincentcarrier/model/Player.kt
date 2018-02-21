package com.vincentcarrier.model

import com.vincentcarrier.model.Board.Companion.BLACK


data class Player(
    val type: PlayerType,
    val color: @Stone Byte
) {
  var score: Float = 0f
    internal set

  var prisoners = 0
    internal set

  override fun toString() = if (color == BLACK) "BLACK" else "WHITE"
}

enum class PlayerType {
  HUMAN, COMPUTER
}