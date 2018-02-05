package com.vincentcarrier.omego

import android.arch.lifecycle.ViewModel
import com.vincentcarrier.model.BLACK
import com.vincentcarrier.model.Board
import com.vincentcarrier.model.Game
import com.vincentcarrier.model.Move
import com.vincentcarrier.model.WHITE


class BoardViewModel : ViewModel() {
  internal val game = Game(
      board = Board(19, listOf(
          Move(3,4, BLACK),
          Move(5,6, WHITE),
          Move(4,4, BLACK)
      ))
  )
}