package com.example.muyu_app.features.user_management.presentation.profile

enum class DigitalWalletProvider { Yape, Plin }

data class DigitalWalletAccount(
    // Sin número de ejemplo: el usuario registra el suyo.
    val phone: String = "",
    val holderName: String = "",
    val isPrimary: Boolean = true,
    val isVerified: Boolean = true,
)

fun walletPhoneDigits(value: String): String = value.filter(Char::isDigit).takeLast(9)

fun formatWalletPhone(value: String): String = walletPhoneDigits(value)
    .chunked(3)
    .joinToString(" ")

fun maskWalletPhone(value: String): String {
    val digits = walletPhoneDigits(value)
    if (digits.length != 9) return digits
    return "${digits.take(3)} *** ${digits.takeLast(3)}"
}

fun isValidWalletPhone(value: String): Boolean {
    val digits = walletPhoneDigits(value)
    return digits.length == 9 && digits.startsWith('9')
}
