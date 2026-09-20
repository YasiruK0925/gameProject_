package com.example.ui.games.word

data class WordItem(
  val word: String,
  val hint: String,
  val category: String
)

data class LetterChip(
  val id: Int,
  val char: Char,
  val isPlaced: Boolean = false
)

data class WordScrambleState(
  val currentWord: WordItem,
  val chips: List<LetterChip> = emptyList(),
  val placedChipIds: List<Int> = emptyList(),
  val score: Int = 0,
  val streak: Int = 0,
  val hintsLeft: Int = 3,
  val secondsLeft: Int = 40,
  val wordsSolved: Int = 0,
  val isGameOver: Boolean = false,
  val isWordSolved: Boolean = false
)

enum class WordGuessResult {
  IN_PROGRESS,
  CORRECT_WORD,
  WRONG_WORD
}
