package com.example.ui.games.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.ArcadeGameHeader
import com.example.ui.components.DPadControls
import com.example.ui.components.GameOverDialog
import com.example.ui.components.SoundHelper
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCanvas
import com.example.ui.theme.ArcadeSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun SnakeGameScreen(
  highScore: Int,
  soundHelper: SoundHelper,
  onGameFinished: (finalScore: Int) -> Unit,
  onBackClick: () -> Unit
) {
  val engine = remember { SnakeEngine(gridWidth = 20, gridHeight = 20) }
  var gameState by remember { mutableStateOf(engine.createInitialState()) }
  var currentHighScore by remember { mutableStateOf(highScore) }
  var isNewHighScore by remember { mutableStateOf(false) }
  var showGameOverDialog by remember { mutableStateOf(false) }

  // Game Loop Ticker
  LaunchedEffect(gameState.isGameOver, gameState.isPaused) {
    while (!gameState.isGameOver && !gameState.isPaused) {
      delay(gameState.speedDelayMs)
      val (newState, result) = engine.step(gameState)
      gameState = newState

      when (result) {
        SnakeStepResult.ATE_FOOD -> soundHelper.playCollect()
        SnakeStepResult.ATE_BONUS -> soundHelper.playSuccess()
        SnakeStepResult.GAME_OVER -> {
          soundHelper.playGameOver()
          if (newState.score > currentHighScore) {
            currentHighScore = newState.score
            isNewHighScore = true
          }
          onGameFinished(newState.score)
          showGameOverDialog = true
        }
        SnakeStepResult.NOTHING -> {}
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = ArcadeCanvas,
    topBar = {
      ArcadeGameHeader(
        title = "Neon Snake",
        score = gameState.score,
        highScore = currentHighScore,
        isPaused = gameState.isPaused,
        isSoundEnabled = soundHelper.isSoundEnabled,
        onBackClick = onBackClick,
        onRestartClick = {
          gameState = engine.createInitialState()
          isNewHighScore = false
          showGameOverDialog = false
        },
        onPauseToggle = {
          if (!gameState.isGameOver) {
            gameState = gameState.copy(isPaused = !gameState.isPaused)
          }
        },
        onSoundToggle = {
          soundHelper.isSoundEnabled = !soundHelper.isSoundEnabled
        },
        accentColor = NeonGreen
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
      // Game Board Canvas with Swipe Gesture
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f, fill = false)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(16.dp))
          .background(ArcadeSurface)
          .border(1.5.dp, ArcadeBorder, RoundedCornerShape(16.dp))
          .testTag("snake_canvas")
          .pointerInput(Unit) {
            var totalDragX = 0f
            var totalDragY = 0f
            detectDragGestures(
              onDragStart = {
                totalDragX = 0f
                totalDragY = 0f
              },
              onDrag = { change, dragAmount ->
                change.consume()
                totalDragX += dragAmount.x
                totalDragY += dragAmount.y
              },
              onDragEnd = {
                val absX = abs(totalDragX)
                val absY = abs(totalDragY)
                if (absX > 30f || absY > 30f) {
                  if (absX > absY) {
                    if (totalDragX > 0) {
                      gameState = engine.changeDirection(gameState, SnakeDirection.RIGHT)
                    } else {
                      gameState = engine.changeDirection(gameState, SnakeDirection.LEFT)
                    }
                  } else {
                    if (totalDragY > 0) {
                      gameState = engine.changeDirection(gameState, SnakeDirection.DOWN)
                    } else {
                      gameState = engine.changeDirection(gameState, SnakeDirection.UP)
                    }
                  }
                  soundHelper.playMove()
                }
              }
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val cellW = size.width / gameState.gridWidth
          val cellH = size.height / gameState.gridHeight

          // Draw subtle grid lines
          for (i in 0..gameState.gridWidth) {
            drawLine(
              color = Color.White.copy(alpha = 0.03f),
              start = Offset(i * cellW, 0f),
              end = Offset(i * cellW, size.height),
              strokeWidth = 1f
            )
          }
          for (j in 0..gameState.gridHeight) {
            drawLine(
              color = Color.White.copy(alpha = 0.03f),
              start = Offset(0f, j * cellH),
              end = Offset(size.width, j * cellH),
              strokeWidth = 1f
            )
          }

          // Draw Normal Food (Neon Pink Glowing Circle)
          val foodX = gameState.food.x * cellW + cellW / 2
          val foodY = gameState.food.y * cellH + cellH / 2
          drawCircle(
            color = NeonPink.copy(alpha = 0.3f),
            radius = cellW * 0.6f,
            center = Offset(foodX, foodY)
          )
          drawCircle(
            color = NeonPink,
            radius = cellW * 0.38f,
            center = Offset(foodX, foodY)
          )

          // Draw Bonus Food if active (Neon Yellow Star/Diamond)
          gameState.bonusItem?.let { bonus ->
            val bx = bonus.point.x * cellW + cellW / 2
            val by = bonus.point.y * cellH + cellH / 2
            drawCircle(
              color = NeonYellow.copy(alpha = 0.35f),
              radius = cellW * 0.75f,
              center = Offset(bx, by)
            )
            drawRoundRect(
              color = NeonYellow,
              topLeft = Offset(bonus.point.x * cellW + cellW * 0.15f, bonus.point.y * cellH + cellH * 0.15f),
              size = Size(cellW * 0.7f, cellH * 0.7f),
              cornerRadius = CornerRadius(4f, 4f)
            )
          }

          // Draw Snake Body
          gameState.snake.forEachIndexed { index, point ->
            val isHead = index == 0
            val color = if (isHead) NeonGreen else NeonCyan.copy(alpha = (1f - (index.toFloat() / (gameState.snake.size + 4))).coerceAtLeast(0.4f))
            val pad = if (isHead) 1.5f else 2.5f

            drawRoundRect(
              color = color,
              topLeft = Offset(point.x * cellW + pad, point.y * cellH + pad),
              size = Size(cellW - pad * 2, cellH - pad * 2),
              cornerRadius = CornerRadius(cellW * 0.3f, cellH * 0.3f)
            )

            // Draw eyes on head
            if (isHead) {
              val eyeRadius = cellW * 0.09f
              val eyeColor = Color.Black
              val eyeOffset = cellW * 0.25f
              when (gameState.direction) {
                SnakeDirection.UP -> {
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + eyeOffset, point.y * cellH + eyeOffset))
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + cellW - eyeOffset, point.y * cellH + eyeOffset))
                }
                SnakeDirection.DOWN -> {
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + eyeOffset, point.y * cellH + cellH - eyeOffset))
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + cellW - eyeOffset, point.y * cellH + cellH - eyeOffset))
                }
                SnakeDirection.LEFT -> {
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + eyeOffset, point.y * cellH + eyeOffset))
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + eyeOffset, point.y * cellH + cellH - eyeOffset))
                }
                SnakeDirection.RIGHT -> {
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + cellW - eyeOffset, point.y * cellH + eyeOffset))
                  drawCircle(eyeColor, eyeRadius, Offset(point.x * cellW + cellW - eyeOffset, point.y * cellH + cellH - eyeOffset))
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // D-Pad Controls
      DPadControls(
        onUp = {
          gameState = engine.changeDirection(gameState, SnakeDirection.UP)
          soundHelper.playMove()
        },
        onDown = {
          gameState = engine.changeDirection(gameState, SnakeDirection.DOWN)
          soundHelper.playMove()
        },
        onLeft = {
          gameState = engine.changeDirection(gameState, SnakeDirection.LEFT)
          soundHelper.playMove()
        },
        onRight = {
          gameState = engine.changeDirection(gameState, SnakeDirection.RIGHT)
          soundHelper.playMove()
        },
        accentColor = NeonGreen,
        modifier = Modifier.padding(bottom = 8.dp)
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
      accentColor = NeonGreen
    )
  }
}
