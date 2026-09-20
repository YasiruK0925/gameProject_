package com.example.ui.navigation

sealed class ArcadeScreen(val route: String, val title: String) {
  object Lobby : ArcadeScreen("lobby", "Mini Arcade")
  object Snake : ArcadeScreen("snake", "Neon Snake")
  object Puzzle2048 : ArcadeScreen("puzzle2048", "2048 Puzzle")
  object Reflex : ArcadeScreen("reflex", "Color Reflex")
  object Space : ArcadeScreen("space", "Space Dodger")
  object Word : ArcadeScreen("word", "Word Scramble")
}
