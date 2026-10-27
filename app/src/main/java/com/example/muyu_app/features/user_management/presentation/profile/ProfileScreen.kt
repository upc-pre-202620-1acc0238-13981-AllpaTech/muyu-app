package com.example.muyu_app.features.user_management.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private enum class FarmerAlertChannel { Call, Push, WhatsApp }

private val CardShape = RoundedCornerShape(20.dp)
private val LightGreenBg = Color(0xFFDCF0DD)
private val DarkGreenText = Color(0xFF2E6930)
private val RedLogout = Color(0xFFC63939)

@Composable
fun ProfileScreen(
    onTabSelected: (MuyuTab) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes avatarRes: Int? = R.drawable.merchant_maria,
    onChangePassword: () -> Unit = {},
    onChangePin: () -> Unit = {},
    onCallSupport: () -> Unit = {},
    onReportIncidence: () -> Unit = {},
    onRateContract: () -> Unit = {},
    onTerms: () -> Unit = {},
    onLogout: () -> Unit = {},
    bottomBar: (@Composable BoxScope.() -> Unit)? = null,
) {
    val container = LocalAppContainer.current
    val currentUser = container.fakeStore.currentUser

    val farmerName = currentUser?.displayName.orEmpty()
    val farmerEmail = currentUser?.email.orEmpty()

    var plots by remember { mutableStateOf<List<Plot>>(emptyList()) }
    var notificationPref by remember { mutableStateOf(FarmerAlertChannel.Call) }
    var syncWifiOnly by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        container.getPlotsUseCase().onSuccess { all ->
            plots = all.filter { it.ownerEmail.equals(farmerEmail, ignoreCase = true) }
        }
    }

    val plotCount = plots.size
    val milestoneCount = plots.count { it.status == "approved" || it.status == "funded" }
    val activeCropCount = plots
        .filter { it.status != "pending" }
        .map { it.crop }
        .distinct()
        .size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuOlive)
                    .statusBarsPadding()
                    .padding(horizontal = 27.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = farmerName,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = farmerName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(
                                R.string.profile_role_place,
                                stringResource(R.string.role_farmer_short),
                                stringResource(R.string.profile_farmer_place),
                            ),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(LightGreenBg)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.merchant_verified),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreenText,
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = MuyuScrollBottomInset),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MetricCard(number = plotCount.toString(), label = stringResource(R.string.farmer_stat_plots), modifier = Modifier.weight(1f))
                    MetricCard(number = milestoneCount.toString(), label = stringResource(R.string.farmer_stat_milestones), modifier = Modifier.weight(1f))
                    MetricCard(number = activeCropCount.toString(), label = stringResource(R.string.farmer_stat_crop), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_contact_details))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "+51 987 654 321",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = farmerEmail,
                                fontSize = 14.sp,
                                color = Color.Gray,
                            )
                        }
                        MuyuAsset(
                            fileName = "publish/edit.svg",
                            contentDescription = stringResource(R.string.profile_edit_contact),
                            width = 20.dp,
                            height = 20.dp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_notification_prefs))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.profile_weather_alerts_question),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OptionChip(
                                text = stringResource(R.string.profile_channel_call),
                                isSelected = notificationPref == FarmerAlertChannel.Call,
                                onClick = { notificationPref = FarmerAlertChannel.Call },
                                modifier = Modifier.weight(1f),
                            )
                            OptionChip(
                                text = stringResource(R.string.profile_channel_push),
                                isSelected = notificationPref == FarmerAlertChannel.Push,
                                onClick = { notificationPref = FarmerAlertChannel.Push },
                                modifier = Modifier.weight(1f),
                            )
                            OptionChip(
                                text = stringResource(R.string.profile_channel_whatsapp),
                                isSelected = notificationPref == FarmerAlertChannel.WhatsApp,
                                onClick = { notificationPref = FarmerAlertChannel.WhatsApp },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_sync))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_sync_wifi),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (syncWifiOnly) DarkGreenText else Color.LightGray)
                                .clickable { syncWifiOnly = !syncWifiOnly }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = stringResource(if (syncWifiOnly) R.string.action_yes else R.string.action_no),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_payment))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.profile_bank_account),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.profile_bank_account_body),
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Column {
                        NavigationRowItem(
                            text = stringResource(R.string.profile_change_password),
                            onClick = onChangePassword,
                        )
                        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                        NavigationRowItem(
                            text = stringResource(R.string.profile_change_pin),
                            onClick = onChangePin,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedActionButton(
                    text = stringResource(R.string.profile_call_support),
                    onClick = onCallSupport,
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedActionButton(
                    text = stringResource(R.string.profile_report_issue),
                    onClick = onReportIncidence,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRateContract() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.profile_rate_contract),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreenText,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .padding(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(R.string.profile_terms),
                            modifier = Modifier.clickable { onTerms() },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                        Text(
                            text = stringResource(R.string.profile_privacy),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(CardShape)
                        .background(RedLogout)
                        .clickable { onLogout() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.profile_logout),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }

        if (bottomBar != null) {
            bottomBar()
        } else {
            MuyuBottomBar(
                selectedTab = MuyuTab.Profile,
                onTabSelected = onTabSelected,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun MetricCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .clip(CardShape)
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = number,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF616161),
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun OptionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) DarkGreenText else Color(0xFFE0E0E0))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
        )
    }
}

@Composable
private fun NavigationRowItem(
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
        )
        MuyuAsset(
            fileName = "publish/chevron.svg",
            contentDescription = null,
            width = 12.dp,
            height = 12.dp,
        )
    }
}

@Composable
private fun OutlinedActionButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, DarkGreenText, CardShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreenText,
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun ProfileScreenPreview() {
    InstallMuyuImageLoader()
    CompositionLocalProvider(LocalAppContainer provides AppContainer()) {
        MuyuappTheme {
            ProfileScreen(onTabSelected = {})
        }
    }
}
