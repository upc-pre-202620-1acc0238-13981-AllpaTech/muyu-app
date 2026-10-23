package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun PlotPhoto(
    imageUri: String?,
    description: String?,
    modifier: Modifier = Modifier,
) {
    var loadFailed by remember(imageUri) {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier.background(Color(0xFFEAF9E9)),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUri.isNullOrBlank() || loadFailed) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "No plot photo available",
                tint = Color(0xFF4A7C1E),
                modifier = Modifier.size(48.dp),
            )
        } else {
            AsyncImage(
                model = imageUri,
                contentDescription = description,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { loadFailed = true },
            )
        }
    }
}
