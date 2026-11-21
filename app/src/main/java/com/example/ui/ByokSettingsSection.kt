package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.CaretDown
import com.adamglin.phosphoricons.regular.Check
import com.adamglin.phosphoricons.regular.Eye
import com.adamglin.phosphoricons.regular.EyeSlash
import com.adamglin.phosphoricons.regular.Key
import com.adamglin.phosphoricons.regular.Trash
import com.example.R

internal enum class ByokProvider(
  val displayName: String,
  val keyPlaceholder: String,
  val defaultModel: String,
  val defaultBaseUrl: String,
) {
  OPENAI("OpenAI", "sk-...", "gpt-4o", "https://api.openai.com/v1"),
  ANTHROPIC("Anthropic", "sk-ant-...", "claude-3-7-sonnet", "https://api.anthropic.com/v1"),
  GEMINI("Gemini", "AIza...", "gemini-2.5-pro", "https://generativelanguage.googleapis.com"),
  OPENROUTER("OpenRouter", "sk-or-...", "openai/gpt-4o", "https://openrouter.ai/api/v1"),
  CUSTOM("Custom", "Enter API key", "custom-model", "https://"),
}

private val ByokCardSurface = Color(0xFF121316)
private val ByokBadgeSurface = Color(0xFF28292E)
private val ByokDivider = Color(0xFF26272C)
private val ByokTextPrimary = Color(0xFFECECEE)
private val ByokTextSecondary = Color(0xFFC2C3C8)
private val ByokIconTint = Color(0xFFE3E4E8)
private val ByokDestructive = Color(0xFFFF6B6B)

@Composable
internal fun ByokSettingsContent(
  selectedProvider: ByokProvider,
  onSelectProvider: (ByokProvider) -> Unit,
  draftApiKey: String,
  onDraftApiKeyChange: (String) -> Unit,
  draftModelId: String,
  onDraftModelIdChange: (String) -> Unit,
  draftBaseUrl: String,
  onDraftBaseUrlChange: (String) -> Unit,
  hasSavedKey: Boolean,
  onSaveClick: () -> Unit,
  onClearClick: () -> Unit,
) {
  var isKeyVisible by remember { mutableStateOf(false) }
  var isProviderMenuExpanded by remember { mutableStateOf(false) }
  var dropdownWidthPx by remember { mutableIntStateOf(0) }
  val caretRotation by
    animateFloatAsState(
      targetValue = if (isProviderMenuExpanded) 180f else 0f,
      label = "byok_provider_caret",
    )
  val density = LocalDensity.current

  Column(
    modifier = Modifier.fillMaxWidth().testTag("byok_settings_content"),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    // Top Header Pill Card matching Settings Profile Header Card
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = ByokCardSurface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier =
            Modifier.size(46.dp)
              .clip(CircleShape)
              .background(ByokBadgeSurface),
        ) {
          Icon(
            imageVector = PhosphorIcons.Regular.Key,
            contentDescription = null,
            tint = ByokTextPrimary,
            modifier = Modifier.size(20.dp),
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stringResource(R.string.byok_header_title),
            style = MaterialTheme.typography.titleMedium.copy(
              fontSize = 17.sp,
              fontWeight = FontWeight.Medium,
            ),
            color = ByokTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Spacer(modifier = Modifier.height(1.dp))
          Text(
            text =
              if (hasSavedKey) {
                "${stringResource(R.string.byok_status_configured)} · ${selectedProvider.displayName}"
              } else {
                stringResource(R.string.byok_header_subtitle)
              },
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
            color = ByokTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          shape = RoundedCornerShape(50),
          color = ByokBadgeSurface,
        ) {
          Text(
            text =
              stringResource(
                if (hasSavedKey) R.string.byok_status_configured else R.string.byok_status_not_set,
              ),
            style = MaterialTheme.typography.labelMedium.copy(
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
            ),
            color = ByokTextPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          )
        }
      }
    }

    // Grouped Settings Card (Provider Dropdown + API Key + Model ID + Base URL)
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = ByokCardSurface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Provider Row with Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .minimumInteractiveComponentSize()
                .onGloballyPositioned { coordinates ->
                  dropdownWidthPx = coordinates.size.width
                }
                .clickable(role = Role.DropdownList) {
                  isProviderMenuExpanded = !isProviderMenuExpanded
                }
                .padding(horizontal = 18.dp, vertical = 15.dp)
                .testTag("byok_provider_dropdown_trigger"),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.byok_provider_label),
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontSize = 15.5.sp,
                  fontWeight = FontWeight.Medium,
                ),
                color = ByokTextPrimary,
              )
              Spacer(modifier = Modifier.height(1.dp))
              Text(
                text = selectedProvider.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = ByokTextSecondary,
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier.width(24.dp)
                  .height(32.dp)
                  .clip(RoundedCornerShape(50))
                  .background(ByokBadgeSurface),
            ) {
              Icon(
                imageVector = PhosphorIcons.Regular.CaretDown,
                contentDescription = null,
                tint = ByokTextPrimary,
                modifier = Modifier.size(13.dp).rotate(caretRotation),
              )
            }
          }

          DropdownMenu(
            expanded = isProviderMenuExpanded,
            onDismissRequest = { isProviderMenuExpanded = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = ByokCardSurface,
            border = BorderStroke(0.8.dp, ByokDivider),
            modifier =
              Modifier.then(
                if (dropdownWidthPx > 0) {
                  Modifier.width(with(density) { dropdownWidthPx.toDp() })
                } else {
                  Modifier.fillMaxWidth()
                },
              )
                .testTag("byok_provider_dropdown_menu"),
          ) {
            ByokProvider.entries.forEach { provider ->
              val selected = provider == selectedProvider
              DropdownMenuItem(
                text = {
                  Text(
                    text = provider.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      fontSize = 15.sp,
                      fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    color = ByokTextPrimary,
                  )
                },
                trailingIcon = {
                  if (selected) {
                    Icon(
                      imageVector = PhosphorIcons.Regular.Check,
                      contentDescription = null,
                      tint = ByokTextPrimary,
                      modifier = Modifier.size(16.dp),
                    )
                  }
                },
                onClick = {
                  isProviderMenuExpanded = false
                  onSelectProvider(provider)
                },
                modifier = Modifier.testTag("byok_provider_${provider.name.lowercase()}"),
              )
            }
          }
        }

        HorizontalDivider(color = ByokDivider, thickness = 0.8.dp)

        // API Key Row
        ByokInlineInputRow(
          label = stringResource(R.string.settings_byok_hint),
          value = draftApiKey,
          onValueChange = onDraftApiKeyChange,
          placeholder = selectedProvider.keyPlaceholder,
          visualTransformation =
            if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
          testTag = "settings_byok_input",
          trailingContent = {
            val toggleDesc = stringResource(R.string.byok_cd_toggle_key)
            Box(
              contentAlignment = Alignment.Center,
              modifier =
                Modifier.minimumInteractiveComponentSize()
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(ByokBadgeSurface)
                  .clickable(role = Role.Button) { isKeyVisible = !isKeyVisible }
                  .semantics { contentDescription = toggleDesc }
                  .testTag("byok_toggle_visibility_button"),
            ) {
              Icon(
                imageVector =
                  if (isKeyVisible) PhosphorIcons.Regular.EyeSlash else PhosphorIcons.Regular.Eye,
                contentDescription = null,
                tint = ByokTextPrimary,
                modifier = Modifier.size(15.dp),
              )
            }
          },
        )

        HorizontalDivider(color = ByokDivider, thickness = 0.8.dp)

        // Model ID Row
        ByokInlineInputRow(
          label = stringResource(R.string.byok_model_label),
          value = draftModelId,
          onValueChange = onDraftModelIdChange,
          placeholder = selectedProvider.defaultModel,
          testTag = "settings_byok_model_input",
        )

        HorizontalDivider(color = ByokDivider, thickness = 0.8.dp)

        // Base URL Row
        ByokInlineInputRow(
          label = stringResource(R.string.byok_base_url_label),
          value = draftBaseUrl,
          onValueChange = onDraftBaseUrlChange,
          placeholder = selectedProvider.defaultBaseUrl,
          testTag = "settings_byok_baseurl_input",
        )
      }
    }

    // Standalone Pill Action Button matching "Upgrade to ReversX AI Plus" Pill
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = ByokCardSurface,
      modifier =
        Modifier.fillMaxWidth()
          .minimumInteractiveComponentSize()
          .clip(RoundedCornerShape(28.dp))
          .clickable(role = Role.Button, onClick = onSaveClick)
          .testTag("settings_byok_save_button"),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = PhosphorIcons.Regular.Check,
          contentDescription = null,
          tint = ByokIconTint,
          modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
          text = stringResource(R.string.byok_save_configuration),
          style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Medium,
          ),
          color = ByokTextPrimary,
        )
      }
    }

    if (hasSavedKey || draftApiKey.isNotEmpty()) {
      Surface(
        shape = RoundedCornerShape(28.dp),
        color = ByokCardSurface,
        modifier =
          Modifier.fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(28.dp))
            .clickable(role = Role.Button, onClick = onClearClick)
            .testTag("settings_byok_clear_button"),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = PhosphorIcons.Regular.Trash,
            contentDescription = null,
            tint = ByokDestructive,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(14.dp))
          Text(
            text = stringResource(R.string.settings_clear),
            style = MaterialTheme.typography.bodyLarge.copy(
              fontSize = 15.5.sp,
              fontWeight = FontWeight.Medium,
            ),
            color = ByokDestructive,
          )
        }
      }
    }
  }
}

@Composable
private fun ByokInlineInputRow(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  testTag: String,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .minimumInteractiveComponentSize()
        .padding(horizontal = 18.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Medium,
        ),
        color = ByokTextSecondary,
      )
      Spacer(modifier = Modifier.height(3.dp))
      Box(contentAlignment = Alignment.CenterStart) {
        if (value.isEmpty()) {
          Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
            color = ByokTextSecondary.copy(alpha = 0.45f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        BasicTextField(
          value = value,
          onValueChange = onValueChange,
          singleLine = true,
          visualTransformation = visualTransformation,
          textStyle =
            MaterialTheme.typography.bodyLarge.copy(
              fontSize = 15.sp,
              color = ByokTextPrimary,
            ),
          cursorBrush = SolidColor(ByokTextPrimary),
          modifier = Modifier.fillMaxWidth().testTag(testTag),
        )
      }
    }
    if (trailingContent != null) {
      Spacer(modifier = Modifier.width(10.dp))
      trailingContent()
    }
  }
}
