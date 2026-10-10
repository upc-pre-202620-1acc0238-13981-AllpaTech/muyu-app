package com.example.muyu_app.features.escrow_custody.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.OnboardingScaffold
import com.example.muyu_app.ui.theme.MuyuappTheme

@Composable
fun EscrowOnboardingScreen(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScaffold(
        illustration = R.drawable.onboarding_escrow,
        illustrationWidth = 138.dp,
        illustrationHeight = 167.dp,
        title = stringResource(R.string.onboarding_escrow_title),
        body = stringResource(R.string.onboarding_escrow_body),
        pageIndex = 1,
        primaryLabel = stringResource(R.string.onboarding_next),
        onPrimary = onNext,
        skipLabel = stringResource(R.string.onboarding_skip),
        illustrationContentDescription = stringResource(R.string.onboarding_escrow_image),
        modifier = modifier,
    )
}

@Preview(showBackground = true, widthDp = 402, heightDp = 874)
@Composable
private fun EscrowOnboardingScreenPreview() {
    MuyuappTheme {
        EscrowOnboardingScreen(onNext = {})
    }
}
