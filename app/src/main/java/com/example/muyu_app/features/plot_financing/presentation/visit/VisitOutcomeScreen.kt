package com.example.muyu_app.features.plot_financing.presentation.visit

import com.example.muyu_app.ui.theme.MuyuBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuSkip
import com.example.muyu_app.ui.theme.MuyuSurface
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch

// Frames "iPhone 17 - 247" y " Visit agreement with new price" (Mockups - Mobile).
private val CardShape = RoundedCornerShape(20.dp)
private val FieldShape = RoundedCornerShape(12.dp)
private val Muted = Color(0xFF4F5A50)
private val ErrorText = Color(0xFF9E2F2F)

/**
 * El agricultor registra el resultado de una visita confirmada. Se guarda en el
 * backend so the merchant and plot reflect the same result.
 */
@Composable
fun VisitOutcomeScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    selectedTab: MuyuTab = MuyuTab.Home,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val visit = container.selectedVisit
    val scope = rememberCoroutineScope()
    var outcome by remember(visit?.id) {
        mutableStateOf(visit?.outcome.orEmpty().ifBlank { MilestoneState.VISIT_AGREEMENT })
    }
    var price by remember(visit?.id) { mutableStateOf(visit?.agreedPrice.orEmpty()) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val agreement = outcome == MilestoneState.VISIT_AGREEMENT

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.visit_outcome_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 18.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (visit == null) {
                    Text(
                        text = stringResource(R.string.visit_outcome_missing),
                        style = textStyle(UrbanistRegular, 16, Muted),
                    )
                    return@Column
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = visit.plotName, style = textStyle(UrbanistSemiBold, 16, Color.Black))
                    Text(
                        text = listOf(visit.farmerName, visit.merchantName)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                        style = textStyle(UrbanistRegular, 16, Muted),
                    )
                    Text(
                        text = listOf(visit.date, visit.time).filter { it.isNotBlank() }.joinToString(", "),
                        style = textStyle(UrbanistRegular, 16, Muted),
                    )
                }

                OutcomeOption(
                    title = stringResource(R.string.visit_agreement),
                    body = stringResource(R.string.visit_agreement_body),
                    selected = agreement,
                    onClick = { outcome = MilestoneState.VISIT_AGREEMENT },
                )
                if (agreement) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CardShape)
                            .background(Color.White)
                            .border(1.dp, MuyuForest, CardShape)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.visit_new_agreed_price),
                            style = textStyle(UrbanistSemiBold, 16, Color.Black),
                        )
                        BasicTextField(
                            value = price,
                            onValueChange = { value ->
                                price = value.filter { it.isDigit() || it == '.' }.take(10)
                            },
                            singleLine = true,
                            textStyle = textStyle(UrbanistRegular, 16, Color.Black),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(FieldShape)
                                .background(MuyuLocateBg)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        )
                        Text(
                            text = stringResource(R.string.visit_new_agreed_price_hint),
                            style = textStyle(UrbanistRegular, 14, Muted),
                        )
                    }
                }
                OutcomeOption(
                    title = stringResource(R.string.visit_no_agreement),
                    body = stringResource(R.string.visit_no_agreement_body),
                    selected = !agreement,
                    onClick = { outcome = MilestoneState.VISIT_NO_AGREEMENT },
                )
                error?.let { Text(text = it, style = textStyle(UrbanistRegular, 14, ErrorText)) }

                Text(
                    text = stringResource(
                        when {
                            saving -> R.string.visit_saving
                            agreement -> R.string.visit_confirm_agreement
                            else -> R.string.visit_save
                        },
                    ),
                    style = textStyle(UrbanistBold, 18, Color.White).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(MuyuAction)
                        .clickable(enabled = !saving) {
                            saving = true
                            error = null
                            scope.launch {
                                container.plotVisits
                                    .recordOutcome(visit.id, outcome, if (agreement) price.trim() else "")
                                    .onSuccess { updated ->
                                        container.selectedVisit = updated
                                        container.selectedPlot
                                            ?.takeIf { it.id == updated.plotId }
                                            ?.let { current ->
                                                container.selectedPlot = current.copy(
                                                    visitOutcome = updated.outcome,
                                                    visitDate = updated.date,
                                                    agreedPrice = updated.agreedPrice,
                                                )
                                            }
                                        onSaved()
                                    }
                                    .onFailure { error = it.message.orEmpty() }
                                saving = false
                            }
                        }
                        .padding(vertical = 16.dp),
                )
            }
        }
        MuyuBottomBar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun OutcomeOption(
    title: String,
    body: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(if (selected) MuyuSkip else Color.White)
            .border(if (selected) 2.dp else 1.dp, MuyuForest, CardShape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = title, style = textStyle(UrbanistSemiBold, 16, Color.Black))
        Text(text = body, style = textStyle(UrbanistRegular, 16, Muted))
    }
}

private fun textStyle(family: FontFamily, size: Int, color: Color) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    color = color,
)
