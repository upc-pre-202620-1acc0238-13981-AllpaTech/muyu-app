package com.example.muyu_app.features.user_management.presentation.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muyu_app.R
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuappTheme
import kotlinx.coroutines.delay

private val LoadingFrames = listOf(
    "loading/dots_1.svg",
    "loading/dots_2.svg",
    "loading/dots_3.svg",
)

@Composable
fun LoadingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var frame by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(380)
            frame = (frame + 1) % LoadingFrames.size
        }
    }

    LaunchedEffect(Unit) {
        delay(1600)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
        contentAlignment = Alignment.Center,
    ) {
        MuyuAsset(
            fileName = LoadingFrames[frame],
            contentDescription = stringResource(R.string.loading_content_description),
            width = 124.dp,
            height = 124.dp,
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun LoadingScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        LoadingScreen(onFinished = {})
    }
}
