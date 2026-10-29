package com.example.muyu_app.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistLight

@Composable
fun OnboardingScaffold(
    @DrawableRes illustration: Int,
    illustrationWidth: Dp,
    illustrationHeight: Dp,
    title: String,
    body: String,
    pageIndex: Int,
    pageCount: Int = 3,
    primaryLabel: String,
    onPrimary: () -> Unit,
    skipLabel: String? = null,
    illustrationContentDescription: String? = null,
    modifier: Modifier = Modifier,
    // Permite omitir la introducción mediante la acción configurada por la pantalla.
    onSkip: () -> Unit = onPrimary,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(illustration),
            contentDescription = illustrationContentDescription,
            modifier = Modifier
                .width(illustrationWidth)
                .height(illustrationHeight),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = body,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistLight,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pageCount) { index ->
                if (index == pageIndex) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MuyuAction),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = if (skipLabel == null) Arrangement.Center else Arrangement.SpaceBetween,
        ) {
            if (skipLabel != null) {
                OnboardingButton(
                    text = skipLabel,
                    background = MuyuSkip,
                    contentColor = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.width(150.dp),
                    onClick = onSkip,
                )
                OnboardingButton(
                    text = primaryLabel,
                    background = MuyuAction,
                    contentColor = Color.White,
                    modifier = Modifier.width(150.dp),
                    onClick = onPrimary,
                )
            } else {
                OnboardingButton(
                    text = primaryLabel,
                    background = MuyuAction,
                    contentColor = Color.White,
                    modifier = Modifier.width(291.dp),
                    onClick = onPrimary,
                )
            }
        }
    }
}

@Composable
private fun OnboardingButton(
    text: String,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = contentColor,
            ),
        )
    }
}
