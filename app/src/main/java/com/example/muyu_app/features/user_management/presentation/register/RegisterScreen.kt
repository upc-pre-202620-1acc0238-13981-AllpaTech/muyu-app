package com.example.muyu_app.features.user_management.presentation.register

import androidx.compose.foundation.background
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import com.example.muyu_app.core.designsystem.components.AuthErrorText
import com.example.muyu_app.ui.theme.MuyuLime
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
import androidx.compose.ui.focus.onFocusChanged
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
import com.example.muyu_app.features.user_management.domain.EmailAlreadyRegisteredException
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
private val ALLOWED_EMAIL_DOMAINS = setOf(
    "gmail.com",
    "outlook.com",
    "outlook.es",
    "hotmail.com",
    "hotmail.es",
    "yahoo.com",
    "yahoo.es",
    "icloud.com",
    "live.com",
    "msn.com",
    "proton.me",
    "protonmail.com",
)
private val NAME_PATTERN = Regex("^[\\p{L}][\\p{L} .'-]{1,59}$")
private val PHONE_PATTERN = Regex("^9\\d{8}$")
private val DNI_PATTERN = Regex("^\\d{8}$")

private enum class RegistrationValidationError {
    NAME,
    EMAIL,
    EMAIL_TAKEN,
    PHONE,
    DNI,
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
    var dni by rememberSaveable { mutableStateOf("") }
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
    var passwordFocused by remember { mutableStateOf(false) }
    var validationError by rememberSaveable {
        mutableStateOf<RegistrationValidationError?>(null)
    }
    var registrationFailed by rememberSaveable { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }

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
            val errorMessage = validationError?.let { stringResource(it.messageResource()) }
            fun messageFor(vararg fields: RegistrationValidationError): String? =
                errorMessage.takeIf { validationError in fields }

            AuthFieldBlock(
                label = stringResource(R.string.register_name),
                value = name,
                error = messageFor(RegistrationValidationError.NAME),
            ) {
                name = it
                clearError()
            }
            AuthFieldBlock(
                label = stringResource(R.string.login_email),
                value = email,
                keyboardType = KeyboardType.Email,
                error = messageFor(RegistrationValidationError.EMAIL, RegistrationValidationError.EMAIL_TAKEN),
            ) {
                email = it
                clearError()
            }
            AuthFieldBlock(
                label = stringResource(R.string.register_phone),
                value = phone,
                keyboardType = KeyboardType.Phone,
                error = messageFor(RegistrationValidationError.PHONE),
            ) {
                phone = it.filter(Char::isDigit).take(9)
                clearError()
            }
            AuthFieldBlock(
                label = stringResource(R.string.register_dni),
                value = dni,
                keyboardType = KeyboardType.Number,
                error = messageFor(RegistrationValidationError.DNI),
            ) {
                dni = it.filter(Char::isDigit).take(8)
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
            FieldError(
                messageFor(
                    RegistrationValidationError.BIRTHDATE_REQUIRED,
                    RegistrationValidationError.BIRTHDATE_INVALID,
                ),
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
                error = messageFor(RegistrationValidationError.PASSWORD),
                bottomSpacing = 8.dp,
                modifier = Modifier.onFocusChanged { passwordFocused = it.hasFocus },
            )
            if (passwordFocused) {
                PasswordChecklist(password = password)
            }
            Spacer(modifier = Modifier.height(12.dp))
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
                error = messageFor(RegistrationValidationError.PASSWORD_MISMATCH),
            )
            TermsCheckbox(
                checked = acceptedTerms,
                onCheckedChange = {
                    acceptedTerms = it
                    clearError()
                },
                onOpenTerms = onTerms,
            )
            FieldError(messageFor(RegistrationValidationError.TERMS))
            if (registrationFailed) {
                Spacer(modifier = Modifier.height(8.dp))
                FieldError(stringResource(R.string.register_error))
            }
            Spacer(modifier = Modifier.height(16.dp))
            AuthPrimaryButton(
                text = stringResource(R.string.register_button),
                enabled = !submitting,
                onClick = {
                    val normalizedName = name.trim()
                    val normalizedEmail = email.trim()
                    val normalizedPhone = phone.trim()
                    val error = validateRegistration(
                        name = normalizedName,
                        email = normalizedEmail,
                        phone = normalizedPhone,
                        dni = dni,
                        birthDate = birthDate,
                        password = password,
                        confirmPassword = confirmPassword,
                        acceptedTerms = acceptedTerms,
                    )
                    if (error != null) {
                        validationError = error
                        registrationFailed = false
                    } else if (!submitting) {
                        // Set before launching so a quick second tap cannot send it twice.
                        submitting = true
                        val formattedBirthDate = checkNotNull(birthDate).let {
                            it.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                        }
                        scope.launch {
                            container.registerUseCase(
                                normalizedName,
                                normalizedEmail,
                                normalizedPhone,
                                formattedBirthDate,
                                password,
                                dni,
                            )
                                .onSuccess { onRegister() }
                                .onFailure { failure ->
                                    if (failure is EmailAlreadyRegisteredException) {
                                        validationError = RegistrationValidationError.EMAIL_TAKEN
                                        registrationFailed = false
                                    } else {
                                        validationError = null
                                        registrationFailed = true
                                    }
                                }
                            submitting = false
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
    dni: String,
    birthDate: LocalDate?,
    password: String,
    confirmPassword: String,
    acceptedTerms: Boolean,
): RegistrationValidationError? {
    if (!NAME_PATTERN.matches(name)) return RegistrationValidationError.NAME
    if (!isAcceptedEmail(email)) return RegistrationValidationError.EMAIL
    if (!PHONE_PATTERN.matches(phone)) return RegistrationValidationError.PHONE
    if (!DNI_PATTERN.matches(dni)) return RegistrationValidationError.DNI
    if (birthDate == null) return RegistrationValidationError.BIRTHDATE_REQUIRED

    val today = LocalDate.now()
    if (
        birthDate.isBefore(today.minusYears(MAXIMUM_AGE)) ||
        birthDate.isAfter(today.minusYears(MINIMUM_AGE))
    ) {
        return RegistrationValidationError.BIRTHDATE_INVALID
    }

    if (!passwordRules(password).all { it.met }) return RegistrationValidationError.PASSWORD
    if (password != confirmPassword) return RegistrationValidationError.PASSWORD_MISMATCH
    if (!acceptedTerms) return RegistrationValidationError.TERMS
    return null
}

private fun isAcceptedEmail(email: String): Boolean {
    if (!EMAIL_PATTERN.matches(email)) return false
    val domain = email.substringAfterLast('@').lowercase(Locale.ROOT)
    return domain in ALLOWED_EMAIL_DOMAINS
}

private fun RegistrationValidationError.messageResource(): Int = when (this) {
    RegistrationValidationError.NAME -> R.string.register_name_invalid
    RegistrationValidationError.EMAIL -> R.string.register_email_invalid
    RegistrationValidationError.EMAIL_TAKEN -> R.string.register_email_taken
    RegistrationValidationError.PHONE -> R.string.register_phone_invalid
    RegistrationValidationError.DNI -> R.string.register_dni_invalid
    RegistrationValidationError.BIRTHDATE_REQUIRED -> R.string.register_birthdate_required
    RegistrationValidationError.BIRTHDATE_INVALID -> R.string.register_birthdate_invalid
    RegistrationValidationError.PASSWORD -> R.string.register_password_invalid
    RegistrationValidationError.PASSWORD_MISMATCH -> R.string.register_password_mismatch
    RegistrationValidationError.TERMS -> R.string.register_terms_required
}

private data class PasswordRule(@StringRes val label: Int, val met: Boolean)

// Las mismas reglas validan el formulario y pintan la lista de ayuda.
private fun passwordRules(password: String): List<PasswordRule> = listOf(
    PasswordRule(R.string.register_rule_length, password.length >= 8),
    PasswordRule(R.string.register_rule_uppercase, password.any(Char::isUpperCase)),
    PasswordRule(R.string.register_rule_lowercase, password.any(Char::isLowerCase)),
    PasswordRule(R.string.register_rule_number, password.any(Char::isDigit)),
    PasswordRule(
        R.string.register_rule_special,
        password.any { !it.isLetterOrDigit() && !it.isWhitespace() },
    ),
    PasswordRule(
        R.string.register_rule_no_spaces,
        password.isNotEmpty() && password.none(Char::isWhitespace),
    ),
)

@Composable
private fun PasswordChecklist(password: String) {
    val metLabel = stringResource(R.string.register_rule_met)
    val missingLabel = stringResource(R.string.register_rule_missing)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        passwordChecklistRules(password).forEach { rule ->
            val label = stringResource(rule.label)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics(mergeDescendants = true) {
                    contentDescription = "$label, ${if (rule.met) metLabel else missingLabel}"
                },
            ) {
                Icon(
                    imageVector = if (rule.met) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (rule.met) MuyuLime else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = if (rule.met) Color.White else Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                )
            }
        }
    }
}

private fun passwordChecklistRules(password: String): List<PasswordRule> = listOf(
    PasswordRule(R.string.register_rule_length, password.length >= 8),
    PasswordRule(
        R.string.register_rule_letter_case,
        password.any(Char::isUpperCase) && password.any(Char::isLowerCase),
    ),
    PasswordRule(
        R.string.register_rule_number_and_special,
        password.any(Char::isDigit) &&
            password.any { !it.isLetterOrDigit() && !it.isWhitespace() },
    ),
    PasswordRule(
        R.string.register_rule_no_spaces,
        password.isNotEmpty() && password.none(Char::isWhitespace),
    ),
)

@Composable
private fun TermsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onOpenTerms: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (checked) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (checked) MuyuLime else MuyuField,
            modifier = Modifier
                .size(48.dp)
                .toggleable(
                    value = checked,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange,
                )
                .padding(12.dp),
        )
        Text(
            text = stringResource(R.string.register_accept_terms),
            color = Color.White,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onOpenTerms),
        )
    }
}

@Composable
private fun FieldError(message: String?) {
    if (message == null) return
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(top = 6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = AuthErrorText,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = message,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = AuthErrorText,
            ),
        )
    }
}

@Composable
private fun AuthFieldBlock(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    hidden: Boolean? = null,
    onToggleVisibility: (() -> Unit)? = null,
    error: String? = null,
    bottomSpacing: Dp = 12.dp,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    AuthLabel(text = label)
    Spacer(modifier = Modifier.height(6.dp))
    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = error != null,
        modifier = modifier,
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
    FieldError(error)
    Spacer(modifier = Modifier.height(bottomSpacing))
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
