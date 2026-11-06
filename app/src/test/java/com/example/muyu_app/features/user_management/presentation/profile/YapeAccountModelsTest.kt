package com.example.muyu_app.features.user_management.presentation.profile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class YapeAccountModelsTest {
    @Test
    fun formatsPeruvianMobileNumber() {
        assertEquals("987 654 321", formatYapePhone("+51 987654321"))
    }

    @Test
    fun masksLinkedMobileNumber() {
        assertEquals("987 *** 321", maskYapePhone("987654321"))
    }

    @Test
    fun validatesNineDigitMobileBeginningWithNine() {
        assertTrue(isValidYapePhone("987654321"))
        assertFalse(isValidYapePhone("187654321"))
        assertFalse(isValidYapePhone("98765432"))
    }
}
