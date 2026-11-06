package com.example.ui.games.snake

enum class SnakeDirection {
  UP, DOWN, LEFT, RIGHT;

  fun isOpposite(other: SnakeDirection): Boolean {
    return (this == UP && other == DOWN) ||
           (this == DOWN && other == UP) ||
           (this == LEFT && other == RIGHT) ||
           (this == RIGHT && other == LEFT)
  }
}

data class GridPoint(val x: Int, val y: Int)

data class BonusItem(val point: GridPoint, val expiryTick: Long)

data class SnakeGameState(
  val snake: List<GridPoint> = listOf(GridPoint(10, 10), GridPoint(10, 11), GridPoint(10, 12)),
  val direction: SnakeDirection = SnakeDirection.UP,
  val nextDirection: SnakeDirection = SnakeDirection.UP,
  val food: GridPoint = GridPoint(10, 5),
  val bonusItem: BonusItem? = null,
  val score: Int = 0,
  val isGameOver: Boolean = false,
  val isPaused: Boolean = false,
  val gridWidth: Int = 20,
  val gridHeight: Int = 20,
  val currentTick: Long = 0,
  val speedDelayMs: Long = 160L
)
