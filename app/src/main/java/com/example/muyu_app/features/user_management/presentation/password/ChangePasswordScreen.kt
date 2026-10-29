package com.example.muyu_app.features.user_management.presentation.password

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.AuthFlowTitle
import com.example.muyu_app.core.designsystem.components.AuthLabel
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.core.designsystem.components.AuthSheet
import com.example.muyu_app.core.designsystem.components.AuthTextField
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

@Composable
fun ChangePasswordScreen(onBack: () -> Unit, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    // Conserva las claves únicamente en memoria durante esta vista previa.
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var showMismatch by remember { mutableStateOf(false) }

    PasswordFlowScaffold(stringResource(R.string.password_new_title), onBack, modifier) {
        PasswordPreviewField(stringResource(R.string.login_password), password) {
            password = it
            showMismatch = false
        }
        Spacer(Modifier.height(12.dp))
        PasswordPreviewField(stringResource(R.string.register_confirm_password), confirmation) {
            confirmation = it
            showMismatch = false
        }
        if (showMismatch) {
            Text(stringResource(R.string.password_mismatch), color = Color(0xFFFFC7C7),
                fontFamily = UrbanistSemiBold, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.password_rules_title), color = Color.White,
            fontFamily = UrbanistSemiBold, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(6.dp))
        // Muestra las sugerencias de Figma sin implementar aún las reglas del servidor.
        listOf(R.string.password_rule_length, R.string.password_rule_number, R.string.password_rule_uppercase).forEach {
            Text("· " + stringResource(it), color = Color.White.copy(alpha = 0.85f),
                fontFamily = UrbanistRegular, fontSize = 14.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.password_confirmation_hint), color = Color.White,
            fontFamily = UrbanistRegular, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.password_demo_notice), color = Color.White.copy(alpha = 0.8f),
            fontFamily = UrbanistRegular, fontSize = 12.sp)
        Spacer(Modifier.height(20.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.profile_change_password),
            enabled = password.isNotBlank() && confirmation.isNotBlank(),
            onClick = {
                if (password != confirmation) {
                    showMismatch = true
                } else {
                    // Completa solo la navegación; no guarda claves ni llama a la API.
                    password = ""
                    confirmation = ""
                    onContinue()
                }
            },
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PasswordPreviewField(label: String, value: String, onValueChange: (String) -> Unit) {
    var hidden by rememberSaveable { mutableStateOf(true) }
    AuthLabel(label)
    Spacer(Modifier.height(8.dp))
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
        trailing = {
            // El control mantiene su nombre accesible al mostrar u ocultar la clave.
            IconButton(onClick = { hidden = !hidden }) {
                MuyuAsset("login/eye.svg", stringResource(if (hidden) R.string.login_show_password
                    else R.string.login_hide_password), 25.dp, 15.dp)
            }
        },
    )
}

@Composable
internal fun PasswordFlowScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes illustration: Int = R.drawable.pin_hero,
    // Describe la ilustración según el flujo de contraseña o PIN que la utiliza.
    @StringRes illustrationDescription: Int = R.string.password_hero_description,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxSize().background(MuyuBackground).imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        AuthFlowTitle(title, onBack)
        Spacer(Modifier.height(8.dp))
        Image(painterResource(illustration), stringResource(illustrationDescription),
            modifier = Modifier.fillMaxWidth(0.8f).height(180.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.height(12.dp))
        // Mantiene accesibles los campos y las acciones con el teclado abierto.
        AuthSheet(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), content = content)
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Preview(showBackground = true, widthDp = 320, heightDp = 720)
@Composable
private fun ChangePasswordPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme { ChangePasswordScreen({}, {}) }
}
