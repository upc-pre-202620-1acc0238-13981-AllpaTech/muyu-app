package com.example.muyu_app.features.user_management.presentation.legal

import com.example.muyu_app.ui.theme.MuyuBackground
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muyu_app.R
import com.example.muyu_app.ui.theme.MuyuNavBar
import com.example.muyu_app.ui.theme.MuyuOlive

@Composable
fun TermsAndConditionsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    LegalDocumentScreen(
        title = stringResource(R.string.profile_terms),
        sections = legalSections(
            R.string.legal_terms_1_title to R.string.legal_terms_1_body,
            R.string.legal_terms_2_title to R.string.legal_terms_2_body,
            R.string.legal_terms_3_title to R.string.legal_terms_3_body,
            R.string.legal_terms_4_title to R.string.legal_terms_4_body,
            R.string.legal_terms_5_title to R.string.legal_terms_5_body,
            R.string.legal_terms_6_title to R.string.legal_terms_6_body,
            R.string.legal_terms_7_title to R.string.legal_terms_7_body,
            R.string.legal_terms_8_title to R.string.legal_terms_8_body,
            R.string.legal_terms_9_title to R.string.legal_terms_9_body,
            R.string.legal_terms_10_title to R.string.legal_terms_10_body,
        ),
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    LegalDocumentScreen(
        title = stringResource(R.string.profile_privacy),
        sections = legalSections(
            R.string.legal_privacy_1_title to R.string.legal_privacy_1_body,
            R.string.legal_privacy_2_title to R.string.legal_privacy_2_body,
            R.string.legal_privacy_3_title to R.string.legal_privacy_3_body,
            R.string.legal_privacy_4_title to R.string.legal_privacy_4_body,
            R.string.legal_privacy_5_title to R.string.legal_privacy_5_body,
            R.string.legal_privacy_6_title to R.string.legal_privacy_6_body,
            R.string.legal_privacy_7_title to R.string.legal_privacy_7_body,
            R.string.legal_privacy_8_title to R.string.legal_privacy_8_body,
            R.string.legal_privacy_9_title to R.string.legal_privacy_9_body,
            R.string.legal_privacy_10_title to R.string.legal_privacy_10_body,
        ),
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun legalSections(vararg items: Pair<Int, Int>): List<Pair<String, String>> =
    items.map { (title, body) -> stringResource(title) to stringResource(body) }

@Composable
private fun LegalDocumentScreen(
    title: String,
    sections: List<Pair<String, String>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().background(MuyuBackground)) {
        Row(
            Modifier.fillMaxWidth().background(MuyuNavBar).statusBarsPadding().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("‹", color = Color.White, fontSize = 40.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(stringResource(R.string.legal_updated), color = Color.Gray, fontSize = 13.sp)
            sections.forEach { (sectionTitle, body) ->
                LegalSection(sectionTitle, body)
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
                    .background(MuyuOlive, RoundedCornerShape(18.dp))
                    .clickable { onBack() }
                    .padding(18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.legal_understood),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun LegalSection(title: String, body: String) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF245B27))
        Spacer(Modifier.height(8.dp))
        Text(body, fontSize = 15.sp, lineHeight = 21.sp, color = Color(0xFF333333))
    }
}
