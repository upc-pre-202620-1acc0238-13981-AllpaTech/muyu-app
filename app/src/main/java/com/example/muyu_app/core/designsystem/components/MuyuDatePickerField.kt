package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.MuyuIconSize
import com.example.muyu_app.ui.theme.MuyuField
import com.example.muyu_app.ui.theme.UrbanistSemiBold
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuyuDatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    containerColor: Color = Color(0xFFEAF9E9),
    labelColor: Color = Color(0xFF213D09),
    authStyle: Boolean = false,
) {
    var showPicker by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val locale = remember(configuration) {
        configuration.locales[0] ?: Locale.getDefault()
    }
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    }

    Column(modifier = modifier) {
        if (authStyle) {
            AuthLabel(text = label)
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = labelColor,
            )
        }
        Spacer(modifier = Modifier.height(if (authStyle) 6.dp else 8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            color = if (authStyle) MuyuField else containerColor,
            shape = if (authStyle) AuthFieldShape else RoundedCornerShape(16.dp),
            border = if (authStyle) BorderStroke(1.dp, Color.Black) else null,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = if (authStyle) 18.dp else 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedDate?.format(dateFormatter) ?: stringResource(R.string.date_select),
                    style = if (authStyle) {
                        TextStyle(
                            fontFamily = UrbanistSemiBold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color.Black,
                        )
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.date_select),
                    modifier = Modifier.size(MuyuIconSize.Form),
                    tint = if (authStyle) Color.Black else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }

    if (showPicker) {
        val validSelectedDate = selectedDate
            ?.takeUnless {
                (minDate != null && it.isBefore(minDate)) ||
                    (maxDate != null && it.isAfter(maxDate))
            }
        val displayedDate = validSelectedDate ?: minDate ?: maxDate
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = validSelectedDate
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli(),
            initialDisplayedMonthMillis = displayedDate
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableYear(year: Int): Boolean =
                    (minDate == null || year >= minDate.year) &&
                        (maxDate == null || year <= maxDate.year)

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    return (minDate == null || !date.isBefore(minDate)) &&
                        (maxDate == null || !date.isAfter(maxDate))
                }
            },
        )

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            if ((minDate == null || !date.isBefore(minDate)) &&
                                (maxDate == null || !date.isAfter(maxDate))
                            ) {
                                onDateSelected(date)
                            }
                        }
                        showPicker = false
                    },
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
