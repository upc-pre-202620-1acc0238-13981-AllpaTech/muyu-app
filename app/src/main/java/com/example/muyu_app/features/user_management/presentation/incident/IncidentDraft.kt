package com.example.muyu_app.features.user_management.presentation.incident

class IncidentDraft {
    var type: String = TYPE_TECHNICAL
    var description: String = ""
    var photoUri: String? = null

    fun clear() {
        type = TYPE_TECHNICAL
        description = ""
        photoUri = null
    }

    companion object {
        const val TYPE_TECHNICAL = "technical"
        const val TYPE_PAYMENT = "payment"
        const val TYPE_PLOT = "plot"
        const val TYPE_OTHER = "other"
    }
}
