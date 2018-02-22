package com.vincentcarrier.omego

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vincentcarrier.model.Game
import kotlinx.android.synthetic.main.activity_main.rootView

class MainActivity : AppCompatActivity() {

  internal val vm by lazy {
    ViewModelProviders.of(this).get(MainViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    fun enterFullscreen() {
      rootView.systemUiVisibility =
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_FULLSCREEN or
          View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    enterFullscreen()
  }

  class MainViewModel : ViewModel() {
    internal lateinit var game: Game
  }
}