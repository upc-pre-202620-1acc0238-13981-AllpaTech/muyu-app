package com.example.muyu_app.core.contact

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import com.example.muyu_app.R

/** Opens a draft in the email app. It never sends a message automatically. */
// Abre un borrador dirigido al agricultor; no envía mensajes automáticamente.
fun openFarmerContact(context: Context, email: String) {
    val recipient = email.trim()
    if (!Patterns.EMAIL_ADDRESS.matcher(recipient).matches()) {
        Toast.makeText(context, R.string.muyu_contact_missing, Toast.LENGTH_LONG).show()
        return
    }
    val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipient, null))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.muyu_contact_unavailable, Toast.LENGTH_LONG).show()
    } catch (_: SecurityException) {
        Toast.makeText(context, R.string.muyu_contact_unavailable, Toast.LENGTH_LONG).show()
    }
}
