package com.example.muyu_app.features.user_management.presentation.profile

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuScrollBottomInset
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.core.image.MuyuAsset
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.MuyuappTheme

private enum class MerchantAlertChannel { Call, Push, WhatsApp }

private val CardShape = RoundedCornerShape(20.dp)
private val LightGreenBg = Color(0xFFDCF0DD)
private val DarkGreenText = Color(0xFF2E6930)
private val RedLogout = Color(0xFFC63939)

@Composable
fun MerchantProfileScreen(
    onTabSelected: (MerchantTab) -> Unit,
    @DrawableRes avatarRes: Int? = R.drawable.profilelacosecha, // Imagen por defecto de la empresa
    onChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {},
    onTerms: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var notificationPref by remember { mutableStateOf(MerchantAlertChannel.Call) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header verde superior de perfil Merchant con statusBarsPadding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuOlive)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = "Distribuidora La Cosecha",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White),
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
                            text = "Distribuidora La\nCosecha",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 26.sp,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(
                                R.string.profile_role_place,
                                stringResource(R.string.role_merchant_short),
                                stringResource(R.string.profile_merchant_place),
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
                                text = stringResource(R.string.profile_verified),
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
                    .padding(top = 16.dp, bottom = MuyuScrollBottomInset),
            ) {
                // CONTACT DETAILS
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
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Distribuidora La Cosecha SAC",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(
                                    R.string.profile_ruc_line,
                                    stringResource(R.string.profile_wholesale),
                                    "20123456789",
                                ),
                                fontSize = 13.sp,
                                color = Color.Gray,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Lima · contacto@lacosecha.pe",
                                fontSize = 13.sp,
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

                // NOTIFICATION PREFERENCES
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
                                isSelected = notificationPref == MerchantAlertChannel.Call,
                                onClick = { notificationPref = MerchantAlertChannel.Call },
                                modifier = Modifier.weight(1f),
                            )
                            OptionChip(
                                text = stringResource(R.string.profile_channel_push),
                                isSelected = notificationPref == MerchantAlertChannel.Push,
                                onClick = { notificationPref = MerchantAlertChannel.Push },
                                modifier = Modifier.weight(1f),
                            )
                            OptionChip(
                                text = stringResource(R.string.profile_channel_whatsapp),
                                isSelected = notificationPref == MerchantAlertChannel.WhatsApp,
                                onClick = { notificationPref = MerchantAlertChannel.WhatsApp },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // New plot alerts Card
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
                            text = stringResource(R.string.profile_new_plot_alerts),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Potatoes · Cusco · from 5,000 kg",
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PAYMENT
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
                            text = stringResource(R.string.profile_saved_methods),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.profile_visa_bcp),
                            fontSize = 14.sp,
                            color = Color.Gray,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECURITY & HELP
                SectionTitle(stringResource(R.string.profile_security_help))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Color.White)
                        .border(1.dp, DarkGreenText.copy(alpha = 0.6f), CardShape)
                        .clickable { onChangePassword() }
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_change_password),
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

                Spacer(modifier = Modifier.height(20.dp))

                // LEGAL
                SectionTitle(stringResource(R.string.profile_legal))
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

                // Botón Cierre de Sesión (Log out)
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

        MerchantBottomBar(
            selectedTab = MerchantTab.Profile,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
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

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun MerchantProfileScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        MerchantProfileScreen(onTabSelected = {})
    }
}
