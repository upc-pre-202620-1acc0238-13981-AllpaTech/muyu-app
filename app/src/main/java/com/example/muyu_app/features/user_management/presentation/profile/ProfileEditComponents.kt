package com.example.muyu_app.features.user_management.presentation.profile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.di.LocalAppContainer

private val ProfileFieldShape = RoundedCornerShape(12.dp)
private val ProfileActionGreen = Color(0xFF2E7D32)

@Composable
internal fun EditableProfileAvatar(
    contentDescription: String,
    size: Dp,
    @DrawableRes fallbackRes: Int?,
) {
    val context = LocalContext.current
    val store = LocalAppContainer.current.fakeStore
    val avatarUri = store.currentUser?.avatarUri.orEmpty()
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            store.updateAvatar(uri.toString())
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFE5E7EB))
            .clickable {
                picker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
    ) {
        when {
            avatarUri.isNotBlank() -> AsyncImage(
                model = avatarUri,
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )

            fallbackRes != null -> Image(
                painter = painterResource(fallbackRes),
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .clip(CircleShape)
                .background(ProfileActionGreen)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.profile_change_photo),
                tint = Color.White,
                modifier = Modifier.size(13.dp),
            )
        }
    }
}

@Composable
internal fun ContactEditField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(hint) },
        singleLine = true,
        shape = ProfileFieldShape,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        prefix = prefix?.let { valuePrefix -> { Text(valuePrefix) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ProfileActionGreen,
            focusedLabelColor = ProfileActionGreen,
        ),
    )
}

@Composable
internal fun ContactEditActions(
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            shape = ProfileFieldShape,
        ) {
            Text(stringResource(R.string.action_cancel))
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            shape = ProfileFieldShape,
            colors = ButtonDefaults.buttonColors(containerColor = ProfileActionGreen),
        ) {
            Text(stringResource(R.string.profile_save_contact))
        }
    }
}

@Composable
internal fun ContactEditIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = stringResource(R.string.profile_edit_contact),
            tint = ProfileActionGreen,
        )
    }
}

@Composable
internal fun LegalTextLink(
    text: String,
    onClick: () -> Unit,
    style: TextStyle,
    color: Color,
    pressedColor: Color = color,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Text(
        text = text,
        style = style,
        color = if (pressed) pressedColor else color,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
    )
}

internal fun localPhoneDigits(phone: String): String = phone
    .filter(Char::isDigit)
    .removePrefix("51")
    .takeLast(9)

internal fun formatProfilePhone(phone: String): String {
    val digits = localPhoneDigits(phone)
    if (digits.isBlank()) return ""
    return "+51 $digits"
}
