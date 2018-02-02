package com.vincentcarrier.model


data class Player(
    val type: PlayerType,
    val color: @Stone Byte
) {
  var score: Float = 0f
    internal set
}

enum class PlayerType {
  HUMAN, COMPUTER
}