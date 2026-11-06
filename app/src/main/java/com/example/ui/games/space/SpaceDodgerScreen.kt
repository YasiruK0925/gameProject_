package com.example.ui.games.space

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ArcadeGameHeader
import com.example.ui.components.GameOverDialog
import com.example.ui.components.SoundHelper
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCanvas
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpaceDodgerScreen(
  highScore: Int,
  soundHelper: SoundHelper,
  onGameFinished: (finalScore: Int) -> Unit,
  onBackClick: () -> Unit
) {
  val engine = remember { SpaceDodgerEngine() }
  var gameState by remember { mutableStateOf(engine.createInitialState()) }
  var currentHighScore by remember { mutableStateOf(highScore) }
  var isNewHighScore by remember { mutableStateOf(false) }
  var showGameOverDialog by remember { mutableStateOf(false) }

  // 30 FPS Game Loop
  LaunchedEffect(gameState.isGameOver, gameState.isPaused) {
    while (!gameState.isGameOver && !gameState.isPaused) {
      delay(33) // ~30 fps
      val (newState, event) = engine.step(gameState)
      gameState = newState

      when (event) {
        SpaceStepEvent.COLLECTED_ORB -> soundHelper.playCollect()
        SpaceStepEvent.COLLECTED_SHIELD -> soundHelper.playSuccess()
        SpaceStepEvent.SHIELD_DEFLECTED -> soundHelper.playCollect()
        SpaceStepEvent.CRASHED_GAME_OVER -> {
          soundHelper.playGameOver()
          if (newState.score > currentHighScore) {
            currentHighScore = newState.score
            isNewHighScore = true
          }
          onGameFinished(newState.score)
          showGameOverDialog = true
        }
        SpaceStepEvent.NONE -> {}
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = ArcadeCanvas,
    topBar = {
      ArcadeGameHeader(
        title = "Space Dodger",
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
        accentColor = NeonCyan
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
      // Wave & Shield Status Banner
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ArcadeCard)
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "SECTOR ${gameState.wave}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = NeonCyan
          )
        }

        if (gameState.hasShield) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(NeonPurple.copy(alpha = 0.25f))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = NeonPurple,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "SHIELD ACTIVE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = NeonPurple
            )
          }
        }
      }

      // Space Dodger Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f, fill = false)
          .aspectRatio(0.78f)
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0xFF070614))
          .border(1.5.dp, ArcadeBorder, RoundedCornerShape(18.dp))
          .testTag("space_canvas")
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              val screenWidth = size.width.toFloat()
              val deltaNorm = dragAmount.x / screenWidth
              gameState = engine.updatePlayerPosition(gameState, gameState.playerX + deltaNorm)
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Draw Stars
          gameState.stars.forEach { star ->
            drawCircle(
              color = Color.White.copy(alpha = 0.75f),
              radius = star.size,
              center = Offset(star.x * w, star.y * h)
            )
          }

          // Draw Collectible Orbs
          gameState.orbs.forEach { orb ->
            val orbColor = if (orb.isShield) NeonPurple else NeonYellow
            val orbCenter = Offset(orb.x * w, orb.y * h)
            drawCircle(
              color = orbColor.copy(alpha = 0.35f),
              radius = w * 0.055f,
              center = orbCenter
            )
            drawCircle(
              color = orbColor,
              radius = w * 0.035f,
              center = orbCenter
            )
          }

          // Draw Asteroids
          gameState.asteroids.forEach { asteroid ->
            val rockCenter = Offset(asteroid.x * w, asteroid.y * h)
            val rad = asteroid.radius * w

            // Glowing boundary
            drawCircle(
              color = NeonOrange.copy(alpha = 0.2f),
              radius = rad * 1.3f,
              center = rockCenter
            )
            // Asteroid body
            drawCircle(
              color = Color(0xFF6B5876),
              radius = rad,
              center = rockCenter
            )
            // Crater detail
            drawCircle(
              color = Color(0xFF4A3B54),
              radius = rad * 0.35f,
              center = Offset(rockCenter.x - rad * 0.2f, rockCenter.y - rad * 0.2f)
            )
          }

          // Draw Player Starship
          val shipCenterX = gameState.playerX * w
          val shipCenterY = 0.86f * h
          val shipW = w * 0.12f
          val shipH = h * 0.07f

          // Shield Bubble if active
          if (gameState.hasShield) {
            drawCircle(
              color = NeonPurple.copy(alpha = 0.25f),
              radius = shipW * 1.2f,
              center = Offset(shipCenterX, shipCenterY)
            )
            drawCircle(
              color = NeonPurple,
              radius = shipW * 1.2f,
              center = Offset(shipCenterX, shipCenterY),
              style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
            )
          }

          // Jet Engine Flame
          val flamePath = Path().apply {
            moveTo(shipCenterX - shipW * 0.25f, shipCenterY + shipH * 0.45f)
            lineTo(shipCenterX, shipCenterY + shipH * 0.95f)
            lineTo(shipCenterX + shipW * 0.25f, shipCenterY + shipH * 0.45f)
            close()
          }
          drawPath(flamePath, color = NeonOrange)

          // Ship Wings & Hull
          val shipPath = Path().apply {
            moveTo(shipCenterX, shipCenterY - shipH * 0.6f) // Nose
            lineTo(shipCenterX + shipW * 0.5f, shipCenterY + shipH * 0.45f) // Right wing
            lineTo(shipCenterX + shipW * 0.2f, shipCenterY + shipH * 0.3f)
            lineTo(shipCenterX - shipW * 0.2f, shipCenterY + shipH * 0.3f)
            lineTo(shipCenterX - shipW * 0.5f, shipCenterY + shipH * 0.45f) // Left wing
            close()
          }
          drawPath(shipPath, color = NeonCyan)

          // Cockpit canopy
          drawCircle(
            color = Color.White,
            radius = shipW * 0.15f,
            center = Offset(shipCenterX, shipCenterY - shipH * 0.1f)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Steering Touch Buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Button(
          onClick = {
            gameState = engine.updatePlayerPosition(gameState, gameState.playerX - 0.08f)
            soundHelper.playMove()
          },
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .padding(end = 8.dp)
            .testTag("space_steer_left"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ArcadeCard)
        ) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Steer Left", tint = NeonCyan)
          Spacer(modifier = Modifier.width(8.dp))
          Text("LEFT", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Button(
          onClick = {
            gameState = engine.updatePlayerPosition(gameState, gameState.playerX + 0.08f)
            soundHelper.playMove()
          },
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .padding(start = 8.dp)
            .testTag("space_steer_right"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ArcadeCard)
        ) {
          Text("RIGHT", fontWeight = FontWeight.Bold, color = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Steer Right", tint = NeonCyan)
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
      accentColor = NeonCyan,
      subtitle = "Survived to Sector ${gameState.wave}"
    )
  }
}
