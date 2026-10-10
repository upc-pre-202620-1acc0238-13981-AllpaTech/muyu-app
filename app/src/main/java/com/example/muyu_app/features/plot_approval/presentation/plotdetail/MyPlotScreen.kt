package com.example.muyu_app.features.plot_approval.presentation.plotdetail

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.presentation.publish.FarmerContextHeader
import com.example.muyu_app.features.plot_approval.presentation.publish.StatusAvailable
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
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
fun MyPlotScreen(
    onBack: () -> Unit,
    onEditPlot: () -> Unit,
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
    val harvest = plot?.harvestDate.orEmpty()
    val amountValue = PlotFinance.parseAmount(plot?.amount.orEmpty())
    val amount = PlotFinance.formatAmount(amountValue)
    val published = plot?.publishedAt?.ifBlank { null } ?: PlotFinance.publishedToday()
    val steps = PlotFinance.disbursement(amountValue)
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
                title = stringResource(R.string.publish_my_plot_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Text(
                    text = stringResource(R.string.publish_published_on, published),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFFC56A16),
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuSoftGreen, CardShape),
                ) {
                    if (!plot?.imageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = plot?.imageUri,
                            contentDescription = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.plot_hero),
                            contentDescription = name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = name,
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black,
                            ),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusAvailable()
                        Spacer(modifier = Modifier.height(4.dp))
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
                        text = stringResource(R.string.publish_requested_plan, amount),
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    steps.forEach { step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${stringResource(step.titleRes)} · ${"%.2f".format(step.percent)} %",
                                style = TextStyle(
                                    fontFamily = UrbanistRegular,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Muted,
                                ),
                            )
                            Text(
                                text = PlotFinance.formatAmount(step.amount),
                                style = TextStyle(
                                    fontFamily = UrbanistSemiBold,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                ),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.publish_plan_total, harvest.ifBlank { "—" }),
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
                            text = stringResource(R.string.publish_seeking_funding),
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFC56A16),
                            ),
                        )
                        Text(
                            text = stringResource(R.string.publish_seeking_funding_body, farmer),
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
                        .clickable(onClick = onEditPlot),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.publish_edit_plot),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.publish_view_visits),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MuyuForest,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        MuyuBottomBar(
            selectedTab = MuyuTab.Plot,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MyPlotScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            MyPlotScreen(onBack = {}, onEditPlot = {}, onTabSelected = {})
        }
    }
}
