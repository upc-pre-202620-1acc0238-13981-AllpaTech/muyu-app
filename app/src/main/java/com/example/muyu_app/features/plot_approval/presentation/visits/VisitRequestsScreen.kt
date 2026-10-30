package com.example.muyu_app.features.plot_approval.presentation.visits

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuSoftGreen
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)

// Connects the farmer decision to the merchant-facing visit status kept by the app container.

@Composable
fun VisitRequestsScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val request = container.merchantVisitRequest?.takeIf {
        it.farmerEmail.equals(container.fakeStore.currentUser?.email, ignoreCase = true)
    }
    val plotName = request?.plotName ?: container.selectedPlot?.name.orEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.visit_requests_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = plotName,
                    fontFamily = UrbanistSemiBold,
                    fontSize = 18.sp,
                    color = MuyuForest,
                )
                if (request == null) Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuSoftGreen, CardShape)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.visit_requests_unavailable_title),
                        fontFamily = UrbanistSemiBold,
                        fontSize = 16.sp,
                        color = MuyuForest,
                    )
                    Text(
                        text = stringResource(R.string.visit_requests_unavailable_body),
                        fontFamily = UrbanistRegular,
                        fontSize = 14.sp,
                        color = Color(0xFF555555),
                    )
                } else Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = if (request.confirmed) {
                            stringResource(R.string.visit_request_confirmed)
                        } else {
                            stringResource(R.string.visit_request_pending)
                        },
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = MuyuForest,
                    )
                    VisitRequestLine(stringResource(R.string.merchant_funder), request.merchantName)
                    VisitRequestLine(stringResource(R.string.merchant_visit_date), request.date)
                    VisitRequestLine(stringResource(R.string.merchant_visit_time), request.time)
                    VisitRequestLine(stringResource(R.string.merchant_visit_place), request.plotPlace)
                    if (request.message.isNotBlank()) {
                        Text(request.message, fontFamily = UrbanistRegular, fontSize = 14.sp)
                    }
                    if (!request.confirmed) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MuyuForest)
                                .clickable {
                                    container.merchantVisitRequest = request.copy(confirmed = true)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.visit_request_accept),
                                color = Color.White,
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Plot,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun VisitRequestLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontFamily = UrbanistRegular, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontFamily = UrbanistSemiBold, fontSize = 13.sp, color = Color.Black)
    }
}
