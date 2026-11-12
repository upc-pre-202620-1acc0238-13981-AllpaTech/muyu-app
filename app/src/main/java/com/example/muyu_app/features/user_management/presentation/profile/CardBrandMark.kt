package com.example.muyu_app.features.user_management.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.muyu_app.R

@Composable
internal fun CardBrandMark(
    brand: BankCardBrand,
    modifier: Modifier = Modifier,
    unknownTint: Color = Color(0xFF6F806E),
    compact: Boolean = false,
) {
    when (brand) {
        BankCardBrand.Visa -> Image(
            painter = painterResource(R.drawable.visa_logo),
            contentDescription = stringResource(R.string.bank_account_visa_logo),
            contentScale = ContentScale.Crop,
            modifier = modifier.size(
                width = if (compact) 38.dp else 54.dp,
                height = if (compact) 17.dp else 22.dp,
            ),
        )

        BankCardBrand.Mastercard -> Box(
            modifier = modifier.width(if (compact) 34.dp else 42.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(if (compact) 19.dp else 23.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEB001B)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(if (compact) 19.dp else 23.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF79E1B).copy(alpha = 0.9f)),
            )
        }

        BankCardBrand.Unknown -> Icon(
            imageVector = Icons.Outlined.CreditCard,
            contentDescription = stringResource(R.string.add_card_unknown_brand),
            tint = unknownTint,
            modifier = modifier.size(28.dp),
        )
    }
}
