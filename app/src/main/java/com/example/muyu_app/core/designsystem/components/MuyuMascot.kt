package com.example.muyu_app.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.UrbanistRegular

/**
 * Estados de Muyu, la mascota que guía al usuario:
 * pagos, revisión o espera, éxito y error.
 */
enum class MascotMood(@DrawableRes val drawable: Int) {
    Payment(R.drawable.mascot_card),
    Reviewing(R.drawable.mascot_search),
    Success(R.drawable.mascot_success),
    Sad(R.drawable.mascot_sad),
}

@Composable
fun MuyuMascot(
    mood: MascotMood,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
) {
    Image(
        painter = painterResource(mood.drawable),
        // Es decorativa: el título que la acompaña ya explica el estado.
        contentDescription = null,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit,
    )
}

/** Mascota con un título y un texto centrados para pantallas de resultado. */
@Composable
fun MascotMessage(
    mood: MascotMood,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MuyuForest,
    mascotSize: Dp = 132.dp,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MuyuMascot(mood = mood, size = mascotSize)
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FredokaSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = titleColor,
            ),
        )
        if (body.isNotBlank()) {
            Text(
                text = body,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontSize = 15.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                    lineHeight = 21.sp,
                ),
            )
        }
    }
}
