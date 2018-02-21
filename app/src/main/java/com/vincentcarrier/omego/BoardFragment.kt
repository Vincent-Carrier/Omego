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
import com.vincentcarrier.model.Legality.KO_RULE_BROKEN
import com.vincentcarrier.model.Legality.LEGAL
import com.vincentcarrier.model.Legality.OCCUPIED
import com.vincentcarrier.model.Legality.OUTSIDE
import com.vincentcarrier.model.Legality.SUICIDE
import kotlinx.android.synthetic.main.fragment_board.boardView
import org.jetbrains.anko.toast

class BoardFragment : Fragment() {

  private val vm by lazy {
    ViewModelProviders.of(this).get(BoardViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
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
        onCellTouched = { coordinate ->
          if (vm.game.isHumansTurn()) {
            val legality = vm.game.submitMove(coordinate)
            when (legality) {
              OUTSIDE -> context.toast("Outside the board")
              OCCUPIED -> context.toast("Occupied")
              SUICIDE -> context.toast("Suicide")
              KO_RULE_BROKEN -> context.toast("Circular")
            }
            legality == LEGAL
          } else false
        }
      }
    }

    super.onActivityCreated(savedInstanceState)
    enterFullscreen()
    setUpBoardView()
  }
}
