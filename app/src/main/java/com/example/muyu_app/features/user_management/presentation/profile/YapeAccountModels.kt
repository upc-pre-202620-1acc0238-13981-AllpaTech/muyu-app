package com.example.muyu_app.features.user_management.presentation.profile

data class YapeAccount(
    val phone: String = "987654321",
    val holderName: String = "",
    val isPrimary: Boolean = true,
    val isVerified: Boolean = true,
)

fun yapePhoneDigits(value: String): String = value.filter(Char::isDigit).takeLast(9)

fun formatYapePhone(value: String): String = yapePhoneDigits(value)
    .chunked(3)
    .joinToString(" ")

fun maskYapePhone(value: String): String {
    val digits = yapePhoneDigits(value)
    if (digits.length != 9) return digits
    return "${digits.take(3)} *** ${digits.takeLast(3)}"
}

fun isValidYapePhone(value: String): Boolean {
    val digits = yapePhoneDigits(value)
    return digits.length == 9 && digits.startsWith('9')
}
