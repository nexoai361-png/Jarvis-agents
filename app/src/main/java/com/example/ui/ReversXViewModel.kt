package com.example.ui

import androidx.lifecycle.ViewModel
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChatHistoryItem(
  val id: String,
  val title: String,
  val timestampLabel: String,
)

enum class VoiceVisualStyle {
  AURORA,
  JARVIS,
  EQUALIZER,
}

data class ReversXUiState(
  val inputText: String = "",
  val isTemporaryChat: Boolean = false,
  val isVoiceModeActive: Boolean = false,
  val isVoiceMuted: Boolean = false,
  val isVoiceListening: Boolean = false,
  val voiceTranscript: String = "",
  val voiceAmplitude: Float = 0f,
  val voiceVisualStyle: VoiceVisualStyle = VoiceVisualStyle.EQUALIZER,
  val isPlusSheetVisible: Boolean = false,
  val isAttachmentSheetVisible: Boolean = false,
  val sidebarSearchQuery: String = "",
  val chatHistory: List<ChatHistoryItem> = emptyList(),
  val activeChatId: String? = null,
)

class ReversXViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(ReversXUiState())
  val uiState: StateFlow<ReversXUiState> = _uiState.asStateFlow()

  fun onInputChanged(newText: String) {
    _uiState.update { it.copy(inputText = newText) }
  }

  fun onQuickActionSelected(prefix: String) {
    _uiState.update { it.copy(inputText = prefix) }
  }

  fun onToggleTemporaryChat() {
    _uiState.update { it.copy(isTemporaryChat = !it.isTemporaryChat) }
  }

  fun onOpenVoiceMode() {
    _uiState.update {
      it.copy(
        isVoiceModeActive = true,
        isVoiceMuted = false,
        voiceTranscript = "",
        voiceAmplitude = 0f,
      )
    }
  }

  fun onCloseVoiceMode() {
    _uiState.update {
      it.copy(
        isVoiceModeActive = false,
        isVoiceListening = false,
        voiceAmplitude = 0f,
      )
    }
  }

  fun onToggleVoiceMute() {
    _uiState.update {
      val nextMuted = !it.isVoiceMuted
      it.copy(
        isVoiceMuted = nextMuted,
        isVoiceListening = if (nextMuted) false else it.isVoiceListening,
        voiceAmplitude = if (nextMuted) 0f else it.voiceAmplitude,
      )
    }
  }

  fun onSelectVoiceVisualStyle(style: VoiceVisualStyle) {
    _uiState.update { it.copy(voiceVisualStyle = style) }
  }

  fun onVoiceListeningChanged(listening: Boolean) {
    _uiState.update {
      it.copy(
        isVoiceListening = listening,
        voiceAmplitude = if (listening) it.voiceAmplitude else 0f,
      )
    }
  }

  fun onVoiceAmplitudeChanged(amplitude: Float) {
    _uiState.update { it.copy(voiceAmplitude = amplitude.coerceIn(0f, 1f)) }
  }

  fun onVoiceTranscriptChanged(transcript: String) {
    _uiState.update { it.copy(voiceTranscript = transcript) }
  }

  fun onSendVoicePromptToChat(timestampLabel: String) {
    _uiState.update { state ->
      val trimmed = state.voiceTranscript.trim()
      if (trimmed.isEmpty()) {
        return@update state.copy(
          isVoiceModeActive = false,
          isVoiceListening = false,
          voiceAmplitude = 0f,
        )
      }
      if (state.isTemporaryChat) {
        return@update state.copy(
          inputText = trimmed,
          voiceTranscript = "",
          isVoiceModeActive = false,
          isVoiceListening = false,
          voiceAmplitude = 0f,
        )
      }
      val newChatId = UUID.randomUUID().toString()
      val newItem =
        ChatHistoryItem(
          id = newChatId,
          title = trimmed,
          timestampLabel = timestampLabel,
        )
      state.copy(
        inputText = "",
        voiceTranscript = "",
        isVoiceModeActive = false,
        isVoiceListening = false,
        voiceAmplitude = 0f,
        chatHistory = listOf(newItem) + state.chatHistory,
        activeChatId = newChatId,
      )
    }
  }

  fun onSetPlusSheetVisible(visible: Boolean) {
    _uiState.update { it.copy(isPlusSheetVisible = visible) }
  }

  fun onSetAttachmentSheetVisible(visible: Boolean) {
    _uiState.update { it.copy(isAttachmentSheetVisible = visible) }
  }

  fun onSidebarSearchChanged(query: String) {
    _uiState.update { it.copy(sidebarSearchQuery = query) }
  }

  fun onSelectChat(chatId: String) {
    _uiState.update {
      it.copy(
        activeChatId = chatId,
        isTemporaryChat = false,
      )
    }
  }

  fun onDeleteChat(chatId: String) {
    _uiState.update { state ->
      state.copy(
        chatHistory = state.chatHistory.filterNot { it.id == chatId },
        activeChatId = if (state.activeChatId == chatId) null else state.activeChatId,
      )
    }
  }

  fun onNewChat() {
    _uiState.update {
      it.copy(
        inputText = "",
        activeChatId = null,
        isVoiceModeActive = false,
        isVoiceListening = false,
      )
    }
  }

  fun onSubmitInput(timestampLabel: String) {
    _uiState.update { state ->
      val trimmed = state.inputText.trim()
      if (trimmed.isEmpty()) {
        return@update state
      }
      if (state.isTemporaryChat) {
        return@update state.copy(inputText = "")
      }
      val newChatId = UUID.randomUUID().toString()
      val newItem =
        ChatHistoryItem(
          id = newChatId,
          title = trimmed,
          timestampLabel = timestampLabel,
        )
      state.copy(
        inputText = "",
        chatHistory = listOf(newItem) + state.chatHistory,
        activeChatId = newChatId,
      )
    }
  }
}
