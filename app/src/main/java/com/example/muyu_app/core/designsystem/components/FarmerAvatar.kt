package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.UrbanistBold

/**
 * Foto de perfil que subió el propio agricultor. Si no tiene, muestra sus iniciales
 * en lugar de una foto de ejemplo.
 */
@Composable
fun FarmerAvatar(
    name: String,
    email: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val avatarUri = remember(email) {
        email?.let { mail ->
            container.fakeStore.users.find { it.email.equals(mail, ignoreCase = true) }?.avatarUri
        }.orEmpty()
    }
    var failed by remember(avatarUri) { mutableStateOf(false) }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MuyuAction),
        contentAlignment = Alignment.Center,
    ) {
        if (avatarUri.isNotBlank() && !failed) {
            AsyncImage(
                model = avatarUri,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { failed = true },
            )
        } else {
            Text(
                text = initials(name),
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.4f).sp,
                    color = Color.White,
                ),
            )
        }
    }
}

private fun initials(name: String): String = name
    .trim()
    .split(Regex("\\s+"))
    .filter { it.isNotBlank() }
    .take(2)
    .joinToString("") { it.first().uppercase() }
    .ifBlank { "?" }
