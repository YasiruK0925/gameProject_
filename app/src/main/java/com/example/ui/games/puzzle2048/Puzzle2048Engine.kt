package com.example.ui.games.puzzle2048

import kotlin.random.Random

class Puzzle2048Engine {

  fun createInitialState(): Puzzle2048State {
    var state = Puzzle2048State(
      grid = List(4) { List(4) { 0 } },
      score = 0,
      isGameOver = false,
      hasWon = false,
      previousState = null
    )
    state = spawnRandomTile(state)
    state = spawnRandomTile(state)
    return state
  }

  fun move(state: Puzzle2048State, direction: MoveDirection): Pair<Puzzle2048State, Boolean> {
    if (state.isGameOver) return state to false

    val currentGrid = state.grid.map { it.toMutableList() }.toMutableList()
    var addedScore = 0
    var hasMoved = false

    when (direction) {
      MoveDirection.LEFT -> {
        for (r in 0 until 4) {
          val (newRow, scoreGain, moved) = slideAndMergeRow(currentGrid[r])
          if (moved) hasMoved = true
          addedScore += scoreGain
          currentGrid[r] = newRow.toMutableList()
        }
      }
      MoveDirection.RIGHT -> {
        for (r in 0 until 4) {
          val reversed = currentGrid[r].reversed()
          val (newRow, scoreGain, moved) = slideAndMergeRow(reversed)
          if (moved) hasMoved = true
          addedScore += scoreGain
          currentGrid[r] = newRow.reversed().toMutableList()
        }
      }
      MoveDirection.UP -> {
        for (c in 0 until 4) {
          val col = (0 until 4).map { currentGrid[it][c] }
          val (newCol, scoreGain, moved) = slideAndMergeRow(col)
          if (moved) hasMoved = true
          addedScore += scoreGain
          for (r in 0 until 4) {
            currentGrid[r][c] = newCol[r]
          }
        }
      }
      MoveDirection.DOWN -> {
        for (c in 0 until 4) {
          val col = (0 until 4).map { currentGrid[it][c] }.reversed()
          val (newCol, scoreGain, moved) = slideAndMergeRow(col)
          if (moved) hasMoved = true
          addedScore += scoreGain
          val restored = newCol.reversed()
          for (r in 0 until 4) {
            currentGrid[r][c] = restored[r]
          }
        }
      }
    }

    if (!hasMoved) {
      return state to false
    }

    val prev = Puzzle2048PreviousState(
      grid = state.grid.map { it.toList() },
      score = state.score
    )

    val updatedGrid = currentGrid.map { it.toList() }
    val newScore = state.score + addedScore
    val hasWon = !state.hasWon && updatedGrid.any { row -> row.any { it >= 2048 } }

    var newState = state.copy(
      grid = updatedGrid,
      score = newScore,
      hasWon = state.hasWon || hasWon,
      previousState = prev
    )

    newState = spawnRandomTile(newState)
    newState = newState.copy(isGameOver = checkGameOver(newState.grid))

    return newState to true
  }

  fun undo(state: Puzzle2048State): Puzzle2048State {
    val prev = state.previousState ?: return state
    return state.copy(
      grid = prev.grid,
      score = prev.score,
      isGameOver = false,
      previousState = null
    )
  }

  fun slideAndMergeRow(row: List<Int>): Triple<List<Int>, Int, Boolean> {
    val nonZero = row.filter { it != 0 }
    val merged = mutableListOf<Int>()
    var scoreGained = 0
    var i = 0
    while (i < nonZero.size) {
      if (i + 1 < nonZero.size && nonZero[i] == nonZero[i + 1]) {
        val mergedVal = nonZero[i] * 2
        merged.add(mergedVal)
        scoreGained += mergedVal
        i += 2
      } else {
        merged.add(nonZero[i])
        i += 1
      }
    }
    while (merged.size < 4) {
      merged.add(0)
    }
    val moved = merged != row
    return Triple(merged, scoreGained, moved)
  }

  fun spawnRandomTile(state: Puzzle2048State): Puzzle2048State {
    val emptyCells = mutableListOf<Pair<Int, Int>>()
    for (r in 0 until 4) {
      for (c in 0 until 4) {
        if (state.grid[r][c] == 0) {
          emptyCells.add(r to c)
        }
      }
    }
    if (emptyCells.isEmpty()) return state

    val (targetR, targetC) = emptyCells[Random.nextInt(emptyCells.size)]
    val tileValue = if (Random.nextFloat() < 0.9f) 2 else 4

    val mutableGrid = state.grid.map { it.toMutableList() }.toMutableList()
    mutableGrid[targetR][targetC] = tileValue

    return state.copy(grid = mutableGrid.map { it.toList() })
  }

  fun checkGameOver(grid: List<List<Int>>): Boolean {
    // Check for any empty cell
    for (r in 0 until 4) {
      for (c in 0 until 4) {
        if (grid[r][c] == 0) return false
      }
    }
    // Check horizontal neighbors
    for (r in 0 until 4) {
      for (c in 0 until 3) {
        if (grid[r][c] == grid[r][c + 1]) return false
      }
    }
    // Check vertical neighbors
    for (r in 0 until 3) {
      for (c in 0 until 4) {
        if (grid[r][c] == grid[r + 1][c]) return false
      }
    }
    return true
  }
}
