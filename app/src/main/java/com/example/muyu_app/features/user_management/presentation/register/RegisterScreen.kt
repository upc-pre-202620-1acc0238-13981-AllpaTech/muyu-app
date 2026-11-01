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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
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
import com.example.muyu_app.core.designsystem.components.MuyuDatePickerField
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val MINIMUM_AGE = 18L
private const val MAXIMUM_AGE = 90L
private val EMAIL_PATTERN = Regex(
    "^[A-Za-z0-9](?:[A-Za-z0-9._%+-]{0,62}[A-Za-z0-9])?@" +
        "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z]{2,})+$",
)
private val NAME_PATTERN = Regex("^[\\p{L}][\\p{L} .'-]{1,59}$")
private val PHONE_PATTERN = Regex("^9\\d{8}$")

private enum class RegistrationValidationError {
    NAME,
    EMAIL,
    PHONE,
    BIRTHDATE_REQUIRED,
    BIRTHDATE_INVALID,
    PASSWORD,
    PASSWORD_MISMATCH,
    TERMS,
}

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
    val birthDateSaver = remember {
        Saver<LocalDate?, String>(
            save = { it?.toString().orEmpty() },
            restore = { value -> value.takeIf { it.isNotEmpty() }?.let(LocalDate::parse) },
        )
    }
    var birthDate by rememberSaveable(stateSaver = birthDateSaver) {
        mutableStateOf<LocalDate?>(null)
    }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    var confirmHidden by rememberSaveable { mutableStateOf(true) }
    var validationError by rememberSaveable {
        mutableStateOf<RegistrationValidationError?>(null)
    }
    var registrationFailed by rememberSaveable { mutableStateOf(false) }

    fun clearError() {
        validationError = null
        registrationFailed = false
    }

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
            AuthFieldBlock(stringResource(R.string.register_name), name) {
                name = it
                clearError()
            }
            AuthFieldBlock(stringResource(R.string.login_email), email, KeyboardType.Email) {
                email = it
                clearError()
            }
            AuthFieldBlock(stringResource(R.string.register_phone), phone, KeyboardType.Phone) {
                phone = it.filter(Char::isDigit).take(9)
                clearError()
            }
            MuyuDatePickerField(
                label = stringResource(R.string.register_birthdate),
                selectedDate = birthDate,
                onDateSelected = {
                    birthDate = it
                    clearError()
                },
                minDate = LocalDate.now().minusYears(MAXIMUM_AGE),
                maxDate = LocalDate.now().minusYears(MINIMUM_AGE),
                authStyle = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            AuthFieldBlock(
                label = stringResource(R.string.login_password),
                value = password,
                onValueChange = {
                    password = it
                    clearError()
                },
                keyboardType = KeyboardType.Password,
                hidden = passwordHidden,
                onToggleVisibility = { passwordHidden = !passwordHidden },
            )
            Text(
                text = stringResource(R.string.register_password_requirements),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (acceptedTerms) "☑" else "☐",
                    color = MuyuField,
                    fontSize = 25.sp,
                    modifier = Modifier.clickable {
                        acceptedTerms = !acceptedTerms
                        clearError()
                    },
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.register_accept_terms),
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onTerms() },
                )
            }
            AuthFieldBlock(
                label = stringResource(R.string.register_confirm_password),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    clearError()
                },
                keyboardType = KeyboardType.Password,
                hidden = confirmHidden,
                onToggleVisibility = { confirmHidden = !confirmHidden },
            )
            val validationMessage = validationError?.let { error ->
                stringResource(error.messageResource())
            }
            if (validationMessage != null || registrationFailed) {
                Text(
                    text = validationMessage ?: stringResource(R.string.register_error),
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
                        val normalizedName = name.trim()
                        val normalizedEmail = email.trim()
                        val normalizedPhone = phone.trim()
                        val error = validateRegistration(
                            name = normalizedName,
                            email = normalizedEmail,
                            phone = normalizedPhone,
                            birthDate = birthDate,
                            password = password,
                            confirmPassword = confirmPassword,
                            acceptedTerms = acceptedTerms,
                        )
                        if (error != null) {
                            validationError = error
                            registrationFailed = false
                            return@launch
                        }
                        val formattedBirthDate = checkNotNull(birthDate).let {
                            it.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                        }
                        container.registerUseCase(
                            normalizedName,
                            normalizedEmail,
                            normalizedPhone,
                            formattedBirthDate,
                            password,
                        )
                            .onSuccess { onRegister() }
                            .onFailure {
                                validationError = null
                                registrationFailed = true
                            }
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
}

private fun validateRegistration(
    name: String,
    email: String,
    phone: String,
    birthDate: LocalDate?,
    password: String,
    confirmPassword: String,
    acceptedTerms: Boolean,
): RegistrationValidationError? {
    if (!NAME_PATTERN.matches(name)) return RegistrationValidationError.NAME
    if (!EMAIL_PATTERN.matches(email)) return RegistrationValidationError.EMAIL
    if (!PHONE_PATTERN.matches(phone)) return RegistrationValidationError.PHONE
    if (birthDate == null) return RegistrationValidationError.BIRTHDATE_REQUIRED

    val today = LocalDate.now()
    if (
        birthDate.isBefore(today.minusYears(MAXIMUM_AGE)) ||
        birthDate.isAfter(today.minusYears(MINIMUM_AGE))
    ) {
        return RegistrationValidationError.BIRTHDATE_INVALID
    }

    val hasValidPassword = password.length >= 8 &&
        password.any(Char::isUpperCase) &&
        password.any(Char::isLowerCase) &&
        password.any(Char::isDigit) &&
        password.none(Char::isWhitespace) &&
        password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    if (!hasValidPassword) return RegistrationValidationError.PASSWORD
    if (password != confirmPassword) return RegistrationValidationError.PASSWORD_MISMATCH
    if (!acceptedTerms) return RegistrationValidationError.TERMS
    return null
}

private fun RegistrationValidationError.messageResource(): Int = when (this) {
    RegistrationValidationError.NAME -> R.string.register_name_invalid
    RegistrationValidationError.EMAIL -> R.string.register_email_invalid
    RegistrationValidationError.PHONE -> R.string.register_phone_invalid
    RegistrationValidationError.BIRTHDATE_REQUIRED -> R.string.register_birthdate_required
    RegistrationValidationError.BIRTHDATE_INVALID -> R.string.register_birthdate_invalid
    RegistrationValidationError.PASSWORD -> R.string.register_password_invalid
    RegistrationValidationError.PASSWORD_MISMATCH -> R.string.register_password_mismatch
    RegistrationValidationError.TERMS -> R.string.register_terms_required
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
