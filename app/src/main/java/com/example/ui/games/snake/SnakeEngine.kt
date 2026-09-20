package com.example.ui.games.snake

import kotlin.random.Random

class SnakeEngine(
  val gridWidth: Int = 20,
  val gridHeight: Int = 20
) {

  fun createInitialState(): SnakeGameState {
    val initialSnake = listOf(
      GridPoint(gridWidth / 2, gridHeight / 2),
      GridPoint(gridWidth / 2, gridHeight / 2 + 1),
      GridPoint(gridWidth / 2, gridHeight / 2 + 2)
    )
    val food = generateFood(initialSnake)
    return SnakeGameState(
      snake = initialSnake,
      direction = SnakeDirection.UP,
      nextDirection = SnakeDirection.UP,
      food = food,
      bonusItem = null,
      score = 0,
      isGameOver = false,
      isPaused = false,
      gridWidth = gridWidth,
      gridHeight = gridHeight,
      currentTick = 0,
      speedDelayMs = 170L
    )
  }

  fun changeDirection(currentState: SnakeGameState, newDirection: SnakeDirection): SnakeGameState {
    if (currentState.isGameOver || currentState.isPaused) return currentState
    if (currentState.direction.isOpposite(newDirection)) return currentState
    return currentState.copy(nextDirection = newDirection)
  }

  fun step(state: SnakeGameState): Pair<SnakeGameState, SnakeStepResult> {
    if (state.isGameOver || state.isPaused) {
      return state to SnakeStepResult.NOTHING
    }

    val tick = state.currentTick + 1
    val dir = state.nextDirection
    val head = state.snake.first()
    val newHead = when (dir) {
      SnakeDirection.UP -> GridPoint(head.x, head.y - 1)
      SnakeDirection.DOWN -> GridPoint(head.x, head.y + 1)
      SnakeDirection.LEFT -> GridPoint(head.x - 1, head.y)
      SnakeDirection.RIGHT -> GridPoint(head.x + 1, head.y)
    }

    // Check Wall Collision
    if (newHead.x < 0 || newHead.x >= state.gridWidth || newHead.y < 0 || newHead.y >= state.gridHeight) {
      return state.copy(isGameOver = true) to SnakeStepResult.GAME_OVER
    }

    // Check Self Collision (excluding tail that moves unless food is eaten)
    val bodyCheck = state.snake.dropLast(1)
    if (bodyCheck.contains(newHead)) {
      return state.copy(isGameOver = true) to SnakeStepResult.GAME_OVER
    }

    val ateFood = newHead == state.food
    val ateBonus = state.bonusItem?.point == newHead

    val newSnake = mutableListOf(newHead)
    if (ateFood || ateBonus) {
      newSnake.addAll(state.snake)
    } else {
      newSnake.addAll(state.snake.dropLast(1))
    }

    var newScore = state.score
    var stepResult = SnakeStepResult.NOTHING

    if (ateFood) {
      newScore += 10
      stepResult = SnakeStepResult.ATE_FOOD
    }
    if (ateBonus) {
      newScore += 50
      stepResult = SnakeStepResult.ATE_BONUS
    }

    val newFood = if (ateFood) generateFood(newSnake) else state.food

    // Bonus spawn logic (spawn every ~40 ticks with chance, expires after 35 ticks)
    var newBonus = state.bonusItem
    if (newBonus != null && (ateBonus || tick >= newBonus.expiryTick)) {
      newBonus = null
    } else if (newBonus == null && tick % 45L == 0L && Random.nextFloat() < 0.6f) {
      val bonusPoint = generateFood(newSnake + listOf(newFood))
      newBonus = BonusItem(bonusPoint, expiryTick = tick + 35L)
    }

    // Speed progression: increases slightly every 40 points, min 80ms
    val speed = (170L - (newScore / 40) * 10L).coerceAtLeast(80L)

    val updatedState = state.copy(
      snake = newSnake,
      direction = dir,
      nextDirection = dir,
      food = newFood,
      bonusItem = newBonus,
      score = newScore,
      currentTick = tick,
      speedDelayMs = speed
    )

    return updatedState to stepResult
  }

  fun generateFood(snake: List<GridPoint>): GridPoint {
    val occupied = snake.toSet()
    val available = mutableListOf<GridPoint>()
    for (x in 0 until gridWidth) {
      for (y in 0 until gridHeight) {
        val p = GridPoint(x, y)
        if (!occupied.contains(p)) {
          available.add(p)
        }
      }
    }
    return if (available.isNotEmpty()) {
      available[Random.nextInt(available.size)]
    } else {
      GridPoint(0, 0)
    }
  }
}

enum class SnakeStepResult {
  NOTHING, ATE_FOOD, ATE_BONUS, GAME_OVER
}
