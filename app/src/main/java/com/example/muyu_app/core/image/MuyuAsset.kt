package com.example.muyu_app.core.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

@Composable
fun MuyuAsset(
    fileName: String,
    contentDescription: String?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/$fileName")
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .width(width)
            .height(height),
        contentScale = ContentScale.Fit,
    )
}
