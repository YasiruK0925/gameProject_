package com.example.ui.games.puzzle2048

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ArcadeGameHeader
import com.example.ui.components.DPadControls
import com.example.ui.components.GameOverDialog
import com.example.ui.components.SoundHelper
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCanvas
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeSurface
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonYellow
import kotlin.math.abs

@Composable
fun Puzzle2048Screen(
  highScore: Int,
  soundHelper: SoundHelper,
  onGameFinished: (finalScore: Int) -> Unit,
  onBackClick: () -> Unit
) {
  val engine = remember { Puzzle2048Engine() }
  var gameState by remember { mutableStateOf(engine.createInitialState()) }
  var currentHighScore by remember { mutableStateOf(highScore) }
  var isNewHighScore by remember { mutableStateOf(false) }
  var showGameOverDialog by remember { mutableStateOf(false) }

  fun executeMove(dir: MoveDirection) {
    val (newState, moved) = engine.move(gameState, dir)
    if (moved) {
      soundHelper.playMerge()
      gameState = newState
      if (newState.score > currentHighScore) {
        currentHighScore = newState.score
        isNewHighScore = true
      }
      if (newState.isGameOver) {
        soundHelper.playGameOver()
        onGameFinished(newState.score)
        showGameOverDialog = true
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = ArcadeCanvas,
    topBar = {
      ArcadeGameHeader(
        title = "2048 Puzzle",
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
        accentColor = NeonOrange
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top row: Win banner and Undo button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (gameState.hasWon) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(NeonYellow.copy(alpha = 0.2f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "★ 2048 ACHIEVED! ★",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = NeonYellow
            )
          }
        } else {
          Text(
            text = "Join numbers to get 2048!",
            style = MaterialTheme.typography.bodySmall,
            color = ArcadeTextDim
          )
        }

        Button(
          onClick = {
            if (gameState.previousState != null) {
              gameState = engine.undo(gameState)
              soundHelper.playMove()
            }
          },
          enabled = gameState.previousState != null,
          colors = ButtonDefaults.buttonColors(
            containerColor = ArcadeCard,
            contentColor = NeonOrange,
            disabledContainerColor = ArcadeCard.copy(alpha = 0.5f),
            disabledContentColor = ArcadeTextDim.copy(alpha = 0.4f)
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("2048_undo_button")
        ) {
          Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Undo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      // 4x4 Grid Board with Swipe Gestures
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .clip(RoundedCornerShape(18.dp))
          .background(ArcadeSurface)
          .border(2.dp, ArcadeBorder, RoundedCornerShape(18.dp))
          .padding(10.dp)
          .testTag("puzzle_2048_grid")
          .pointerInput(Unit) {
            var dragX = 0f
            var dragY = 0f
            detectDragGestures(
              onDragStart = {
                dragX = 0f
                dragY = 0f
              },
              onDrag = { change, dragAmount ->
                change.consume()
                dragX += dragAmount.x
                dragY += dragAmount.y
              },
              onDragEnd = {
                val absX = abs(dragX)
                val absY = abs(dragY)
                if (absX > 25f || absY > 25f) {
                  if (absX > absY) {
                    if (dragX > 0) executeMove(MoveDirection.RIGHT) else executeMove(MoveDirection.LEFT)
                  } else {
                    if (dragY > 0) executeMove(MoveDirection.DOWN) else executeMove(MoveDirection.UP)
                  }
                }
              }
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (r in 0 until 4) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              for (c in 0 until 4) {
                val value = gameState.grid[r][c]
                TileView(
                  value = value,
                  modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .testTag("tile_${r}_${c}")
                )
              }
            }
          }
        }
      }

      // D-Pad Controls for precision sliding
      DPadControls(
        onUp = { executeMove(MoveDirection.UP) },
        onDown = { executeMove(MoveDirection.DOWN) },
        onLeft = { executeMove(MoveDirection.LEFT) },
        onRight = { executeMove(MoveDirection.RIGHT) },
        accentColor = NeonOrange,
        modifier = Modifier.padding(bottom = 6.dp)
      )
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
      accentColor = NeonOrange
    )
  }
}

@Composable
fun TileView(
  value: Int,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, fontSize) = getTileAppearance(value)

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor),
    contentAlignment = Alignment.Center
  ) {
    if (value > 0) {
      Text(
        text = value.toString(),
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.ExtraBold,
          fontSize = fontSize
        ),
        color = textColor
      )
    }
  }
}

private fun getTileAppearance(value: Int): Triple<Color, Color, androidx.compose.ui.unit.TextUnit> {
  return when (value) {
    0 -> Triple(Color(0xFF242240), Color.Transparent, 18.sp)
    2 -> Triple(Color(0xFF2E3258), Color(0xFFE2E8F0), 24.sp)
    4 -> Triple(Color(0xFF3B4070), Color(0xFFF1F5F9), 24.sp)
    8 -> Triple(Color(0xFFF97316), Color.White, 24.sp)
    16 -> Triple(Color(0xFFEA580C), Color.White, 22.sp)
    32 -> Triple(Color(0xFFEF4444), Color.White, 22.sp)
    64 -> Triple(Color(0xFFDC2626), Color.White, 22.sp)
    128 -> Triple(Color(0xFFEAB308), Color.Black, 20.sp)
    256 -> Triple(Color(0xFFFACC15), Color.Black, 20.sp)
    512 -> Triple(Color(0xFF10B981), Color.White, 18.sp)
    1024 -> Triple(Color(0xFF06B6D4), Color.White, 16.sp)
    2048 -> Triple(Color(0xFF8B5CF6), Color.White, 16.sp)
    else -> Triple(Color(0xFFEC4899), Color.White, 14.sp)
  }
}
