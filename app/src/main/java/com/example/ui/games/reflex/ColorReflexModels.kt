package com.example.ui.games.reflex

enum class ReflexColor(val id: Int, val label: String) {
  GREEN(0, "EMERALD"),
  RED(1, "CRIMSON"),
  BLUE(2, "COBALT"),
  YELLOW(3, "AMBER");

  companion object {
    fun fromId(id: Int): ReflexColor = entries.firstOrNull { it.id == id } ?: GREEN
  }
}

data class ColorReflexState(
  val sequence: List<Int> = emptyList(),
  val playerStep: Int = 0,
  val activeHighlight: Int? = null,
  val isDemonstrating: Boolean = false,
  val score: Int = 0,
  val round: Int = 1,
  val streak: Int = 0,
  val lives: Int = 3,
  val isGameOver: Boolean = false
)

enum class PlayerTapResult {
  CORRECT_STEP,
  ROUND_COMPLETED,
  WRONG_STEP_CONTINUE,
  GAME_OVER
}
