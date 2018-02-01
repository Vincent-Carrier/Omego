package com.vincentcarrier.model

enum class PlayerType {
  HUMAN, COMPUTER
}

data class Player(val type: PlayerType, val color: @Stone Byte)