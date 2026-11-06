package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.ui.components.SoundHelper
import com.example.ui.games.puzzle2048.Puzzle2048Screen
import com.example.ui.games.reflex.ColorReflexScreen
import com.example.ui.games.snake.SnakeGameScreen
import com.example.ui.games.space.SpaceDodgerScreen
import com.example.ui.games.word.WordScrambleScreen
import com.example.ui.lobby.ArcadeLobbyScreen
import com.example.ui.navigation.ArcadeScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private var soundHelper: SoundHelper? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getInstance(this)
    val repository = GameRepository(database.gameRecordDao())
    val sound = SoundHelper(this)
    soundHelper = sound

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          ArcadeApp(repository = repository, soundHelper = sound)
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    soundHelper?.release()
    soundHelper = null
  }
}

@Composable
fun ArcadeApp(
  repository: GameRepository,
  soundHelper: SoundHelper
) {
  var currentScreen by remember { mutableStateOf<ArcadeScreen>(ArcadeScreen.Lobby) }
  val records by repository.allRecords.collectAsStateWithLifecycle(initialValue = emptyList())
  val scope = rememberCoroutineScope()

  fun getHighScoreFor(gameId: String): Int {
    return records.find { it.gameId == gameId }?.highScore ?: 0
  }

  BackHandler(enabled = currentScreen != ArcadeScreen.Lobby) {
    currentScreen = ArcadeScreen.Lobby
  }

  when (val screen = currentScreen) {
    is ArcadeScreen.Lobby -> {
      ArcadeLobbyScreen(
        records = records,
        soundHelper = soundHelper,
        onNavigateToGame = { targetScreen ->
          currentScreen = targetScreen
        }
      )
    }

    is ArcadeScreen.Snake -> {
      SnakeGameScreen(
        highScore = getHighScoreFor("snake"),
        soundHelper = soundHelper,
        onGameFinished = { score ->
          scope.launch {
            repository.recordGameFinished("snake", "Neon Snake", score)
          }
        },
        onBackClick = { currentScreen = ArcadeScreen.Lobby }
      )
    }

    is ArcadeScreen.Puzzle2048 -> {
      Puzzle2048Screen(
        highScore = getHighScoreFor("puzzle2048"),
        soundHelper = soundHelper,
        onGameFinished = { score ->
          scope.launch {
            repository.recordGameFinished("puzzle2048", "2048 Puzzle", score)
          }
        },
        onBackClick = { currentScreen = ArcadeScreen.Lobby }
      )
    }

    is ArcadeScreen.Reflex -> {
      ColorReflexScreen(
        highScore = getHighScoreFor("reflex"),
        soundHelper = soundHelper,
        onGameFinished = { score ->
          scope.launch {
            repository.recordGameFinished("reflex", "Color Reflex", score)
          }
        },
        onBackClick = { currentScreen = ArcadeScreen.Lobby }
      )
    }

    is ArcadeScreen.Space -> {
      SpaceDodgerScreen(
        highScore = getHighScoreFor("space"),
        soundHelper = soundHelper,
        onGameFinished = { score ->
          scope.launch {
            repository.recordGameFinished("space", "Space Dodger", score)
          }
        },
        onBackClick = { currentScreen = ArcadeScreen.Lobby }
      )
    }

    is ArcadeScreen.Word -> {
      WordScrambleScreen(
        highScore = getHighScoreFor("word"),
        soundHelper = soundHelper,
        onGameFinished = { score ->
          scope.launch {
            repository.recordGameFinished("word", "Word Scramble", score)
          }
        },
        onBackClick = { currentScreen = ArcadeScreen.Lobby }
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

