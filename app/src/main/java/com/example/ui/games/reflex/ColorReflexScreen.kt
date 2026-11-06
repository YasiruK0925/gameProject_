package com.example.ui.games.reflex

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ColorReflexScreen(
  highScore: Int,
  soundHelper: SoundHelper,
  onGameFinished: (finalScore: Int) -> Unit,
  onBackClick: () -> Unit
) {
  val engine = remember { ColorReflexEngine() }
  var gameState by remember { mutableStateOf(engine.createInitialState()) }
  var currentHighScore by remember { mutableStateOf(highScore) }
  var isNewHighScore by remember { mutableStateOf(false) }
  var showGameOverDialog by remember { mutableStateOf(false) }
  var activeHighlight by remember { mutableStateOf<Int?>(null) }
  val scope = rememberCoroutineScope()

  // Demonstration loop when computer is showing the pattern
  LaunchedEffect(gameState.isDemonstrating, gameState.round, gameState.lives) {
    if (gameState.isDemonstrating && !gameState.isGameOver) {
      delay(400) // slight pause before sequence starts
      for (colorId in gameState.sequence) {
        activeHighlight = colorId
        soundHelper.playReflexTone(colorId)
        delay(380)
        activeHighlight = null
        delay(140)
      }
      gameState = gameState.copy(isDemonstrating = false)
    }
  }

  fun onPadTapped(colorId: Int) {
    if (gameState.isDemonstrating || gameState.isGameOver) return

    activeHighlight = colorId
    soundHelper.playReflexTone(colorId)
    scope.launch {
      delay(180)
      if (activeHighlight == colorId) activeHighlight = null
    }

    val (newState, result) = engine.handlePlayerTap(gameState, colorId)
    gameState = newState

    when (result) {
      PlayerTapResult.CORRECT_STEP -> {}
      PlayerTapResult.ROUND_COMPLETED -> {
        soundHelper.playSuccess()
        if (newState.score > currentHighScore) {
          currentHighScore = newState.score
          isNewHighScore = true
        }
      }
      PlayerTapResult.WRONG_STEP_CONTINUE -> {
        soundHelper.playGameOver()
      }
      PlayerTapResult.GAME_OVER -> {
        soundHelper.playGameOver()
        if (newState.score > currentHighScore) {
          currentHighScore = newState.score
          isNewHighScore = true
        }
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
        title = "Color Reflex",
        score = gameState.score,
        highScore = currentHighScore,
        isSoundEnabled = soundHelper.isSoundEnabled,
        onBackClick = onBackClick,
        onRestartClick = {
          gameState = engine.createInitialState()
          isNewHighScore = false
          showGameOverDialog = false
          activeHighlight = null
        },
        onSoundToggle = {
          soundHelper.isSoundEnabled = !soundHelper.isSoundEnabled
        },
        accentColor = NeonBlue
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 20.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Status and Info Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(ArcadeCard)
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Round & Streak
        Column {
          Text(
            text = "ROUND ${gameState.round}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = NeonCyan
          )
          if (gameState.streak > 1) {
            Text(
              text = "STREAK: ${gameState.streak}x",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = NeonYellow
            )
          }
        }

        // Status Indicator
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
              if (gameState.isDemonstrating) NeonYellow.copy(alpha = 0.2f) else NeonGreen.copy(alpha = 0.2f)
            )
            .border(
              1.dp,
              if (gameState.isDemonstrating) NeonYellow else NeonGreen,
              RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = if (gameState.isDemonstrating) "WATCH..." else "YOUR TURN!",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (gameState.isDemonstrating) NeonYellow else NeonGreen
          )
        }

        // Lives Hearts
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          for (i in 1..3) {
            Icon(
              imageVector = if (i <= gameState.lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = null,
              tint = if (i <= gameState.lives) NeonPink else ArcadeTextDim,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // 4 Large Glowing Reflex Pads Grid
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .padding(12.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // GREEN (Quadrant 0) - Top Left
            ReflexPad(
              id = 0,
              baseColor = NeonGreen,
              isHighlighted = activeHighlight == 0,
              shape = RoundedCornerShape(topStart = 48.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
              modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .testTag("reflex_pad_0"),
              onClick = { onPadTapped(0) }
            )

            // RED (Quadrant 1) - Top Right
            ReflexPad(
              id = 1,
              baseColor = NeonPink,
              isHighlighted = activeHighlight == 1,
              shape = RoundedCornerShape(topStart = 16.dp, topEnd = 48.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
              modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .testTag("reflex_pad_1"),
              onClick = { onPadTapped(1) }
            )
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // BLUE (Quadrant 2) - Bottom Left
            ReflexPad(
              id = 2,
              baseColor = NeonBlue,
              isHighlighted = activeHighlight == 2,
              shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 48.dp, bottomEnd = 16.dp),
              modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .testTag("reflex_pad_2"),
              onClick = { onPadTapped(2) }
            )

            // YELLOW (Quadrant 3) - Bottom Right
            ReflexPad(
              id = 3,
              baseColor = NeonYellow,
              isHighlighted = activeHighlight == 3,
              shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 48.dp),
              modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .testTag("reflex_pad_3"),
              onClick = { onPadTapped(3) }
            )
          }
        }

        // Center arcade branding badge
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(ArcadeCanvas)
            .border(2.dp, ArcadeBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "⚡",
            fontSize = 22.sp
          )
        }
      }

      // Instruction helper text
      Text(
        text = if (gameState.isDemonstrating) "Memorize the glowing color lights" else "Tap the pads in the same sequence!",
        style = MaterialTheme.typography.bodyMedium,
        color = ArcadeTextDim,
        modifier = Modifier.padding(bottom = 12.dp)
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
        activeHighlight = null
      },
      onExitToLobby = {
        showGameOverDialog = false
        onBackClick()
      },
      accentColor = NeonBlue,
      subtitle = "Reached Round ${gameState.round}"
    )
  }
}

@Composable
fun ReflexPad(
  id: Int,
  baseColor: Color,
  isHighlighted: Boolean,
  shape: RoundedCornerShape,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val animatedColor by animateColorAsState(
    targetValue = if (isHighlighted) baseColor else baseColor.copy(alpha = 0.28f),
    animationSpec = tween(durationMillis = 120),
    label = "padColor_$id"
  )

  Box(
    modifier = modifier
      .clip(shape)
      .background(animatedColor)
      .border(
        width = if (isHighlighted) 3.dp else 1.5.dp,
        color = if (isHighlighted) Color.White else baseColor.copy(alpha = 0.6f),
        shape = shape
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color.White),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    if (isHighlighted) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.4f))
      )
    }
  }
}
