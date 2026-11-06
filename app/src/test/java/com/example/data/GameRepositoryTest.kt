package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GameRepositoryTest {

  private lateinit var database: AppDatabase
  private lateinit var repository: GameRepository

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = GameRepository(database.gameRecordDao())
  }

  @After
  fun closeDb() {
    database.close()
  }

  @Test
  fun testRecordGameFinishedInsertsRecordAndUpdatesHighScore() = runTest {
    // First record: score 50
    val isNewHigh1 = repository.recordGameFinished("snake", "Neon Snake", 50, streak = 2)
    assertTrue(isNewHigh1)

    val record1 = repository.getRecord("snake").first()
    assertEquals("snake", record1?.gameId)
    assertEquals(50, record1?.highScore)
    assertEquals(1, record1?.gamesPlayed)
    assertEquals(2, record1?.bestStreak)

    // Second record: lower score 30
    val isNewHigh2 = repository.recordGameFinished("snake", "Neon Snake", 30, streak = 1)
    assertFalse(isNewHigh2)

    val record2 = repository.getRecord("snake").first()
    assertEquals(50, record2?.highScore) // Still 50
    assertEquals(2, record2?.gamesPlayed) // Incremented to 2

    // Third record: higher score 120
    val isNewHigh3 = repository.recordGameFinished("snake", "Neon Snake", 120, streak = 5)
    assertTrue(isNewHigh3)

    val record3 = repository.getRecord("snake").first()
    assertEquals(120, record3?.highScore) // Updated to 120
    assertEquals(3, record3?.gamesPlayed)
    assertEquals(5, record3?.bestStreak)
  }

  @Test
  fun testGetAllRecordsReturnsAllGames() = runTest {
    repository.recordGameFinished("snake", "Neon Snake", 80)
    repository.recordGameFinished("puzzle2048", "2048 Puzzle", 256)
    repository.recordGameFinished("space", "Space Dodger", 420)

    val all = repository.allRecords.first()
    assertEquals(3, all.size)
  }
}
