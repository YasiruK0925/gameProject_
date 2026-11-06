package com.example.ui.games.word

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ArcadeGameHeader
import com.example.ui.components.GameOverDialog
import com.example.ui.components.SoundHelper
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCanvas
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeSurface
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordScrambleScreen(
  highScore: Int,
  soundHelper: SoundHelper,
  onGameFinished: (finalScore: Int) -> Unit,
  onBackClick: () -> Unit
) {
  val engine = remember { WordScrambleEngine() }
  var gameState by remember { mutableStateOf(engine.createInitialState()) }
  var currentHighScore by remember { mutableStateOf(highScore) }
  var isNewHighScore by remember { mutableStateOf(false) }
  var showGameOverDialog by remember { mutableStateOf(false) }
  var isWrongGuessFlash by remember { mutableStateOf(false) }

  // Countdown Timer Ticker
  LaunchedEffect(gameState.isGameOver, gameState.isWordSolved) {
    while (!gameState.isGameOver && !gameState.isWordSolved) {
      delay(1000)
      val newSec = gameState.secondsLeft - 1
      if (newSec <= 0) {
        soundHelper.playGameOver()
        gameState = gameState.copy(secondsLeft = 0, isGameOver = true)
        if (gameState.score > currentHighScore) {
          currentHighScore = gameState.score
          isNewHighScore = true
        }
        onGameFinished(gameState.score)
        showGameOverDialog = true
      } else {
        gameState = gameState.copy(secondsLeft = newSec)
      }
    }
  }

  // Handle Word Solved advance
  LaunchedEffect(gameState.isWordSolved) {
    if (gameState.isWordSolved && !gameState.isGameOver) {
      soundHelper.playSuccess()
      if (gameState.score > currentHighScore) {
        currentHighScore = gameState.score
        isNewHighScore = true
      }
      delay(900)
      gameState = engine.loadNextWord(gameState)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = ArcadeCanvas,
    topBar = {
      ArcadeGameHeader(
        title = "Word Scramble",
        score = gameState.score,
        highScore = currentHighScore,
        isSoundEnabled = soundHelper.isSoundEnabled,
        onBackClick = onBackClick,
        onRestartClick = {
          gameState = engine.createInitialState()
          isNewHighScore = false
          showGameOverDialog = false
        },
        onSoundToggle = {
          soundHelper.isSoundEnabled = !soundHelper.isSoundEnabled
        },
        accentColor = NeonPurple
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Info Bar: Category, Streak, Timer
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(ArcadeCard)
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NeonPurple.copy(alpha = 0.25f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = gameState.currentWord.category,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = NeonPurple
          )
        }

        if (gameState.streak > 1) {
          Text(
            text = "${gameState.streak}x STREAK",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = NeonYellow
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = if (gameState.secondsLeft <= 10) NeonPink else NeonCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${gameState.secondsLeft}s",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (gameState.secondsLeft <= 10) NeonPink else NeonCyan
          )
        }
      }

      // Hint Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(ArcadeCard)
          .border(1.dp, ArcadeBorder, RoundedCornerShape(16.dp))
          .padding(14.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "CLUE",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            color = ArcadeTextDim
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "\"${gameState.currentWord.hint}\"",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color.White,
            textAlign = TextAlign.Center
          )
        }
      }

      // Target Letter Slots
      val targetLen = gameState.currentWord.word.length
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp)
          .testTag("word_target_slots"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (i in 0 until targetLen) {
          val placedId = gameState.placedChipIds.getOrNull(i)
          val placedChip = placedId?.let { id -> gameState.chips.find { it.id == id } }

          TargetLetterSlot(
            chip = placedChip,
            isSuccess = gameState.isWordSolved,
            isWrong = isWrongGuessFlash,
            onClick = {
              if (placedId != null) {
                gameState = engine.removePlacedChip(gameState, placedId)
                soundHelper.playMove()
              }
            },
            modifier = Modifier.testTag("target_slot_$i")
          )
          if (i < targetLen - 1) {
            Spacer(modifier = Modifier.width(6.dp))
          }
        }
      }

      // Scrambled Letter Chips to Tap
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Tap letters to build word:",
          style = MaterialTheme.typography.labelSmall,
          color = ArcadeTextDim
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("scramble_chips_row"),
          horizontalArrangement = Arrangement.Center,
          verticalArrangement = Arrangement.Center
        ) {
          gameState.chips.forEach { chip ->
            LetterChipView(
              chip = chip,
              onClick = {
                if (!chip.isPlaced) {
                  val (newState, result) = engine.placeChip(gameState, chip.id)
                  soundHelper.playMove()
                  gameState = newState
                  if (result == WordGuessResult.WRONG_WORD) {
                    soundHelper.playGameOver()
                    isWrongGuessFlash = true
                  }
                }
              },
              modifier = Modifier.padding(4.dp)
            )
          }
        }
      }

      // Action Control Buttons: Clear, Hint, Skip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        // Clear
        OutlinedButton(
          onClick = {
            gameState = engine.clearPlaced(gameState)
            soundHelper.playMove()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .padding(end = 4.dp)
            .testTag("word_clear_button"),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, ArcadeBorder)
        ) {
          Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = ArcadeTextDim, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("CLEAR", fontSize = 12.sp, color = Color.White)
        }

        // Hint
        Button(
          onClick = {
            if (gameState.hintsLeft > 0) {
              gameState = engine.useHint(gameState)
              soundHelper.playCollect()
            }
          },
          enabled = gameState.hintsLeft > 0,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .padding(horizontal = 4.dp)
            .testTag("word_hint_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
          Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint", tint = NeonYellow, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("HINT (${gameState.hintsLeft})", fontSize = 12.sp, color = Color.White)
        }

        // Skip
        OutlinedButton(
          onClick = {
            gameState = engine.loadNextWord(gameState.copy(streak = 0))
            soundHelper.playMove()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .padding(start = 4.dp)
            .testTag("word_skip_button"),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, ArcadeBorder)
        ) {
          Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Skip", tint = ArcadeTextDim, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("SKIP", fontSize = 12.sp, color = Color.White)
        }
      }
    }
  }

  if (showGameOverDialog) {
    GameOverDialog(
      score = gameState.score,
      highScore = currentHighScore,
      isNewHighScore = isNewHighScore,
      onPlayAgain = {
        showGameOverDialog = false
        isNewHighScore = false
        gameState = engine.createInitialState()
      },
      onExitToLobby = {
        showGameOverDialog = false
        onBackClick()
      },
      accentColor = NeonPurple,
      subtitle = "Solved ${gameState.wordsSolved} words"
    )
  }
}

@Composable
fun TargetLetterSlot(
  chip: LetterChip?,
  isSuccess: Boolean,
  isWrong: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderCol = when {
    isSuccess -> NeonGreen
    isWrong -> NeonPink
    chip != null -> NeonPurple
    else -> ArcadeBorder
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(if (chip != null) ArcadeCard else ArcadeSurface)
      .border(2.dp, borderCol, RoundedCornerShape(12.dp))
      .clickable(enabled = chip != null, onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    if (chip != null) {
      Text(
        text = chip.char.toString(),
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = if (isSuccess) NeonGreen else Color.White
      )
    }
  }
}

@Composable
fun LetterChipView(
  chip: LetterChip,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bg = if (chip.isPlaced) ArcadeSurface.copy(alpha = 0.35f) else ArcadeCard
  val textCol = if (chip.isPlaced) ArcadeTextDim.copy(alpha = 0.3f) else Color.White

  Box(
    modifier = modifier
      .size(50.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(bg)
      .border(
        width = 1.5.dp,
        color = if (chip.isPlaced) Color.Transparent else NeonPurple.copy(alpha = 0.7f),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(
        enabled = !chip.isPlaced,
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = NeonPurple),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = chip.char.toString(),
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
      color = textCol
    )
  }
}
