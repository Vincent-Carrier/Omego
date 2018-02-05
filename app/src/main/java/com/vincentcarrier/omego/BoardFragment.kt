package com.vincentcarrier.omego

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_board.boardView

class BoardFragment : Fragment() {

  private val vm by lazy { ViewModelProviders.of(this).get(BoardViewModel::class.java) }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.fragment_board, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    fun enterFullscreen() {
          view?.systemUiVisibility =
          SYSTEM_UI_FLAG_LAYOUT_STABLE or
          SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
          SYSTEM_UI_FLAG_HIDE_NAVIGATION or
          SYSTEM_UI_FLAG_FULLSCREEN or
          SYSTEM_UI_FLAG_IMMERSIVE
    }

    fun setUpBoardView() {
      with(boardView) {
        board = vm.game.board
        setOnTouchListener { _, event ->
          val c = pixelToCoordinate(event.x, event.y)
          if (vm.game.playMove(c)) invalidate()
          true
        }
      }
    }

    super.onActivityCreated(savedInstanceState)
    enterFullscreen()
    setUpBoardView()
  }
}
