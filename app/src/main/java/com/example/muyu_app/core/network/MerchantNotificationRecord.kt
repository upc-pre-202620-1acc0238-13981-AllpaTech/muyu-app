package com.example.muyu_app.core.network

data class MerchantNotificationRecord(
    val id: String = "",
    val merchantEmail: String = "",
    val type: String = TYPE_EVIDENCE,
    val plotId: Int = 0,
    val plotName: String = "",
    val farmerName: String = "",
    val milestoneKey: String = "",
    val amountLabel: String = "",
    val extensionDate: String = "",
    val title: String = "",
    val body: String = "",
    val createdAtMillis: Long = 0L,
) {
    companion object {
        const val TYPE_EVIDENCE = "evidence"
        const val TYPE_EXTENSION = "extension"
        const val TYPE_HARVEST = "harvest"
        const val TYPE_WEATHER = "weather"
    }
}
