package com.vincentcarrier.model

import java.util.Stack


typealias History = GoStack<Pair<Turn, Grid>>

class GoStack<E> : Stack<E>() {
  // TODO: Fix access leak
  fun last(): E {
    @Suppress("UNCHECKED_CAST")
    return elementData[-1] as E
  }
}