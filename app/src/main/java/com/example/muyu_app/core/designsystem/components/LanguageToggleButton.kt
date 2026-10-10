package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.core.i18n.AppLanguage
import com.example.muyu_app.ui.theme.MuyuWeatherChip
import com.example.muyu_app.ui.theme.UrbanistBold

@Composable
fun LanguageToggleButton(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MuyuWeatherChip)
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = language.code,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
            ),
        )
    }
}
