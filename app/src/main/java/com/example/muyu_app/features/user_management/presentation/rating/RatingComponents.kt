package com.example.muyu_app.features.user_management.presentation.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.ui.theme.*

// Mantiene los datos del contrato del prototipo hasta integrar las valoraciones con la API.
internal object RatingPreviewContract {
    const val buyer = "Valeria Mendoza"
    const val company = "Distribuidora La Cosecha"
    const val plot = "Esperanza"
}

@Composable
internal fun RatingText(text: String, modifier: Modifier = Modifier, size: Int = 14,
                        bold: Boolean = false, color: Color = Color.Black) {
    Text(text, modifier, color = color, fontSize = size.sp,
        fontFamily = if (bold) UrbanistBold else UrbanistRegular,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
}

@Composable
internal fun RatingStars(value: Int, onSelect: ((Int) -> Unit)? = null) {
    // Cada estrella permite elegir la puntuación y anuncia su valor a lectores de pantalla.
    Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.Center) {
        (1..5).forEach { score ->
            val description = stringResource(R.string.rating_star_description, score)
            Box(Modifier.size(48.dp).then(if (onSelect != null) Modifier.selectable(
                selected = value == score, role = Role.RadioButton, onClick = { onSelect(score) }
            ) else Modifier), contentAlignment = Alignment.Center) {
                Box(Modifier.size(40.dp).clip(CircleShape)
                    .background(if (score <= value) MuyuAction else MuyuMutedChip),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.StarBorder, description, Modifier.size(25.dp),
                        tint = if (score <= value) Color.White else Color(0xFF53605A))
                }
            }
        }
    }
}

@Composable
internal fun RatingButton(text: String, onClick: () -> Unit, enabled: Boolean = true,
                          outlined: Boolean = false) {
    val colors = ButtonDefaults.buttonColors(containerColor = if (outlined) Color.White else MuyuAction,
        contentColor = if (outlined) MuyuForest else Color.White,
        disabledContainerColor = MuyuMutedChip, disabledContentColor = Color.DarkGray)
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 56.dp)
        .then(if (outlined) Modifier.border(1.dp, MuyuForest, RoundedCornerShape(14.dp)) else Modifier),
        enabled = enabled, colors = colors, shape = RoundedCornerShape(14.dp)) {
        RatingText(text, bold = true, color = if (outlined) MuyuForest else if (enabled) Color.White else Color.DarkGray)
    }
}

@Composable
internal fun RatingCard(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color.White)
        .border(1.dp, MuyuSoftGreen.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
}
