package com.example.muyu_app.features.user_management.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.muyu_app.R
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuField
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLime
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val SheetShape = RoundedCornerShape(topStart = 63.dp, topEnd = 63.dp)
private val FieldShape = RoundedCornerShape(10.dp)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {},
) {
    val container = LocalAppContainer.current
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(container.loginUseCase))
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            viewModel.consumeAuthentication()
            onLoginSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.login_title),
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
            painter = painterResource(R.drawable.login_hero),
            contentDescription = stringResource(R.string.login_hero_content_description),
            modifier = Modifier
                .width(311.445.dp)
                .height(214.897.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(15.dp))
        LoginForm(
            email = state.email,
            password = state.password,
            passwordHidden = state.isHidden,
            errorMessage = state.errorMessage,
            onEmailChange = viewModel::onEmailChanged,
            onPasswordChange = viewModel::onPasswordChanged,
            onTogglePassword = viewModel::togglePasswordVisibility,
            onForgotPassword = onForgotPassword,
            onLogin = viewModel::login,
            onRegister = onRegister,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    passwordHidden: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onForgotPassword: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val labelStyle = TextStyle(
        fontFamily = UrbanistSemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = Color.White,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SheetShape)
            .background(MuyuForest)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
            .padding(top = 48.dp, bottom = 24.dp),
    ) {
        Text(text = stringResource(R.string.login_email), style = labelStyle)
        Spacer(modifier = Modifier.height(6.dp))
        LoginField(
            value = email,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leading = {
                LoginAsset(
                    fileName = "email.svg",
                    contentDescription = stringResource(R.string.login_email_icon),
                    width = 19.5.dp,
                    height = 15.dp,
                )
            },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(R.string.login_password), style = labelStyle)
        Spacer(modifier = Modifier.height(6.dp))
        LoginField(
            value = password,
            onValueChange = onPasswordChange,
            visualTransformation = if (passwordHidden) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leading = {
                LoginAsset(
                    fileName = "lock.svg",
                    contentDescription = stringResource(R.string.login_password_icon),
                    width = 19.dp,
                    height = 20.dp,
                )
            },
            trailing = {
                LoginAsset(
                    fileName = "eye.svg",
                    contentDescription = stringResource(
                        if (passwordHidden) {
                            R.string.login_show_password
                        } else {
                            R.string.login_hide_password
                        },
                    ),
                    width = 25.dp,
                    height = 15.dp,
                    modifier = Modifier.clickable(onClick = onTogglePassword),
                )
            },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.login_forgot_password),
            modifier = Modifier.clickable(onClick = onForgotPassword),
            style = labelStyle,
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_invalid),
                style = labelStyle.copy(color = Color(0xFFFFC7C7), fontSize = 16.sp),
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(FieldShape)
                .background(MuyuLime)
                .clickable(onClick = onLogin),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.login_button),
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black,
                ),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.login_no_account),
                style = labelStyle.copy(color = MuyuField.copy(alpha = 0.5f)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.login_register),
                modifier = Modifier.clickable(onClick = onRegister),
                style = labelStyle,
            )
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    leading: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(FieldShape)
            .background(MuyuField)
            .border(1.dp, Color.Black, FieldShape)
            .padding(start = 24.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            textStyle = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(Color.Black),
        )
        trailing?.invoke()
    }
}

@Composable
private fun LoginAsset(
    fileName: String,
    contentDescription: String?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/login/$fileName")
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .width(width)
            .height(height),
        contentScale = ContentScale.Fit,
    )
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun LoginScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            LoginScreen(onLoginSuccess = {})
        }
    }
}
