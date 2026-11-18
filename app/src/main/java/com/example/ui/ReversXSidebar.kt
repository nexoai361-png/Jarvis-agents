package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Books
import com.adamglin.phosphoricons.regular.Clock
import com.adamglin.phosphoricons.regular.Folder
import com.adamglin.phosphoricons.regular.Images
import com.adamglin.phosphoricons.regular.MagnifyingGlass
import com.adamglin.phosphoricons.regular.NotePencil
import com.adamglin.phosphoricons.regular.Plug
import com.adamglin.phosphoricons.regular.Trash
import com.adamglin.phosphoricons.regular.X
import com.example.R
import com.example.ui.theme.DrawerActiveItem
import com.example.ui.theme.DrawerSearchSurface
import com.example.ui.theme.PlusIconAccent
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SidebarAvatarCircle
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
  onGetPlusClick: () -> Unit,
) {
  var isSearchOpen by remember { mutableStateOf(false) }
  var showAllRecents by remember { mutableStateOf(false) }

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
              onDeleteClick = { onDeleteChat(item.id) },
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
        onProfileClick = onGetPlusClick,
      )
    }
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

@Composable
private fun SidebarRecentRow(
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
        .clip(RoundedCornerShape(10.dp))
        .background(if (isSelected) DrawerActiveItem else Color.Transparent)
        .clickable(role = Role.Button, onClick = onClick)
        .padding(start = 10.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
        .testTag("chat_history_item_${item.id}"),
    verticalAlignment = Alignment.CenterVertically,
  ) {
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
}

@Composable
private fun SidebarBottomBar(
  onChatClick: () -> Unit,
  onProfileClick: () -> Unit,
) {
  val profileDesc = stringResource(R.string.cd_profile_avatar)

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
          .clickable(role = Role.Button, onClick = onProfileClick)
          .semantics { contentDescription = profileDesc }
          .testTag("sidebar_profile_avatar_button"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(30.dp)
              .clip(CircleShape)
              .background(SidebarAvatarCircle),
        ) {
          Text(
            text = stringResource(R.string.sidebar_profile_initial),
            style = MaterialTheme.typography.labelMedium.copy(
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
            ),
            color = Color.White,
          )
        }
      }
    }
  }
}
