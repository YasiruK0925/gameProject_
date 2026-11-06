package com.example.ui.games.reflex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ColorReflexEngineTest {

  private lateinit var engine: ColorReflexEngine

  @Before
  fun setUp() {
    engine = ColorReflexEngine()
  }

  @Test
  fun testInitialState() {
    val state = engine.createInitialState()
    assertEquals(1, state.sequence.size)
    assertEquals(0, state.playerStep)
    assertEquals(3, state.lives)
    assertEquals(0, state.score)
    assertEquals(1, state.round)
    assertTrue(state.isDemonstrating)
    assertFalse(state.isGameOver)
  }

  @Test
  fun testCorrectFirstStepInMultiStepSequence() {
    // Target sequence: [0, 2]
    val state = ColorReflexState(
      sequence = listOf(0, 2),
      playerStep = 0,
      isDemonstrating = false,
      lives = 3,
      score = 0
    )

    val (newState, result) = engine.handlePlayerTap(state, 0)

    assertEquals(PlayerTapResult.CORRECT_STEP, result)
    assertEquals(1, newState.playerStep)
    assertEquals(3, newState.lives)
    assertFalse(newState.isGameOver)
  }

  @Test
  fun testCompleteRoundAdvancesSequenceAndAwardsScore() {
    // Target sequence: [1]
    val state = ColorReflexState(
      sequence = listOf(1),
      playerStep = 0,
      isDemonstrating = false,
      lives = 3,
      score = 0,
      round = 1,
      streak = 0
    )

    val (newState, result) = engine.handlePlayerTap(state, 1)

    assertEquals(PlayerTapResult.ROUND_COMPLETED, result)
    assertEquals(2, newState.sequence.size) // extended
    assertEquals(2, newState.round)
    assertEquals(1, newState.streak)
    assertEquals(15, newState.score) // 1*10 + 1*5 = 15
    assertTrue(newState.isDemonstrating)
  }

  @Test
  fun testWrongTapLosesLifeAndResetsStreak() {
    val state = ColorReflexState(
      sequence = listOf(0, 1),
      playerStep = 0,
      isDemonstrating = false,
      lives = 3,
      streak = 2
    )

    val (newState, result) = engine.handlePlayerTap(state, 3) // Expected 0, tapped 3

    assertEquals(PlayerTapResult.WRONG_STEP_CONTINUE, result)
    assertEquals(2, newState.lives)
    assertEquals(0, newState.streak)
    assertEquals(0, newState.playerStep)
    assertTrue(newState.isDemonstrating)
  }

  @Test
  fun testGameOverWhenLivesDepleted() {
    val state = ColorReflexState(
      sequence = listOf(0),
      playerStep = 0,
      isDemonstrating = false,
      lives = 1
    )

    val (newState, result) = engine.handlePlayerTap(state, 2)

    assertEquals(PlayerTapResult.GAME_OVER, result)
    assertEquals(0, newState.lives)
    assertTrue(newState.isGameOver)
  }
}
