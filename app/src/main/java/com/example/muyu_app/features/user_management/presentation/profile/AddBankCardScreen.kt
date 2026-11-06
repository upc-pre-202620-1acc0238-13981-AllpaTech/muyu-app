package com.example.muyu_app.features.user_management.presentation.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.ui.theme.MuyuOlive
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistRegular
import java.time.YearMonth

private val AddCardShape = RoundedCornerShape(14.dp)
private val AddCardGreen = Color(0xFF2E7D32)
private val AddCardBorder = Color(0xFF6F9B61)

@Composable
fun AddBankCardScreen(
    onBack: () -> Unit,
    onSave: (SavedBankCard) -> Unit,
    modifier: Modifier = Modifier,
) {
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var holderName by rememberSaveable { mutableStateOf("") }
    var expiration by rememberSaveable { mutableStateOf("") }
    var cvv by rememberSaveable { mutableStateOf("") }
    var cardTypeName by rememberSaveable { mutableStateOf(BankCardType.Debit.name) }
    var isPrimary by rememberSaveable { mutableStateOf(true) }
    val cardType = BankCardType.valueOf(cardTypeName)
    val brand = detectCardBrand(cardNumber)
    val digits = cardNumber.filter(Char::isDigit)
    val numberValid = passesLuhnCheck(digits) && brand != BankCardBrand.Unknown
    val expirationValid = validExpiration(expiration)
    val holderValid = holderName.trim().length >= 3
    val cvvValid = cvv.length == 3
    val canSave = numberValid && expirationValid && holderValid && cvvValid

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground),
    ) {
        GreenTitleHeader(
            title = stringResource(R.string.add_card_title),
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 22.dp, bottom = 24.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = null,
                    tint = Color(0xFF155B35),
                    modifier = Modifier.size(32.dp),
                )
                Text(
                    text = stringResource(R.string.add_card_data_title),
                    style = TextStyle(
                        fontFamily = UrbanistBold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF155B35),
                    ),
                )
            }

            Spacer(Modifier.height(22.dp))
            AddCardLabel(stringResource(R.string.add_card_number))
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { cardNumber = formatCardNumberInput(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.add_card_number_hint)) },
                trailingIcon = {
                    CardBrandMark(
                        brand = brand,
                        modifier = Modifier.padding(end = 8.dp),
                        compact = true,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = digits.length == 16 && !numberValid,
                supportingText = if (digits.length == 16 && !numberValid) {
                    { Text(stringResource(R.string.add_card_number_error)) }
                } else {
                    null
                },
                shape = AddCardShape,
                colors = addCardFieldColors(),
            )

            Spacer(Modifier.height(14.dp))
            AddCardLabel(stringResource(R.string.add_card_holder))
            OutlinedTextField(
                value = holderName,
                onValueChange = { holderName = it.take(50) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.add_card_holder_hint)) },
                singleLine = true,
                shape = AddCardShape,
                colors = addCardFieldColors(),
            )

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1.2f)) {
                    AddCardLabel(stringResource(R.string.add_card_expiration))
                    OutlinedTextField(
                        value = expiration,
                        onValueChange = { expiration = formatExpiration(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.add_card_expiration_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = expiration.length == 5 && !expirationValid,
                        shape = AddCardShape,
                        colors = addCardFieldColors(),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    AddCardLabel(stringResource(R.string.add_card_cvv))
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it.filter(Char::isDigit).take(3) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("•••") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        shape = AddCardShape,
                        colors = addCardFieldColors(),
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            AddCardLabel(stringResource(R.string.add_card_type))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CardTypeOption(
                    text = stringResource(R.string.add_card_debit),
                    selected = cardType == BankCardType.Debit,
                    modifier = Modifier.weight(1f),
                    onClick = { cardTypeName = BankCardType.Debit.name },
                )
                CardTypeOption(
                    text = stringResource(R.string.add_card_credit),
                    selected = cardType == BankCardType.Credit,
                    modifier = Modifier.weight(1f),
                    onClick = { cardTypeName = BankCardType.Credit.name },
                )
            }

            Spacer(Modifier.height(12.dp))
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
                        checkedColor = AddCardGreen,
                        uncheckedColor = AddCardBorder,
                    ),
                )
                Text(
                    text = stringResource(R.string.add_card_primary),
                    fontFamily = UrbanistRegular,
                    fontSize = 16.sp,
                )
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AddCardShape)
                    .background(Color(0xFFEAF5DA))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = AddCardGreen,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = stringResource(R.string.add_card_security),
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
                    .clip(AddCardShape)
                    .background(if (canSave) MuyuOlive else Color(0xFFB8C7B9))
                    .clickable(enabled = canSave) {
                        onSave(
                            SavedBankCard(
                                lastFour = digits.takeLast(4),
                                holderName = holderName.trim(),
                                expiration = expiration,
                                type = cardType,
                                brand = brand,
                                isPrimary = isPrimary,
                            ),
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.add_card_save),
                    fontFamily = UrbanistBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun AddCardLabel(text: String) {
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
private fun CardTypeOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(AddCardShape)
            .background(if (selected) AddCardGreen else Color.White)
            .border(1.dp, AddCardBorder, AddCardShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = UrbanistBold,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else AddCardGreen,
        )
    }
}

@Composable
private fun addCardFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AddCardGreen,
    unfocusedBorderColor = AddCardBorder,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
)

private fun formatExpiration(value: String): String {
    val digits = value.filter(Char::isDigit).take(4)
    return if (digits.length <= 2) digits else "${digits.take(2)}/${digits.drop(2)}"
}

private fun validExpiration(value: String): Boolean {
    if (!Regex("\\d{2}/\\d{2}").matches(value)) return false
    val month = value.take(2).toIntOrNull() ?: return false
    val year = value.takeLast(2).toIntOrNull() ?: return false
    if (month !in 1..12) return false
    val now = YearMonth.now()
    return YearMonth.of(2000 + year, month) >= now
}
