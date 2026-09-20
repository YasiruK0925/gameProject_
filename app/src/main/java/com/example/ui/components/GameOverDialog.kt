package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow

@Composable
fun GameOverDialog(
  score: Int,
  highScore: Int,
  isNewHighScore: Boolean,
  onPlayAgain: () -> Unit,
  onExitToLobby: () -> Unit,
  title: String = "GAME OVER",
  subtitle: String? = null,
  accentColor: Color = NeonPink
) {
  Dialog(
    onDismissRequest = {},
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    AnimatedVisibility(
      visible = true,
      enter = fadeIn(tween(250)) + scaleIn(tween(250))
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .testTag("game_over_dialog"),
        shape = RoundedCornerShape(24.dp),
        color = ArcadeCard,
        tonalElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(
          1.5.dp,
          Brush.verticalGradient(listOf(accentColor, ArcadeBorder))
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Trophy / Game Over Icon
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(accentColor.copy(alpha = 0.15f))
              .border(2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = if (isNewHighScore) NeonYellow else accentColor,
              modifier = Modifier.size(36.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 2.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center
          )

          if (isNewHighScore) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(NeonYellow.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
              Text(
                text = "★ NEW HIGH SCORE! ★",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NeonYellow
              )
            }
          }

          if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodyMedium,
              color = ArcadeTextDim,
              textAlign = TextAlign.Center
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Stats Display Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color.Black.copy(alpha = 0.35f))
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "FINAL SCORE",
                  style = MaterialTheme.typography.labelSmall,
                  color = ArcadeTextDim
                )
                Text(
                  text = score.toString(),
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                  color = accentColor
                )
              }
              Box(
                modifier = Modifier
                  .width(1.dp)
                  .height(36.dp)
                  .background(ArcadeBorder)
              )
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "BEST SCORE",
                  style = MaterialTheme.typography.labelSmall,
                  color = ArcadeTextDim
                )
                Text(
                  text = highScore.toString(),
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                  color = NeonYellow
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Action Buttons
          Button(
            onClick = onPlayAgain,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("play_again_button"),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.Replay, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "PLAY AGAIN",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = onExitToLobby,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("exit_lobby_button"),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ArcadeBorder)
          ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ArcadeTextDim)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "BACK TO LOBBY",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = Color.White
            )
          }
        }
      }
    }
  }
}
