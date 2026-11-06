package com.example.ui.games.word

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WordScrambleEngineTest {

  private lateinit var engine: WordScrambleEngine

  @Before
  fun setUp() {
    engine = WordScrambleEngine()
  }

  @Test
  fun testInitialState() {
    val state = engine.createInitialState()
    assertTrue(state.currentWord.word.isNotEmpty())
    assertEquals(state.currentWord.word.length, state.chips.size)
    assertEquals(0, state.placedChipIds.size)
    assertEquals(0, state.score)
    assertEquals(3, state.hintsLeft)
    assertEquals(40, state.secondsLeft)
    assertFalse(state.isGameOver)
    assertFalse(state.isWordSolved)
  }

  @Test
  fun testPlaceAndRemoveChip() {
    val word = WordItem("STAR", "A glowing point in the night sky", "SPACE")
    val chips = listOf(
      LetterChip(0, 'S'),
      LetterChip(1, 'T'),
      LetterChip(2, 'A'),
      LetterChip(3, 'R')
    )
    val state = WordScrambleState(currentWord = word, chips = chips)

    val (placedState, result) = engine.placeChip(state, 0)
    assertEquals(WordGuessResult.IN_PROGRESS, result)
    assertEquals(listOf(0), placedState.placedChipIds)
    assertTrue(placedState.chips[0].isPlaced)

    val removedState = engine.removePlacedChip(placedState, 0)
    assertEquals(emptyList<Int>(), removedState.placedChipIds)
    assertFalse(removedState.chips[0].isPlaced)
  }

  @Test
  fun testCorrectWordSubmission() {
    val word = WordItem("CAT", "Feline friend", "ANIMALS")
    val chips = listOf(
      LetterChip(0, 'C'),
      LetterChip(1, 'A'),
      LetterChip(2, 'T')
    )
    var state = WordScrambleState(currentWord = word, chips = chips, secondsLeft = 30)

    val (s1, r1) = engine.placeChip(state, 0)
    val (s2, r2) = engine.placeChip(s1, 1)
    val (s3, r3) = engine.placeChip(s2, 2)

    assertEquals(WordGuessResult.CORRECT_WORD, r3)
    assertTrue(s3.isWordSolved)
    assertEquals(1, s3.streak)
    assertEquals(1, s3.wordsSolved)
    assertTrue(s3.score > 0)
  }

  @Test
  fun testWrongWordSubmission() {
    val word = WordItem("CAT", "Feline friend", "ANIMALS")
    val chips = listOf(
      LetterChip(0, 'T'),
      LetterChip(1, 'A'),
      LetterChip(2, 'C')
    )
    var state = WordScrambleState(currentWord = word, chips = chips)

    val (s1, _) = engine.placeChip(state, 0)
    val (s2, _) = engine.placeChip(s1, 1)
    val (s3, result) = engine.placeChip(s2, 2)

    assertEquals(WordGuessResult.WRONG_WORD, result)
    assertFalse(s3.isWordSolved)
  }

  @Test
  fun testHintUsagePlacesNextCorrectLetter() {
    val word = WordItem("LION", "King of the jungle", "ANIMALS")
    val chips = listOf(
      LetterChip(0, 'O'),
      LetterChip(1, 'L'),
      LetterChip(2, 'N'),
      LetterChip(3, 'I')
    )
    val state = WordScrambleState(currentWord = word, chips = chips, hintsLeft = 3)

    val hintedState = engine.useHint(state)
    assertEquals(2, hintedState.hintsLeft)
    assertEquals(1, hintedState.placedChipIds.size)
    assertEquals('L', hintedState.chips.find { it.id == hintedState.placedChipIds.first() }?.char)
  }
}
