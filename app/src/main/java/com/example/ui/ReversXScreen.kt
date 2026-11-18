package com.example.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowUp
import com.adamglin.phosphoricons.regular.FileText
import com.adamglin.phosphoricons.regular.Globe
import com.adamglin.phosphoricons.regular.Image
import com.adamglin.phosphoricons.regular.Images
import com.adamglin.phosphoricons.regular.Microphone
import com.adamglin.phosphoricons.regular.Pencil
import com.adamglin.phosphoricons.regular.Plus
import com.adamglin.phosphoricons.regular.X
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.theme.PlusAccent
import com.example.ui.theme.PlusIconAccent
import com.example.ui.theme.TempActiveSurface
import com.example.ui.theme.VoiceGradientEnd
import com.example.ui.theme.VoiceGradientStart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReversXScreen(
  viewModel: ReversXViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val inputFocusRequester = remember { FocusRequester() }

  BackHandler(enabled = drawerState.isOpen) {
    scope.launch { drawerState.close() }
  }

  val context = LocalContext.current
  val defaultPhotoLabel = stringResource(R.string.attachment_photo_label)
  val defaultFileLabel = stringResource(R.string.attachment_file_label)

  val photoPickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      if (uri != null) {
        val label = resolveDisplayName(context, uri) ?: defaultPhotoLabel
        viewModel.onAttachItem(label = label, isPhoto = true)
      }
    }

  val filePickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
      if (uri != null) {
        val label = resolveDisplayName(context, uri) ?: defaultFileLabel
        viewModel.onAttachItem(label = label, isPhoto = false)
      }
    }

  val justNowLabel = stringResource(R.string.sidebar_chat_just_now)
  val imagePromptPrefix = stringResource(R.string.prompt_prefix_image)
  val writePromptPrefix = stringResource(R.string.prompt_prefix_write)
  val searchPromptPrefix = stringResource(R.string.prompt_prefix_search)

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ReversXDrawerContent(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSidebarSearchChanged,
        onNewChatClick = {
          viewModel.onNewChat()
          scope.launch { drawerState.close() }
        },
        onSectionActionClick = { section ->
          if (section == "images") {
            viewModel.onQuickActionSelected(imagePromptPrefix)
            inputFocusRequester.requestFocus()
          }
          scope.launch { drawerState.close() }
        },
        onSelectChat = { chatId ->
          viewModel.onSelectChat(chatId)
          scope.launch { drawerState.close() }
        },
        onDeleteChat = viewModel::onDeleteChat,
        onRenameChat = viewModel::onRenameChat,
        onTogglePinChat = viewModel::onTogglePinChat,
        onGetPlusClick = {
          scope.launch { drawerState.close() }
          viewModel.onSetPlusSheetVisible(true)
        },
      )
    },
  ) {
    Box(
      modifier =
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.TopCenter,
    ) {
      Column(
        modifier =
          Modifier.fillMaxSize()
            .widthIn(max = 600.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
      ) {
        TopActionBar(
          isTemporaryChat = uiState.isTemporaryChat,
          onMenuClick = { scope.launch { drawerState.open() } },
          onGetPlusClick = { viewModel.onSetPlusSheetVisible(true) },
          onTempChatClick = { viewModel.onToggleTemporaryChat() },
        )

        Box(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          contentAlignment = Alignment.TopCenter,
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp),
          ) {
            AnimatedVisibility(
              visible = uiState.isTemporaryChat,
              enter = fadeIn(),
              exit = fadeOut(),
            ) {
              StatusPill(text = stringResource(R.string.temp_chat_active))
            }
          }
        }

        QuickActionsSection(
          onCreateImageClick = {
            viewModel.onQuickActionSelected(imagePromptPrefix)
            inputFocusRequester.requestFocus()
          },
          onWriteOrEditClick = {
            viewModel.onQuickActionSelected(writePromptPrefix)
            inputFocusRequester.requestFocus()
          },
          onSearchWebClick = {
            viewModel.onQuickActionSelected(searchPromptPrefix)
            inputFocusRequester.requestFocus()
          },
        )

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedVisibility(
          visible = uiState.attachedItemLabel != null,
          enter = fadeIn(),
          exit = fadeOut(),
        ) {
          uiState.attachedItemLabel?.let { label ->
            AttachmentChip(
              label = label,
              isPhoto = uiState.isAttachmentPhoto,
              onRemove = viewModel::onClearAttachment,
            )
          }
        }

        ComposerBar(
          query = uiState.inputText,
          isVoiceActive = uiState.isVoiceModeActive,
          isAddMenuExpanded = uiState.isAttachmentMenuExpanded,
          focusRequester = inputFocusRequester,
          onQueryChange = viewModel::onInputChanged,
          onToggleAddMenu = {
            viewModel.onSetAttachmentMenuExpanded(!uiState.isAttachmentMenuExpanded)
          },
          onMicClick = {
            VoiceSoundPlayer.playActivationChime()
            viewModel.onOpenVoiceMode()
          },
          onPrimaryActionClick = {
            if (uiState.inputText.isNotBlank()) {
              viewModel.onSubmitInput(justNowLabel)
            } else {
              VoiceSoundPlayer.playActivationChime()
              viewModel.onOpenVoiceMode()
            }
          },
        )
      }
    }
  }

  AnimatedVisibility(
    visible = uiState.isVoiceModeActive,
    enter = fadeIn() + scaleIn(initialScale = 0.96f),
    exit = fadeOut() + scaleOut(targetScale = 0.96f),
  ) {
    ReversXVoiceModeScreen(
      uiState = uiState,
      onToggleMute = viewModel::onToggleVoiceMute,
      onSelectVisualStyle = viewModel::onSelectVoiceVisualStyle,
      onListeningChanged = viewModel::onVoiceListeningChanged,
      onAmplitudeChanged = viewModel::onVoiceAmplitudeChanged,
      onTranscriptChanged = viewModel::onVoiceTranscriptChanged,
      onSendPromptToChat = { viewModel.onSendVoicePromptToChat(justNowLabel) },
      onClose = viewModel::onCloseVoiceMode,
    )
  }

  if (uiState.isAttachmentMenuExpanded) {
    BackHandler { viewModel.onSetAttachmentMenuExpanded(false) }
    ComposerPlusBottomSheet(
      onDismiss = { viewModel.onSetAttachmentMenuExpanded(false) },
      onPickPhotos = {
        viewModel.onSetAttachmentMenuExpanded(false)
        photoPickerLauncher.launch(
          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
        )
      },
      onPickFiles = {
        viewModel.onSetAttachmentMenuExpanded(false)
        filePickerLauncher.launch(arrayOf("*/*"))
      },
      onSelectImageMode = {
        viewModel.onSetAttachmentMenuExpanded(false)
        viewModel.onQuickActionSelected(imagePromptPrefix)
        inputFocusRequester.requestFocus()
      },
      onSelectWebMode = {
        viewModel.onSetAttachmentMenuExpanded(false)
        viewModel.onQuickActionSelected(searchPromptPrefix)
        inputFocusRequester.requestFocus()
      },
      onSelectWriteMode = {
        viewModel.onSetAttachmentMenuExpanded(false)
        viewModel.onQuickActionSelected(writePromptPrefix)
        inputFocusRequester.requestFocus()
      },
    )
  }

  if (uiState.isPlusSheetVisible) {
    BackHandler { viewModel.onSetPlusSheetVisible(false) }
    ModalBottomSheet(
      onDismissRequest = { viewModel.onSetPlusSheetVisible(false) },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          SparkleIcon(color = PlusIconAccent, modifier = Modifier.size(20.dp))
          Text(
            text = stringResource(R.string.plus_sheet_title),
            style = MaterialTheme.typography.titleMedium,
            color = PlusAccent,
          )
        }
        Text(
          text = stringResource(R.string.plus_sheet_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(
            onClick = { viewModel.onSetPlusSheetVisible(false) },
            modifier = Modifier.testTag("plus_sheet_close_button"),
          ) {
            Text(stringResource(R.string.plus_sheet_close), color = PlusAccent)
          }
        }
      }
    }
  }
}

@Composable
private fun TopActionBar(
  isTemporaryChat: Boolean,
  onMenuClick: () -> Unit,
  onGetPlusClick: () -> Unit,
  onTempChatClick: () -> Unit,
) {
  val menuDesc = stringResource(R.string.cd_menu)
  val tempChatDesc = stringResource(R.string.cd_temporary_chat)

  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(44.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onMenuClick)
          .semantics { contentDescription = menuDesc }
          .testTag("menu_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        StaggeredMenuIcon(
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(18.dp),
        )
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .height(42.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onGetPlusClick)
          .testTag("get_plus_button"),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
      ) {
        SparkleIcon(color = PlusIconAccent, modifier = Modifier.size(15.dp))
        Text(
          text = stringResource(R.string.get_plus),
          style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
          ),
          color = PlusAccent,
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Surface(
      shape = CircleShape,
      color = if (isTemporaryChat) TempActiveSurface else MaterialTheme.colorScheme.surface,
      border =
        BorderStroke(
          0.5.dp,
          if (isTemporaryChat) PlusIconAccent.copy(alpha = 0.6f)
          else MaterialTheme.colorScheme.outline,
        ),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(44.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onTempChatClick)
          .semantics { contentDescription = tempChatDesc }
          .testTag("temporary_chat_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        TemporaryChatIcon(
          color = if (isTemporaryChat) PlusAccent else MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}

@Composable
private fun QuickActionsSection(
  onCreateImageClick: () -> Unit,
  onWriteOrEditClick: () -> Unit,
  onSearchWebClick: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    QuickActionRow(
      icon = PhosphorIcons.Regular.Image,
      label = stringResource(R.string.action_create_image),
      testTag = "action_create_image",
      onClick = onCreateImageClick,
    )
    QuickActionRow(
      icon = PhosphorIcons.Regular.Pencil,
      label = stringResource(R.string.action_write_or_edit),
      testTag = "action_write_or_edit",
      onClick = onWriteOrEditClick,
    )
    QuickActionRow(
      icon = PhosphorIcons.Regular.Globe,
      label = stringResource(R.string.action_search_web),
      testTag = "action_search_web",
      onClick = onSearchWebClick,
    )
  }
}

@Composable
private fun QuickActionRow(
  icon: ImageVector,
  label: String,
  testTag: String,
  onClick: () -> Unit,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(14.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 4.dp, vertical = 12.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.size(20.dp),
    )
    Spacer(modifier = Modifier.width(14.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
      ),
      color = MaterialTheme.colorScheme.onBackground,
    )
  }
}

@Composable
private fun AttachmentChip(
  label: String,
  isPhoto: Boolean,
  onRemove: () -> Unit,
) {
  val removeDesc = stringResource(R.string.cd_remove_attachment)
  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
  ) {
    Surface(
      shape = RoundedCornerShape(50),
      color = Color(0xFF1A1A20),
      border = BorderStroke(0.5.dp, Color(0xFF2D2D36)),
    ) {
      Row(
        modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector =
            if (isPhoto) PhosphorIcons.Regular.Images else PhosphorIcons.Regular.FileText,
          contentDescription = null,
          tint = PlusIconAccent,
          modifier = Modifier.size(14.dp),
        )
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.widthIn(max = 200.dp),
        )
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(20.dp)
              .clip(CircleShape)
              .clickable(role = Role.Button, onClick = onRemove)
              .semantics { contentDescription = removeDesc }
              .testTag("remove_attachment_chip_button"),
        ) {
          Icon(
            imageVector = PhosphorIcons.Regular.X,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun ComposerBar(
  query: String,
  isVoiceActive: Boolean,
  isAddMenuExpanded: Boolean,
  focusRequester: FocusRequester,
  onQueryChange: (String) -> Unit,
  onToggleAddMenu: () -> Unit,
  onMicClick: () -> Unit,
  onPrimaryActionClick: () -> Unit,
) {
  val addDesc = stringResource(R.string.cd_add_attachment)
  val micDesc = stringResource(R.string.cd_voice_input)
  val voiceModeDesc = stringResource(R.string.cd_voice_mode)
  val sendDesc = stringResource(R.string.cd_send_message)

  val plusRotation by
    animateFloatAsState(
      targetValue = if (isAddMenuExpanded) 45f else 0f,
      animationSpec = spring(dampingRatio = 0.72f, stiffness = 420f),
      label = "plus_rotation",
    )

  Surface(
    shape = RoundedCornerShape(50),
    color = Color(0xFF141417),
    border = BorderStroke(0.5.dp, Color(0xFF26262C)),
    modifier =
      Modifier.fillMaxWidth()
        .padding(horizontal = 14.dp)
        .padding(bottom = 12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 5.dp, top = 4.dp, bottom = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier.size(34.dp)
            .clip(CircleShape)
            .background(if (isAddMenuExpanded) Color(0xFF25252D) else Color.Transparent)
            .clickable(role = Role.Button, onClick = onToggleAddMenu)
            .semantics {
              contentDescription = addDesc
            }
            .testTag("add_attachment_button"),
      ) {
        Icon(
          imageVector = PhosphorIcons.Regular.Plus,
          contentDescription = null,
          tint = if (isAddMenuExpanded) PlusAccent else MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(19.dp).rotate(plusRotation),
        )
      }

      Spacer(modifier = Modifier.width(4.dp))

      Box(
        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart,
      ) {
        BasicTextField(
          value = query,
          onValueChange = onQueryChange,
          singleLine = true,
          textStyle =
            MaterialTheme.typography.bodyLarge.copy(
              color = MaterialTheme.colorScheme.onBackground,
              fontSize = 15.sp,
            ),
          cursorBrush = SolidColor(PlusIconAccent),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions =
            KeyboardActions(
              onSend = {
                if (query.isNotBlank()) {
                  onPrimaryActionClick()
                }
              },
            ),
          modifier =
            Modifier.fillMaxWidth()
              .focusRequester(focusRequester)
              .testTag("ask_reversx_input"),
        )
      }

      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier.size(34.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onMicClick)
            .semantics {
              contentDescription = micDesc
            }
            .testTag("mic_button"),
      ) {
        Icon(
          imageVector = PhosphorIcons.Regular.Microphone,
          contentDescription = null,
          tint = if (isVoiceActive) PlusIconAccent else MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(18.dp),
        )
      }

      Spacer(modifier = Modifier.width(4.dp))

      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier.size(32.dp)
            .clip(CircleShape)
            .background(
              brush = Brush.linearGradient(listOf(VoiceGradientStart, VoiceGradientEnd)),
            )
            .clickable(role = Role.Button, onClick = onPrimaryActionClick)
            .semantics {
              contentDescription = if (query.isNotBlank()) sendDesc else voiceModeDesc
            }
            .testTag("voice_or_send_button"),
      ) {
        if (query.isNotBlank()) {
          Icon(
            imageVector = PhosphorIcons.Regular.ArrowUp,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp),
          )
        } else {
          WaveformIcon(
            color = Color.White,
            modifier = Modifier.size(15.dp),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposerPlusBottomSheet(
  onDismiss: () -> Unit,
  onPickPhotos: () -> Unit,
  onPickFiles: () -> Unit,
  onSelectImageMode: () -> Unit,
  onSelectWebMode: () -> Unit,
  onSelectWriteMode: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    sheetMaxWidth = Dp.Unspecified,
    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
    containerColor = Color(0xFF151519),
    contentColor = MaterialTheme.colorScheme.onBackground,
    scrimColor = Color.Black.copy(alpha = 0.62f),
    dragHandle = {
      BottomSheetDefaults.DragHandle(
        color = Color(0xFF4A4A55),
        width = 40.dp,
        height = 4.dp,
      )
    },
    modifier = Modifier.fillMaxWidth().testTag("composer_plus_sheet"),
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 16.dp)
          .padding(bottom = 22.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        DropdownTileButton(
          icon = PhosphorIcons.Regular.Images,
          label = stringResource(R.string.attachment_photos),
          onClick = onPickPhotos,
          testTag = "attachment_photos_item",
          modifier = Modifier.weight(1f),
        )
        DropdownTileButton(
          icon = PhosphorIcons.Regular.FileText,
          label = stringResource(R.string.attachment_files),
          onClick = onPickFiles,
          testTag = "attachment_files_item",
          modifier = Modifier.weight(1f),
        )
      }

      HorizontalDivider(
        color = Color(0xFF26262E),
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 14.dp),
      )

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        DropdownToolRow(
          icon = PhosphorIcons.Regular.Image,
          iconTint = PlusIconAccent,
          title = stringResource(R.string.action_create_image),
          subtitle = stringResource(R.string.dropdown_subtitle_image),
          onClick = onSelectImageMode,
          testTag = "dropdown_create_image",
        )
        DropdownToolRow(
          icon = PhosphorIcons.Regular.Globe,
          iconTint = Color(0xFF67E8F9),
          title = stringResource(R.string.action_search_web),
          subtitle = stringResource(R.string.dropdown_subtitle_web),
          onClick = onSelectWebMode,
          testTag = "dropdown_search_web",
        )
        DropdownToolRow(
          icon = PhosphorIcons.Regular.Pencil,
          iconTint = MaterialTheme.colorScheme.onBackground,
          title = stringResource(R.string.action_write_or_edit),
          subtitle = stringResource(R.string.dropdown_subtitle_write),
          onClick = onSelectWriteMode,
          testTag = "dropdown_write_edit",
        )
      }
    }
  }
}

@Composable
private fun DropdownTileButton(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF1E1E24),
    border = BorderStroke(0.5.dp, Color(0xFF2D2D36)),
    modifier =
      modifier
        .clip(RoundedCornerShape(14.dp))
        .clickable(role = Role.Button, onClick = onClick)
        .testTag(testTag),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(20.dp),
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Medium,
        ),
        color = MaterialTheme.colorScheme.onBackground,
      )
    }
  }
}

@Composable
private fun DropdownToolRow(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 9.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Surface(
      shape = CircleShape,
      color = Color(0xFF1F1F26),
      modifier = Modifier.size(32.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(16.dp),
        )
      }
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
        ),
        color = MaterialTheme.colorScheme.onBackground,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.5.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

private fun resolveDisplayName(context: Context, uri: Uri): String? {
  return runCatching {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
      ?.use { cursor ->
        if (cursor.moveToFirst()) {
          val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (idx >= 0) cursor.getString(idx) else null
        } else {
          null
        }
      }
  }.getOrNull()
}

@Composable
private fun StatusPill(text: String) {
  Surface(
    shape = CircleShape,
    color = TempActiveSurface,
    border = BorderStroke(0.5.dp, PlusIconAccent.copy(alpha = 0.4f)),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelLarge,
      color = PlusAccent,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
    )
  }
}

@Composable
private fun StaggeredMenuIcon(
  color: Color,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier) {
    val strokeWidth = 1.9.dp.toPx()
    val startX = size.width * 0.12f
    val topY = size.height * 0.34f
    val bottomY = size.height * 0.66f
    drawLine(
      color = color,
      start = Offset(startX, topY),
      end = Offset(size.width * 0.88f, topY),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Round,
    )
    drawLine(
      color = color,
      start = Offset(startX, bottomY),
      end = Offset(size.width * 0.54f, bottomY),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Round,
    )
  }
}

@Composable
internal fun SparkleIcon(
  color: Color,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val path =
      Path().apply {
        moveTo(cx, 0f)
        quadraticTo(cx, cy, w, cy)
        quadraticTo(cx, cy, cx, h)
        quadraticTo(cx, cy, 0f, cy)
        quadraticTo(cx, cy, cx, 0f)
        close()
      }
    drawPath(path = path, color = color)
  }
}

@Composable
internal fun TemporaryChatIcon(
  color: Color,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier) {
    val strokeWidth = 1.7.dp.toPx()
    val inset = strokeWidth
    val arcSize = Size(size.width - inset * 2f, size.height - inset * 2f)
    val topLeft = Offset(inset, inset)

    // Dashed chat bubble ring with a subtle opening for the bottom-left tail
    val angles = listOf(-80f to 36f, -24f to 36f, 32f to 36f, 88f to 36f, 168f to 36f, 224f to 36f)
    angles.forEach { (start, sweep) ->
      drawArc(
        color = color,
        startAngle = start,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
      )
    }

    // Small chat bubble tail at bottom-left (~135 deg)
    val tailPath =
      Path().apply {
        moveTo(size.width * 0.25f, size.height * 0.77f)
        lineTo(size.width * 0.16f, size.height * 0.90f)
        lineTo(size.width * 0.34f, size.height * 0.85f)
      }
    drawPath(
      path = tailPath,
      color = color,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )
  }
}

@Composable
private fun WaveformIcon(
  color: Color,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier) {
    val heights = listOf(0.36f, 0.78f, 0.56f, 0.32f)
    val barWidth = 2.1.dp.toPx()
    val totalBars = heights.size
    val spacing = (size.width - totalBars * barWidth) / (totalBars - 1)
    val corner = CornerRadius(barWidth / 2f, barWidth / 2f)

    heights.forEachIndexed { index, ratio ->
      val barHeight = size.height * ratio
      val x = index * (barWidth + spacing)
      val y = (size.height - barHeight) / 2f
      drawRoundRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(barWidth, barHeight),
        cornerRadius = corner,
      )
    }
  }
}
