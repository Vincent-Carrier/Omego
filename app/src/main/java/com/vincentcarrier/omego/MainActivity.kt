package com.vincentcarrier.omego

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import com.vincentcarrier.model.Game
import com.vincentcarrier.omego.board.BoardFragment
import com.vincentcarrier.omego.newgame.NewGameFragment


class MainActivity : AppCompatActivity() {

  internal val vm by lazy {
    ViewModelProviders.of(this).get(MainViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    fun enterFullscreen() {
      window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
      SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
      SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
      SYSTEM_UI_FLAG_HIDE_NAVIGATION or
      SYSTEM_UI_FLAG_FULLSCREEN or
      SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    super.onCreate(savedInstanceState)
    enterFullscreen()
    setContentView(R.layout.activity_main)
    if (vm.game == null) {
      transaction { replace(R.id.fragment_container, NewGameFragment(), NewGameFragment.TAG) }
    } else {
      transaction { replace(R.id.fragment_container, BoardFragment(), BoardFragment.TAG) }
    }
  }
}

class MainViewModel : ViewModel() {
  internal var game: Game? = null
}