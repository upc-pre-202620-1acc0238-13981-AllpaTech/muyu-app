package com.example.muyu_app.features.user_management.presentation.role

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.AuthPrimaryButton
import com.example.muyu_app.core.image.InstallMuyuImageLoader
import com.example.muyu_app.ui.theme.FredokaSemiBold
import com.example.muyu_app.ui.theme.MuyuAction
import com.example.muyu_app.ui.theme.MuyuBackground
import com.example.muyu_app.ui.theme.MuyuForest
import com.example.muyu_app.ui.theme.MuyuappTheme
import com.example.muyu_app.ui.theme.UrbanistBold
import com.example.muyu_app.ui.theme.UrbanistLight

private val CardShape = RoundedCornerShape(20.dp)

@Composable
fun RoleSelectionScreen(
    onNext: () -> Unit,
    onFarmerSelected: () -> Unit,
    onMerchantSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = painterResource(R.drawable.muyu_logo),
            contentDescription = stringResource(R.string.splash_logo_content_description),
            modifier = Modifier
                .width(141.dp)
                .height(72.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.role_question),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontFamily = FredokaSemiBold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.role_subtitle),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = UrbanistLight,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(32.dp))
        RoleCard(
            image = R.drawable.role_farmer,
            imageDescription = stringResource(R.string.role_farmer_image),
            title = stringResource(R.string.role_farmer_title),
            body = stringResource(R.string.role_farmer_body),
            onClick = onFarmerSelected,
        )
        Spacer(modifier = Modifier.height(20.dp))
        RoleCard(
            image = R.drawable.role_merchant,
            imageDescription = stringResource(R.string.role_merchant_image),
            title = stringResource(R.string.role_merchant_title),
            body = stringResource(R.string.role_merchant_body),
            onClick = onMerchantSelected,
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.role_next),
            onClick = onNext,
            background = MuyuAction,
            contentColor = Color.White,
        )
    }
}

@Composable
private fun RoleCard(
    image: Int,
    imageDescription: String,
    title: String,
    body: String,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(CardShape)
            .background(Color.White)
            .border(1.dp, MuyuForest, CardShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 26.dp, vertical = 18.dp),
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = imageDescription,
            modifier = Modifier
                .width(67.dp)
                .height(64.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = TextStyle(
                fontFamily = UrbanistBold,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            style = TextStyle(
                fontFamily = UrbanistLight,
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                color = Color.Black,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun RoleSelectionScreenPreview() {
    InstallMuyuImageLoader()
    MuyuappTheme {
        RoleSelectionScreen(onNext = {}, onFarmerSelected = {}, onMerchantSelected = {})
    }
}
