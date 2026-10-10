package com.example.muyu_app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuStepActive
import com.example.muyu_app.ui.theme.MuyuStepIdle
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistBold

@Composable
fun PublishStepper(
    currentStep: Int,
    section: String,
    stepLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            contentAlignment = Alignment.Center,
        ) {
            MuyuAsset(
                fileName = "publish/step_line.svg",
                contentDescription = null,
                width = 330.dp,
                height = 4.dp,
            )
            if (currentStep == 1) {
                MuyuAsset(
                    fileName = "publish/step_line_active.svg",
                    contentDescription = null,
                    width = 74.dp,
                    height = 4.dp,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            if (currentStep >= 2) {
                MuyuAsset(
                    fileName = "publish/step_line_done.svg",
                    contentDescription = null,
                    width = 135.dp,
                    height = 4.dp,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }
            if (currentStep >= 3) {
                MuyuAsset(
                    fileName = "publish/step_line_done.svg",
                    contentDescription = null,
                    width = 135.dp,
                    height = 4.dp,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepDot(index = 1, currentStep = currentStep)
                StepDot(index = 2, currentStep = currentStep)
                StepDot(index = 3, currentStep = currentStep)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = section,
                style = TextStyle(
                    fontFamily = UrbanistBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                ),
            )
            Text(
                text = stepLabel,
                style = TextStyle(
                    fontFamily = UrbanistBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                ),
            )
        }
    }
}

@Composable
private fun StepDot(
    index: Int,
    currentStep: Int,
) {
    val completed = index < currentStep
    val active = index <= currentStep
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (active) MuyuStepActive else MuyuStepIdle),
        contentAlignment = Alignment.Center,
    ) {
        if (completed) {
            MuyuAsset(
                fileName = "publish/check.svg",
                contentDescription = null,
                width = 19.dp,
                height = 14.dp,
            )
        } else {
            Text(
                text = index.toString(),
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (active) Color(0xFFFFFEFE) else MuyuForest,
                ),
            )
        }
    }
}
