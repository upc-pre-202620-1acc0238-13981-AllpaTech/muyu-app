package com.example.muyu_app.features.plot_financing.presentation.publish

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.designsystem.components.MuyuBottomBar
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.core.designsystem.components.PublishStepper
import com.example.muyu_app.core.di.AppContainer
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.core.image.imageUriToPortableDataUrl
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.presentation.publish.cycleLabelToRes
import com.example.muyu_app.features.plot_approval.presentation.publish.draftAmountDisplay
import com.example.muyu_app.features.plot_approval.presentation.publish.publishFlowTitle
import com.example.muyu_app.ui.theme.MuyuCheckbox
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuPlanBadgeText
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch

private val InputShape = RoundedCornerShape(16.dp)
private val ItemCardShape = RoundedCornerShape(16.dp)
private val OuterContainerShape = RoundedCornerShape(20.dp)
private val CheckShape = RoundedCornerShape(8.dp)
private val ButtonShape = RoundedCornerShape(16.dp)

private val LightBlueBg = Color(0xFFE3F0F9)
private val DarkBlueText = Color(0xFF285C83)
private val CircleGreenBg = Color(0xFFC8E6C9)
private val CircleGreenText = Color(0xFF2E7D32)
private val BorderGreenLight = Color(0xFFC8E6C9)

data class MilestoneItem(
    val id: Int,
    val name: String,
    val amount: String
)

@Composable
fun PublishPlotFinancingScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    onPublished: () -> Unit = {},
    onUpdated: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val draft = container.plotDraft
    val scope = rememberCoroutineScope()
    val sessionKey = draft.editingPlotId ?: 0
    val isEditing = draft.isEditing

    var totalAmount by remember(sessionKey) { mutableStateOf(draftAmountDisplay(draft.amount)) }
    var selectedCycleRes by remember(sessionKey) { mutableStateOf(cycleLabelToRes(draft.cycle)) }
    val fixedMilestonesCount = draft.milestones.ifBlank { "4" }

    var expandedCycleDropdown by remember { mutableStateOf(false) }

    val cycleOptions = listOf(
        R.string.publish_cycle_3,
        R.string.publish_cycle_6,
        R.string.publish_cycle_9,
        R.string.publish_cycle_12,
    )
    val planting = stringResource(R.string.publish_plan_planting)
    val irrigation = stringResource(R.string.publish_plan_irrigation)
    val fertilization = stringResource(R.string.publish_plan_fertilization)
    val harvest = stringResource(R.string.publish_plan_harvest)

    val milestonesList = remember(totalAmount, planting, irrigation, fertilization, harvest) {
        val steps = PlotFinance.disbursement(PlotFinance.parseAmount(totalAmount))
        listOf(
            MilestoneItem(1, planting, PlotFinance.formatAmount(steps[0].amount)),
            MilestoneItem(2, irrigation, PlotFinance.formatAmount(steps[1].amount)),
            MilestoneItem(3, fertilization, PlotFinance.formatAmount(steps[2].amount)),
            MilestoneItem(4, harvest, PlotFinance.formatAmount(steps[3].amount)),
        )
    }

    var accepted by remember(sessionKey) { mutableStateOf(isEditing) }
    var publishing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = publishFlowTitle(draft),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 12.dp, bottom = MuyuScrollBottomInset),
            ) {
                PublishStepper(
                    currentStep = 3,
                    section = stringResource(R.string.publish_step_financing),
                    stepLabel = stringResource(R.string.publish_step_3),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Monto total del financiamiento
                Text(
                    text = stringResource(R.string.publish_total_financing),
                    style = TextStyle(
                        fontFamily = UrbanistSemiBold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = totalAmount,
                    onValueChange = { totalAmount = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = InputShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = MuyuOlive,
                        unfocusedBorderColor = MuyuOlive.copy(alpha = 0.6f)
                    ),
                    textStyle = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Selectores de Duración del ciclo e Hitos de pago
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duración del ciclo (Mantiene desplegable con ícono chevron)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.publish_cycle),
                            style = TextStyle(
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color.Black,
                            ),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box {
                            OutlinedTextField(
                                value = stringResource(selectedCycleRes),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedCycleDropdown = true },
                                enabled = false,
                                shape = InputShape,
                                trailingIcon = {
                                    MuyuAsset(
                                        fileName = "publish/chevron.svg",
                                        contentDescription = null,
                                        width = 14.dp,
                                        height = 14.dp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledContainerColor = Color.White,
                                    disabledBorderColor = MuyuOlive.copy(alpha = 0.6f),
                                    disabledTextColor = Color.Black
                                ),
                                textStyle = TextStyle(
                                    fontFamily = UrbanistRegular,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                            )
                            DropdownMenu(
                                expanded = expandedCycleDropdown,
                                onDismissRequest = { expandedCycleDropdown = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .border(1.dp, MuyuOlive.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                cycleOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(option),
                                                style = TextStyle(
                                                    fontFamily = UrbanistRegular,
                                                    fontSize = 15.sp,
                                                    color = Color.Black
                                                )
                                            )
                                        },
                                        onClick = {
                                            selectedCycleRes = option
                                            expandedCycleDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Hitos de pago (Texto fijo sin la flecha desplegable)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.publish_milestones),
                            style = TextStyle(
                                fontFamily = UrbanistSemiBold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color.Black,
                            ),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = fixedMilestonesCount,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            shape = InputShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledContainerColor = Color.White,
                                disabledBorderColor = MuyuOlive.copy(alpha = 0.6f),
                                disabledTextColor = Color.Black
                            ),
                            textStyle = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Plan de desembolsos
                Text(
                    text = stringResource(R.string.publish_plan),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Contenedor principal del plan de desembolsos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OuterContainerShape)
                        .background(Color.White)
                        .border(1.dp, BorderGreenLight, OuterContainerShape)
                        .padding(10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        milestonesList.forEach { milestone ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(ItemCardShape)
                                    .background(Color.White)
                                    .border(1.dp, BorderGreenLight, ItemCardShape)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(CircleGreenBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = milestone.id.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CircleGreenText
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = milestone.name,
                                            style = TextStyle(
                                                fontFamily = UrbanistBold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color.Black
                                            )
                                        )
                                    }

                                    Text(
                                        text = milestone.amount,
                                        style = TextStyle(
                                            fontFamily = UrbanistBold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Caja informativa celeste con ícono
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OuterContainerShape)
                        .background(LightBlueBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MuyuAsset(
                            fileName = "publish/shield_check.svg",
                            contentDescription = null,
                            width = 24.dp,
                            height = 24.dp
                        )
                        Text(
                            text = stringResource(R.string.publish_funds_held),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = DarkBlueText,
                                lineHeight = 18.sp
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox y texto de acuerdo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OuterContainerShape)
                        .background(Color.White)
                        .border(1.dp, MuyuOlive.copy(alpha = 0.6f), OuterContainerShape)
                        .clickable { accepted = !accepted }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CheckShape)
                                .background(if (accepted) MuyuPlanBadgeText else Color.White)
                                .border(1.dp, if (accepted) MuyuPlanBadgeText else MuyuCheckbox, CheckShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (accepted) {
                                Text(
                                    text = "✓",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.publish_accept_custody),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color.Black,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón Confirmar y publicar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(ButtonShape)
                        .background(if (accepted) MuyuOlive else Color(0xFFD9D9D9))
                        .clickable(enabled = accepted && !publishing) {
                            publishing = true
                            scope.launch {
                                draft.amount = totalAmount
                                draft.cycle = when (selectedCycleRes) {
                                    R.string.publish_cycle_3 -> "3 months"
                                    R.string.publish_cycle_9 -> "9 months"
                                    R.string.publish_cycle_12 -> "12 months"
                                    else -> "6 months"
                                }
                                draft.milestones = fixedMilestonesCount
                                draft.imageUri = imageUriToPortableDataUrl(context, draft.imageUri)
                                val payload = draft.toNewPlot()
                                val editId = draft.editingPlotId
                                if (editId != null) {
                                    container.updatePlotUseCase(editId, payload)
                                        .onSuccess { plot ->
                                            container.selectedPlot = plot
                                            draft.clear()
                                            onUpdated()
                                        }
                                        .onFailure { publishing = false }
                                } else {
                                    container.createPlotUseCase(payload)
                                        .onSuccess { plot ->
                                            container.selectedPlot = plot
                                            draft.clear()
                                            onPublished()
                                        }
                                        .onFailure { publishing = false }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(
                            if (isEditing) R.string.publish_save_changes else R.string.publish_confirm_short,
                        ),
                        color = if (accepted) Color.White else Color.Black.copy(alpha = 0.55f),
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

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun PublishPlotFinancingScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            PublishPlotFinancingScreen(onBack = {}, onTabSelected = {})
        }
    }
}
