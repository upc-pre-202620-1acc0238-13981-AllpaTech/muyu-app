package com.example.muyu_app.features.user_management.presentation.profile

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.MuyuPageGray
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular

private val YapeShape = RoundedCornerShape(14.dp)
private val YapeGreen = Color(0xFF2E7D32)
private val YapeBorder = Color(0xFF6F9B61)
private val YapeDanger = Color(0xFFC93434)

@Composable
fun EditYapeScreen(
    onBack: () -> Unit,
    onSave: (YapeAccount) -> Unit,
    onUnlink: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = LocalAppContainer.current
    val account = container.yapeAccount
    val currentUser = container.fakeStore.currentUser
    val holderName = account?.holderName
        ?.takeIf(String::isNotBlank)
        ?: currentUser?.displayName?.takeIf(String::isNotBlank)
        ?: stringResource(R.string.yape_holder_fallback)
    var phoneDigits by rememberSaveable(account?.phone) {
        mutableStateOf(yapePhoneDigits(account?.phone ?: currentUser?.phone.orEmpty()))
    }
    var verified by rememberSaveable(account?.phone) {
        mutableStateOf(account?.isVerified == true && isValidYapePhone(phoneDigits))
    }
    var verificationAttempted by rememberSaveable { mutableStateOf(false) }
    var isPrimary by rememberSaveable(account?.isPrimary) {
        mutableStateOf(account?.isPrimary ?: true)
    }
    val phoneValid = isValidYapePhone(phoneDigits)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        GreenTitleHeader(
            title = stringResource(R.string.yape_edit_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 28.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(YapeShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFD8DDD8), YapeShape)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.yape_logo),
                    contentDescription = stringResource(R.string.bank_account_yape),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.bank_account_yape),
                            fontFamily = UrbanistBold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF064E25),
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFE4F4DC))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = stringResource(
                                    if (account != null) R.string.yape_linked else R.string.yape_not_linked,
                                ),
                                fontFamily = UrbanistBold,
                                fontSize = 12.sp,
                                color = YapeGreen,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = maskYapePhone(phoneDigits).ifBlank { stringResource(R.string.yape_no_phone) },
                        fontFamily = UrbanistRegular,
                        fontSize = 17.sp,
                        color = Color(0xFF555555),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.yape_account_data),
                style = TextStyle(
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF155B35),
                ),
            )
            Spacer(Modifier.height(18.dp))

            YapeLabel(stringResource(R.string.yape_phone_label))
            OutlinedTextField(
                value = formatYapePhone(phoneDigits),
                onValueChange = { value ->
                    phoneDigits = yapePhoneDigits(value)
                    verified = false
                    verificationAttempted = false
                },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("+51 ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                isError = verificationAttempted && !phoneValid,
                supportingText = if (verificationAttempted && !phoneValid) {
                    { Text(stringResource(R.string.yape_phone_error)) }
                } else {
                    null
                },
                shape = YapeShape,
                colors = yapeFieldColors(),
            )

            Spacer(Modifier.height(12.dp))
            YapeOutlinedButton(
                text = stringResource(R.string.yape_verify_phone),
                onClick = {
                    verificationAttempted = true
                    verified = phoneValid
                },
            )

            if (verified) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(YapeShape)
                        .background(Color(0xFFEAF5DA))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = YapeGreen,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(
                        text = stringResource(R.string.yape_phone_verified),
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF234B22),
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            YapeLabel(stringResource(R.string.yape_holder_label))
            OutlinedTextField(
                value = holderName,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                shape = YapeShape,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = Color(0xFFF1F2F1),
                    disabledTextColor = Color(0xFF222222),
                    unfocusedContainerColor = Color(0xFFF1F2F1),
                    unfocusedBorderColor = Color(0xFFD0D5D0),
                ),
            )

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isPrimary = !isPrimary },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = isPrimary,
                    onCheckedChange = { isPrimary = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = YapeGreen,
                        uncheckedColor = YapeBorder,
                    ),
                )
                Text(
                    text = stringResource(R.string.yape_primary),
                    modifier = Modifier.weight(1f),
                    fontFamily = UrbanistRegular,
                    fontSize = 16.sp,
                )
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(YapeShape)
                    .background(Color(0xFFEAF5DA))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFF155B35),
                    modifier = Modifier.size(30.dp),
                )
                Text(
                    text = stringResource(R.string.yape_disbursement_info),
                    modifier = Modifier.weight(1f),
                    fontFamily = UrbanistRegular,
                    fontSize = 14.sp,
                    color = Color(0xFF273326),
                )
            }

            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(YapeShape)
                    .background(if (verified) MuyuOlive else Color(0xFFB8C7B9))
                    .clickable(enabled = verified) {
                        onSave(
                            YapeAccount(
                                phone = phoneDigits,
                                holderName = holderName,
                                isPrimary = isPrimary,
                                isVerified = true,
                            ),
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.yape_save_changes),
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White,
                )
            }

            if (account != null) {
                Spacer(Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.yape_unlink),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = onUnlink)
                        .padding(10.dp),
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = YapeDanger,
                )
            }
        }
    }
}

@Composable
private fun YapeLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 7.dp),
        fontFamily = UrbanistBold,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = Color(0xFF252525),
    )
}

@Composable
private fun YapeOutlinedButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(YapeShape)
            .background(Color.White)
            .border(1.5.dp, YapeGreen, YapeShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = UrbanistBold,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF155B35),
        )
    }
}

@Composable
private fun yapeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = YapeGreen,
    unfocusedBorderColor = YapeBorder,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
)
