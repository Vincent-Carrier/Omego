package com.vincentcarrier.omego

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import com.vincentcarrier.model.Game


class MainActivity : AppCompatActivity() {

  internal val vm by lazy {
    ViewModelProviders.of(this).get(MainViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    fun enterFullscreen() {
      requestWindowFeature(FEATURE_NO_TITLE)
      window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
    }

    super.onCreate(savedInstanceState)
    enterFullscreen()
    setContentView(R.layout.activity_main)
  }

  class MainViewModel : ViewModel() {
    internal lateinit var game: Game
  }
}