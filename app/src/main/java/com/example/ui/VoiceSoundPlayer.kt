package com.example.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object VoiceSoundPlayer {
  private const val SAMPLE_RATE = 44100
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  fun playActivationChime() {
    scope.launch {
      val samples = buildRisingChimePcm()
      val bufferBytes = samples.size * 2
      var track: AudioTrack? = null
      runCatching {
        val attributes =
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val format =
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        track =
          AudioTrack.Builder()
            .setAudioAttributes(attributes)
            .setAudioFormat(format)
            .setBufferSizeInBytes(bufferBytes)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
            .apply {
              write(samples, 0, samples.size)
              play()
            }
        delay(340L)
      }
      runCatching {
        track?.stop()
        track?.release()
      }
    }
  }

  private fun buildRisingChimePcm(): ShortArray {
    val durationSec = 0.28f
    val totalSamples = (SAMPLE_RATE * durationSec).toInt()
    val output = ShortArray(totalSamples)

    for (i in 0 until totalSamples) {
      val t = i.toFloat() / SAMPLE_RATE

      // First warm chime note (C5 = 523.25 Hz)
      val note1 =
        if (t <= 0.18f) {
          val env = envelope(t, duration = 0.18f, attack = 0.012f, decayRate = 16f)
          val wave = sin(2.0 * PI * 523.25 * t) + 0.18 * sin(2.0 * PI * 1046.5 * t)
          (wave * env).toFloat()
        } else {
          0f
        }

      // Second rising harmonic note (G5 = 783.99 Hz) starting at 70ms
      val t2 = t - 0.07f
      val note2 =
        if (t2 >= 0f) {
          val env = envelope(t2, duration = 0.21f, attack = 0.015f, decayRate = 13f)
          val wave = sin(2.0 * PI * 783.99 * t2) + 0.15 * sin(2.0 * PI * 1567.98 * t2)
          (wave * env).toFloat()
        } else {
          0f
        }

      val mixed = ((note1 * 0.55f + note2 * 0.65f) * 0.45f).coerceIn(-1f, 1f)
      output[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
    }
    return output
  }

  private fun envelope(
    t: Float,
    duration: Float,
    attack: Float,
    decayRate: Float,
  ): Float {
    val attackGain =
      if (t < attack) {
        (0.5f * (1f - cos(PI.toFloat() * t / attack)))
      } else {
        1f
      }
    val tailFade =
      if (t > duration - 0.025f) {
        ((duration - t) / 0.025f).coerceIn(0f, 1f)
      } else {
        1f
      }
    val decay = exp((-decayRate * t).toDouble()).toFloat()
    return attackGain * decay * tailFade
  }
}
