package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ArcadeCard
import com.example.ui.theme.NeonCyan

@Composable
fun DPadControls(
  onUp: () -> Unit,
  onDown: () -> Unit,
  onLeft: () -> Unit,
  onRight: () -> Unit,
  accentColor: Color = NeonCyan,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // UP
    DPadButton(
      icon = Icons.Default.KeyboardArrowUp,
      contentDescription = "Up",
      testTag = "dpad_up",
      accentColor = accentColor,
      onClick = onUp
    )

    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // LEFT
      DPadButton(
        icon = Icons.Default.KeyboardArrowLeft,
        contentDescription = "Left",
        testTag = "dpad_left",
        accentColor = accentColor,
        onClick = onLeft
      )

      Spacer(modifier = Modifier.width(54.dp))

      // RIGHT
      DPadButton(
        icon = Icons.Default.KeyboardArrowRight,
        contentDescription = "Right",
        testTag = "dpad_right",
        accentColor = accentColor,
        onClick = onRight
      )
    }

    // DOWN
    DPadButton(
      icon = Icons.Default.KeyboardArrowDown,
      contentDescription = "Down",
      testTag = "dpad_down",
      accentColor = accentColor,
      onClick = onDown
    )
  }
}

@Composable
fun DPadButton(
  icon: ImageVector,
  contentDescription: String,
  testTag: String,
  accentColor: Color,
  onClick: () -> Unit
) {
  FilledIconButton(
    onClick = onClick,
    modifier = Modifier
      .size(54.dp)
      .testTag(testTag),
    shape = CircleShape,
    colors = IconButtonDefaults.filledIconButtonColors(
      containerColor = ArcadeCard,
      contentColor = accentColor
    )
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      modifier = Modifier.size(34.dp)
    )
  }
}
