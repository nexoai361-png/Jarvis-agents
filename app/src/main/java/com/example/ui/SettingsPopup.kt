package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import com.adamglin.phosphoricons.regular.CaretDown
import com.adamglin.phosphoricons.regular.Key
import com.adamglin.phosphoricons.regular.Palette
import com.adamglin.phosphoricons.regular.Pencil
import com.adamglin.phosphoricons.regular.SlidersHorizontal
import com.adamglin.phosphoricons.regular.X
import com.example.R

private enum class SettingsSubPage {
  MAIN,
  GENERAL,
  BYOK,
  APPEARANCE,
}

private val PopupContainerColor = Color(0xFF1E1F23)
private val CardSurfaceColor = Color(0xFF121316)
private val AvatarGreenColor = Color(0xFF3E7B27)
private val BadgeCircleColor = Color(0xFF28292E)
private val CardDividerColor = Color(0xFF26272C)
private val PopupTextPrimary = Color(0xFFECECEE)
private val PopupTextSecondary = Color(0xFFC2C3C8)
private val PopupIconTint = Color(0xFFE3E4E8)

@Composable
internal fun SettingsScreen(
  onDismiss: () -> Unit,
  onUpgradePlusClick: () -> Unit,
) {
  var currentPage by remember { mutableStateOf(SettingsSubPage.MAIN) }
  var hapticsEnabled by remember { mutableStateOf(true) }
  var startTempChat by remember { mutableStateOf(false) }
  var amoledBlack by remember { mutableStateOf(true) }
  var savedApiKey by remember { mutableStateOf("") }
  var draftApiKey by remember { mutableStateOf("") }

  BackHandler {
    if (currentPage != SettingsSubPage.MAIN) {
      currentPage = SettingsSubPage.MAIN
    } else {
      onDismiss()
    }
  }

  val closeDesc =
    stringResource(
      if (currentPage == SettingsSubPage.MAIN) R.string.cd_settings else R.string.settings_back,
    )

  Box(
    modifier =
      Modifier.fillMaxSize()
        .background(PopupContainerColor)
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding()
        .testTag("settings_full_screen"),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier.fillMaxSize().widthIn(max = 600.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.minimumInteractiveComponentSize()
              .size(44.dp)
              .clip(CircleShape)
              .clickable(role = Role.Button) {
                if (currentPage != SettingsSubPage.MAIN) {
                  currentPage = SettingsSubPage.MAIN
                } else {
                  onDismiss()
                }
              }
              .semantics { contentDescription = closeDesc }
              .testTag("settings_top_back_or_close_button"),
        ) {
          Icon(
            imageVector =
              if (currentPage == SettingsSubPage.MAIN) PhosphorIcons.Regular.X
              else PhosphorIcons.Regular.ArrowLeft,
            contentDescription = null,
            tint = PopupTextPrimary,
            modifier = Modifier.size(20.dp),
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text =
            when (currentPage) {
              SettingsSubPage.MAIN -> stringResource(R.string.cd_settings)
              SettingsSubPage.GENERAL -> stringResource(R.string.settings_general)
              SettingsSubPage.BYOK -> stringResource(R.string.settings_byok)
              SettingsSubPage.APPEARANCE -> stringResource(R.string.settings_appearance)
            },
          style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
          ),
          color = PopupTextPrimary,
        )
      }

      Column(
        modifier =
          Modifier.fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        when (currentPage) {
          SettingsSubPage.MAIN -> {
            SettingsProfileHeaderCard()

            SettingsSinglePillButton(
              onClick = onUpgradePlusClick,
              testTag = "settings_upgrade_plus_button",
            )

            Surface(
              shape = RoundedCornerShape(24.dp),
              color = CardSurfaceColor,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.fillMaxWidth()) {
                SettingsGroupRow(
                  icon = PhosphorIcons.Regular.SlidersHorizontal,
                  label = stringResource(R.string.settings_general),
                  testTag = "settings_general_button",
                  onClick = { currentPage = SettingsSubPage.GENERAL },
                )
                HorizontalDivider(
                  color = CardDividerColor,
                  thickness = 0.8.dp,
                )
                SettingsGroupRow(
                  icon = PhosphorIcons.Regular.Key,
                  label = stringResource(R.string.settings_byok),
                  testTag = "settings_byok_button",
                  onClick = {
                    draftApiKey = savedApiKey
                    currentPage = SettingsSubPage.BYOK
                  },
                )
                HorizontalDivider(
                  color = CardDividerColor,
                  thickness = 0.8.dp,
                )
                SettingsGroupRow(
                  icon = PhosphorIcons.Regular.Palette,
                  label = stringResource(R.string.settings_appearance),
                  testTag = "settings_appearance_button",
                  onClick = { currentPage = SettingsSubPage.APPEARANCE },
                )
              }
            }
          }

          SettingsSubPage.GENERAL -> {
            Surface(
              shape = RoundedCornerShape(24.dp),
              color = CardSurfaceColor,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.fillMaxWidth()) {
                SettingsToggleRow(
                  label = stringResource(R.string.settings_general_haptics),
                  checked = hapticsEnabled,
                  onCheckedChange = { hapticsEnabled = it },
                )
                HorizontalDivider(color = CardDividerColor, thickness = 0.8.dp)
                SettingsToggleRow(
                  label = stringResource(R.string.settings_general_temp_default),
                  checked = startTempChat,
                  onCheckedChange = { startTempChat = it },
                )
              }
            }
          }

          SettingsSubPage.BYOK -> {
            Surface(
              shape = RoundedCornerShape(24.dp),
              color = CardSurfaceColor,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                OutlinedTextField(
                  value = draftApiKey,
                  onValueChange = { draftApiKey = it },
                  singleLine = true,
                  visualTransformation = PasswordVisualTransformation(),
                  label = { Text(stringResource(R.string.settings_byok_hint)) },
                  shape = RoundedCornerShape(12.dp),
                  colors =
                    OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = PopupTextPrimary,
                      unfocusedBorderColor = CardDividerColor,
                      focusedLabelColor = PopupTextPrimary,
                      unfocusedLabelColor = PopupTextSecondary,
                      focusedTextColor = PopupTextPrimary,
                      unfocusedTextColor = PopupTextPrimary,
                      cursorColor = PopupTextPrimary,
                    ),
                  modifier = Modifier.fillMaxWidth().testTag("settings_byok_input"),
                )
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                ) {
                  if (savedApiKey.isNotEmpty()) {
                    TextButton(
                      onClick = {
                        savedApiKey = ""
                        draftApiKey = ""
                        currentPage = SettingsSubPage.MAIN
                      },
                    ) {
                      Text(stringResource(R.string.settings_clear), color = Color(0xFFFF6B6B))
                    }
                  }
                  TextButton(
                    onClick = {
                      savedApiKey = draftApiKey.trim()
                      currentPage = SettingsSubPage.MAIN
                    },
                  ) {
                    Text(stringResource(R.string.settings_save), color = PopupTextPrimary)
                  }
                }
              }
            }
          }

          SettingsSubPage.APPEARANCE -> {
            Surface(
              shape = RoundedCornerShape(24.dp),
              color = CardSurfaceColor,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(modifier = Modifier.fillMaxWidth()) {
                SettingsToggleRow(
                  label = stringResource(R.string.settings_appearance_amoled),
                  checked = amoledBlack,
                  onCheckedChange = { amoledBlack = it },
                )
                HorizontalDivider(color = CardDividerColor, thickness = 0.8.dp)
                Row(
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = stringResource(R.string.settings_appearance_system_font),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                    color = PopupTextPrimary,
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsProfileHeaderCard() {
  Surface(
    shape = RoundedCornerShape(28.dp),
    color = CardSurfaceColor,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(modifier = Modifier.size(50.dp)) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(46.dp)
              .align(Alignment.Center)
              .clip(CircleShape)
              .background(AvatarGreenColor),
        ) {
          Text(
            text = stringResource(R.string.settings_profile_initial),
            style = MaterialTheme.typography.titleLarge.copy(
              fontSize = 22.sp,
              fontWeight = FontWeight.Normal,
            ),
            color = Color.White,
          )
        }
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(20.dp)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(BadgeCircleColor)
              .border(BorderStroke(1.5.dp, CardSurfaceColor), CircleShape),
        ) {
          Icon(
            imageVector = PhosphorIcons.Regular.Pencil,
            contentDescription = null,
            tint = PopupTextPrimary,
            modifier = Modifier.size(10.dp),
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.settings_profile_name),
          style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
          ),
          color = PopupTextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
          text = stringResource(R.string.settings_profile_email),
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
          color = PopupTextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Box(
        contentAlignment = Alignment.Center,
        modifier =
          Modifier.width(24.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .background(BadgeCircleColor),
      ) {
        Icon(
          imageVector = PhosphorIcons.Regular.CaretDown,
          contentDescription = null,
          tint = PopupTextPrimary,
          modifier = Modifier.size(13.dp),
        )
      }
    }
  }
}

@Composable
private fun SettingsSinglePillButton(
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    shape = RoundedCornerShape(28.dp),
    color = CardSurfaceColor,
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(28.dp))
        .clickable(role = Role.Button, onClick = onClick)
        .testTag(testTag),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      SparkleIcon(
        color = PopupIconTint,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(14.dp))
      Text(
        text = stringResource(R.string.settings_upgrade_plus),
        style = MaterialTheme.typography.bodyLarge.copy(
          fontSize = 15.5.sp,
          fontWeight = FontWeight.Medium,
        ),
        color = PopupTextPrimary,
      )
    }
  }
}

@Composable
private fun SettingsGroupRow(
  icon: ImageVector,
  label: String,
  testTag: String,
  onClick: () -> Unit,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 18.dp, vertical = 15.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = PopupIconTint,
      modifier = Modifier.size(20.dp),
    )
    Spacer(modifier = Modifier.width(14.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.5.sp,
        fontWeight = FontWeight.Medium,
      ),
      color = PopupTextPrimary,
    )
  }
}

@Composable
private fun SettingsToggleRow(
  label: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clickable { onCheckedChange(!checked) }
        .padding(horizontal = 18.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
      color = PopupTextPrimary,
      modifier = Modifier.weight(1f),
    )
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}
