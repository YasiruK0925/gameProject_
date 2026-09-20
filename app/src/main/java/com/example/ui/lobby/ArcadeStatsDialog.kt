package com.example.ui.lobby

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.window.Dialog
import com.example.data.GameRecord
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeSurface
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonYellow

@Composable
fun ArcadeStatsDialog(
  records: List<GameRecord>,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .testTag("arcade_stats_dialog"),
      color = ArcadeCard,
      tonalElevation = 8.dp,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ArcadeBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NeonYellow.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = NeonYellow,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "ARCADE HALL OF FAME",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = Color.White
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.testTag("stats_close_button")) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ArcadeTextDim)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Records List
        if (records.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No games played yet.\nPlay any game to set records!",
              color = ArcadeTextDim,
              style = MaterialTheme.typography.bodyMedium,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(records) { record ->
              StatRecordRow(record = record)
            }
          }
        }
      }
    }
  }
}

@Composable
fun StatRecordRow(record: GameRecord) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(ArcadeSurface)
      .border(1.dp, ArcadeBorder, RoundedCornerShape(14.dp))
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = record.gameTitle,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
      Text(
        text = "${record.gamesPlayed} plays",
        style = MaterialTheme.typography.labelSmall,
        color = ArcadeTextDim
      )
    }

    Column(horizontalAlignment = Alignment.End) {
      Text(
        text = "${record.highScore} PTS",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
        color = NeonCyan
      )
      if (record.bestStreak > 0) {
        Text(
          text = "Streak: ${record.bestStreak}",
          style = MaterialTheme.typography.labelSmall,
          color = NeonYellow
        )
      }
    }
  }
}
