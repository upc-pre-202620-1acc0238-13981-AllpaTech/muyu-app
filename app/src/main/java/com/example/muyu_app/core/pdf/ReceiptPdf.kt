package com.example.muyu_app.core.pdf

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri

/**
 * Escribe un comprobante simple de una página A4 en el archivo que eligió el usuario.
 * Devuelve false si no se pudo escribir, para que la pantalla avise.
 */
fun writeReceiptPdf(
    context: Context,
    uri: Uri,
    title: String,
    rows: List<Pair<String, String>>,
    footer: String,
): Boolean = runCatching {
    val document = PdfDocument()
    try {
        val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val canvas = page.canvas
        val brandPaint = Paint().apply {
            textSize = 28f
            isFakeBoldText = true
            color = android.graphics.Color.rgb(31, 77, 25)
        }
        val titlePaint = Paint().apply {
            textSize = 22f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }
        val labelPaint = Paint().apply {
            textSize = 15f
            color = android.graphics.Color.rgb(110, 110, 110)
        }
        val valuePaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }
        val footerPaint = Paint().apply {
            textSize = 13f
            color = android.graphics.Color.rgb(30, 91, 130)
        }
        canvas.drawText("MUYU", 48f, 70f, brandPaint)
        canvas.drawText(title, 48f, 115f, titlePaint)
        var y = 170f
        rows.forEach { (label, value) ->
            canvas.drawText(label, 48f, y, labelPaint)
            canvas.drawText(value, 220f, y, valuePaint)
            y += 34f
        }
        canvas.drawText(footer, 48f, y + 30f, footerPaint)
        document.finishPage(page)
        val output = context.contentResolver.openOutputStream(uri) ?: return@runCatching false
        output.use { document.writeTo(it) }
        true
    } finally {
        document.close()
    }
}.getOrDefault(false)
