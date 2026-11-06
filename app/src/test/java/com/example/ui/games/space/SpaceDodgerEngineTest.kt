package com.example.ui.games.space

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpaceDodgerEngineTest {

  private lateinit var engine: SpaceDodgerEngine

  @Before
  fun setUp() {
    engine = SpaceDodgerEngine()
  }

  @Test
  fun testInitialState() {
    val state = engine.createInitialState()
    assertEquals(0.5f, state.playerX, 0.001f)
    assertEquals(0, state.score)
    assertEquals(1, state.wave)
    assertFalse(state.isGameOver)
    assertFalse(state.hasShield)
  }

  @Test
  fun testPlayerPositionClamped() {
    val state = engine.createInitialState()
    // Move way to left
    val leftState = engine.updatePlayerPosition(state, -0.5f)
    assertEquals(0.08f, leftState.playerX, 0.001f)

    // Move way to right
    val rightState = engine.updatePlayerPosition(state, 1.5f)
    assertEquals(0.92f, rightState.playerX, 0.001f)

    // Valid move
    val midState = engine.updatePlayerPosition(state, 0.35f)
    assertEquals(0.35f, midState.playerX, 0.001f)
  }

  @Test
  fun testAsteroidCollisionCausesGameOverWithoutShield() {
    val state = engine.createInitialState().copy(
      playerX = 0.5f,
      hasShield = false,
      asteroids = listOf(
        Asteroid(id = 1L, x = 0.5f, y = 0.86f, radius = 0.05f, speed = 0.01f)
      )
    )

    val (newState, event) = engine.step(state)

    assertEquals(SpaceStepEvent.CRASHED_GAME_OVER, event)
    assertTrue(newState.isGameOver)
  }

  @Test
  fun testShieldDeflectsAsteroidAndProtectsShip() {
    val state = engine.createInitialState().copy(
      playerX = 0.5f,
      hasShield = true,
      shieldTicksLeft = 50,
      asteroids = listOf(
        Asteroid(id = 1L, x = 0.5f, y = 0.86f, radius = 0.05f, speed = 0.01f)
      )
    )

    val (newState, event) = engine.step(state)

    assertEquals(SpaceStepEvent.SHIELD_DEFLECTED, event)
    assertFalse(newState.isGameOver)
    assertFalse(newState.hasShield) // Shield consumed
    assertEquals(0, newState.asteroids.size) // Asteroid destroyed
  }

  @Test
  fun testOrbCollectionAwardsPoints() {
    val state = engine.createInitialState().copy(
      playerX = 0.5f,
      score = 10,
      orbs = listOf(
        CosmicOrb(id = 1L, x = 0.5f, y = 0.86f, isShield = false)
      )
    )

    val (newState, event) = engine.step(state)

    assertEquals(SpaceStepEvent.COLLECTED_ORB, event)
    assertFalse(newState.isGameOver)
    assertEquals(41, newState.score) // 10 + 30 (orb) + 1 (survival tick) = 41
  }
}
