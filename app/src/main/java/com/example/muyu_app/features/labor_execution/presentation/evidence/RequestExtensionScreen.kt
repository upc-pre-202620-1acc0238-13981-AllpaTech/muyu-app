package com.example.muyu_app.features.labor_execution.presentation.evidence

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.muyu_app.core.plot.refreshSelectedPlot
import com.example.muyu_app.features.plot_approval.domain.currentMilestone
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CardShape = RoundedCornerShape(20.dp)
private val FieldShape = RoundedCornerShape(14.dp)
private val LightGreenBg = Color(0xFFEDF8EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestExtensionScreen(
    onBack: () -> Unit,
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val plot = remember { container.liveFundedPlot() }
    val current = plot?.currentMilestone()
    val definition = MilestoneCatalog.definition(current?.key ?: container.selectedMilestoneKey)
    val milestoneTitle = stringResource(definition.titleRes)
    val currentDeadline = current?.dueLabel?.ifBlank { null } ?: "03 Oct 2026"
    var newDate by remember { mutableStateOf(current?.extensionDate.orEmpty()) }
    var reason by remember { mutableStateOf(current?.extensionReason.orEmpty()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val canSend = newDate.isNotBlank() && reason.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GreenTitleHeader(
                title = stringResource(R.string.extension_title),
                onBack = onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                // Tarjeta Superior con Información de Hito y Fecha Límite Actual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, MuyuForest, CardShape)
                        .padding(18.dp),
                ) {
                    Column {
                        Text(
                            text = "$milestoneTitle · ${plot?.name ?: "El Rosal Plot"}",
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black,
                            ),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Current deadline: $currentDeadline",
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 15.sp,
                                color = Color.Gray,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo: Fecha Propuesta
                FieldLabel("Proposed new date")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(FieldShape)
                        .background(LightGreenBg)
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = newDate.ifBlank { "dd/mm/yy" },
                            style = TextStyle(
                                fontFamily = UrbanistBold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black,
                            ),
                        )
                        MuyuAsset(
                            fileName = "publish/calendar.svg",
                            contentDescription = null,
                            width = 20.dp,
                            height = 20.dp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo: Motivo del Retraso
                FieldLabel("Reason for delay *")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(CardShape)
                        .background(LightGreenBg)
                        .padding(16.dp),
                ) {
                    BasicTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        textStyle = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 15.sp,
                            color = Color.Black,
                        ),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { inner ->
                            if (reason.isBlank()) {
                                Text(
                                    text = "E.g. Rain prevented safe fertilizer application",
                                    style = TextStyle(
                                        fontFamily = UrbanistRegular,
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                    ),
                                )
                            }
                            inner()
                        },
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Botón Enviar Solicitud
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(if (canSend) MuyuForest else MuyuForest.copy(alpha = 0.5f))
                        .clickable(enabled = canSend) {
                            val id = plot?.id ?: return@clickable
                            val key = current?.key ?: container.selectedMilestoneKey
                            container.fakeStore.requestExtension(id, key, newDate, reason)
                            container.refreshSelectedPlot()
                            onBack()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Send request",
                        style = TextStyle(
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
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

    if (showDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            newDate = SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)
                                .format(Date(millis))
                        }
                        showDatePicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 8.dp),
        style = TextStyle(
            fontFamily = UrbanistBold,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.Black,
        ),
    )
}