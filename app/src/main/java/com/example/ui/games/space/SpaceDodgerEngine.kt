package com.example.ui.games.space

import kotlin.math.hypot
import kotlin.random.Random

class SpaceDodgerEngine {

  fun createInitialState(): SpaceDodgerState {
    val initialStars = (0 until 35).map {
      SpaceStar(
        x = Random.nextFloat(),
        y = Random.nextFloat(),
        speed = 0.005f + Random.nextFloat() * 0.015f,
        size = 1.5f + Random.nextFloat() * 3f
      )
    }
    return SpaceDodgerState(
      playerX = 0.5f,
      stars = initialStars,
      asteroids = emptyList(),
      orbs = emptyList(),
      score = 0,
      wave = 1,
      hasShield = false,
      shieldTicksLeft = 0,
      isGameOver = false,
      isPaused = false,
      ticks = 0
    )
  }

  fun updatePlayerPosition(state: SpaceDodgerState, targetX: Float): SpaceDodgerState {
    if (state.isGameOver || state.isPaused) return state
    val clamped = targetX.coerceIn(0.08f, 0.92f)
    return state.copy(playerX = clamped)
  }

  fun step(state: SpaceDodgerState): Pair<SpaceDodgerState, SpaceStepEvent> {
    if (state.isGameOver || state.isPaused) {
      return state to SpaceStepEvent.NONE
    }

    val currentTick = state.ticks + 1
    val wave = 1 + (state.score / 150)
    val baseAsteroidSpeed = 0.016f + (wave * 0.003f)

    // Update Stars
    val updatedStars = state.stars.map { star ->
      var newY = star.y + star.speed
      if (newY > 1.0f) newY = 0f
      star.copy(y = newY)
    }

    // Move Asteroids
    val remainingAsteroids = mutableListOf<Asteroid>()
    for (a in state.asteroids) {
      val newY = a.y + a.speed
      if (newY <= 1.15f) {
        remainingAsteroids.add(a.copy(y = newY, rotation = a.rotation + 2.5f))
      }
    }

    // Spawn new Asteroids
    val spawnInterval = (14 - wave.coerceAtMost(8)).coerceAtLeast(6)
    if (currentTick % spawnInterval == 0L) {
      val newAsteroid = Asteroid(
        id = currentTick,
        x = 0.06f + Random.nextFloat() * 0.88f,
        y = -0.08f,
        radius = 0.045f + Random.nextFloat() * 0.035f,
        speed = baseAsteroidSpeed + Random.nextFloat() * 0.008f,
        rotation = Random.nextFloat() * 360f
      )
      remainingAsteroids.add(newAsteroid)
    }

    // Move Orbs
    val remainingOrbs = mutableListOf<CosmicOrb>()
    for (orb in state.orbs) {
      val newY = orb.y + 0.014f
      if (newY <= 1.15f) {
        remainingOrbs.add(orb.copy(y = newY))
      }
    }

    // Spawn new Orbs (points or shield)
    if (currentTick % 45L == 0L && Random.nextFloat() < 0.65f) {
      val isShield = Random.nextFloat() < 0.22f
      val newOrb = CosmicOrb(
        id = currentTick * 10,
        x = 0.1f + Random.nextFloat() * 0.8f,
        y = -0.05f,
        isShield = isShield
      )
      remainingOrbs.add(newOrb)
    }

    // Check Player Collisions
    val playerY = 0.86f
    val playerRadius = 0.055f

    var event = SpaceStepEvent.NONE
    var newScore = state.score + 1
    var hasShield = state.hasShield
    var shieldTicks = state.shieldTicksLeft

    if (shieldTicks > 0) {
      shieldTicks -= 1
      if (shieldTicks <= 0) hasShield = false
    }

    // Check Orbs
    val retainedOrbs = mutableListOf<CosmicOrb>()
    for (orb in remainingOrbs) {
      val dist = hypot(orb.x - state.playerX, orb.y - playerY)
      if (dist < playerRadius + 0.045f) {
        if (orb.isShield) {
          hasShield = true
          shieldTicks = 160 // ~8 seconds of shield
          event = SpaceStepEvent.COLLECTED_SHIELD
        } else {
          newScore += 30
          event = SpaceStepEvent.COLLECTED_ORB
        }
      } else {
        retainedOrbs.add(orb)
      }
    }

    // Check Asteroid collision
    val finalAsteroids = mutableListOf<Asteroid>()
    var crashed = false

    for (a in remainingAsteroids) {
      val dist = hypot(a.x - state.playerX, a.y - playerY)
      if (dist < playerRadius + a.radius) {
        if (hasShield) {
          hasShield = false
          shieldTicks = 0
          event = SpaceStepEvent.SHIELD_DEFLECTED
          newScore += 15
          // asteroid destroyed by shield
        } else {
          crashed = true
          event = SpaceStepEvent.CRASHED_GAME_OVER
          break
        }
      } else {
        finalAsteroids.add(a)
      }
    }

    val updatedState = state.copy(
      asteroids = if (crashed) remainingAsteroids else finalAsteroids,
      orbs = retainedOrbs,
      stars = updatedStars,
      score = newScore,
      wave = wave,
      hasShield = hasShield,
      shieldTicksLeft = shieldTicks,
      isGameOver = crashed,
      ticks = currentTick
    )

    return updatedState to event
  }
}
