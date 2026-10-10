package com.example.muyu_app.features.user_management.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.AuthLabel
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.core.designsystem.components.AuthSheet
import com.example.muyu_app.core.designsystem.components.AuthTextField
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuField
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit = {},
    onTerms: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var birthdate by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var confirmHidden by rememberSaveable { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.register_title),
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 10.dp, bottom = 18.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FredokaSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = MuyuForest,
            ),
        )
        AuthSheet(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            AuthFieldBlock(stringResource(R.string.register_name), name) { name = it }
            AuthFieldBlock(stringResource(R.string.login_email), email, KeyboardType.Email) { email = it }
            AuthFieldBlock(stringResource(R.string.register_phone), phone, KeyboardType.Phone) { phone = it }
            AuthLabel(stringResource(R.string.register_birthdate))
            Spacer(modifier = Modifier.height(6.dp))
            AuthTextField(
                value = birthdate,
                onValueChange = { birthdate = it },
                modifier = Modifier.clickable { showDatePicker = true },
            )
            Spacer(modifier = Modifier.height(12.dp))
            AuthFieldBlock(
                label = stringResource(R.string.login_password),
                value = password,
                onValueChange = { password = it },
                keyboardType = KeyboardType.Password,
                hidden = passwordHidden,
                onToggleVisibility = { passwordHidden = !passwordHidden },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (acceptedTerms) "☑" else "☐",
                    color = MuyuField,
                    fontSize = 25.sp,
                    modifier = Modifier.clickable { acceptedTerms = !acceptedTerms },
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "He leído y acepto los términos y condiciones",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onTerms() },
                )
            }
            AuthFieldBlock(
                label = stringResource(R.string.register_confirm_password),
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                keyboardType = KeyboardType.Password,
                hidden = confirmHidden,
                onToggleVisibility = { confirmHidden = !confirmHidden },
            )
            if (error != null) {
                Text(
                    text = stringResource(R.string.register_error),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFFFFC7C7),
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            AuthPrimaryButton(
                text = stringResource(R.string.register_button),
                onClick = {
                    scope.launch {
                        if (!acceptedTerms || password != confirmPassword || email.isBlank() || password.isBlank()) {
                            error = "invalid"
                            return@launch
                        }
                        container.registerUseCase(name, email, phone, birthdate, password)
                            .onSuccess { onRegister() }
                            .onFailure { error = "invalid" }
                    }
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.register_have_account),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.5f),
                    ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.login_title),
                    modifier = Modifier.clickable(onClick = onLogin),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MuyuField,
                    ),
                )
            }
        }
    }
    if (showDatePicker) {
        val dateState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            birthdate = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                                .format(Date(millis))
                        }
                        showDatePicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = dateState)
        }
    }
}

@Composable
private fun AuthFieldBlock(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    hidden: Boolean? = null,
    onToggleVisibility: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    AuthLabel(text = label)
    Spacer(modifier = Modifier.height(6.dp))
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (hidden == true) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailing = if (onToggleVisibility != null) {
            {
                MuyuAsset(
                    fileName = "login/eye.svg",
                    contentDescription = stringResource(
                        if (hidden == true) R.string.login_show_password else R.string.login_hide_password,
                    ),
                    width = 25.dp,
                    height = 15.dp,
                    modifier = Modifier.clickable(onClick = onToggleVisibility),
                )
            }
        } else {
            null
        },
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun RegisterScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            RegisterScreen(onRegister = {})
        }
    }
}
