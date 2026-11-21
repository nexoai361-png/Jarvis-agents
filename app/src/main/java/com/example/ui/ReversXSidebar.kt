package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Books
import com.adamglin.phosphoricons.regular.Clock
import com.adamglin.phosphoricons.regular.Folder
import com.adamglin.phosphoricons.regular.GearSix
import com.adamglin.phosphoricons.regular.Images
import com.adamglin.phosphoricons.regular.MagnifyingGlass
import com.adamglin.phosphoricons.regular.NotePencil
import com.adamglin.phosphoricons.regular.Pencil
import com.adamglin.phosphoricons.regular.Plug
import com.adamglin.phosphoricons.regular.PushPin
import com.adamglin.phosphoricons.regular.PushPinSlash
import com.adamglin.phosphoricons.regular.Trash
import com.adamglin.phosphoricons.regular.X
import com.example.R
import com.example.ui.theme.DrawerActiveItem
import com.example.ui.theme.DrawerSearchSurface
import com.example.ui.theme.PlusIconAccent
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SidebarChatBlue
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceOutline

@Composable
internal fun ReversXDrawerContent(
  uiState: ReversXUiState,
  onSearchQueryChanged: (String) -> Unit,
  onNewChatClick: () -> Unit,
  onSectionActionClick: (String) -> Unit,
  onSelectChat: (String) -> Unit,
  onDeleteChat: (String) -> Unit,
  onRenameChat: (String, String) -> Unit,
  onTogglePinChat: (String) -> Unit,
  onSettingsClick: () -> Unit,
) {
  var isSearchOpen by remember { mutableStateOf(false) }
  var showAllRecents by remember { mutableStateOf(false) }
  var chatPendingDelete by remember { mutableStateOf<ChatHistoryItem?>(null) }
  var chatPendingRename by remember { mutableStateOf<ChatHistoryItem?>(null) }

  val filteredHistory =
    remember(uiState.chatHistory, uiState.sidebarSearchQuery) {
      val query = uiState.sidebarSearchQuery.trim()
      if (query.isEmpty()) {
        uiState.chatHistory
      } else {
        uiState.chatHistory.filter { it.title.contains(query, ignoreCase = true) }
      }
    }

  val visibleHistory =
    remember(filteredHistory, showAllRecents, uiState.sidebarSearchQuery) {
      if (showAllRecents || uiState.sidebarSearchQuery.isNotBlank() || filteredHistory.size <= 8) {
        filteredHistory
      } else {
        filteredHistory.take(8)
      }
    }

  ModalDrawerSheet(
    drawerContainerColor = PureBlack,
    drawerContentColor = MaterialTheme.colorScheme.onBackground,
    modifier = Modifier.width(320.dp),
  ) {
    Column(
      modifier =
        Modifier.fillMaxHeight()
          .fillMaxWidth()
          .background(PureBlack)
          .statusBarsPadding()
          .navigationBarsPadding(),
    ) {
      SidebarTopHeader(
        isSearchOpen = isSearchOpen,
        onToggleSearch = {
          isSearchOpen = !isSearchOpen
          if (!isSearchOpen) onSearchQueryChanged("")
        },
      )

      AnimatedVisibility(visible = isSearchOpen) {
        SidebarSearchField(
          query = uiState.sidebarSearchQuery,
          onQueryChanged = onSearchQueryChanged,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      SidebarMenuItems(
        onImagesClick = { onSectionActionClick("images") },
        onLibraryClick = { onSectionActionClick("library") },
        onProjectsClick = { onSectionActionClick("projects") },
        onScheduledClick = { onSectionActionClick("scheduled") },
        onPluginsClick = { onSectionActionClick("plugins") },
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = stringResource(R.string.sidebar_recents),
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp),
      )

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
      ) {
        if (visibleHistory.isEmpty()) {
          item {
            Text(
              text =
                stringResource(
                  if (uiState.sidebarSearchQuery.isNotBlank()) R.string.sidebar_no_matches
                  else R.string.sidebar_empty_title,
                ),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            )
          }
        } else {
          items(items = visibleHistory, key = { it.id }) { item ->
            SidebarRecentRow(
              item = item,
              isSelected = uiState.activeChatId == item.id && !uiState.isTemporaryChat,
              onClick = { onSelectChat(item.id) },
              onDeleteClick = { chatPendingDelete = item },
              onRenameClick = { chatPendingRename = item },
              onPinClick = { onTogglePinChat(item.id) },
            )
          }
          if (filteredHistory.size > 8 && uiState.sidebarSearchQuery.isBlank()) {
            item {
              Text(
                text =
                  stringResource(
                    if (showAllRecents) R.string.sidebar_show_less else R.string.sidebar_see_all,
                  ),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showAllRecents = !showAllRecents }
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .testTag("sidebar_see_all_button"),
              )
            }
          }
        }
      }

      SidebarBottomBar(
        onChatClick = onNewChatClick,
        onSettingsClick = onSettingsClick,
      )
    }
  }

  chatPendingDelete?.let { target ->
    DeleteChatConfirmDialog(
      chatTitle = target.title,
      onConfirm = {
        onDeleteChat(target.id)
        chatPendingDelete = null
      },
      onDismiss = { chatPendingDelete = null },
    )
  }

  chatPendingRename?.let { target ->
    RenameChatDialog(
      initialTitle = target.title,
      onConfirm = { newTitle ->
        onRenameChat(target.id, newTitle)
        chatPendingRename = null
      },
      onDismiss = { chatPendingRename = null },
    )
  }
}

@Composable
private fun SidebarTopHeader(
  isSearchOpen: Boolean,
  onToggleSearch: () -> Unit,
) {
  val searchDesc = stringResource(R.string.cd_search_chats)

  Row(
    modifier = Modifier.fillMaxWidth().padding(start = 22.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(R.string.app_name),
      style = MaterialTheme.typography.titleLarge.copy(
        fontSize = 21.sp,
        fontWeight = FontWeight.SemiBold,
      ),
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.weight(1f),
    )

    Surface(
      shape = CircleShape,
      color = SurfaceElevated,
      border = BorderStroke(0.5.dp, SurfaceOutline),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(42.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onToggleSearch)
          .semantics { contentDescription = searchDesc }
          .testTag("sidebar_search_toggle_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector =
            if (isSearchOpen) PhosphorIcons.Regular.X
            else PhosphorIcons.Regular.MagnifyingGlass,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@Composable
private fun SidebarSearchField(
  query: String,
  onQueryChanged: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val clearDesc = stringResource(R.string.sidebar_clear_search)

  Surface(
    shape = RoundedCornerShape(50),
    color = DrawerSearchSurface,
    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
    modifier = modifier.fillMaxWidth().height(42.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = PhosphorIcons.Regular.MagnifyingGlass,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(10.dp))
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        if (query.isEmpty()) {
          Text(
            text = stringResource(R.string.sidebar_search_placeholder),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        BasicTextField(
          value = query,
          onValueChange = onQueryChanged,
          singleLine = true,
          textStyle =
            MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onBackground,
            ),
          cursorBrush = SolidColor(PlusIconAccent),
          modifier = Modifier.fillMaxWidth().testTag("sidebar_search_input"),
        )
      }
      if (query.isNotEmpty()) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(28.dp)
              .clip(CircleShape)
              .clickable(role = Role.Button, onClick = { onQueryChanged("") })
              .semantics { contentDescription = clearDesc }
              .testTag("sidebar_clear_search_button"),
        ) {
          Icon(
            imageVector = PhosphorIcons.Regular.X,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun SidebarMenuItems(
  onImagesClick: () -> Unit,
  onLibraryClick: () -> Unit,
  onProjectsClick: () -> Unit,
  onScheduledClick: () -> Unit,
  onPluginsClick: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    SidebarNavRow(
      icon = PhosphorIcons.Regular.Images,
      label = stringResource(R.string.sidebar_menu_images),
      testTag = "sidebar_menu_images",
      onClick = onImagesClick,
    )
    SidebarNavRow(
      icon = PhosphorIcons.Regular.Books,
      label = stringResource(R.string.sidebar_menu_library),
      testTag = "sidebar_menu_library",
      onClick = onLibraryClick,
    )
    SidebarNavRow(
      icon = PhosphorIcons.Regular.Folder,
      label = stringResource(R.string.sidebar_menu_projects),
      testTag = "sidebar_menu_projects",
      onClick = onProjectsClick,
    )
    SidebarNavRow(
      icon = PhosphorIcons.Regular.Clock,
      label = stringResource(R.string.sidebar_menu_scheduled),
      testTag = "sidebar_menu_scheduled",
      onClick = onScheduledClick,
    )
    SidebarNavRow(
      icon = PhosphorIcons.Regular.Plug,
      label = stringResource(R.string.sidebar_menu_plugins),
      testTag = "sidebar_menu_plugins",
      onClick = onPluginsClick,
    )
  }
}

@Composable
private fun SidebarNavRow(
  icon: ImageVector,
  label: String,
  testTag: String,
  onClick: () -> Unit,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(12.dp))
        .clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.size(19.dp),
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.5.sp,
        fontWeight = FontWeight.Normal,
      ),
      color = MaterialTheme.colorScheme.onBackground,
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SidebarRecentRow(
  item: ChatHistoryItem,
  isSelected: Boolean,
  onClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onRenameClick: () -> Unit,
  onPinClick: () -> Unit,
) {
  val deleteDesc = stringResource(R.string.sidebar_delete_chat)
  val haptics = LocalHapticFeedback.current
  var isContextMenuOpen by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .minimumInteractiveComponentSize()
          .clip(RoundedCornerShape(10.dp))
          .background(if (isSelected) DrawerActiveItem else Color.Transparent)
          .combinedClickable(
            role = Role.Button,
            onClick = onClick,
            onLongClick = {
              haptics.performHapticFeedback(HapticFeedbackType.LongPress)
              isContextMenuOpen = true
            },
          )
          .padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
          .testTag("chat_history_item_${item.id}"),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (item.isPinned) {
        Icon(
          imageVector = PhosphorIcons.Regular.PushPin,
          contentDescription = null,
          tint = PlusIconAccent,
          modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = item.title,
        style =
          MaterialTheme.typography.bodyLarge.copy(
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
          ),
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f),
      )
      if (isSelected) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(32.dp)
              .clip(CircleShape)
              .clickable(role = Role.Button, onClick = onDeleteClick)
              .semantics { contentDescription = deleteDesc }
              .testTag("delete_chat_${item.id}"),
        ) {
          Icon(
            imageVector = PhosphorIcons.Regular.Trash,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp),
          )
        }
      }
    }

    DropdownMenu(
      expanded = isContextMenuOpen,
      onDismissRequest = { isContextMenuOpen = false },
      shape = RoundedCornerShape(16.dp),
      containerColor = Color(0xFF1A1A20),
      border = BorderStroke(0.5.dp, Color(0xFF2D2D36)),
      modifier = Modifier.testTag("chat_hold_dropdown_${item.id}"),
    ) {
      DropdownMenuItem(
        text = {
          Text(
            text =
              stringResource(
                if (item.isPinned) R.string.chat_action_unpin else R.string.chat_action_pin,
              ),
            color = MaterialTheme.colorScheme.onBackground,
          )
        },
        leadingIcon = {
          Icon(
            imageVector =
              if (item.isPinned) PhosphorIcons.Regular.PushPinSlash
              else PhosphorIcons.Regular.PushPin,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(18.dp),
          )
        },
        onClick = {
          isContextMenuOpen = false
          onPinClick()
        },
        modifier = Modifier.testTag("chat_action_pin_${item.id}"),
      )
      DropdownMenuItem(
        text = {
          Text(
            text = stringResource(R.string.chat_action_rename),
            color = MaterialTheme.colorScheme.onBackground,
          )
        },
        leadingIcon = {
          Icon(
            imageVector = PhosphorIcons.Regular.Pencil,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(18.dp),
          )
        },
        onClick = {
          isContextMenuOpen = false
          onRenameClick()
        },
        modifier = Modifier.testTag("chat_action_rename_${item.id}"),
      )
      DropdownMenuItem(
        text = {
          Text(
            text = stringResource(R.string.chat_action_delete),
            color = Color(0xFFFF6B6B),
          )
        },
        leadingIcon = {
          Icon(
            imageVector = PhosphorIcons.Regular.Trash,
            contentDescription = null,
            tint = Color(0xFFFF6B6B),
            modifier = Modifier.size(18.dp),
          )
        },
        onClick = {
          isContextMenuOpen = false
          onDeleteClick()
        },
        modifier = Modifier.testTag("chat_action_delete_${item.id}"),
      )
    }
  }
}

@Composable
private fun DeleteChatConfirmDialog(
  chatTitle: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF161618),
    titleContentColor = Color.White,
    textContentColor = Color(0xFFA0A0AB),
    shape = RoundedCornerShape(24.dp),
    title = {
      Text(
        text = stringResource(R.string.delete_chat_dialog_title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )
    },
    text = {
      Text(
        text = stringResource(R.string.delete_chat_dialog_message, chatTitle),
        style = MaterialTheme.typography.bodyMedium,
      )
    },
    confirmButton = {
      TextButton(
        onClick = onConfirm,
        modifier = Modifier.testTag("delete_chat_confirm_button"),
      ) {
        Text(
          text = stringResource(R.string.chat_action_delete),
          color = Color(0xFFFF6B6B),
          fontWeight = FontWeight.SemiBold,
        )
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("delete_chat_cancel_button"),
      ) {
        Text(
          text = stringResource(R.string.dialog_cancel),
          color = Color.White,
          fontWeight = FontWeight.SemiBold,
        )
      }
    },
  )
}

@Composable
private fun RenameChatDialog(
  initialTitle: String,
  onConfirm: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  var textValue by remember(initialTitle) {
    mutableStateOf(
      TextFieldValue(
        text = initialTitle,
        selection = TextRange(0, initialTitle.length),
      ),
    )
  }
  val focusRequester = remember { FocusRequester() }
  val trimmed = textValue.text.trim()
  val canRename = trimmed.isNotEmpty() && trimmed != initialTitle

  LaunchedEffect(initialTitle) {
    focusRequester.requestFocus()
  }

  val customSelectionColors =
    TextSelectionColors(
      handleColor = Color.White,
      backgroundColor = Color(0xFF6B6B73),
    )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF141416),
      modifier =
        Modifier.fillMaxWidth()
          .padding(horizontal = 20.dp)
          .widthIn(max = 420.dp)
          .testTag("rename_chat_dialog"),
    ) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
      ) {
        CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
          OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            singleLine = true,
            label = {
              Text(
                text = stringResource(R.string.rename_chat_label),
                fontSize = 13.sp,
              )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
              fontSize = 16.sp,
              color = Color.White,
            ),
            shape = RoundedCornerShape(8.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.85f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
              ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
              KeyboardActions(
                onDone = {
                  if (canRename) onConfirm(trimmed)
                },
              ),
            modifier =
              Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .testTag("rename_chat_input"),
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TextButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("rename_chat_cancel_button"),
          ) {
            Text(
              text = stringResource(R.string.dialog_cancel),
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          TextButton(
            onClick = { if (canRename) onConfirm(trimmed) },
            enabled = canRename,
            modifier = Modifier.testTag("rename_chat_confirm_button"),
          ) {
            Text(
              text = stringResource(R.string.chat_action_rename),
              color = if (canRename) Color.White else Color(0xFF6B6B73),
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SidebarBottomBar(
  onChatClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  val settingsDesc = stringResource(R.string.cd_settings)

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .padding(start = 20.dp, end = 22.dp, top = 10.dp, bottom = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      shape = RoundedCornerShape(50),
      color = SidebarChatBlue,
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .height(44.dp)
          .clip(RoundedCornerShape(50))
          .clickable(role = Role.Button, onClick = onChatClick)
          .testTag("sidebar_chat_cta_button"),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector = PhosphorIcons.Regular.NotePencil,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(17.dp),
        )
        Text(
          text = stringResource(R.string.sidebar_chat_button),
          style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
          ),
          color = Color.White,
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Surface(
      shape = CircleShape,
      color = SurfaceElevated,
      border = BorderStroke(0.5.dp, SurfaceOutline),
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(44.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onSettingsClick)
          .semantics { contentDescription = settingsDesc }
          .testTag("sidebar_settings_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = PhosphorIcons.Regular.GearSix,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.size(20.dp),
        )
      }
    }
  }
}
