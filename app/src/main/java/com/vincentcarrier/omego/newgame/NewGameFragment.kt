package com.vincentcarrier.omego.newgame

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vincentcarrier.model.Game
import com.vincentcarrier.omego.R
import com.vincentcarrier.omego.board.BoardFragment
import com.vincentcarrier.omego.mainVm
import com.vincentcarrier.omego.transaction
import kotlinx.android.synthetic.main.fragment_new_game.boardSizeButtonGroup
import kotlinx.android.synthetic.main.fragment_new_game.startGameButton


class NewGameFragment : Fragment() {
  companion object {
    const val TAG = "NEW_GAME"
  }

  init {
    retainInstance = true
  }

  val vm by lazy {
    ViewModelProviders.of(this).get(NewGameViewModel::class.java)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_new_game, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    boardSizeButtonGroup.setOnCheckedChangeListener { _, checkedId ->
      vm.boardSize = when (checkedId) {
        R.id.nineButton -> 9
        R.id.thirteenButton -> 13
        R.id.nineteenButton -> 19
        else -> throw IllegalStateException("One of the three size buttons should be selected")
      }
    }

    startGameButton.setOnClickListener {
      mainVm.game = Game(vm.boardSize)
      transaction { replace(R.id.main_fragment, BoardFragment(), BoardFragment.TAG) }
    }
  }


}

class NewGameViewModel : ViewModel() {
  var boardSize = 19
}