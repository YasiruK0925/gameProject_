package com.example.ui.games.reflex

import kotlin.random.Random

class ColorReflexEngine {

  fun createInitialState(): ColorReflexState {
    val initialSequence = listOf(Random.nextInt(4))
    return ColorReflexState(
      sequence = initialSequence,
      playerStep = 0,
      activeHighlight = null,
      isDemonstrating = true,
      score = 0,
      round = 1,
      streak = 0,
      lives = 3,
      isGameOver = false
    )
  }

  fun handlePlayerTap(state: ColorReflexState, tappedId: Int): Pair<ColorReflexState, PlayerTapResult> {
    if (state.isGameOver || state.isDemonstrating || state.sequence.isEmpty()) {
      return state to PlayerTapResult.CORRECT_STEP
    }

    val expectedId = state.sequence[state.playerStep]
    if (tappedId == expectedId) {
      val nextStep = state.playerStep + 1
      if (nextStep >= state.sequence.size) {
        // Completed the full sequence!
        val newRound = state.round + 1
        val newStreak = state.streak + 1
        val addedScore = state.sequence.size * 10 + (newStreak * 5)
        val extendedSequence = state.sequence + Random.nextInt(4)

        val newState = state.copy(
          sequence = extendedSequence,
          playerStep = 0,
          score = state.score + addedScore,
          round = newRound,
          streak = newStreak,
          isDemonstrating = true,
          activeHighlight = null
        )
        return newState to PlayerTapResult.ROUND_COMPLETED
      } else {
        val newState = state.copy(playerStep = nextStep)
        return newState to PlayerTapResult.CORRECT_STEP
      }
    } else {
      // Wrong tap
      val remainingLives = state.lives - 1
      if (remainingLives <= 0) {
        val newState = state.copy(
          lives = 0,
          isGameOver = true,
          isDemonstrating = false
        )
        return newState to PlayerTapResult.GAME_OVER
      } else {
        // Reset player step and restart demonstration
        val newState = state.copy(
          lives = remainingLives,
          streak = 0,
          playerStep = 0,
          isDemonstrating = true,
          activeHighlight = null
        )
        return newState to PlayerTapResult.WRONG_STEP_CONTINUE
      }
    }
  }
}
