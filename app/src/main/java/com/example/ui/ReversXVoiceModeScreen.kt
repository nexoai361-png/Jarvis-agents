package com.example.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowUp
import com.adamglin.phosphoricons.regular.Microphone
import com.adamglin.phosphoricons.regular.MicrophoneSlash
import com.adamglin.phosphoricons.regular.Pause
import com.adamglin.phosphoricons.regular.Waveform
import com.adamglin.phosphoricons.regular.X
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.theme.DrawerDivider
import com.example.ui.theme.DrawerPlusBorder
import com.example.ui.theme.DrawerSearchSurface
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGlowDeep
import com.example.ui.theme.JarvisIndigo
import com.example.ui.theme.JarvisMutedRed
import com.example.ui.theme.JarvisMutedSurface
import com.example.ui.theme.PlusAccent
import com.example.ui.theme.PlusIconAccent
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TempActiveSurface
import com.example.ui.theme.VoiceGradientEnd
import com.example.ui.theme.VoiceGradientStart
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

@Composable
internal fun ReversXVoiceModeScreen(
  uiState: ReversXUiState,
  onToggleMute: () -> Unit,
  onSelectVisualStyle: (VoiceVisualStyle) -> Unit,
  onListeningChanged: (Boolean) -> Unit,
  onAmplitudeChanged: (Float) -> Unit,
  onTranscriptChanged: (String) -> Unit,
  onSendPromptToChat: () -> Unit,
  onClose: () -> Unit,
) {
  BackHandler(enabled = true, onBack = onClose)

  val context = LocalContext.current
  val isRecognitionSupported = remember(context) {
    SpeechRecognizer.isRecognitionAvailable(context)
  }
  var hasMicPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED,
    )
  }
  var userRequestedListen by remember { mutableStateOf(true) }

  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      hasMicPermission = granted
      if (granted) {
        userRequestedListen = true
      }
    }

  val speechRecognizer =
    remember(context, isRecognitionSupported) {
      if (isRecognitionSupported) {
        runCatching { SpeechRecognizer.createSpeechRecognizer(context) }.getOrNull()
      } else {
        null
      }
    }

  val startListening = remember(speechRecognizer) {
    {
      if (speechRecognizer != null) {
        val intent =
          Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
              RecognizerIntent.EXTRA_LANGUAGE_MODEL,
              RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
          }
        runCatching {
          speechRecognizer.cancel()
          speechRecognizer.startListening(intent)
        }.onFailure {
          onListeningChanged(false)
        }
      }
    }
  }

  DisposableEffect(speechRecognizer) {
    if (speechRecognizer != null) {
      speechRecognizer.setRecognitionListener(
        object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {
            onListeningChanged(true)
          }

          override fun onBeginningOfSpeech() {
            onListeningChanged(true)
          }

          override fun onRmsChanged(rmsdB: Float) {
            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            onAmplitudeChanged(normalized)
          }

          override fun onBufferReceived(buffer: ByteArray?) = Unit

          override fun onEndOfSpeech() {
            onListeningChanged(false)
          }

          override fun onError(error: Int) {
            onListeningChanged(false)
          }

          override fun onResults(results: Bundle?) {
            val text =
              results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()
            if (!text.isNullOrEmpty()) {
              onTranscriptChanged(text)
            }
            onListeningChanged(false)
          }

          override fun onPartialResults(partialResults: Bundle?) {
            val partial =
              partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()
            if (!partial.isNullOrEmpty()) {
              onTranscriptChanged(partial)
            }
          }

          override fun onEvent(eventType: Int, params: Bundle?) = Unit
        },
      )
    }

    onDispose {
      runCatching {
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
      }
      onListeningChanged(false)
    }
  }

  LaunchedEffect(hasMicPermission, uiState.isVoiceMuted, userRequestedListen, speechRecognizer) {
    if (hasMicPermission && !uiState.isVoiceMuted && userRequestedListen && speechRecognizer != null) {
      startListening()
    } else {
      runCatching { speechRecognizer?.stopListening() }
      onListeningChanged(false)
    }
  }

  val statusLabel =
    when {
      uiState.isVoiceMuted -> stringResource(R.string.voice_status_muted)
      !hasMicPermission -> stringResource(R.string.voice_status_permission)
      !isRecognitionSupported -> stringResource(R.string.voice_status_unavailable)
      uiState.isVoiceListening -> stringResource(R.string.voice_status_listening)
      else -> stringResource(R.string.voice_status_standby)
    }

  Box(
    modifier =
      Modifier.fillMaxSize()
        .background(
          Brush.radialGradient(
            colors = listOf(JarvisGlowDeep, PureBlack, PureBlack),
          ),
        )
        .testTag("jarvis_voice_mode_screen"),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier =
        Modifier.fillMaxSize()
          .widthIn(max = 600.dp)
          .statusBarsPadding()
          .navigationBarsPadding()
          .padding(horizontal = 20.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      VoiceHudTopBar(
        isListening = uiState.isVoiceListening,
        isMuted = uiState.isVoiceMuted,
        isTemporaryChat = uiState.isTemporaryChat,
      )

      Spacer(modifier = Modifier.height(14.dp))

      VoiceStyleSwitcherBar(
        selectedStyle = uiState.voiceVisualStyle,
        onSelectStyle = onSelectVisualStyle,
      )

      Spacer(modifier = Modifier.weight(0.55f))

      BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        val coreSize = minOf(maxWidth * 0.78f, 300.dp)
        val orbModifier =
          Modifier.size(coreSize)
            .clip(CircleShape)
            .clickable(
              role = Role.Button,
              onClick = {
                if (!hasMicPermission) {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                } else if (uiState.isVoiceMuted) {
                  onToggleMute()
                  userRequestedListen = true
                } else {
                  userRequestedListen = !userRequestedListen
                }
              },
            )
            .testTag("jarvis_core_orb")

        AnimatedContent(
          targetState = uiState.voiceVisualStyle,
          transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(260)) },
          label = "voice_style_switch",
        ) { style ->
          when (style) {
            VoiceVisualStyle.AURORA ->
              FluidAuroraOrb(
                isListening = uiState.isVoiceListening,
                isMuted = uiState.isVoiceMuted,
                audioLevel = uiState.voiceAmplitude,
                modifier = orbModifier,
              )

            VoiceVisualStyle.JARVIS ->
              JarvisNeuralCore(
                isListening = uiState.isVoiceListening,
                isMuted = uiState.isVoiceMuted,
                audioLevel = uiState.voiceAmplitude,
                modifier = orbModifier,
              )

            VoiceVisualStyle.EQUALIZER ->
              SymmetricFrequencyEqualizer(
                isListening = uiState.isVoiceListening,
                isMuted = uiState.isVoiceMuted,
                audioLevel = uiState.voiceAmplitude,
                modifier =
                  Modifier.fillMaxWidth()
                    .height(coreSize)
                    .clip(RoundedCornerShape(32.dp))
                    .clickable(
                      role = Role.Button,
                      onClick = {
                        if (!hasMicPermission) {
                          permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else if (uiState.isVoiceMuted) {
                          onToggleMute()
                          userRequestedListen = true
                        } else {
                          userRequestedListen = !userRequestedListen
                        }
                      },
                    )
                    .testTag("equalizer_visualizer"),
              )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Surface(
        shape = CircleShape,
        color = if (uiState.isVoiceMuted) JarvisMutedSurface else TempActiveSurface.copy(alpha = 0.7f),
        border =
          BorderStroke(
            0.5.dp,
            if (uiState.isVoiceMuted) JarvisMutedRed.copy(alpha = 0.45f)
            else JarvisCyan.copy(alpha = 0.35f),
          ),
      ) {
        Text(
          text = statusLabel,
          style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
          color = if (uiState.isVoiceMuted) JarvisMutedRed else JarvisCyan,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text =
          uiState.voiceTranscript.ifBlank {
            stringResource(R.string.voice_prompt_hint)
          },
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = 19.sp,
            fontWeight = if (uiState.voiceTranscript.isBlank()) FontWeight.Normal else FontWeight.Medium,
          ),
        color =
          if (uiState.voiceTranscript.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
          else MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
      )

      Spacer(modifier = Modifier.weight(0.65f))

      AnimatedVisibility(
        visible = uiState.voiceTranscript.isNotBlank(),
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Surface(
          shape = CircleShape,
          color = TempActiveSurface,
          border = BorderStroke(0.5.dp, PlusIconAccent.copy(alpha = 0.55f)),
          modifier =
            Modifier.padding(bottom = 20.dp)
              .minimumInteractiveComponentSize()
              .clip(CircleShape)
              .clickable(role = Role.Button, onClick = onSendPromptToChat)
              .testTag("voice_send_prompt_button"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = stringResource(R.string.voice_use_prompt),
              style = MaterialTheme.typography.labelLarge,
              color = PlusAccent,
            )
            Icon(
              imageVector = PhosphorIcons.Regular.ArrowUp,
              contentDescription = null,
              tint = PlusAccent,
              modifier = Modifier.size(16.dp),
            )
          }
        }
      }

      VoiceControlBar(
        isMuted = uiState.isVoiceMuted,
        isActive = !uiState.isVoiceMuted && (uiState.isVoiceListening || userRequestedListen),
        onMuteToggle = {
          if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
          } else {
            onToggleMute()
          }
        },
        onCenterToggle = {
          if (!hasMicPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
          } else if (uiState.isVoiceMuted) {
            onToggleMute()
            userRequestedListen = true
          } else {
            userRequestedListen = !userRequestedListen
          }
        },
        onClose = onClose,
      )
    }
  }
}

@Composable
private fun VoiceHudTopBar(
  isListening: Boolean,
  isMuted: Boolean,
  isTemporaryChat: Boolean,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Surface(
      shape = CircleShape,
      color = DrawerSearchSurface.copy(alpha = 0.85f),
      border = BorderStroke(0.5.dp, DrawerDivider),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Box(
          modifier =
            Modifier.size(8.dp)
              .clip(CircleShape)
              .background(
                when {
                  isMuted -> JarvisMutedRed
                  isListening -> JarvisCyan
                  else -> PlusIconAccent
                },
              ),
        )
        Text(
          text = stringResource(R.string.voice_hud_brand),
          style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
          color = MaterialTheme.colorScheme.onBackground,
        )
      }
    }

    if (isTemporaryChat) {
      Surface(
        shape = CircleShape,
        color = TempActiveSurface,
        border = BorderStroke(0.5.dp, DrawerPlusBorder),
      ) {
        Text(
          text = stringResource(R.string.temp_chat_active),
          style = MaterialTheme.typography.labelMedium,
          color = PlusAccent,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }
  }
}

@Composable
private fun VoiceStyleSwitcherBar(
  selectedStyle: VoiceVisualStyle,
  onSelectStyle: (VoiceVisualStyle) -> Unit,
) {
  Surface(
    shape = CircleShape,
    color = DrawerSearchSurface.copy(alpha = 0.9f),
    border = BorderStroke(0.5.dp, DrawerDivider),
  ) {
    Row(
      modifier = Modifier.padding(3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      VoiceStyleChip(
        label = stringResource(R.string.voice_style_aurora),
        selected = selectedStyle == VoiceVisualStyle.AURORA,
        onClick = { onSelectStyle(VoiceVisualStyle.AURORA) },
        testTag = "voice_style_aurora_chip",
      )
      VoiceStyleChip(
        label = stringResource(R.string.voice_style_jarvis),
        selected = selectedStyle == VoiceVisualStyle.JARVIS,
        onClick = { onSelectStyle(VoiceVisualStyle.JARVIS) },
        testTag = "voice_style_jarvis_chip",
      )
      VoiceStyleChip(
        label = stringResource(R.string.voice_style_equalizer),
        selected = selectedStyle == VoiceVisualStyle.EQUALIZER,
        onClick = { onSelectStyle(VoiceVisualStyle.EQUALIZER) },
        testTag = "voice_style_equalizer_chip",
      )
    }
  }
}

@Composable
private fun VoiceStyleChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    shape = CircleShape,
    color = if (selected) TempActiveSurface else Color.Transparent,
    border = if (selected) BorderStroke(0.5.dp, DrawerPlusBorder) else null,
    modifier =
      Modifier.minimumInteractiveComponentSize()
        .clip(CircleShape)
        .clickable(role = Role.Button, onClick = onClick)
        .testTag(testTag),
  ) {
    Text(
      text = label,
      style =
        MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        ),
      color = if (selected) PlusAccent else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
    )
  }
}

@Composable
private fun FluidAuroraOrb(
  isListening: Boolean,
  isMuted: Boolean,
  audioLevel: Float,
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition(label = "fluid_aurora_orb")

  val driftPhase by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec = infiniteRepeatable(tween(5200, easing = LinearEasing)),
      label = "aurora_drift",
    )

  val fastRipplePhase by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing)),
      label = "aurora_ripple",
    )

  val swirlRotation by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 360f,
      animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
      label = "aurora_swirl",
    )

  val breathe by
    transition.animateFloat(
      initialValue = 0.93f,
      targetValue = 1.07f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(2200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "aurora_breathe",
    )

  val smoothedAudio by
    animateFloatAsState(
      targetValue = if (isMuted) 0f else if (isListening) maxOf(audioLevel, 0.28f) else 0.14f,
      animationSpec = spring(dampingRatio = 0.6f, stiffness = 280f),
      label = "aurora_audio",
    )

  Canvas(modifier = modifier) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val center = Offset(cx, cy)
    val maxR = size.minDimension / 2f

    // 1. Soft atmospheric aurora bloom
    drawCircle(
      brush =
        Brush.radialGradient(
          colors =
            if (isMuted) {
              listOf(
                JarvisMutedRed.copy(alpha = 0.18f),
                JarvisMutedSurface.copy(alpha = 0.12f),
                Color.Transparent,
              )
            } else {
              listOf(
                VoiceGradientEnd.copy(alpha = 0.34f + smoothedAudio * 0.22f),
                JarvisCyan.copy(alpha = 0.16f + smoothedAudio * 0.14f),
                JarvisIndigo.copy(alpha = 0.08f),
                Color.Transparent,
              )
            },
          center = center,
          radius = maxR,
        ),
      radius = maxR,
      center = center,
    )

    val baseRadius = maxR * 0.56f * (if (isMuted) 0.92f else breathe) * (1f + smoothedAudio * 0.14f)
    val deformAmp = (if (isMuted) 4.dp.toPx() else 9.dp.toPx() + smoothedAudio * 18.dp.toPx())

    // 2. Back liquid plasma layer (Violet -> Indigo)
    val backBlob = buildAuroraBlobPath(
      cx = cx,
      cy = cy,
      baseRadius = baseRadius * 1.06f,
      amplitude = deformAmp,
      lobeA = 3f,
      lobeB = 5f,
      phaseA = driftPhase,
      phaseB = -fastRipplePhase,
    )
    rotate(degrees = swirlRotation * 0.5f, pivot = center) {
      drawPath(
        path = backBlob,
        brush =
          Brush.linearGradient(
            colors =
              if (isMuted) {
                listOf(JarvisMutedRed.copy(alpha = 0.4f), JarvisMutedSurface.copy(alpha = 0.65f))
              } else {
                listOf(JarvisIndigo.copy(alpha = 0.85f), VoiceGradientEnd.copy(alpha = 0.78f))
              },
            start = Offset(cx - baseRadius, cy - baseRadius),
            end = Offset(cx + baseRadius, cy + baseRadius),
          ),
      )
    }

    // 3. Mid liquid plasma layer (Cyan -> Periwinkle)
    val midBlob = buildAuroraBlobPath(
      cx = cx,
      cy = cy,
      baseRadius = baseRadius * 0.94f,
      amplitude = deformAmp * 0.9f,
      lobeA = 4f,
      lobeB = 2f,
      phaseA = -driftPhase * 1.2f,
      phaseB = fastRipplePhase * 1.1f,
    )
    rotate(degrees = -swirlRotation * 0.8f, pivot = center) {
      drawPath(
        path = midBlob,
        brush =
          Brush.radialGradient(
            colors =
              if (isMuted) {
                listOf(
                  JarvisMutedRed.copy(alpha = 0.32f),
                  JarvisMutedSurface.copy(alpha = 0.45f),
                  Color.Transparent,
                )
              } else {
                listOf(
                  JarvisCyan.copy(alpha = 0.78f),
                  VoiceGradientStart.copy(alpha = 0.68f),
                  Color.Transparent,
                )
              },
            center = Offset(cx + baseRadius * 0.16f, cy - baseRadius * 0.14f),
            radius = baseRadius * 1.15f,
          ),
      )
    }

    // 4. Inner luminous core blob (Soft White -> Lavender)
    val coreBlob = buildAuroraBlobPath(
      cx = cx,
      cy = cy,
      baseRadius = baseRadius * 0.72f,
      amplitude = deformAmp * 0.55f,
      lobeA = 3f,
      lobeB = 4f,
      phaseA = fastRipplePhase,
      phaseB = driftPhase,
    )
    drawPath(
      path = coreBlob,
      brush =
        Brush.radialGradient(
          colors =
            if (isMuted) {
              listOf(
                JarvisMutedRed.copy(alpha = 0.48f),
                JarvisMutedSurface.copy(alpha = 0.35f),
                Color.Transparent,
              )
            } else {
              listOf(
                Color.White.copy(alpha = 0.92f),
                PlusAccent.copy(alpha = 0.82f),
                VoiceGradientEnd.copy(alpha = 0.35f),
                Color.Transparent,
              )
            },
          center = center,
          radius = baseRadius * 0.85f,
        ),
    )

    // 5. Delicate liquid surface specular arc
    if (!isMuted) {
      val shimmerR = baseRadius * 0.78f
      rotate(degrees = swirlRotation, pivot = center) {
        drawArc(
          color = Color.White.copy(alpha = 0.35f + smoothedAudio * 0.2f),
          startAngle = -55f,
          sweepAngle = 72f,
          useCenter = false,
          topLeft = Offset(cx - shimmerR, cy - shimmerR),
          size = Size(shimmerR * 2f, shimmerR * 2f),
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
      }
    }
  }
}

private fun buildAuroraBlobPath(
  cx: Float,
  cy: Float,
  baseRadius: Float,
  amplitude: Float,
  lobeA: Float,
  lobeB: Float,
  phaseA: Float,
  phaseB: Float,
): Path {
  val path = Path()
  val steps = 96
  for (i in 0..steps) {
    val theta = (i.toFloat() / steps) * (2f * PI).toFloat()
    val offset =
      (sin(lobeA * theta + phaseA) * 0.6f + cos(lobeB * theta + phaseB) * 0.4f) * amplitude
    val r = baseRadius + offset
    val x = cx + r * cos(theta)
    val y = cy + r * sin(theta)
    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
  }
  path.close()
  return path
}

@Composable
private fun SymmetricFrequencyEqualizer(
  isListening: Boolean,
  isMuted: Boolean,
  audioLevel: Float,
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition(label = "symmetric_equalizer")

  val wavePhase by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
      label = "eq_wave_phase",
    )

  val harmonicPhase by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)),
      label = "eq_harmonic_phase",
    )

  val smoothedAudio by
    animateFloatAsState(
      targetValue = if (isMuted) 0f else if (isListening) maxOf(audioLevel, 0.30f) else 0.14f,
      animationSpec = spring(dampingRatio = 0.62f, stiffness = 340f),
      label = "eq_smoothed_audio",
    )

  Canvas(modifier = modifier) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val center = Offset(cx, cy)

    // Soft center ambient glow
    drawCircle(
      brush =
        Brush.radialGradient(
          colors =
            if (isMuted) {
              listOf(JarvisMutedRed.copy(alpha = 0.12f), Color.Transparent)
            } else {
              listOf(
                JarvisIndigo.copy(alpha = 0.22f + smoothedAudio * 0.16f),
                JarvisCyan.copy(alpha = 0.08f),
                Color.Transparent,
              )
            },
          center = center,
          radius = size.minDimension * 0.65f,
        ),
      radius = size.minDimension * 0.65f,
      center = center,
    )

    // Subtle horizontal equilibrium line
    drawLine(
      color =
        if (isMuted) JarvisMutedRed.copy(alpha = 0.16f)
        else Color.White.copy(alpha = 0.08f),
      start = Offset(size.width * 0.08f, cy),
      end = Offset(size.width * 0.92f, cy),
      strokeWidth = 1.dp.toPx(),
      cap = StrokeCap.Round,
    )

    val barCount = 25
    val centerIndex = barCount / 2
    val horizontalInset = size.width * 0.08f
    val usableWidth = size.width - horizontalInset * 2f
    val slotWidth = usableWidth / barCount
    val barWidth = (slotWidth * 0.48f).coerceIn(3.dp.toPx(), 7.dp.toPx())
    val maxBarHeight = size.height * 0.72f
    val minBarHeight = 8.dp.toPx()

    val barBrush =
      if (isMuted) {
        Brush.verticalGradient(
          colors = listOf(JarvisMutedRed.copy(alpha = 0.45f), JarvisMutedRed.copy(alpha = 0.2f)),
        )
      } else {
        Brush.verticalGradient(
          colors =
            listOf(
              JarvisCyan,
              PlusAccent,
              VoiceGradientEnd,
              PlusAccent,
              JarvisCyan,
            ),
        )
      }

    for (i in 0 until barCount) {
      val dist = abs(i - centerIndex).toFloat() / centerIndex.toFloat()
      val bellEnvelope = exp(-2.3f * dist * dist)

      val barHeight =
        if (isMuted) {
          minBarHeight * (0.75f + 0.35f * bellEnvelope)
        } else {
          val wave1 = (sin(wavePhase - dist * 5.2f) + 1f) * 0.5f
          val wave2 = (cos(harmonicPhase + dist * 8.4f) + 1f) * 0.5f
          val dynamicEnergy = (0.35f + 0.65f * (wave1 * 0.62f + wave2 * 0.38f))
          val amplitudeFactor = (0.14f + smoothedAudio * 0.86f)
          val target =
            minBarHeight + (maxBarHeight - minBarHeight) * bellEnvelope * dynamicEnergy * amplitudeFactor
          target.coerceIn(minBarHeight, maxBarHeight)
        }

      val x = horizontalInset + i * slotWidth + (slotWidth - barWidth) / 2f
      val y = cy - barHeight / 2f
      val corner = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f)

      drawRoundRect(
        brush = barBrush,
        topLeft = Offset(x, y),
        size = Size(barWidth, barHeight),
        cornerRadius = corner,
      )
    }
  }
}

@Composable
private fun JarvisNeuralCore(
  isListening: Boolean,
  isMuted: Boolean,
  audioLevel: Float,
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition(label = "jarvis_core")

  val outerRotation by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 360f,
      animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
      label = "outer_rotation",
    )

  val innerRotation by
    transition.animateFloat(
      initialValue = 360f,
      targetValue = 0f,
      animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing)),
      label = "inner_rotation",
    )

  val wavePhase by
    transition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
      label = "wave_phase",
    )

  val breathe by
    transition.animateFloat(
      initialValue = 0.94f,
      targetValue = 1.06f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(1800, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "breathe",
    )

  val smoothedAudio by
    animateFloatAsState(
      targetValue = if (isMuted) 0f else if (isListening) maxOf(audioLevel, 0.25f) else 0.12f,
      animationSpec = spring(dampingRatio = 0.65f, stiffness = 320f),
      label = "smoothed_audio",
    )

  val primaryRingColor = if (isMuted) JarvisMutedRed.copy(alpha = 0.65f) else JarvisCyan
  val secondaryRingColor = if (isMuted) JarvisMutedRed.copy(alpha = 0.4f) else PlusIconAccent

  Canvas(modifier = modifier) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val center = Offset(cx, cy)
    val maxR = size.minDimension / 2f

    // 1. Ambient holographic backdrop glow
    drawCircle(
      brush =
        Brush.radialGradient(
          colors =
            listOf(
              if (isMuted) JarvisMutedRed.copy(alpha = 0.14f)
              else JarvisIndigo.copy(alpha = 0.28f + smoothedAudio * 0.18f),
              if (isMuted) Color.Transparent
              else JarvisCyan.copy(alpha = 0.08f + smoothedAudio * 0.08f),
              Color.Transparent,
            ),
          center = center,
          radius = maxR,
        ),
      radius = maxR,
      center = center,
    )

    // 2. Outer HUD precision tick ring (60 ticks)
    val tickRadius = maxR * 0.88f
    rotate(degrees = outerRotation * 0.35f, pivot = center) {
      for (i in 0 until 60) {
        val angleRad = (i * 6f) * (PI / 180.0)
        val isMajor = i % 5 == 0
        val inner = tickRadius - (if (isMajor) 7.dp.toPx() else 3.5.dp.toPx())
        val start =
          Offset(
            x = cx + (inner * cos(angleRad)).toFloat(),
            y = cy + (inner * sin(angleRad)).toFloat(),
          )
        val end =
          Offset(
            x = cx + (tickRadius * cos(angleRad)).toFloat(),
            y = cy + (tickRadius * sin(angleRad)).toFloat(),
          )
        drawLine(
          color = primaryRingColor.copy(alpha = if (isMajor) 0.45f else 0.18f),
          start = start,
          end = end,
          strokeWidth = if (isMajor) 1.5.dp.toPx() else 0.9.dp.toPx(),
          cap = StrokeCap.Round,
        )
      }
    }

    // 3. Outer rotating segmented arc brackets
    val outerArcRadius = maxR * 0.77f
    val outerArcTopLeft = Offset(cx - outerArcRadius, cy - outerArcRadius)
    val outerArcSize = Size(outerArcRadius * 2f, outerArcRadius * 2f)
    rotate(degrees = outerRotation, pivot = center) {
      val segments = listOf(10f to 58f, 130f to 42f, 225f to 68f, 320f to 26f)
      segments.forEach { (startAngle, sweep) ->
        drawArc(
          color = primaryRingColor.copy(alpha = 0.65f),
          startAngle = startAngle,
          sweepAngle = sweep,
          useCenter = false,
          topLeft = outerArcTopLeft,
          size = outerArcSize,
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
      }
    }

    // 4. Counter-rotating inner orbital arcs
    val midArcRadius = maxR * 0.65f
    val midArcTopLeft = Offset(cx - midArcRadius, cy - midArcRadius)
    val midArcSize = Size(midArcRadius * 2f, midArcRadius * 2f)
    rotate(degrees = innerRotation, pivot = center) {
      val innerSegments = listOf(0f to 85f, 120f to 65f, 230f to 90f)
      innerSegments.forEach { (startAngle, sweep) ->
        drawArc(
          color = secondaryRingColor.copy(alpha = 0.72f),
          startAngle = startAngle,
          sweepAngle = sweep,
          useCenter = false,
          topLeft = midArcTopLeft,
          size = midArcSize,
          style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round),
        )
      }
    }

    // 5. Audio-reactive harmonic wave ring
    val baseWaveRadius = maxR * 0.50f * (if (isMuted) 0.96f else breathe)
    val ampPx = (4.dp.toPx() + smoothedAudio * 18.dp.toPx())
    val wavePath = Path()
    val steps = 120
    for (step in 0..steps) {
      val theta = (step.toFloat() / steps) * (2f * PI).toFloat()
      val harmonic1 = sin(6f * theta + wavePhase)
      val harmonic2 = cos(4f * theta - wavePhase * 1.4f) * 0.6f
      val r = baseWaveRadius + ampPx * (harmonic1 + harmonic2) * 0.5f
      val x = cx + r * cos(theta)
      val y = cy + r * sin(theta)
      if (step == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
    }
    wavePath.close()

    drawPath(
      path = wavePath,
      brush =
        Brush.sweepGradient(
          colors =
            if (isMuted) listOf(JarvisMutedRed.copy(alpha = 0.5f), JarvisMutedRed.copy(alpha = 0.25f), JarvisMutedRed.copy(alpha = 0.5f))
            else listOf(JarvisCyan, VoiceGradientStart, VoiceGradientEnd, JarvisCyan),
          center = center,
        ),
      style = Stroke(width = (2.4.dp + (smoothedAudio * 1.6f).dp).toPx()),
    )

    // 6. Luminous energy core sphere
    val coreRadius = maxR * 0.31f * (1f + smoothedAudio * 0.14f)
    drawCircle(
      brush =
        Brush.radialGradient(
          colors =
            if (isMuted) {
              listOf(
                JarvisMutedRed.copy(alpha = 0.45f),
                JarvisMutedSurface.copy(alpha = 0.8f),
                Color.Transparent,
              )
            } else {
              listOf(
                Color.White.copy(alpha = 0.92f),
                PlusAccent.copy(alpha = 0.85f),
                JarvisIndigo.copy(alpha = 0.55f),
                Color.Transparent,
              )
            },
          center = center,
          radius = coreRadius * 1.35f,
        ),
      radius = coreRadius * 1.35f,
      center = center,
    )

    // 7. Crisp inner core ring
    drawCircle(
      color = if (isMuted) JarvisMutedRed.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.55f),
      radius = coreRadius * 0.62f,
      center = center,
      style = Stroke(width = 1.2.dp.toPx()),
    )
  }
}

@Composable
private fun VoiceControlBar(
  isMuted: Boolean,
  isActive: Boolean,
  onMuteToggle: () -> Unit,
  onCenterToggle: () -> Unit,
  onClose: () -> Unit,
) {
  val muteDesc =
    stringResource(if (isMuted) R.string.cd_voice_unmute else R.string.cd_voice_mute)
  val centerDesc =
    stringResource(if (isActive) R.string.cd_voice_pause else R.string.cd_voice_resume)
  val closeDesc = stringResource(R.string.cd_voice_close)

  Row(
    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      shape = CircleShape,
      color = if (isMuted) JarvisMutedSurface else SurfaceElevated,
      border =
        BorderStroke(
          0.5.dp,
          if (isMuted) JarvisMutedRed.copy(alpha = 0.5f) else DrawerDivider,
        ),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(56.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onMuteToggle)
          .semantics { contentDescription = muteDesc }
          .testTag("voice_mute_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector =
            if (isMuted) PhosphorIcons.Regular.MicrophoneSlash
            else PhosphorIcons.Regular.Microphone,
          contentDescription = null,
          tint = if (isMuted) JarvisMutedRed else MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(22.dp),
        )
      }
    }

    Box(
      contentAlignment = Alignment.Center,
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(66.dp)
          .clip(CircleShape)
          .background(
            brush =
              if (isActive) {
                Brush.linearGradient(listOf(VoiceGradientStart, VoiceGradientEnd))
              } else {
                Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated))
              },
          )
          .clickable(role = Role.Button, onClick = onCenterToggle)
          .semantics { contentDescription = centerDesc }
          .testTag("voice_center_toggle_button"),
    ) {
      Icon(
        imageVector =
          if (isActive) PhosphorIcons.Regular.Pause
          else PhosphorIcons.Regular.Waveform,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(26.dp),
      )
    }

    Surface(
      shape = CircleShape,
      color = SurfaceElevated,
      border = BorderStroke(0.5.dp, DrawerDivider),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(56.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onClose)
          .semantics { contentDescription = closeDesc }
          .testTag("voice_close_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = PhosphorIcons.Regular.X,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(22.dp),
        )
      }
    }
  }
}
