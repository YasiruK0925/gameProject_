package com.example.ui.games.puzzle2048

enum class MoveDirection {
  UP, DOWN, LEFT, RIGHT
}

data class Puzzle2048PreviousState(
  val grid: List<List<Int>>,
  val score: Int
)

data class Puzzle2048State(
  val grid: List<List<Int>> = List(4) { List(4) { 0 } },
  val score: Int = 0,
  val isGameOver: Boolean = false,
  val hasWon: Boolean = false,
  val previousState: Puzzle2048PreviousState? = null
)
