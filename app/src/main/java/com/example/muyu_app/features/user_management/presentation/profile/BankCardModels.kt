package com.example.muyu_app.features.user_management.presentation.profile

enum class BankCardType { Debit, Credit }

enum class BankCardBrand { Visa, Mastercard, Unknown }

data class SavedBankCard(
    val lastFour: String = "4589",
    val holderName: String = "",
    val expiration: String = "09/29",
    val type: BankCardType = BankCardType.Debit,
    val brand: BankCardBrand = BankCardBrand.Visa,
    val isPrimary: Boolean = true,
)

fun detectCardBrand(number: String): BankCardBrand {
    val digits = number.filter(Char::isDigit)
    if (digits.startsWith('4')) return BankCardBrand.Visa

    val firstTwo = digits.take(2).toIntOrNull()
    val firstFour = digits.take(4).toIntOrNull()
    return if (firstTwo in 51..55 || firstFour in 2221..2720) {
        BankCardBrand.Mastercard
    } else {
        BankCardBrand.Unknown
    }
}

fun formatCardNumberInput(number: String): String = number
    .filter(Char::isDigit)
    .take(16)
    .chunked(4)
    .joinToString(" ")

fun maskedCardNumber(lastFour: String): String = "••••    ••••    ••••    $lastFour"

fun passesLuhnCheck(number: String): Boolean {
    val digits = number.filter(Char::isDigit)
    if (digits.length != 16) return false
    val sum = digits.reversed().mapIndexed { index, char ->
        var value = char.digitToInt()
        if (index % 2 == 1) {
            value *= 2
            if (value > 9) value -= 9
        }
        value
    }.sum()
    return sum % 10 == 0
}

// Texto corto para mostrar la tarjeta en pagos y perfil, por ejemplo "Visa •••• 4589".
fun bankCardSummary(card: SavedBankCard): String {
    val brand = when (card.brand) {
        BankCardBrand.Visa -> "Visa"
        BankCardBrand.Mastercard -> "Mastercard"
        BankCardBrand.Unknown -> ""
    }
    return listOf(brand, "•••• ${card.lastFour}").filter { it.isNotBlank() }.joinToString(" ")
}
