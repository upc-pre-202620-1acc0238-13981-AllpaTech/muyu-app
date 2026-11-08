package com.example.muyu_app.core.network

import com.example.muyu_app.features.plot_approval.domain.PlotVisit

/**
 * The device holds every plot in the catalog, but a user can only change the
 * ones they own or fund, so those are the only ones sent back to the backend.
 */
fun PlotRecord.isSyncableBy(email: String): Boolean =
    email.isNotBlank() && (
        ownerEmail.equals(email, ignoreCase = true) ||
            fundedByEmail.equals(email, ignoreCase = true)
        )

/** A visit can only be changed by the farmer or the merchant taking part in it. */
fun PlotVisit.isSyncableBy(email: String): Boolean =
    email.isNotBlank() && (
        farmerEmail.equals(email, ignoreCase = true) ||
            merchantEmail.equals(email, ignoreCase = true)
        )
