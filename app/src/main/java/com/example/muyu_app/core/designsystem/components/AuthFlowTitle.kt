package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuForest

@Composable
fun AuthFlowTitle(title: String, onBack: (() -> Unit)?, modifier: Modifier = Modifier) {
    // Permite volver sin convertir los títulos de acceso en encabezados de producto.
    Box(
        modifier = modifier.fillMaxWidth().statusBarsPadding()
            .heightIn(min = 64.dp).padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 8.dp),
            fontFamily = FredokaSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = MuyuForest,
        )
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.password_back), tint = MuyuForest)
            }
        }
    }
}
