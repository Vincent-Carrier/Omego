package com.vincentcarrier.omego

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vincentcarrier.model.BLACK
import com.vincentcarrier.model.Coordinate
import com.vincentcarrier.model.Game
import com.vincentcarrier.model.WHITE
import kotlinx.android.synthetic.main.activity_main.boardView

class MainActivity : AppCompatActivity() {

  private val game = Game().apply {
    board.placeStone(Coordinate(3,3), BLACK)
    board.placeStone(Coordinate(4,4), BLACK)
    board.placeStone(Coordinate(4,5), WHITE)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    boardView.game = game
  }
}
