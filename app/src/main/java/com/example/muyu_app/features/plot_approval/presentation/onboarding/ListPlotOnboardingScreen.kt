package com.example.muyu_app.features.plot_approval.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.OnboardingScaffold
import com.example.muyu_app.ui.theme.MuyuappTheme

@Composable
fun ListPlotOnboardingScreen(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScaffold(
        illustration = R.drawable.onboarding_plot,
        illustrationWidth = 195.dp,
        illustrationHeight = 130.dp,
        title = stringResource(R.string.onboarding_plot_title),
        body = stringResource(R.string.onboarding_plot_body),
        pageIndex = 0,
        primaryLabel = stringResource(R.string.onboarding_next),
        onPrimary = onNext,
        skipLabel = stringResource(R.string.onboarding_skip),
        illustrationContentDescription = stringResource(R.string.onboarding_plot_image),
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun ListPlotOnboardingScreenPreview() {
    MuyuappTheme {
        ListPlotOnboardingScreen(onNext = {})
    }
}
