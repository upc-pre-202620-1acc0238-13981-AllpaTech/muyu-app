package com.example.muyu_app.features.user_management.presentation.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.core.designsystem.components.AuthSheet
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuTryAgainText
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val PinShape = RoundedCornerShape(20.dp)

@Composable
fun SafetyPinScreen(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var digits by rememberSaveable { mutableStateOf(List(4) { "" }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.pin_title),
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FredokaSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = MuyuForest,
            ),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(R.drawable.pin_hero),
            contentDescription = stringResource(R.string.pin_hero_content_description),
            modifier = Modifier
                .width(340.dp)
                .height(235.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthSheet(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.pin_heading),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = Color.White,
                ),
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                digits.forEachIndexed { index, digit ->
                    PinBox(
                        value = digit,
                        onValueChange = { value ->
                            digits = digits.toMutableList().also { list ->
                                // Acepta únicamente una cifra del cero al nueve en cada casilla.
                                list[index] = value.filter { it in '0'..'9' }.takeLast(1)
                            }
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            AuthPrimaryButton(
                text = stringResource(R.string.pin_accept),
                // Exige completar las cuatro casillas; esto no verifica el código con un servidor.
                enabled = digits.all { it.length == 1 },
                onClick = { if (digits.all { it.length == 1 }) onAccept() },
            )
            Spacer(modifier = Modifier.height(14.dp))
            AuthPrimaryButton(
                text = stringResource(R.string.pin_try_again),
                onClick = {},
                background = MuyuOlive,
                contentColor = MuyuTryAgainText,
            )
        }
    }
}

@Composable
private fun PinBox(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(PinShape)
            .background(Color.White)
            .border(1.dp, Color.Black, PinShape),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush = SolidColor(Color.Black),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun SafetyPinScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        SafetyPinScreen(onAccept = {})
    }
}
