package com.example.ui.games.word

import kotlin.random.Random

class WordScrambleEngine {

  val wordBank = listOf(
    WordItem("GALAXY", "A massive cosmic system of stars", "SPACE"),
    WordItem("COSMOS", "The universe seen as an orderly system", "SPACE"),
    WordItem("PLANET", "Celestial body orbiting a star", "SPACE"),
    WordItem("METEOR", "A shooting streak of rock in space", "SPACE"),
    WordItem("ROCKET", "Space vehicle powered by engines", "SPACE"),
    WordItem("JUNGLE", "A dense tropical forest ecosystem", "NATURE"),
    WordItem("VOLCANO", "Mountain with glowing hot lava", "NATURE"),
    WordItem("CANYON", "Deep gorge with steep stone cliffs", "NATURE"),
    WordItem("ISLAND", "Piece of land surrounded by ocean", "NATURE"),
    WordItem("GLACIER", "Slowly moving massive river of ice", "NATURE"),
    WordItem("FALCON", "Swift bird of prey with sharp talons", "ANIMALS"),
    WordItem("DOLPHIN", "Playful highly intelligent marine mammal", "ANIMALS"),
    WordItem("PANTHER", "Graceful and stealthy black wildcat", "ANIMALS"),
    WordItem("CHEETAH", "The fastest land mammal on earth", "ANIMALS"),
    WordItem("OCTOPUS", "Eight-armed clever ocean creature", "ANIMALS"),
    WordItem("PIXEL", "The smallest single element on a display", "TECH"),
    WordItem("MATRIX", "A rectangular grid of numbers or data", "TECH"),
    WordItem("CYBER", "Related to computers and virtual networks", "TECH"),
    WordItem("ROBOT", "Autonomous machine built to assist", "TECH"),
    WordItem("ARCADE", "Classic venue packed with gaming machines", "GAMES")
  )

  fun createInitialState(): WordScrambleState {
    val word = pickRandomWord(emptySet())
    val chips = createScrambledChips(word.word)
    return WordScrambleState(
      currentWord = word,
      chips = chips,
      placedChipIds = emptyList(),
      score = 0,
      streak = 0,
      hintsLeft = 3,
      secondsLeft = 40,
      wordsSolved = 0,
      isGameOver = false,
      isWordSolved = false
    )
  }

  fun pickRandomWord(usedWords: Set<String>): WordItem {
    val available = wordBank.filter { !usedWords.contains(it.word) }
    return if (available.isNotEmpty()) {
      available[Random.nextInt(available.size)]
    } else {
      wordBank[Random.nextInt(wordBank.size)]
    }
  }

  fun createScrambledChips(word: String): List<LetterChip> {
    val chars = word.uppercase().toList()
    var shuffled = chars.shuffled()
    // ensure scrambled word does not match original if length > 3
    var attempts = 0
    while (shuffled == chars && attempts < 10) {
      shuffled = chars.shuffled()
      attempts++
    }
    return shuffled.mapIndexed { index, ch ->
      LetterChip(id = index, char = ch, isPlaced = false)
    }
  }

  fun placeChip(state: WordScrambleState, chipId: Int): Pair<WordScrambleState, WordGuessResult> {
    if (state.isGameOver || state.isWordSolved) return state to WordGuessResult.IN_PROGRESS
    val chip = state.chips.find { it.id == chipId } ?: return state to WordGuessResult.IN_PROGRESS
    if (chip.isPlaced) return state to WordGuessResult.IN_PROGRESS

    val newPlacedIds = state.placedChipIds + chipId
    val newChips = state.chips.map {
      if (it.id == chipId) it.copy(isPlaced = true) else it
    }

    // Check if all letters are placed
    if (newPlacedIds.size == state.currentWord.word.length) {
      val formedWord = newPlacedIds.mapNotNull { id -> newChips.find { it.id == id }?.char }.joinToString("")
      if (formedWord.equals(state.currentWord.word, ignoreCase = true)) {
        val wordScore = (state.currentWord.word.length * 15) + (state.secondsLeft * 2) + (state.streak * 10)
        val updated = state.copy(
          chips = newChips,
          placedChipIds = newPlacedIds,
          score = state.score + wordScore,
          streak = state.streak + 1,
          wordsSolved = state.wordsSolved + 1,
          isWordSolved = true
        )
        return updated to WordGuessResult.CORRECT_WORD
      } else {
        return state.copy(chips = newChips, placedChipIds = newPlacedIds) to WordGuessResult.WRONG_WORD
      }
    }

    return state.copy(chips = newChips, placedChipIds = newPlacedIds) to WordGuessResult.IN_PROGRESS
  }

  fun removePlacedChip(state: WordScrambleState, chipId: Int): WordScrambleState {
    if (state.isGameOver || state.isWordSolved) return state
    val newPlacedIds = state.placedChipIds.filter { it != chipId }
    val newChips = state.chips.map {
      if (it.id == chipId) it.copy(isPlaced = false) else it
    }
    return state.copy(chips = newChips, placedChipIds = newPlacedIds)
  }

  fun clearPlaced(state: WordScrambleState): WordScrambleState {
    if (state.isGameOver || state.isWordSolved) return state
    val newChips = state.chips.map { it.copy(isPlaced = false) }
    return state.copy(chips = newChips, placedChipIds = emptyList())
  }

  fun useHint(state: WordScrambleState): WordScrambleState {
    if (state.hintsLeft <= 0 || state.isGameOver || state.isWordSolved) return state
    val targetWord = state.currentWord.word.uppercase()
    val nextIndex = state.placedChipIds.size
    if (nextIndex >= targetWord.length) return state

    val neededChar = targetWord[nextIndex]
    // Find an unplaced chip with this char
    val chip = state.chips.find { !it.isPlaced && it.char == neededChar } ?: return state

    val (newState, _) = placeChip(state.copy(hintsLeft = state.hintsLeft - 1), chip.id)
    return newState
  }

  fun loadNextWord(state: WordScrambleState): WordScrambleState {
    val nextWord = pickRandomWord(setOf(state.currentWord.word))
    val newChips = createScrambledChips(nextWord.word)
    return state.copy(
      currentWord = nextWord,
      chips = newChips,
      placedChipIds = emptyList(),
      secondsLeft = 40,
      isWordSolved = false
    )
  }
}
