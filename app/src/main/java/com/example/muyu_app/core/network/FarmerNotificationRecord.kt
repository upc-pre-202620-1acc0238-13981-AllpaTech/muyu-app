package com.example.muyu_app.core.network

/** Aviso para el agricultor sobre su cosecha financiada. */
data class FarmerNotificationRecord(
    val id: String = "",
    val farmerEmail: String = "",
    val type: String = TYPE_HARVEST_RECEIVED,
    val plotId: Int = 0,
    val plotName: String = "",
    val body: String = "",
    val createdAtMillis: Long = 0L,
) {
    companion object {
        const val TYPE_HARVEST_RECEIVED = "harvest_received"
        const val TYPE_HARVEST_INCIDENT = "harvest_incident"
        const val TYPE_FUNDS_SECURED = "funds_secured"
    }
}
