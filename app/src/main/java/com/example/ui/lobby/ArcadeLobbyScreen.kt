package com.example.ui.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.GameRecord
import com.example.ui.components.SoundHelper
import com.example.ui.navigation.ArcadeScreen
import com.example.ui.theme.ArcadeBorder
import com.example.ui.theme.ArcadeCanvas
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.ArcadeSurface
import com.example.ui.theme.ArcadeTextDim
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import kotlin.random.Random

data class GameCardItem(
  val id: String,
  val title: String,
  val category: String,
  val description: String,
  val iconEmoji: String,
  val accentColor: Color,
  val screen: ArcadeScreen
)

val ARCADE_GAMES = listOf(
  GameCardItem(
    id = "snake",
    title = "Neon Snake",
    category = "ARCADE CLASSIC",
    description = "Slither, devour food dots, grab bonus stars, and dodge your tail.",
    iconEmoji = "🐍",
    accentColor = NeonGreen,
    screen = ArcadeScreen.Snake
  ),
  GameCardItem(
    id = "puzzle2048",
    title = "2048 Puzzle",
    category = "NUMBER PUZZLE",
    description = "Slide and merge matching number blocks to reach the legendary 2048 tile.",
    iconEmoji = "🔢",
    accentColor = NeonOrange,
    screen = ArcadeScreen.Puzzle2048
  ),
  GameCardItem(
    id = "reflex",
    title = "Color Reflex",
    category = "MEMORY & SPEED",
    description = "Memorize the glowing color light pulses and repeat the sequence.",
    iconEmoji = "⚡",
    accentColor = NeonBlue,
    screen = ArcadeScreen.Reflex
  ),
  GameCardItem(
    id = "space",
    title = "Space Dodger",
    category = "SPACE SURVIVAL",
    description = "Pilot your starship through meteor showers, collect shields and orbs.",
    iconEmoji = "🚀",
    accentColor = NeonCyan,
    screen = ArcadeScreen.Space
  ),
  GameCardItem(
    id = "word",
    title = "Word Scramble",
    category = "BRAIN TEASER",
    description = "Unscramble letters against the clock with clues and build streaks.",
    iconEmoji = "🔤",
    accentColor = NeonPurple,
    screen = ArcadeScreen.Word
  )
)

@Composable
fun ArcadeLobbyScreen(
  records: List<GameRecord>,
  soundHelper: SoundHelper,
  onNavigateToGame: (ArcadeScreen) -> Unit
) {
  var showStatsDialog by remember { mutableStateOf(false) }
  val totalScore = records.sumOf { it.highScore }
  val totalPlays = records.sumOf { it.gamesPlayed }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = ArcadeCanvas,
    topBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ArcadeCard,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "🎮",
              fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "MINI ARCADE",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 1.sp
                ),
                color = Color.White
              )
              Text(
                text = "5 Retro Mini Games",
                style = MaterialTheme.typography.labelSmall,
                color = ArcadeTextDim
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                soundHelper.isSoundEnabled = !soundHelper.isSoundEnabled
              },
              modifier = Modifier.testTag("lobby_sound_toggle")
            ) {
              Icon(
                imageVector = if (soundHelper.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                contentDescription = "Sound Toggle",
                tint = if (soundHelper.isSoundEnabled) NeonCyan else ArcadeTextDim
              )
            }

            IconButton(
              onClick = { showStatsDialog = true },
              modifier = Modifier.testTag("lobby_stats_button")
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Leaderboard",
                tint = NeonYellow
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Hero Banner
      item {
        ArcadeHeroBanner(
          totalScore = totalScore,
          totalPlays = totalPlays,
          onQuickPlay = {
            val randomGame = ARCADE_GAMES[Random.nextInt(ARCADE_GAMES.size)]
            soundHelper.playCollect()
            onNavigateToGame(randomGame.screen)
          }
        )
      }

      // Section Title
      item {
        Text(
          text = "CHOOSE YOUR GAME",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp
          ),
          color = ArcadeTextDim,
          modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
      }

      // 5 Mini Game Cards
      items(ARCADE_GAMES) { game ->
        val record = records.find { it.gameId == game.id }
        val highScore = record?.highScore ?: 0

        MiniGameCard(
          game = game,
          highScore = highScore,
          onPlayClick = {
            soundHelper.playMove()
            onNavigateToGame(game.screen)
          }
        )
      }
    }
  }

  if (showStatsDialog) {
    ArcadeStatsDialog(
      records = records,
      onDismiss = { showStatsDialog = false }
    )
  }
}

@Composable
fun ArcadeHeroBanner(
  totalScore: Int,
  totalPlays: Int,
  onQuickPlay: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(
        Brush.linearGradient(
          listOf(Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFF0F172A))
        )
      )
      .border(
        1.5.dp,
        Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
        RoundedCornerShape(20.dp)
      )
      .padding(18.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Text(
            text = "ARCADE PASSPORT",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = NeonCyan
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Total Arcade XP",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Text(
            text = "$totalScore PTS",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = NeonYellow
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "PLAYS",
              style = MaterialTheme.typography.labelSmall,
              color = ArcadeTextDim
            )
            Text(
              text = totalPlays.toString(),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Play Button
      Button(
        onClick = onQuickPlay,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("quick_play_button"),
        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "QUICK PLAY RANDOM",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    }
  }
}

@Composable
fun MiniGameCard(
  game: GameCardItem,
  highScore: Int,
  onPlayClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(ArcadeCard)
      .border(1.2.dp, ArcadeBorder, RoundedCornerShape(18.dp))
      .clickable(onClick = onPlayClick)
      .padding(16.dp)
      .testTag("game_card_${game.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Icon Box
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(game.accentColor.copy(alpha = 0.15f))
            .border(1.5.dp, game.accentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = game.iconEmoji,
            fontSize = 26.sp
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = game.category,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = game.accentColor
          )
          Text(
            text = game.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Text(
            text = game.description,
            style = MaterialTheme.typography.bodySmall,
            color = ArcadeTextDim,
            maxLines = 2
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Best: $highScore pts",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (highScore > 0) NeonYellow else ArcadeTextDim
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Play Button
      Button(
        onClick = onPlayClick,
        modifier = Modifier
          .height(42.dp)
          .testTag("play_button_${game.id}"),
        colors = ButtonDefaults.buttonColors(containerColor = game.accentColor),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp)
      ) {
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play ${game.title}", tint = Color.Black)
        Spacer(modifier = Modifier.width(2.dp))
        Text(
          text = "PLAY",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
          color = Color.Black
        )
      }
    }
  }
}
