package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
  @PrimaryKey
  val gameId: String,
  val gameTitle: String,
  val highScore: Int = 0,
  val gamesPlayed: Int = 0,
  val bestStreak: Int = 0,
  val lastPlayedAt: Long = System.currentTimeMillis()
)
