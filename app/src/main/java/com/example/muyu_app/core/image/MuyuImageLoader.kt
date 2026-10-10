package com.example.muyu_app.core.image

import android.content.Context
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.svg.SvgDecoder

@Composable
fun InstallMuyuImageLoader() {
    setSingletonImageLoaderFactory { context: Context ->
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}
