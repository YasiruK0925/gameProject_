package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.math.max

class GameRepository(private val dao: GameRecordDao) {

  val allRecords: Flow<List<GameRecord>> = dao.getAllRecords()

  fun getRecord(gameId: String): Flow<GameRecord?> = dao.getRecord(gameId)

  suspend fun recordGameFinished(
    gameId: String,
    gameTitle: String,
    finalScore: Int,
    streak: Int = 0
  ): Boolean {
    val existing = dao.getRecordSync(gameId)
    val isNewHighScore = (existing == null && finalScore > 0) || (existing != null && finalScore > existing.highScore)
    val updated = GameRecord(
      gameId = gameId,
      gameTitle = gameTitle,
      highScore = if (existing != null) max(existing.highScore, finalScore) else finalScore,
      gamesPlayed = (existing?.gamesPlayed ?: 0) + 1,
      bestStreak = if (existing != null) max(existing.bestStreak, streak) else streak,
      lastPlayedAt = System.currentTimeMillis()
    )
    dao.insertOrUpdate(updated)
    return isNewHighScore
  }

  suspend fun resetAllStats() {
    dao.clearAll()
  }
}
