package com.example.ui.games.snake

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SnakeEngineTest {

  private lateinit var engine: SnakeEngine

  @Before
  fun setUp() {
    engine = SnakeEngine(gridWidth = 20, gridHeight = 20)
  }

  @Test
  fun testInitialState() {
    val state = engine.createInitialState()
    assertEquals(3, state.snake.size)
    assertEquals(0, state.score)
    assertFalse(state.isGameOver)
    assertFalse(state.isPaused)
    assertEquals(SnakeDirection.UP, state.direction)
  }

  @Test
  fun testValidStepMovement() {
    val state = engine.createInitialState()
    val head = state.snake.first()
    val (nextState, result) = engine.step(state)

    assertEquals(SnakeStepResult.NOTHING, result)
    assertFalse(nextState.isGameOver)
    assertEquals(3, nextState.snake.size)
    assertEquals(GridPoint(head.x, head.y - 1), nextState.snake.first())
  }

  @Test
  fun testChangeDirectionValidAndInvalid() {
    val state = engine.createInitialState() // direction is UP
    // Turning opposite (DOWN) should be ignored
    val invalidTurn = engine.changeDirection(state, SnakeDirection.DOWN)
    assertEquals(SnakeDirection.UP, invalidTurn.nextDirection)

    // Turning orthogonal (LEFT or RIGHT) should be allowed
    val validTurn = engine.changeDirection(state, SnakeDirection.LEFT)
    assertEquals(SnakeDirection.LEFT, validTurn.nextDirection)
  }

  @Test
  fun testEatingFoodGrowsSnakeAndScores() {
    val state = engine.createInitialState()
    val head = state.snake.first()
    // Place food directly in path of head (one step UP)
    val foodPoint = GridPoint(head.x, head.y - 1)
    val customState = state.copy(food = foodPoint)

    val (nextState, result) = engine.step(customState)

    assertEquals(SnakeStepResult.ATE_FOOD, result)
    assertEquals(10, nextState.score)
    assertEquals(4, nextState.snake.size)
    assertNotEquals(foodPoint, nextState.food) // New food generated
  }

  @Test
  fun testWallCollisionTriggersGameOver() {
    // Snake at top border facing UP
    val topBorderSnake = listOf(GridPoint(10, 0), GridPoint(10, 1), GridPoint(10, 2))
    val state = engine.createInitialState().copy(
      snake = topBorderSnake,
      direction = SnakeDirection.UP,
      nextDirection = SnakeDirection.UP
    )

    val (nextState, result) = engine.step(state)

    assertEquals(SnakeStepResult.GAME_OVER, result)
    assertTrue(nextState.isGameOver)
  }

  @Test
  fun testSelfCollisionTriggersGameOver() {
    // Create a coiled snake that hits itself on next step
    // Head at (5, 5), body at (5, 6), (6, 6), (6, 5). If head moves RIGHT to (6, 5), it hits (6, 5).
    val coiledSnake = listOf(
      GridPoint(5, 5),
      GridPoint(5, 6),
      GridPoint(6, 6),
      GridPoint(6, 5),
      GridPoint(7, 5)
    )
    val state = engine.createInitialState().copy(
      snake = coiledSnake,
      direction = SnakeDirection.RIGHT,
      nextDirection = SnakeDirection.RIGHT,
      food = GridPoint(0, 0)
    )

    val (nextState, result) = engine.step(state)

    assertEquals(SnakeStepResult.GAME_OVER, result)
    assertTrue(nextState.isGameOver)
  }
}
