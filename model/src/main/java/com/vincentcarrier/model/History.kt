package com.vincentcarrier.model

import java.util.Stack


typealias History = GoStack<Moment>

class GoStack<E> : Stack<E>() {
  // TODO: Fix access leak
  val last: E
    get() {
      @Suppress("UNCHECKED_CAST")
      return elementData[-1] as E
    }
}

data class Moment(
    val turn: Turn,
    val grid: Grid
)