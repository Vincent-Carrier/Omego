package com.vincentcarrier.omego.board

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vincentcarrier.model.Legality.KO_RULE_BROKEN
import com.vincentcarrier.model.Legality.LEGAL
import com.vincentcarrier.model.Legality.OCCUPIED
import com.vincentcarrier.model.Legality.OUTSIDE
import com.vincentcarrier.model.Legality.SUICIDE
import com.vincentcarrier.omego.R
import com.vincentcarrier.omego.mainVm
import com.vincentcarrier.omego.toast
import kotlinx.android.synthetic.main.fragment_board.boardView

class BoardFragment : Fragment() {
  companion object {
    const val TAG = "BOARD"
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
    return inflater.inflate(R.layout.fragment_board, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    fun setUpBoardView() {
      boardView.board = mainVm.game.board
      boardView.onBoardTouched = { coordinate ->
        if (mainVm.game.isHumansTurn) {
          val legality = mainVm.game.submitMove(coordinate)
          when (legality) {
            OUTSIDE -> toast("Outside the board")
            OCCUPIED -> toast("Occupied")
            SUICIDE -> toast("Suicide")
            KO_RULE_BROKEN -> toast("Circular")
          }
          legality == LEGAL
        } else false
      }
    }

    super.onActivityCreated(savedInstanceState)
    setUpBoardView()
  }
}