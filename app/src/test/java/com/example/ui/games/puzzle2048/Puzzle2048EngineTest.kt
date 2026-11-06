package com.example.ui.games.puzzle2048

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Puzzle2048EngineTest {

  private lateinit var engine: Puzzle2048Engine

  @Before
  fun setUp() {
    engine = Puzzle2048Engine()
  }

  @Test
  fun testInitialStateHasTwoTiles() {
    val state = engine.createInitialState()
    val nonZeroCount = state.grid.sumOf { row -> row.count { it > 0 } }
    assertEquals(2, nonZeroCount)
    assertEquals(0, state.score)
    assertFalse(state.isGameOver)
  }

  @Test
  fun testSlideAndMergeRow() {
    // Row: [2, 2, 4, 8] -> should merge first two to 4 -> [4, 4, 8, 0], scoreGain = 4
    val (merged, scoreGain, moved) = engine.slideAndMergeRow(listOf(2, 2, 4, 8))
    assertEquals(listOf(4, 4, 8, 0), merged)
    assertEquals(4, scoreGain)
    assertTrue(moved)
  }

  @Test
  fun testSlideAndMergeRowWithFourTwos() {
    // Row: [2, 2, 2, 2] -> [4, 4, 0, 0], scoreGain = 8
    val (merged, scoreGain, moved) = engine.slideAndMergeRow(listOf(2, 2, 2, 2))
    assertEquals(listOf(4, 4, 0, 0), merged)
    assertEquals(8, scoreGain)
    assertTrue(moved)
  }

  @Test
  fun testMoveLeft() {
    val initialGrid = listOf(
      listOf(2, 0, 2, 0),
      listOf(0, 4, 0, 4),
      listOf(0, 0, 0, 0),
      listOf(0, 0, 0, 0)
    )
    val state = Puzzle2048State(grid = initialGrid, score = 0)
    val (newState, moved) = engine.move(state, MoveDirection.LEFT)

    assertTrue(moved)
    assertEquals(12, newState.score) // 4 + 8 = 12
    assertEquals(4, newState.grid[0][0])
    assertEquals(8, newState.grid[1][0])
    assertNotNull(newState.previousState)
  }

  @Test
  fun testUndoMoveRestoresGridAndScore() {
    val initialGrid = listOf(
      listOf(2, 0, 2, 0),
      listOf(0, 0, 0, 0),
      listOf(0, 0, 0, 0),
      listOf(0, 0, 0, 0)
    )
    val state = Puzzle2048State(grid = initialGrid, score = 0)
    val (movedState, _) = engine.move(state, MoveDirection.LEFT)

    assertEquals(4, movedState.score)
    val undoneState = engine.undo(movedState)

    assertEquals(0, undoneState.score)
    assertEquals(initialGrid, undoneState.grid)
  }

  @Test
  fun testCheckGameOver() {
    val gameOverGrid = listOf(
      listOf(2, 4, 2, 4),
      listOf(4, 2, 4, 2),
      listOf(2, 4, 2, 4),
      listOf(4, 2, 4, 2)
    )
    assertTrue(engine.checkGameOver(gameOverGrid))

    val notGameOverGrid = listOf(
      listOf(2, 4, 2, 4),
      listOf(4, 2, 4, 2),
      listOf(2, 4, 2, 4),
      listOf(4, 2, 4, 0) // has an empty space
    )
    assertFalse(engine.checkGameOver(notGameOverGrid))

    val mergeableGrid = listOf(
      listOf(2, 2, 4, 8), // has adjacent 2s
      listOf(4, 8, 2, 4),
      listOf(2, 4, 8, 2),
      listOf(4, 2, 4, 8)
    )
    assertFalse(engine.checkGameOver(mergeableGrid))
  }
}
