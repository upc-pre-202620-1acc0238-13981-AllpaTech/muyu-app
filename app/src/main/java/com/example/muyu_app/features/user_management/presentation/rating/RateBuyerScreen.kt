package com.example.muyu_app.features.user_management.presentation.rating

import com.example.muyu_app.ui.theme.MuyuBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.ui.theme.*

@Composable
fun RateBuyerScreen(rating: Int, comment: String, onRatingChange: (Int) -> Unit,
                    onCommentChange: (String) -> Unit, onBack: () -> Unit, onSubmit: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MuyuBackground).navigationBarsPadding().imePadding()) {
        GreenTitleHeader(stringResource(R.string.rating_title), onBack)
        // Permite completar el formulario con teclado abierto y en pantallas pequeñas.
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            RatingText(stringResource(R.string.rating_section), size = 13, bold = true)
            Spacer(Modifier.height(2.dp))
            RatingText(stringResource(R.string.rating_contract_ended, stringResource(R.string.rating_cycle_date)), size = 12)
            RatingText(RatingPreviewContract.plot, size = 20, bold = true)
            RatingText(stringResource(R.string.rating_buyer, RatingPreviewContract.buyer), color = Color.DarkGray)
            RatingText(RatingPreviewContract.company, color = Color.DarkGray)
            Spacer(Modifier.height(2.dp))
            RatingText(stringResource(R.string.rating_question), size = 20, bold = true)
            RatingStars(rating, onRatingChange)
            RatingText(stringResource(R.string.rating_selected, rating), size = 12, color = MuyuOlive)
            RatingText(stringResource(R.string.rating_comment_title), size = 20, bold = true)
            OutlinedTextField(value = comment, onValueChange = onCommentChange,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                placeholder = { RatingText(stringResource(R.string.rating_comment_hint), color = Color.Gray) },
                textStyle = TextStyle(fontFamily = UrbanistRegular, fontSize = 15.sp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black, focusedContainerColor = MuyuPageGray,
                    unfocusedContainerColor = MuyuPageGray, focusedBorderColor = MuyuAction,
                    unfocusedBorderColor = MuyuMutedChip, cursorColor = MuyuForest))
            RatingButton(stringResource(R.string.rating_submit), onSubmit, enabled = rating in 1..5)
        }
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun RateBuyerPreview() {
    MuyuappTheme { RateBuyerScreen(4, "", {}, {}, {}, {}) }
}
