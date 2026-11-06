package com.example.muyu_app.features.user_management.presentation.profile

import com.example.muyu_app.core.di.AppContainer

/** Billetera Yape o Plin con la que el comerciante paga sus financiamientos. */
data class MerchantWallet(
    val provider: DigitalWalletProvider,
    val phone: String,
    val holderName: String,
)

private const val KEY_MERCHANT_WALLET = "merchant_wallet"
private const val SEPARATOR = "\u001F"

/** Cada comerciante guarda su propia billetera; una cuenta nueva empieza sin ninguna. */
fun AppContainer.merchantWallet(): MerchantWallet? {
    val email = currentUserEmail() ?: return null
    val raw = userPreferences?.loadString(email, KEY_MERCHANT_WALLET, "").orEmpty()
    val parts = raw.split(SEPARATOR)
    if (parts.size < 2 || parts[1].isBlank()) return null
    val provider = DigitalWalletProvider.entries.find { it.name == parts[0] } ?: return null
    return MerchantWallet(provider, parts[1], parts.getOrElse(2) { "" })
}

fun AppContainer.saveMerchantWallet(wallet: MerchantWallet?) {
    val email = currentUserEmail() ?: return
    val value = wallet?.let {
        listOf(it.provider.name, walletPhoneDigits(it.phone), it.holderName).joinToString(SEPARATOR)
    }.orEmpty()
    userPreferences?.saveString(email, KEY_MERCHANT_WALLET, value)
}

fun merchantWalletSummary(wallet: MerchantWallet): String =
    "${wallet.provider.name} ${maskWalletPhone(wallet.phone)}"
