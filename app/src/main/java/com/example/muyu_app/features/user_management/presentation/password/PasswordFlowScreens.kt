package com.example.muyu_app.features.user_management.presentation.password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

@Composable
fun PasswordInstructionsScreen(email: String, onBack: () -> Unit, onContinue: () -> Unit) {
    var resendPreview by rememberSaveable { mutableStateOf(false) }
    // Oculta parte del correo sin consultar si existe una cuenta registrada.
    val maskedEmail = email.takeIf { '@' in it }?.let {
        it.substringBefore('@').take(1) + "***@" + it.substringAfter('@')
    }.orEmpty()
    PasswordFlowScaffold(stringResource(R.string.password_instructions_title), onBack,
        illustration = R.drawable.reset_hero) {
        Text(stringResource(R.string.password_instructions_body), color = Color.White,
            fontFamily = UrbanistSemiBold, fontSize = 20.sp)
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.password_instructions_email, maskedEmail),
            color = Color.White, fontFamily = UrbanistRegular, fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))
        // Aclara que recorrer el prototipo no envía correos ni verifica códigos.
        Text(stringResource(R.string.password_demo_email), color = Color.White.copy(alpha = 0.85f),
            fontFamily = UrbanistRegular, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        AuthPrimaryButton(stringResource(R.string.password_preview_continue), onContinue)
        TextButton(onClick = { resendPreview = true }) {
            Text(stringResource(R.string.password_resend), color = Color.White)
        }
        if (resendPreview) {
            Text(stringResource(R.string.password_resend_demo), color = Color.White,
                fontFamily = UrbanistRegular, modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Composable
fun PasswordFlowCompleteScreen(fromProfile: Boolean, onContinue: () -> Unit) {
    PasswordFlowScaffold(stringResource(R.string.password_flow_complete_title), onContinue) {
        // Confirma el recorrido visual sin afirmar que la contraseña real haya cambiado.
        Text(stringResource(R.string.password_flow_complete_body), color = Color.White,
            fontFamily = UrbanistRegular, fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))
        AuthPrimaryButton(
            text = stringResource(if (fromProfile) R.string.password_back_profile else R.string.password_back_login),
            onClick = onContinue,
        )
        Spacer(Modifier.height(24.dp))
    }
}
