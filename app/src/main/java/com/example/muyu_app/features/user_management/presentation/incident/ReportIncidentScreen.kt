package com.example.muyu_app.features.user_management.presentation.incident

import com.example.muyu_app.ui.theme.MuyuBackground
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.di.LocalAppContainer

private val IncidentShape = RoundedCornerShape(14.dp)
private val IncidentGreen = Color(0xFF2E7D32)

private data class IncidentTypeOption(val key: String, val label: Int)

@Composable
fun ReportIncidentScreen(
    onBack: () -> Unit,
    onAttachPhoto: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val draft = LocalAppContainer.current.incidentDraft
    var selectedType by rememberSaveable { mutableStateOf(draft.type) }
    var description by rememberSaveable { mutableStateOf(draft.description) }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        IncidentTypeOption(IncidentDraft.TYPE_TECHNICAL, R.string.report_type_technical),
        IncidentTypeOption(IncidentDraft.TYPE_PAYMENT, R.string.report_type_payment),
        IncidentTypeOption(IncidentDraft.TYPE_PLOT, R.string.report_type_plot),
        IncidentTypeOption(IncidentDraft.TYPE_OTHER, R.string.report_type_other),
    )
    val selectedLabel = stringResource(options.first { it.key == selectedType }.label)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        GreenTitleHeader(stringResource(R.string.report_title), onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(IncidentShape)
                    .background(Color(0xFFFFF3DF))
                    .padding(14.dp),
            ) {
                Text(
                    text = stringResource(R.string.report_no_connection),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9A5A00),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.report_offline_body),
                    fontSize = 14.sp,
                    color = Color(0xFF6F4A16),
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.report_type_label), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(IncidentShape)
                        .background(Color(0xFFE7F5E8))
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(selectedLabel)
                    Text("⌄", color = IncidentGreen, fontWeight = FontWeight.Bold)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f),
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(stringResource(option.label)) },
                            onClick = {
                                selectedType = option.key
                                draft.type = option.key
                                expanded = false
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.report_description_label), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    draft.description = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                placeholder = { Text(stringResource(R.string.report_description_hint)) },
                shape = IncidentShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IncidentGreen,
                    unfocusedBorderColor = Color(0xFFA8C9AA),
                    focusedContainerColor = Color(0xFFE7F5E8),
                    unfocusedContainerColor = Color(0xFFE7F5E8),
                ),
            )

            Spacer(Modifier.height(16.dp))
            IncidentButton(
                text = if (draft.photoUri.isNullOrBlank()) {
                    stringResource(R.string.report_attach_photo)
                } else {
                    stringResource(R.string.report_photo_attached)
                },
                filled = false,
                onClick = {
                    draft.type = selectedType
                    draft.description = description
                    onAttachPhoto()
                },
            )
            Spacer(Modifier.height(12.dp))
            IncidentButton(
                text = stringResource(R.string.report_send),
                filled = true,
                enabled = description.isNotBlank(),
                onClick = {
                    draft.type = selectedType
                    draft.description = description.trim()
                    onSubmitted()
                },
            )
        }
    }
}

@Composable
private fun IncidentButton(
    text: String,
    filled: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val background = when {
        !enabled -> Color(0xFFB8C7B9)
        filled -> IncidentGreen
        else -> Color.White
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(IncidentShape)
            .background(background)
            .border(1.dp, IncidentGreen, IncidentShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = if (filled) Color.White else IncidentGreen,
        )
    }
}
