package com.vincentcarrier.omego

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
  @JvmField
  @Rule val activityRule = ActivityTestRule(MainActivity::class.java)
  val activity: MainActivity = activityRule.activity

  @Test fun rotates_without_crashing() {
    activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
    activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
  }
}
