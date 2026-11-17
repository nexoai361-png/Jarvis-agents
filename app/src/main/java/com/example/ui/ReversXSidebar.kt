package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DrawerActiveItem
import com.example.ui.theme.DrawerDivider
import com.example.ui.theme.DrawerPlusBorder
import com.example.ui.theme.DrawerSearchSurface
import com.example.ui.theme.DrawerSurface
import com.example.ui.theme.PlusAccent
import com.example.ui.theme.PlusIconAccent
import com.example.ui.theme.TempActiveSurface

@Composable
internal fun ReversXDrawerContent(
  uiState: ReversXUiState,
  onSearchQueryChanged: (String) -> Unit,
  onNewChatClick: () -> Unit,
  onToggleTemporaryChat: () -> Unit,
  onSelectChat: (String) -> Unit,
  onDeleteChat: (String) -> Unit,
  onGetPlusClick: () -> Unit,
) {
  val filteredHistory =
    remember(uiState.chatHistory, uiState.sidebarSearchQuery) {
      val query = uiState.sidebarSearchQuery.trim()
      if (query.isEmpty()) {
        uiState.chatHistory
      } else {
        uiState.chatHistory.filter { it.title.contains(query, ignoreCase = true) }
      }
    }

  ModalDrawerSheet(
    drawerContainerColor = DrawerSurface,
    drawerContentColor = MaterialTheme.colorScheme.onBackground,
    modifier = Modifier.width(312.dp),
  ) {
    Column(
      modifier =
        Modifier.fillMaxHeight()
          .fillMaxWidth()
          .statusBarsPadding()
          .navigationBarsPadding(),
    ) {
      SidebarHeader(onNewChatClick = onNewChatClick)

      SidebarSearchField(
        query = uiState.sidebarSearchQuery,
        onQueryChanged = onSearchQueryChanged,
        modifier = Modifier.padding(horizontal = 16.dp),
      )

      Spacer(modifier = Modifier.height(14.dp))

      SidebarModeActions(
        isNewChatSelected = uiState.activeChatId == null && !uiState.isTemporaryChat,
        isTemporaryChat = uiState.isTemporaryChat,
        onNewChatClick = onNewChatClick,
        onToggleTemporaryChat = onToggleTemporaryChat,
      )

      Spacer(modifier = Modifier.height(14.dp))

      HorizontalDivider(color = DrawerDivider, thickness = 0.5.dp)

      Text(
        text = stringResource(R.string.sidebar_history_section),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
      )

      Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        if (filteredHistory.isEmpty()) {
          SidebarEmptyHistoryState(
            isSearching = uiState.sidebarSearchQuery.isNotBlank(),
          )
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            items(items = filteredHistory, key = { it.id }) { item ->
              SidebarChatHistoryRow(
                item = item,
                isSelected = uiState.activeChatId == item.id && !uiState.isTemporaryChat,
                onClick = { onSelectChat(item.id) },
                onDeleteClick = { onDeleteChat(item.id) },
              )
            }
          }
        }
      }

      HorizontalDivider(color = DrawerDivider, thickness = 0.5.dp)

      SidebarPlusFooterCard(
        onClick = onGetPlusClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
      )
    }
  }
}

@Composable
private fun SidebarHeader(onNewChatClick: () -> Unit) {
  val newChatDesc = stringResource(R.string.cd_new_chat_icon)

  Row(
    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f),
    ) {
      Surface(
        shape = CircleShape,
        color = TempActiveSurface,
        border = BorderStroke(0.5.dp, DrawerPlusBorder),
        modifier = Modifier.size(32.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          SparkleIcon(color = PlusIconAccent, modifier = Modifier.size(15.dp))
        }
      }
      Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
      )
    }

    Box(
      contentAlignment = Alignment.Center,
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(40.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onNewChatClick)
          .semantics { contentDescription = newChatDesc }
          .testTag("sidebar_header_new_chat_button"),
    ) {
      Icon(
        imageVector = Icons.Outlined.EditNote,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(22.dp),
      )
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
    modifier = modifier.fillMaxWidth().height(44.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Rounded.Search,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp),
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
            Modifier.minimumInteractiveComponentSize()
              .size(28.dp)
              .clip(CircleShape)
              .clickable(role = Role.Button, onClick = { onQueryChanged("") })
              .semantics { contentDescription = clearDesc }
              .testTag("sidebar_clear_search_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun SidebarModeActions(
  isNewChatSelected: Boolean,
  isTemporaryChat: Boolean,
  onNewChatClick: () -> Unit,
  onToggleTemporaryChat: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .minimumInteractiveComponentSize()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isNewChatSelected) DrawerActiveItem else Color.Transparent)
          .clickable(role = Role.Button, onClick = onNewChatClick)
          .padding(horizontal = 12.dp, vertical = 10.dp)
          .testTag("drawer_new_chat_item"),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = Icons.Rounded.Add,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(20.dp),
      )
      Spacer(modifier = Modifier.width(12.dp))
      Text(
        text = stringResource(R.string.drawer_new_chat),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onBackground,
      )
    }

    Row(
      modifier =
        Modifier.fillMaxWidth()
          .minimumInteractiveComponentSize()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isTemporaryChat) TempActiveSurface else Color.Transparent)
          .clickable(role = Role.Button, onClick = onToggleTemporaryChat)
          .padding(horizontal = 12.dp, vertical = 10.dp)
          .testTag("drawer_temp_chat_item"),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TemporaryChatIcon(
        color = if (isTemporaryChat) PlusAccent else MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(14.dp))
      Text(
        text = stringResource(R.string.drawer_temp_chat),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = if (isTemporaryChat) PlusAccent else MaterialTheme.colorScheme.onBackground,
      )
    }
  }
}

@Composable
private fun SidebarChatHistoryRow(
  item: ChatHistoryItem,
  isSelected: Boolean,
  onClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  val deleteDesc = stringResource(R.string.sidebar_delete_chat)

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(12.dp))
        .background(if (isSelected) DrawerActiveItem else Color.Transparent)
        .clickable(role = Role.Button, onClick = onClick)
        .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
        .testTag("chat_history_item_${item.id}"),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Outlined.ChatBubbleOutline,
      contentDescription = null,
      tint =
        if (isSelected) PlusIconAccent
        else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(17.dp),
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = item.title,
        style =
          MaterialTheme.typography.bodyMedium.copy(
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
          ),
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = item.timestampLabel,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
      )
    }
    Box(
      contentAlignment = Alignment.Center,
      modifier =
        Modifier.minimumInteractiveComponentSize()
          .size(36.dp)
          .clip(CircleShape)
          .clickable(role = Role.Button, onClick = onDeleteClick)
          .semantics { contentDescription = deleteDesc }
          .testTag("delete_chat_${item.id}"),
    ) {
      Icon(
        imageVector = Icons.Outlined.DeleteOutline,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(17.dp),
      )
    }
  }
}

@Composable
private fun SidebarEmptyHistoryState(isSearching: Boolean) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Surface(
      shape = CircleShape,
      color = DrawerSearchSurface,
      border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
      modifier = Modifier.size(44.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = if (isSearching) Icons.Outlined.SearchOff else Icons.Outlined.Forum,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
        )
      }
    }
    Text(
      text =
        stringResource(
          if (isSearching) R.string.sidebar_no_matches else R.string.sidebar_empty_title,
        ),
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center,
    )
    if (!isSearching) {
      Text(
        text = stringResource(R.string.sidebar_empty_subtitle),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
private fun SidebarPlusFooterCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = TempActiveSurface.copy(alpha = 0.7f),
    border = BorderStroke(0.5.dp, DrawerPlusBorder),
    modifier =
      modifier
        .fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(16.dp))
        .clickable(role = Role.Button, onClick = onClick)
        .testTag("sidebar_plus_footer_card"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = DrawerSearchSurface,
        modifier = Modifier.size(34.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          SparkleIcon(color = PlusIconAccent, modifier = Modifier.size(16.dp))
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.plus_sheet_title),
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
          color = PlusAccent,
        )
        Text(
          text = stringResource(R.string.sidebar_plus_card_subtitle),
          style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        contentDescription = null,
        tint = PlusAccent,
        modifier = Modifier.size(20.dp),
      )
    }
  }
}
