package com.example.muyu_app.features.user_management.presentation.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.ui.theme.*

@Composable
fun RatingSubmittedScreen(rating: Int, comment: String, farmerName: String,
                          onBackToProfile: () -> Unit) {
    var showContract by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(MuyuPageGray).navigationBarsPadding()) {
        GreenTitleHeader(stringResource(R.string.rating_submitted_title), onBackToProfile)
        // Los frames 329 y 330 comparten este contenido desplazable y los botones inferiores.
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RatingCard {
                RatingText(stringResource(R.string.rating_later_stage, stringResource(R.string.rating_cycle_date)),
                    size = 13, bold = true, color = Color(0xFF854500))
                Spacer(Modifier.height(12.dp))
                Box(Modifier.align(Alignment.CenterHorizontally).size(64.dp).clip(CircleShape)
                    .background(MuyuApprovedBg), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.CheckCircleOutline, null, Modifier.size(32.dp), tint = MuyuForest)
                }
                RatingText(stringResource(R.string.rating_thanks), Modifier.align(Alignment.CenterHorizontally),
                    size = 24, bold = true, color = MuyuForest)
                RatingText(stringResource(R.string.rating_thanks_body, RatingPreviewContract.buyer),
                    Modifier.align(Alignment.CenterHorizontally), color = Color.DarkGray)
                Spacer(Modifier.height(8.dp))
                BuyerRatingCard(rating)
                Spacer(Modifier.height(4.dp))
                CompletedContractCard()
            }
            RatingText(stringResource(R.string.rating_submitted_by, farmerName,
                RatingPreviewContract.buyer, RatingPreviewContract.company) + " " +
                comment.ifBlank { stringResource(R.string.rating_no_comment) }, color = Color.Gray)
            RatingText(stringResource(R.string.rating_preview_notice), size = 12, color = Color.Gray)
        }
        Column(Modifier.fillMaxWidth().background(MuyuPageGray).padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            RatingButton(stringResource(R.string.rating_view_contract), { showContract = true })
            RatingButton(stringResource(R.string.rating_back_profile), onBackToProfile, outlined = true)
        }
    }
    // Muestra el contrato del prototipo sin abrir una parcela distinta de la valorada.
    if (showContract) {
        AlertDialog(onDismissRequest = { showContract = false },
            title = { RatingText(stringResource(R.string.rating_contract_title), size = 20, bold = true) },
            text = { Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RatingText(RatingPreviewContract.buyer, bold = true)
                RatingText(RatingPreviewContract.company)
                CompletedContractCard()
            } },
            containerColor = Color.White,
            confirmButton = { TextButton(onClick = { showContract = false }) {
                RatingText(stringResource(R.string.rating_close), color = MuyuForest, bold = true)
            } })
    }
}

@Composable
private fun BuyerRatingCard(rating: Int) {
    RatingCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(MuyuBackground), contentAlignment = Alignment.Center) {
                RatingText("VM", bold = true, color = MuyuForest)
            }
            Column {
                RatingText(RatingPreviewContract.buyer, size = 16, bold = true)
                RatingText(RatingPreviewContract.company, size = 13, color = Color.DarkGray)
            }
        }
        RatingStars(rating)
        RatingText(stringResource(R.string.rating_submitted_value, rating), bold = true, color = MuyuForest)
    }
}

@Composable
private fun CompletedContractCard() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        RatingText(stringResource(R.string.rating_completed), size = 16, bold = true, color = MuyuForest)
        Image(painterResource(R.drawable.plot_hero), contentDescription = RatingPreviewContract.plot,
            Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            RatingText(stringResource(R.string.rating_plot_label), color = Color.Gray)
            RatingText(RatingPreviewContract.plot, bold = true)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            RatingText(stringResource(R.string.rating_closure_label), color = Color.Gray)
            RatingText(stringResource(R.string.rating_cycle_date), bold = true)
        }
        RatingText("✓  " + stringResource(R.string.rating_milestones),
            Modifier.clip(RoundedCornerShape(20.dp)).background(MuyuApprovedBg).padding(horizontal = 12.dp, vertical = 5.dp),
            size = 12, color = MuyuForest)
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun RatingSubmittedPreview() {
    MuyuappTheme { RatingSubmittedScreen(4, "", "Miguel Quispe", {}) }
}
