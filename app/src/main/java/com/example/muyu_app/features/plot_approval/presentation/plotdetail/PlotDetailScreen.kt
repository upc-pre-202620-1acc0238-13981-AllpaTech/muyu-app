package com.example.muyu_app.features.plot_approval.presentation.plotdetail

import com.example.muyu_app.ui.theme.MuyuNavBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuCalendarBg
import com.example.muyu_app.ui.theme.MuyuFinancedBg
import com.example.muyu_app.ui.theme.MuyuFinancedText
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuMutedChip
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.MuyuSurface
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistExtraBold
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(20.dp)

@Composable
fun PlotDetailScreen(
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuSurface),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlotHeader()
            Image(
                painter = painterResource(R.drawable.plot_hero),
                contentDescription = stringResource(R.string.plot_hero_content_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(239.dp),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 19.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.plot_title),
                        style = TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 25.sp,
                            color = Color.Black,
                        ),
                    )
                    StatusChip(
                        text = stringResource(R.string.plot_status_financed),
                        background = MuyuFinancedBg,
                        contentColor = MuyuFinancedText,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.plot_meta),
                    style = TextStyle(
                        fontFamily = UrbanistRegular,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black.copy(alpha = 0.5f),
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                MilestonesCard()
                Spacer(modifier = Modifier.height(16.dp))
                VisitCard()
                Spacer(modifier = Modifier.height(16.dp))
                PlotActionButton(
                    text = stringResource(R.string.plot_record_evidence),
                    background = MuyuAction,
                    contentColor = Color.White,
                    extraBold = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                PlotActionButton(
                    text = stringResource(R.string.plot_request_extension),
                    background = Color.White,
                    contentColor = MuyuOlive,
                    bordered = true,
                )
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
private fun PlotHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Unifica el encabezado de la parcela con el verde oscuro de la navegación.
            .background(MuyuNavBar)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 30.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MuyuAsset(
                fileName = "plot/back.svg",
                contentDescription = stringResource(R.string.plot_back),
                width = 18.dp,
                height = 19.dp,
                modifier = Modifier.offset(y = 1.dp),
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = stringResource(R.string.plot_header),
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
private fun MilestonesCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 12.dp, vertical = 14.dp),
    ) {
        Text(
            text = stringResource(R.string.plot_milestones),
            style = TextStyle(
                fontFamily = UrbanistExtraBold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.6f),
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        MilestoneRow(
            number = "1",
            title = stringResource(R.string.plot_sowing),
            date = stringResource(R.string.plot_date_sowing),
            chipText = stringResource(R.string.plot_approved),
            chipBackground = MuyuApprovedBg,
            chipColor = MuyuForest,
        )
        HorizontalDivider(color = Color.Black.copy(alpha = 0.12f))
        MilestoneRow(
            number = "2",
            title = stringResource(R.string.plot_irrigation),
            date = stringResource(R.string.plot_date_irrigation),
            chipText = stringResource(R.string.plot_approved),
            chipBackground = MuyuApprovedBg,
            chipColor = MuyuForest,
        )
        HorizontalDivider(color = Color.Black.copy(alpha = 0.12f))
        MilestoneRow(
            number = "3",
            title = stringResource(R.string.plot_fertilization),
            date = stringResource(R.string.plot_date_today),
            chipText = stringResource(R.string.plot_earring),
            chipBackground = MuyuPendingBg,
            chipColor = MuyuPendingText,
        )
        HorizontalDivider(color = Color.Black.copy(alpha = 0.12f))
        MilestoneRow(
            number = "4",
            title = stringResource(R.string.plot_harvest),
            date = "—",
            chipText = stringResource(R.string.plot_earring),
            chipBackground = MuyuMutedChip,
            chipColor = Color.Black.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun MilestoneRow(
    number: String,
    title: String,
    date: String,
    chipText: String,
    chipBackground: Color,
    chipColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.Center) {
            MuyuAsset(
                fileName = "plot/milestone.svg",
                contentDescription = null,
                width = 40.dp,
                height = 40.dp,
            )
            Text(
                text = number,
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MuyuForest,
                ),
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
            ),
        )
        Text(
            text = date,
            style = TextStyle(
                fontFamily = UrbanistLight,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.5f),
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatusChip(text = chipText, background = chipBackground, contentColor = chipColor)
    }
}

@Composable
private fun VisitCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CardShape)
                .background(MuyuCalendarBg),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.plot_calendar),
                contentDescription = null,
                modifier = Modifier
                    .width(47.dp)
                    .height(44.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(R.string.plot_visit_title),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.plot_visit_body),
                style = TextStyle(
                    fontFamily = UrbanistRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                ),
            )
        }
    }
}

@Composable
private fun PlotActionButton(
    text: String,
    background: Color,
    contentColor: Color,
    extraBold: Boolean = false,
    bordered: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(CardShape)
            .background(background)
            .then(
                if (bordered) Modifier.border(1.dp, MuyuForest, CardShape) else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = if (extraBold) UrbanistExtraBold else UrbanistBold,
                fontWeight = if (extraBold) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 20.sp,
                color = contentColor,
            ),
        )
    }
}

@Composable
private fun StatusChip(
    text: String,
    background: Color,
    contentColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(ChipShape)
            .background(background)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = contentColor,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PlotDetailScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        PlotDetailScreen(onTabSelected = {})
    }
}
