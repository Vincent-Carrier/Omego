package com.vincentcarrier.omego

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vincentcarrier.omego.boardview.addFragment

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    addFragment(R.id.board_fragment, BoardFragment())
  }
}