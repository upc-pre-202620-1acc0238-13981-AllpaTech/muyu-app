package com.example.muyu_app.features.climate_monitoring.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.OnboardingScaffold
import com.example.muyu_app.ui.theme.MuyuappTheme

@Composable
fun WeatherOnboardingScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScaffold(
        illustration = R.drawable.onboarding_weather,
        illustrationWidth = 261.dp,
        illustrationHeight = 235.dp,
        title = stringResource(R.string.onboarding_weather_title),
        body = stringResource(R.string.onboarding_weather_body),
        pageIndex = 2,
        primaryLabel = stringResource(R.string.onboarding_start),
        onPrimary = onStart,
        illustrationContentDescription = stringResource(R.string.onboarding_weather_image),
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun WeatherOnboardingScreenPreview() {
    MuyuappTheme {
        WeatherOnboardingScreen(onStart = {})
    }
}
