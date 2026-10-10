package com.example.muyu_app.core.location

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.muyu_app.R
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.network.isNetworkConnected
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.UrbanistRegular

private val MapShape = RoundedCornerShape(20.dp)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PlotLocationMap(
    latitude: Double?,
    longitude: Double?,
    overlay: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val online = isNetworkConnected(context)
    Box(
        modifier = modifier
            .clip(MapShape)
            .border(1.dp, MuyuForest, MapShape),
        contentAlignment = Alignment.Center,
    ) {
        if (latitude != null && longitude != null) {
            val realLat = if (latitude > 0) -latitude else latitude
            val realLng = if (longitude > 0) -longitude else longitude
            if (online) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                            webViewClient = WebViewClient()
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                userAgentString =
                                    "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
                            }
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        }
                    },
                    update = { webView ->
                        val delta = 0.005
                        val minLng = realLng - delta
                        val minLat = realLat - delta
                        val maxLng = realLng + delta
                        val maxLat = realLat + delta
                        val mapUrl =
                            "https://www.openstreetmap.org/export/embed.html?bbox=$minLng%2C$minLat%2C$maxLng%2C$maxLat&layer=mapnik&marker=$realLat%2C$realLng"
                        if (webView.tag != mapUrl) {
                            webView.tag = mapUrl
                            webView.loadUrl(mapUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                StaticMapFallback(latitude = realLat, longitude = realLng)
            }
        } else {
            Image(
                painter = painterResource(R.drawable.publish_map),
                contentDescription = stringResource(R.string.publish_map),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            MuyuAsset(
                fileName = "publish/pin.svg",
                contentDescription = null,
                width = 17.dp,
                height = 21.dp,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
        ) {
            overlay()
        }
    }
}

@Composable
private fun StaticMapFallback(latitude: Double, longitude: Double) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.publish_map),
            contentDescription = stringResource(R.string.publish_map),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MuyuAsset(
                fileName = "publish/pin.svg",
                contentDescription = null,
                width = 17.dp,
                height = 21.dp,
            )
        }
        Text(
            text = PlotFinance.formatCoordinates(latitude, longitude),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MuyuForest,
            ),
        )
    }
}
