package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonYellow

@Composable
fun ArcadeGameHeader(
  title: String,
  score: Int,
  highScore: Int,
  isPaused: Boolean = false,
  isSoundEnabled: Boolean = true,
  onBackClick: () -> Unit,
  onRestartClick: () -> Unit,
  onPauseToggle: (() -> Unit)? = null,
  onSoundToggle: (() -> Unit)? = null,
  accentColor: Color = NeonCyan,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = ArcadeCard,
    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
    tonalElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.08f))
              .testTag("header_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Lobby",
              tint = Color.White
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = Color.White
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (onSoundToggle != null) {
            IconButton(
              onClick = onSoundToggle,
              modifier = Modifier.testTag("header_sound_button")
            ) {
              Icon(
                imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                contentDescription = "Toggle Sound",
                tint = if (isSoundEnabled) accentColor else ArcadeTextDim
              )
            }
          }

          if (onPauseToggle != null) {
            IconButton(
              onClick = onPauseToggle,
              modifier = Modifier.testTag("header_pause_button")
            ) {
              Icon(
                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = if (isPaused) "Resume" else "Pause",
                tint = accentColor
              )
            }
          }

          IconButton(
            onClick = onRestartClick,
            modifier = Modifier.testTag("header_restart_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Restart Game",
              tint = Color.White.copy(alpha = 0.85f)
            )
          }
        }
      }

      // Scores Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        ScorePill(
          label = "SCORE",
          value = score.toString(),
          labelColor = ArcadeTextDim,
          valueColor = accentColor,
          modifier = Modifier.testTag("score_display")
        )
        ScorePill(
          label = "BEST",
          value = highScore.toString(),
          labelColor = ArcadeTextDim,
          valueColor = NeonYellow,
          modifier = Modifier.testTag("high_score_display")
        )
      }
    }
  }
}

@Composable
fun ScorePill(
  label: String,
  value: String,
  labelColor: Color,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color.Black.copy(alpha = 0.35f))
      .padding(horizontal = 20.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.sp
        ),
        color = labelColor
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.ExtraBold
        ),
        color = valueColor
      )
    }
  }
}
