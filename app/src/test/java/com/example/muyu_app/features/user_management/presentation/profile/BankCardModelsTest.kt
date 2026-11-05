package com.example.muyu_app.features.user_management.presentation.profile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BankCardModelsTest {
    @Test
    fun detectsVisaFromNumberPrefix() {
        assertEquals(BankCardBrand.Visa, detectCardBrand("4111 1111 1111 1111"))
    }

    @Test
    fun detectsMastercardFromBinRange() {
        assertEquals(BankCardBrand.Mastercard, detectCardBrand("5555 5555 5555 4444"))
        assertEquals(BankCardBrand.Mastercard, detectCardBrand("2221 0000 0000 0000"))
    }

    @Test
    fun validatesCardNumberWithLuhnCheck() {
        assertTrue(passesLuhnCheck("4111 1111 1111 1111"))
        assertTrue(passesLuhnCheck("5555 5555 5555 4444"))
        assertFalse(passesLuhnCheck("4111 1111 1111 1112"))
    }

    @Test
    fun formatsAndMasksCardNumber() {
        assertEquals("4111 1111 1111 1111", formatCardNumberInput("4111111111111111"))
        assertEquals("••••    ••••    ••••    1111", maskedCardNumber("1111"))
    }
}
