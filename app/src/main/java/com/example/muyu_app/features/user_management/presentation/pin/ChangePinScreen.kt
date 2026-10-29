package com.example.muyu_app.features.user_management.presentation.pin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.features.user_management.presentation.password.PasswordFlowScaffold
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

@Composable
fun ChangePinScreen(onBack: () -> Unit, onComplete: () -> Unit) {
    // Mantiene el PIN solo en memoria y lo separa del PIN de recuperación de contraseña.
    var newPin by remember { mutableStateOf(List(4) { "" }) }
    var confirmation by remember { mutableStateOf(List(4) { "" }) }
    var confirming by remember { mutableStateOf(false) }
    var mismatch by remember { mutableStateOf(false) }
    val digits = if (confirming) confirmation else newPin
    val focus = remember(confirming) { List(4) { FocusRequester() } }
    // Enfoca la primera casilla al entrar o pasar a la confirmación.
    LaunchedEffect(confirming) { focus.first().requestFocus() }
    val goBack: () -> Unit = {
        if (confirming) {
            // Vuelve a editar el nuevo PIN sin regresar directamente al perfil.
            confirming = false
            confirmation = List(4) { "" }
            mismatch = false
        } else {
            onBack()
        }
    }
    BackHandler(enabled = confirming, onBack = goBack)

    PasswordFlowScaffold(
        title = stringResource(if (confirming) R.string.change_pin_confirm_title else R.string.change_pin_title),
        onBack = goBack,
        illustrationDescription = R.string.pin_hero_content_description,
    ) {
        Text(
            text = stringResource(if (confirming) R.string.change_pin_confirm_heading else R.string.change_pin_heading),
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            fontFamily = UrbanistSemiBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            digits.forEachIndexed { index, digit ->
                val description = stringResource(R.string.change_pin_digit_label, index + 1)
                val shape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier.weight(1f).height(68.dp).clip(shape)
                        .background(Color.White)
                        .border(1.dp, if (mismatch) Color(0xFFB3261E) else Color.Black, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicTextField(
                        value = digit,
                        onValueChange = { value ->
                            // Permite una cifra por casilla sin registrar ni enviar el código.
                            val next = value.filter { it in '0'..'9' }.takeLast(1)
                            val edited = digits.toMutableList().also { it[index] = next }
                            if (confirming) confirmation = edited else newPin = edited
                            mismatch = false
                            if (next.isNotEmpty() && index < 3) focus[index + 1].requestFocus()
                        },
                        modifier = Modifier.fillMaxWidth().focusRequester(focus[index])
                            .semantics { contentDescription = description },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        cursorBrush = SolidColor(Color.Black),
                        textStyle = TextStyle(fontFamily = UrbanistBold, fontSize = 24.sp,
                            color = Color.Black, textAlign = TextAlign.Center),
                    )
                }
            }
        }
        if (mismatch) {
            Text(stringResource(R.string.change_pin_mismatch), color = Color(0xFFFFC7C7),
                fontFamily = UrbanistSemiBold, modifier = Modifier.padding(top = 12.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.change_pin_demo_notice), color = Color.White.copy(alpha = 0.85f),
            fontFamily = UrbanistRegular, fontSize = 14.sp)
        Spacer(Modifier.height(28.dp))
        AuthPrimaryButton(
            text = stringResource(if (confirming) R.string.change_pin_confirm else R.string.change_pin_continue),
            enabled = digits.all { it.length == 1 },
            onClick = {
                when {
                    !confirming -> {
                        confirmation = List(4) { "" }
                        confirming = true
                    }
                    newPin != confirmation -> mismatch = true
                    else -> {
                        // Finaliza únicamente la vista previa y borra ambas entradas.
                        newPin = List(4) { "" }
                        confirmation = List(4) { "" }
                        onComplete()
                    }
                }
            },
        )
        TextButton(onClick = goBack) {
            Text(stringResource(if (confirming) R.string.change_pin_edit else R.string.change_pin_cancel), color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun PinFlowCompleteScreen(onBackToProfile: () -> Unit) {
    PasswordFlowScaffold(stringResource(R.string.change_pin_complete_title), onBackToProfile,
        illustrationDescription = R.string.pin_hero_content_description) {
        // No afirma que el PIN real se haya actualizado en el dispositivo o en la API.
        Text(stringResource(R.string.change_pin_complete_body), color = Color.White,
            fontFamily = UrbanistRegular, fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))
        AuthPrimaryButton(stringResource(R.string.password_back_profile), onBackToProfile)
        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Preview(showBackground = true, widthDp = 320, heightDp = 720)
@Composable
private fun ChangePinPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme { ChangePinScreen({}, {}) }
}
