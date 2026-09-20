package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class SoundHelper(context: Context) {
  private var toneGenerator: ToneGenerator? = null
  private val vibrator: Vibrator? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  } catch (e: Exception) {
    null
  }

  var isSoundEnabled: Boolean = true
  var isHapticsEnabled: Boolean = true

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
    } catch (e: Exception) {
      Log.e("SoundHelper", "ToneGenerator init failed", e)
    }
  }

  fun playTone(toneType: Int, durationMs: Int = 100) {
    if (!isSoundEnabled) return
    try {
      toneGenerator?.startTone(toneType, durationMs)
    } catch (e: Exception) {
      Log.e("SoundHelper", "playTone error", e)
    }
  }

  fun playMove() {
    playTone(ToneGenerator.TONE_PROP_BEEP, 50)
  }

  fun playCollect() {
    playTone(ToneGenerator.TONE_PROP_ACK, 80)
    vibrate(30)
  }

  fun playMerge() {
    playTone(ToneGenerator.TONE_PROP_PROMPT, 100)
    vibrate(40)
  }

  fun playSuccess() {
    playTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 200)
    vibrate(60)
  }

  fun playGameOver() {
    playTone(ToneGenerator.TONE_PROP_NACK, 350)
    vibrate(150)
  }

  fun playReflexTone(quadrant: Int) {
    val tone = when (quadrant % 4) {
      0 -> ToneGenerator.TONE_DTMF_1
      1 -> ToneGenerator.TONE_DTMF_2
      2 -> ToneGenerator.TONE_DTMF_3
      else -> ToneGenerator.TONE_DTMF_4
    }
    playTone(tone, 150)
    vibrate(40)
  }

  fun vibrate(milliseconds: Long) {
    if (!isHapticsEnabled || vibrator == null) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(milliseconds)
      }
    } catch (e: Exception) {
      // ignore
    }
  }

  fun release() {
    try {
      toneGenerator?.release()
    } catch (e: Exception) {
      // ignore
    }
    toneGenerator = null
  }
}
