package com.vincentcarrier.model


object TestCase {
  val board = Board("""
    OOOWW
    BBOOW
    WBBOW
    WBOOO
    BBOWW
    """.trimIndent())

  val whiteGroup = listOf(3 to 0, 4 to 0,
                                                 4 to 1,
                                                 4 to 2)

  val blackGroup = listOf(0 to 1, 1 to 1,
                                                 1 to 2, 2 to 2,
                                                 1 to 3,
                                         0 to 4, 1 to 4)

  val whiteLiberties = listOf(2 to 0,
                                                    3 to 1,
                                                    3 to 2,
                                                           4 to 3)
}