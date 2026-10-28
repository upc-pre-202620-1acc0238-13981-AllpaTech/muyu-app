package com.example.muyu_app.features.user_management.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.text.TextStyle
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
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuCheckbox
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuHeader
import com.example.muyu_app.ui.theme.MuyuLime
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBlack
import com.example.muyu_app.ui.theme.UrbanistLight
import com.example.muyu_app.ui.theme.UrbanistRegular
import com.example.muyu_app.ui.theme.UrbanistSemiBold

private enum class MerchantAlertChannel { Call, Push, WhatsApp }

private val CardShape = RoundedCornerShape(16.dp)
private val LegalCardShape = RoundedCornerShape(20.dp)
private val ChannelShape = RoundedCornerShape(10.dp)
private val LogoutShape = RoundedCornerShape(28.dp)
private val VerifiedShape = RoundedCornerShape(12.dp)
private val MutedText = Color.Black.copy(alpha = 0.5f)
private val SectionColor = Color.Black.copy(alpha = 0.6f)
private val VerifiedChipBg = Color(0xFFFEF7FF)
private val VerifiedChipText = Color(0xFF4F378A)
private val LogoutRed = Color(0xFFB84444)

private val TitleStyle = TextStyle(
    fontFamily = UrbanistSemiBold,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    color = Color.Black,
)
private val BodyStyle = TextStyle(
    fontFamily = UrbanistLight,
    fontWeight = FontWeight.Light,
    fontSize = 16.sp,
    color = MutedText,
)

@Composable
fun MerchantProfileScreen(
    onTabSelected: (MerchantTab) -> Unit,
    @DrawableRes avatarRes: Int? = R.drawable.profilelacosecha,
    onChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {},
    onTerms: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var notificationPref by remember { mutableStateOf(MerchantAlertChannel.Call) }
    val companyName = stringResource(R.string.merchant_company_name)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MuyuNavBar)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = companyName,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD9D9D9)),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD9D9D9)),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .height(23.dp)
                            .clip(VerifiedShape)
                            .background(VerifiedChipBg)
                            .padding(horizontal = 11.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_verified),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 14.sp,
                                color = VerifiedChipText,
                            ),
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = companyName,
                        style = TextStyle(
                            fontFamily = UrbanistBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = Color.White,
                            lineHeight = 28.sp,
                        ),
                    )
                    Text(
                        text = stringResource(
                            R.string.profile_role_place,
                            stringResource(R.string.role_merchant_short),
                            stringResource(R.string.profile_merchant_place),
                        ),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 16.sp,
                            color = Color.White,
                        ),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 21.dp)
                    .padding(top = 26.dp, bottom = MuyuScrollBottomInset),
            ) {
                SectionTitle(stringResource(R.string.profile_contact_details))
                ProfileCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.profile_company_legal_name),
                                style = TitleStyle,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(
                                    R.string.profile_ruc_line,
                                    stringResource(R.string.profile_wholesale),
                                    "20123456789",
                                ),
                                style = BodyStyle,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.profile_company_location_email),
                                style = BodyStyle,
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
                ProfileCard {
                    Text(
                        text = stringResource(R.string.profile_weather_alerts_question),
                        style = BodyStyle,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
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

                Spacer(modifier = Modifier.height(14.dp))

                ProfileCard {
                    Text(
                        text = stringResource(R.string.profile_new_plot_alerts),
                        style = TitleStyle,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.profile_new_plot_alerts_body),
                        style = BodyStyle,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_payment))
                ProfileCard {
                    Text(
                        text = stringResource(R.string.profile_saved_methods),
                        style = TitleStyle,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.profile_visa_bcp),
                        style = BodyStyle,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_security_help))
                ProfileCard(
                    modifier = Modifier.clickable { onChangePassword() },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_change_password),
                            style = BodyStyle,
                        )
                        MuyuAsset(
                            fileName = "publish/chevron.svg",
                            contentDescription = null,
                            width = 8.dp,
                            height = 14.dp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(stringResource(R.string.profile_legal))
                ProfileCard(
                    shape = LegalCardShape,
                    borderColor = MuyuForest,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(R.string.profile_terms),
                            modifier = Modifier.clickable { onTerms() },
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 16.sp,
                                color = MuyuForest,
                            ),
                        )
                        Text(
                            text = stringResource(R.string.profile_privacy),
                            style = TextStyle(
                                fontFamily = UrbanistRegular,
                                fontSize = 16.sp,
                                color = MuyuForest,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(LogoutShape)
                        .background(LogoutRed)
                        .clickable { onLogout() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.profile_logout),
                        style = TextStyle(
                            fontFamily = UrbanistRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                        ),
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
        style = TextStyle(
            fontFamily = UrbanistBlack,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = SectionColor,
        ),
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CardShape,
    borderColor: Color = MuyuLime,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, borderColor, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        content = content,
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
            .height(36.dp)
            .clip(ChannelShape)
            .background(if (isSelected) MuyuHeader else MuyuCheckbox)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = UrbanistSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isSelected) Color(0xFFFFFEFE) else Color.Black,
            ),
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
