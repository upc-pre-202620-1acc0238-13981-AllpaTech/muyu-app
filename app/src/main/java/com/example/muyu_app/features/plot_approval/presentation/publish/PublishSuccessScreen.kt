package com.example.muyu_app.features.plot_approval.presentation.publish

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPlanBadgeText
import com.example.muyu_app.ui.theme.MuyuSoftGreen
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(18.dp)
private val ButtonShape = RoundedCornerShape(16.dp)
private val InfoBg = Color(0xFFFFF6E8)
private val Muted = Color.Black.copy(alpha = 0.55f)

@Composable
fun PublishSuccessScreen(
    onViewPlot: () -> Unit,
    onHome: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val plot = container.selectedPlot
    val farmer = container.fakeStore.currentUser?.displayName
        ?: plot?.ownerName.orEmpty().ifBlank { stringResource(R.string.farmer_name) }
    val name = plot?.name?.ifBlank { null } ?: stringResource(R.string.farmer_plot_rosal)
    val crop = plot?.crop.orEmpty()
    val area = plot?.area.orEmpty()
    val place = plot?.place.orEmpty()
    val amount = plot?.amount.orEmpty().ifBlank { PlotFinance.formatAmount(8500.0) }
    val milestones = plot?.milestones?.ifBlank { "4" } ?: "4"
    val published = plot?.publishedAt?.ifBlank { null } ?: PlotFinance.publishedToday()
    val coords = if (plot?.latitude != null && plot.longitude != null) {
        PlotFinance.formatCoordinates(plot.latitude, plot.longitude)
    } else {
        null
    }
    val accuracy = plot?.accuracyMeters

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FarmerContextHeader(
                farmerName = farmer,
                title = stringResource(R.string.publish_result_title),
                onBack = onHome,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.publish_before_funding, published),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFFC56A16),
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MuyuApprovedBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✓", color = MuyuPlanBadgeText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.publish_success_title),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black,
                    ),
                )
                Text(
                    text = stringResource(R.string.publish_success_body, name),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Muted,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuSoftGreen, CardShape)
                        .padding(16.dp),
                ) {
                    Text(
                        text = name,
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black,
                        ),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StatusAvailable()
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = listOf(crop, area, place).filter { it.isNotBlank() }.joinToString(" · "),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = Muted,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuSoftGreen, CardShape)
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.publish_by_farmer, farmer),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryLine(stringResource(R.string.publish_financing_label), amount)
                    SummaryLine(stringResource(R.string.publish_plan_label), stringResource(R.string.publish_hitos, milestones))
                    SummaryLine(stringResource(R.string.publish_published_label), published)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuSoftGreen, CardShape)
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.publish_gps_valid),
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MuyuForest,
                        ),
                    )
                    if (coords != null) {
                        Text(
                            text = if (accuracy != null) {
                                stringResource(R.string.publish_gps_detail, coords, accuracy)
                            } else {
                                coords
                            },
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Muted,
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(InfoBg)
                        .padding(14.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.publish_available_unfunded),
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFC56A16),
                            ),
                        )
                        Text(
                            text = stringResource(R.string.publish_available_unfunded_body),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF6B4A16),
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(ButtonShape)
                        .background(MuyuOlive)
                        .clickable(onClick = onViewPlot),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.publish_view_plot),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(ButtonShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, ButtonShape)
                        .clickable(onClick = onHome),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.publish_go_home),
                        color = MuyuForest,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
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
internal fun FarmerContextHeader(
    farmerName: String,
    title: String,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MuyuHeader)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.publish_farmer_role, farmerName),
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f),
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onBack),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuyuAsset(
                fileName = "publish/back.svg",
                contentDescription = stringResource(R.string.plot_back),
                width = 17.dp,
                height = 19.dp,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .rotate(-90f),
            )
            Text(
                text = title,
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

@Composable
internal fun StatusAvailable() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("✓", color = MuyuPlanBadgeText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = stringResource(R.string.merchant_available),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MuyuPlanBadgeText,
            ),
        )
    }
}

@Composable
internal fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = UrbanistRegular,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Muted,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PublishSuccessScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            PublishSuccessScreen(onViewPlot = {}, onHome = {}, onTabSelected = {})
        }
    }
}
