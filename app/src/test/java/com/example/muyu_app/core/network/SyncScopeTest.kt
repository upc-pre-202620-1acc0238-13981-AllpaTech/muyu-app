package com.example.muyu_app.core.network

import com.example.muyu_app.features.plot_approval.domain.PlotVisit
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncScopeTest {
    private fun plot(owner: String, funder: String? = null) = PlotRecord(
        id = 1, name = "", crop = "", area = "", harvestDate = "", place = "", stage = "",
        progress = 0, status = "available", amount = "", cycle = "", milestones = "",
        imageUri = null, ownerEmail = owner, fundedByEmail = funder,
    )

    private fun visit(farmer: String, merchant: String) = PlotVisit(
        id = 1, plotId = 1, plotName = "", plotPlace = "", farmerEmail = farmer, farmerName = "",
        merchantEmail = merchant, merchantName = "", date = "", time = "", message = "",
        status = "", outcome = "", agreedPrice = "",
    )

    @Test
    fun `owner can sync a plot regardless of letter case`() {
        assertTrue(plot("ana@gmail.com").isSyncableBy("ANA@gmail.com"))
    }

    @Test
    fun `merchant who funded a plot can sync it`() {
        assertTrue(plot("ana@gmail.com", funder = "luis@gmail.com").isSyncableBy("luis@gmail.com"))
    }

    @Test
    fun `another user's plot is not synced`() {
        assertFalse(plot("ana@gmail.com", funder = "luis@gmail.com").isSyncableBy("eva@gmail.com"))
        assertFalse(plot("ana@gmail.com").isSyncableBy("eva@gmail.com"))
    }

    @Test
    fun `nothing is synced without a signed-in user`() {
        assertFalse(plot("").isSyncableBy(""))
        assertFalse(visit("", "").isSyncableBy(""))
    }

    @Test
    fun `only the farmer and merchant of a visit can sync it`() {
        val visit = visit("ana@gmail.com", "luis@gmail.com")
        assertTrue(visit.isSyncableBy("ana@gmail.com"))
        assertTrue(visit.isSyncableBy("luis@gmail.com"))
        assertFalse(visit.isSyncableBy("eva@gmail.com"))
    }
}
