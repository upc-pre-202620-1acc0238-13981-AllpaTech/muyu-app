package com.example.muyu_app.features.user_management.presentation.resetpassword

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.muyu_app.core.designsystem.components.AuthLabel
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.core.designsystem.components.AuthSheet
import com.example.muyu_app.core.designsystem.components.AuthTextField
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

@Composable
fun ResetPasswordScreen(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var showEmailError by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.reset_title),
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
            painter = painterResource(R.drawable.reset_hero),
            contentDescription = stringResource(R.string.reset_hero_content_description),
            modifier = Modifier
                .width(341.dp)
                .height(218.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(28.dp))
        AuthSheet(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            AuthLabel(text = stringResource(R.string.reset_heading), color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.reset_body),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                ),
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = stringResource(R.string.reset_email_prompt),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = Color.White,
                ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            AuthTextField(
                value = email,
                onValueChange = { email = it; showEmailError = false },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            // Explica por qué no se puede avanzar con el correo ingresado.
            if (showEmailError) {
                Text(
                    text = stringResource(R.string.muyu_email_invalid),
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            AuthPrimaryButton(
                text = stringResource(R.string.reset_next),
                onClick = {
                    // Valida el formato del correo antes de avanzar en la demostración de recuperación.
                    if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        email = email.trim()
                        onNext()
                    } else {
                        showEmailError = true
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun ResetPasswordScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        ResetPasswordScreen(onNext = {})
    }
}
