package com.vincentcarrier.omego

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.model.Board
import com.vincentcarrier.model.Game


class BoardViewModel : ViewModel() {
  internal val game = Game(
      board = Board()
  ).apply {
    submitMove("A18")
    submitMove("B15")
    submitMove("C8")
  }
}