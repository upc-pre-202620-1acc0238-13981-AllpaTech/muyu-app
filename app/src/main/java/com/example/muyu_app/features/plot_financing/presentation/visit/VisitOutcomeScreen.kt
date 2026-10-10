package com.example.muyu_app.features.plot_financing.presentation.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.plot.liveFundedPlot
import com.example.muyu_app.core.plot.merchantDisplayName
import com.example.muyu_app.core.plot.refreshSelectedPlot
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuLocateBg
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CardShape = RoundedCornerShape(20.dp)
private val SelectedBg = Color(0xFFEAFBE2)

@Composable
fun VisitOutcomeScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onReleased: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val plot = remember { container.liveFundedPlot() }
    val merchant = container.merchantDisplayName(plot)
    val plotName = plot?.name.orEmpty()
    val visitWhen = remember(plot?.visitDate, plot?.publishedAt) {
        formatVisitWhen(plot?.visitDate?.ifBlank { null } ?: plot?.publishedAt.orEmpty())
    }
    var outcome by remember {
        mutableStateOf(plot?.visitOutcome.orEmpty().ifBlank { MilestoneState.VISIT_AGREEMENT })
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
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
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text(
                        text = plotName,
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.Black,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.visit_card_merchant, merchant, visitWhen),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.5f),
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutcomeOption(
                    title = stringResource(R.string.visit_agreement),
                    body = stringResource(R.string.visit_agreement_body),
                    selected = outcome == MilestoneState.VISIT_AGREEMENT,
                    onClick = { outcome = MilestoneState.VISIT_AGREEMENT },
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutcomeOption(
                    title = stringResource(R.string.visit_no_agreement),
                    body = stringResource(R.string.visit_no_agreement_body),
                    selected = outcome == MilestoneState.VISIT_NO_AGREEMENT,
                    onClick = { outcome = MilestoneState.VISIT_NO_AGREEMENT },
                )
                Spacer(modifier = Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(CardShape)
                        .background(MuyuAction)
                        .clickable {
                            val id = plot?.id ?: return@clickable
                            container.fakeStore.saveVisit(id, outcome, PlotFinance.publishedToday())
                            container.refreshSelectedPlot()
                            if (outcome == MilestoneState.VISIT_NO_AGREEMENT) {
                                onReleased()
                            } else {
                                onSaved()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.visit_save),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White,
                        ),
                    )
                }
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Home,
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
            .background(if (selected) SelectedBg else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) MuyuForest else Color.Black.copy(alpha = 0.12f),
                shape = CardShape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = body,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = if (selected) MuyuOlive else Color.Black.copy(alpha = 0.5f),
                lineHeight = 22.sp,
            ),
        )
    }
}

private fun formatVisitWhen(raw: String): String {
    val parsed = PlotFinance.parseDate(raw)
    if (parsed != null) {
        return SimpleDateFormat("d MMM, hh:mm a", Locale.ENGLISH).format(parsed)
    }
    return SimpleDateFormat("d MMM, hh:mm a", Locale.ENGLISH).format(Date())
}
