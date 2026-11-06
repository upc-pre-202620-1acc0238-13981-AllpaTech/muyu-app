package com.example.muyu_app.features.climate_monitoring.presentation.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.MascotMood
import com.example.muyu_app.core.designsystem.components.MuyuMascot
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import com.example.muyu_app.features.plot_approval.domain.WeatherMeasure
import com.example.muyu_app.ui.theme.MuyuApprovedBg
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPendingBg
import com.example.muyu_app.ui.theme.MuyuPendingText
import com.example.muyu_app.ui.theme.MuyuTermsBg
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistMedium
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import kotlinx.coroutines.launch

private val CardShape = RoundedCornerShape(20.dp)
private val ChipShape = RoundedCornerShape(100.dp)
private val ButtonShape = RoundedCornerShape(15.dp)

/** Identifica la alerta del día para una parcela, para no mezclar alertas distintas. */
fun weatherAlertKey(plotId: Int, risk: WeatherRisk): String =
    "${plotId}_${PlotFinance.publishedToday()}_${risk.name}"

/**
 * US33: el agricultor marca las recomendaciones que aplicó ante una alerta activa.
 * Si sale sin confirmar, la alerta sigue pendiente de atención.
 */
@Composable
fun WeatherMeasuresCard(
    plot: Plot,
    risk: WeatherRisk,
    recommendationIds: List<Int>,
) {
    val context = LocalContext.current
    val container = LocalAppContainer.current
    val scope = rememberCoroutineScope()
    val alertKey = weatherAlertKey(plot.id, risk)
    // La clave guardada es el nombre del recurso para que se lea en cualquier idioma.
    val keyOf: (Int) -> String = { id -> context.resources.getResourceEntryName(id) }
    var attended by remember(alertKey) {
        mutableStateOf(
            container.fakeStore.plotById(plot.id)?.weatherMeasures?.find { it.alertKey == alertKey },
        )
    }
    var selected by rememberSaveable(alertKey) { mutableStateOf(emptyList<String>()) }
    var saving by remember { mutableStateOf(false) }

    val done = attended
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    if (done != null) R.string.weather_measures_attended else R.string.weather_measures_pending,
                ),
                modifier = Modifier
                    .clip(ChipShape)
                    .background(if (done != null) MuyuApprovedBg else MuyuPendingBg)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = TextStyle(
                    fontFamily = UrbanistSemiBold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = if (done != null) MuyuForest else MuyuPendingText,
                ),
            )
            Spacer(Modifier.weight(1f))
            if (done != null) MuyuMascot(mood = MascotMood.Success, size = 56.dp)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (done != null) {
                stringResource(R.string.weather_measures_attended_body, done.attendedAt)
            } else {
                stringResource(R.string.weather_measures_hint)
            },
            style = TextStyle(fontFamily = UrbanistRegular, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.65f)),
        )
        Spacer(Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            recommendationIds.forEach { id ->
                val key = keyOf(id)
                val checked = if (done != null) key in done.measures else key in selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = checked,
                            enabled = done == null,
                            role = Role.Checkbox,
                            onValueChange = { on -> selected = if (on) selected + key else selected - key },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = when {
                            done != null && checked -> Icons.Filled.CheckCircle
                            checked -> Icons.Filled.CheckBox
                            else -> Icons.Filled.CheckBoxOutlineBlank
                        },
                        contentDescription = null,
                        tint = if (checked) MuyuOlive else Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(id),
                        style = TextStyle(
                            fontFamily = UrbanistMedium,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = if (done != null && !checked) Color.Black.copy(alpha = 0.4f) else Color.Black,
                        ),
                    )
                }
            }
        }
        if (done == null) {
            Spacer(Modifier.height(16.dp))
            val enabled = selected.isNotEmpty() && !saving
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(ButtonShape)
                    .background(if (enabled) MuyuOlive else MuyuTermsBg)
                    .clickable(enabled = enabled, role = Role.Button) {
                        saving = true
                        scope.launch {
                            val measure = WeatherMeasure(
                                alertKey = alertKey,
                                measures = selected,
                                attendedAt = PlotFinance.timestampNow(),
                                attendedAtMillis = System.currentTimeMillis(),
                                attendedBy = container.currentUserEmail().orEmpty(),
                            )
                            container.fakeStore.recordWeatherMeasures(plot.id, measure)
                            container.fakeStore.plotById(plot.id)?.let { record ->
                                container.syncPlot(container.fakeStore.toDomain(record))
                            }
                            attended = measure
                            saving = false
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(
                        if (saving) R.string.harvest_saving else R.string.weather_measures_confirm,
                    ),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (enabled) Color.White else Color.Black.copy(alpha = 0.5f),
                    ),
                )
            }
        }
    }
}
