package com.example.ui.games.space

data class Asteroid(
  val id: Long,
  val x: Float,          // 0.0 to 1.0
  val y: Float,          // 0.0 to 1.0
  val radius: Float,     // Normalized ratio
  val speed: Float,
  val rotation: Float = 0f
)

data class CosmicOrb(
  val id: Long,
  val x: Float,
  val y: Float,
  val isShield: Boolean = false
)

data class SpaceStar(
  val x: Float,
  val y: Float,
  val speed: Float,
  val size: Float
)

data class SpaceDodgerState(
  val playerX: Float = 0.5f,
  val playerWidth: Float = 0.14f,
  val playerHeight: Float = 0.08f,
  val hasShield: Boolean = false,
  val shieldTicksLeft: Int = 0,
  val asteroids: List<Asteroid> = emptyList(),
  val orbs: List<CosmicOrb> = emptyList(),
  val stars: List<SpaceStar> = emptyList(),
  val score: Int = 0,
  val wave: Int = 1,
  val isGameOver: Boolean = false,
  val isPaused: Boolean = false,
  val ticks: Long = 0
)

enum class SpaceStepEvent {
  NONE,
  COLLECTED_ORB,
  COLLECTED_SHIELD,
  SHIELD_DEFLECTED,
  CRASHED_GAME_OVER
}
