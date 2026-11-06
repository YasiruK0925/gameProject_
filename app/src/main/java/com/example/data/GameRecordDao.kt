package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
  @Query("SELECT * FROM game_records ORDER BY gameTitle ASC")
  fun getAllRecords(): Flow<List<GameRecord>>

  @Query("SELECT * FROM game_records WHERE gameId = :gameId LIMIT 1")
  fun getRecord(gameId: String): Flow<GameRecord?>

  @Query("SELECT * FROM game_records WHERE gameId = :gameId LIMIT 1")
  suspend fun getRecordSync(gameId: String): GameRecord?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(record: GameRecord)

  @Query("DELETE FROM game_records")
  suspend fun clearAll()
}
