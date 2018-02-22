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
import com.vincentcarrier.omego.MainActivity
import com.vincentcarrier.omego.R.layout
import kotlinx.android.synthetic.main.fragment_board.boardView
import org.jetbrains.anko.toast

class BoardFragment : Fragment() {
  companion object {
    const val TAG = "BOARD"
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
    return inflater.inflate(layout.fragment_board, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    fun setUpBoardView() {
      with(boardView) {
        val vm = (activity as MainActivity).vm
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
    setUpBoardView()
  }
}
