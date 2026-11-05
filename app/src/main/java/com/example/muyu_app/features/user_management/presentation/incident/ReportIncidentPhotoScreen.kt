package com.example.muyu_app.features.user_management.presentation.incident

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.muyu_app.R
import com.example.muyu_app.core.designsystem.components.GreenTitleHeader
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.ui.theme.MuyuPageGray

@Composable
fun ReportIncidentPhotoScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val draft = LocalAppContainer.current.incidentDraft
    var photoUri by remember { mutableStateOf(draft.photoUri) }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            photoUri = uri.toString()
            draft.photoUri = uri.toString()
        }
    }
    val shape = RoundedCornerShape(16.dp)
    val green = Color(0xFF2E7D32)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuyuPageGray),
    ) {
        GreenTitleHeader(stringResource(R.string.report_attach_photo), onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(shape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFA8C9AA), shape)
                    .clickable {
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (photoUri.isNullOrBlank()) {
                    Text(
                        text = stringResource(R.string.report_attach_photo),
                        color = green,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = stringResource(R.string.report_photo_attached),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(shape)
                    .background(if (photoUri.isNullOrBlank()) Color(0xFFB8C7B9) else green)
                    .clickable(enabled = !photoUri.isNullOrBlank(), onClick = onContinue),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.report_send_request),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
