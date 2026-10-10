package com.example.muyu_app.core.image

import android.content.Context
import android.net.Uri
import android.util.Base64

fun imageUriToPortableDataUrl(context: Context, value: String?): String? {
    if (value.isNullOrBlank() || value.startsWith("http") || value.startsWith("data:")) return value
    return runCatching {
        val uri = Uri.parse(value)
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return value
        val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
        "data:$mime;base64,${Base64.encodeToString(bytes, Base64.NO_WRAP)}"
    }.getOrDefault(value)
}
