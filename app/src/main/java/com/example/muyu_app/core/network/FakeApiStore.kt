package com.example.muyu_app.core.network

import com.example.muyu_app.core.environment.Environment
import com.example.muyu_app.features.plot_approval.domain.MilestoneCatalog
import com.example.muyu_app.features.plot_approval.domain.MilestoneState
import com.example.muyu_app.features.plot_approval.domain.Plot
import com.example.muyu_app.features.plot_approval.domain.PlotFinance
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

data class UserRecord(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val phone: String = "",
    val birthdate: String = "",
) {
    val displayName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}

data class PlotRecord(
    val id: Int,
    val name: String,
    val crop: String,
    val area: String,
    val harvestDate: String,
    val place: String,
    val stage: String,
    val progress: Int,
    val status: String,
    val amount: String,
    val cycle: String,
    val milestones: String,
    val imageUri: String?,
    val ownerEmail: String,
    val ownerName: String = "",
    val fundedByEmail: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Int? = null,
    val publishedAt: String = "",
    val milestoneStates: List<MilestoneState> = emptyList(),
    val visitOutcome: String = "",
    val visitDate: String = "",
)

data class PlotUpdatePayload(
    val name: String = "",
    val crop: String = "",
    val area: String = "",
    val harvestDate: String = "",
    val place: String = "",
    val amount: String = "",
    val cycle: String = "",
    val milestones: String = "",
    val imageUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Int? = null,
)

class FakeApiStore(
    private val persistence: FakeApiPersistence? = null,
) {
    val users = CopyOnWriteArrayList<UserRecord>()
    val plots = CopyOnWriteArrayList<PlotRecord>()
    val merchantNotifications = CopyOnWriteArrayList<MerchantNotificationRecord>()
    val syncHistory = CopyOnWriteArrayList<SyncHistoryRecord>()

    @Volatile
    var token: String? = null

    @Volatile
    var currentUser: UserRecord? = null

    init {
        val snapshot = persistence?.load()
        if (snapshot != null) {
            users.addAll(snapshot.users.map { it.normalized() })
            plots.addAll(snapshot.plots.map { it.normalized() })
            merchantNotifications.addAll(snapshot.merchantNotifications.map { it.normalized() })
            syncHistory.addAll(snapshot.syncHistory)
            migrateMerchantNotificationIds()
            seedCredentialsIfMissing()
            if (persistence.shouldApplyPlotReset()) {
                plots.clear()
                merchantNotifications.clear()
                persistence.markPlotResetApplied()
            }
            if (plots.isNotEmpty()) {
                realignSeedOwners()
                syncMerchantNotificationsFromPlots()
            }
            persist()
        } else {
            users.addAll(seedUsers())
            persist()
        }
        restoreSession()
    }

    fun startSession(user: UserRecord, sessionToken: String) {
        currentUser = user
        token = sessionToken
        persistence?.saveSession(user.email, sessionToken)
    }

    fun endSession() {
        currentUser = null
        token = null
        persistence?.clearSession()
    }

    private fun restoreSession() {
        val (email, savedToken) = persistence?.loadSession() ?: return
        val user = users.find { it.email.equals(email, ignoreCase = true) }
        if (user == null) {
            persistence?.clearSession()
            return
        }
        currentUser = user
        token = savedToken
    }

    fun persist() {
        persistence?.save(
            users.toList(),
            plots.toList(),
            merchantNotifications.toList(),
            syncHistory.toList(),
        )
    }

    fun syncHistoryForOwner(ownerEmail: String): List<SyncHistoryRecord> =
        syncHistory
            .filter { it.ownerEmail.equals(ownerEmail, ignoreCase = true) }
            .sortedByDescending { it.syncedAtMillis }

    fun appendSyncHistory(entry: SyncHistoryRecord) {
        syncHistory.add(0, entry)
        if (syncHistory.size > 50) {
            syncHistory.removeAt(syncHistory.lastIndex)
        }
        persist()
    }

    fun notificationsForMerchant(email: String): List<MerchantNotificationRecord> {
        syncMerchantNotificationsFromPlots()
        return merchantNotifications
            .filter { it.merchantEmail.equals(email, ignoreCase = true) }
            .sortedByDescending { it.createdAtMillis }
    }

    fun plotById(id: Int): PlotRecord? = plots.find { it.id == id }

    fun ensureMilestones(id: Int): PlotRecord? {
        val plot = plotById(id) ?: return null
        if (plot.milestoneStates.isNotEmpty()) return plot
        val seeded = plot.copy(milestoneStates = MilestoneCatalog.seed(plot.stage, plot.progress))
        replacePlot(seeded)
        return seeded
    }

    fun replacePlot(updated: PlotRecord) {
        val index = plots.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            plots[index] = updated
            persist()
        }
    }

    fun updateMilestone(plotId: Int, milestone: MilestoneState) {
        val plot = ensureMilestones(plotId) ?: return
        val milestoneToSave = if (
            milestone.status == MilestoneState.STATUS_SUBMITTED &&
            milestone.submittedAtMillis <= 0L
        ) {
            milestone.copy(submittedAtMillis = System.currentTimeMillis())
        } else {
            milestone
        }
        val next = if (plot.milestoneStates.any { it.key == milestoneToSave.key }) {
            plot.milestoneStates.map { if (it.key == milestoneToSave.key) milestoneToSave else it }
        } else {
            plot.milestoneStates + milestoneToSave
        }
        val currentIndex = MilestoneCatalog.all.indexOfFirst { it.key == milestone.key }
        val progress = when {
            milestoneToSave.status == MilestoneState.STATUS_APPROVED ||
                milestoneToSave.status == MilestoneState.STATUS_SUBMITTED -> ((currentIndex + 1) * 25).coerceAtMost(100)
            else -> plot.progress
        }
        replacePlot(
            plot.copy(
                milestoneStates = next,
                stage = stageTitle(milestoneToSave.key),
                progress = progress,
            ),
        )
        if (milestoneToSave.status == MilestoneState.STATUS_SUBMITTED) {
            val latest = plotById(plotId) ?: plot.copy(milestoneStates = next)
            notifyEvidenceSubmitted(latest, milestoneToSave)
        }
    }

    fun milestoneReleaseAmountFor(plotId: Int, milestoneKey: String): String {
        val plot = plotById(plotId) ?: return ""
        return milestoneReleaseAmount(plot, milestoneKey)
    }

    fun reviewMilestone(plotId: Int, milestoneKey: String, approved: Boolean) {
        val plot = ensureMilestones(plotId) ?: return
        val state = plot.milestoneStates.find { it.key == milestoneKey } ?: return
        val status = if (approved) {
            MilestoneState.STATUS_APPROVED
        } else {
            MilestoneState.STATUS_NEEDS_CORRECTION
        }
        updateMilestone(plotId, state.copy(status = status))
        if (approved) {
            val dedupe = evidenceDedupe(plotId, milestoneKey)
            if (merchantNotifications.removeAll { it.id == dedupe }) {
                persist()
            }
        }
    }

    fun saveVisit(plotId: Int, outcome: String, date: String) {
        val plot = plotById(plotId) ?: return
        if (outcome == MilestoneState.VISIT_NO_AGREEMENT) {
            replacePlot(
                plot.copy(
                    visitOutcome = outcome,
                    visitDate = date,
                    status = "available",
                    fundedByEmail = null,
                    stage = "Available",
                    progress = 0,
                ),
            )
        } else {
            replacePlot(plot.copy(visitOutcome = outcome, visitDate = date))
        }
    }

    fun requestExtension(plotId: Int, milestoneKey: String, date: String, reason: String) {
        val plot = ensureMilestones(plotId) ?: return
        val next = plot.milestoneStates.map { state ->
            if (state.key == milestoneKey) {
                state.copy(extensionDate = date, extensionReason = reason, dueLabel = date)
            } else {
                state
            }
        }
        replacePlot(plot.copy(milestoneStates = next))
        notifyExtensionRequested(plot, milestoneKey, date, reason)
    }

    private fun notifyEvidenceSubmitted(plot: PlotRecord, milestone: MilestoneState) {
        val merchant = plot.fundedByEmail ?: return
        val dedupe = evidenceDedupe(plot.id, milestone.key)
        val amount = milestoneReleaseAmount(plot, milestone.key)
        val farmer = plot.ownerName.ifBlank { ownerName(plot.ownerEmail) }
        val createdAt = notificationTime(milestone)
        val record = MerchantNotificationRecord(
            id = dedupe,
            merchantEmail = merchant,
            type = MerchantNotificationRecord.TYPE_EVIDENCE,
            plotId = plot.id,
            plotName = plot.name,
            farmerName = farmer,
            milestoneKey = milestone.key,
            amountLabel = amount,
            title = "",
            body = "",
            createdAtMillis = createdAt,
        )
        val index = merchantNotifications.indexOfFirst { it.id == dedupe }
        if (index >= 0) {
            if (merchantNotifications[index] == record) return
            merchantNotifications[index] = record
        } else {
            merchantNotifications.add(0, record)
        }
        persist()
    }

    private fun migrateMerchantNotificationIds() {
        val merged = merchantNotifications
            .map { notification ->
                if (notification.type != MerchantNotificationRecord.TYPE_EVIDENCE) {
                    notification
                } else {
                    notification.copy(id = evidenceDedupe(notification.plotId, notification.milestoneKey))
                }
            }
            .groupBy { it.id }
            .map { (_, group) -> group.maxBy { it.createdAtMillis } }
            .sortedByDescending { it.createdAtMillis }
        merchantNotifications.clear()
        merchantNotifications.addAll(merged)
    }

    private fun notifyExtensionRequested(
        plot: PlotRecord,
        milestoneKey: String,
        date: String,
        reason: String,
    ) {
        val merchant = plot.fundedByEmail ?: return
        val dedupe = "ext_${plot.id}_${milestoneKey}_${date}_${reason.hashCode()}"
        if (merchantNotifications.any { it.id == dedupe }) return
        val farmer = plot.ownerName.ifBlank { ownerName(plot.ownerEmail) }
        addNotification(
            MerchantNotificationRecord(
                id = dedupe,
                merchantEmail = merchant,
                type = MerchantNotificationRecord.TYPE_EXTENSION,
                plotId = plot.id,
                plotName = plot.name,
                farmerName = farmer,
                milestoneKey = milestoneKey,
                amountLabel = "",
                extensionDate = date,
                title = "",
                body = reason,
                createdAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    private fun addNotification(record: MerchantNotificationRecord) {
        merchantNotifications.add(0, record)
        persist()
    }

    private fun syncMerchantNotificationsFromPlots() {
        val existing = merchantNotifications.map { it.id }.toSet()
        plots.filter { !it.fundedByEmail.isNullOrBlank() && it.status == "funded" }.forEach { plot ->
            plot.milestoneStates.forEach { milestone ->
                if (milestone.status == MilestoneState.STATUS_SUBMITTED) {
                    notifyEvidenceSubmitted(plot, milestone)
                }
                if (milestone.extensionReason.isNotBlank() && milestone.extensionDate.isNotBlank()) {
                    val id = "ext_${plot.id}_${milestone.key}_${milestone.extensionDate}_${milestone.extensionReason.hashCode()}"
                    if (id !in existing) {
                        notifyExtensionRequested(
                            plot,
                            milestone.key,
                            milestone.extensionDate,
                            milestone.extensionReason,
                        )
                    }
                }
            }
            val merchant = plot.fundedByEmail ?: return@forEach
            val harvestId = "harvest_${plot.id}_${plot.harvestDate}"
            if (harvestId !in existing && plot.harvestDate.isNotBlank()) {
                val harvest = PlotFinance.parseDate(plot.harvestDate) ?: return@forEach
                val days = daysUntil(harvest)
                if (days in 0..45) {
                    addNotification(
                        MerchantNotificationRecord(
                            id = harvestId,
                            merchantEmail = merchant,
                            type = MerchantNotificationRecord.TYPE_HARVEST,
                            plotId = plot.id,
                            plotName = plot.name,
                            farmerName = plot.ownerName.ifBlank { ownerName(plot.ownerEmail) },
                            milestoneKey = MilestoneState.KEY_HARVEST,
                            amountLabel = "",
                            title = "",
                            body = plot.harvestDate,
                            createdAtMillis = System.currentTimeMillis(),
                        ),
                    )
                }
            }
        }
    }

    private fun evidenceDedupe(plotId: Int, milestoneKey: String): String =
        "evidence_${plotId}_${milestoneKey}"

    private fun notificationTime(milestone: MilestoneState): Long {
        if (milestone.submittedAtMillis > 0L) return milestone.submittedAtMillis
        if (milestone.recordedAt.isNotBlank() && milestone.recordedAt == PlotFinance.publishedToday()) {
            return System.currentTimeMillis()
        }
        return PlotFinance.parseDate(milestone.recordedAt)?.time ?: System.currentTimeMillis()
    }

    private fun milestoneReleaseAmount(plot: PlotRecord, milestoneKey: String): String {
        val total = PlotFinance.parseAmount(plot.amount)
        val index = MilestoneCatalog.all.indexOfFirst { it.key == milestoneKey }.coerceAtLeast(0)
        val step = PlotFinance.disbursement(total).getOrNull(index)
        return step?.let { PlotFinance.formatAmount(it.amount) }.orEmpty()
    }

    private fun daysUntil(date: java.util.Date): Int {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val target = Calendar.getInstance().apply { time = date }
        val diff = target.timeInMillis - start.timeInMillis
        return (diff / 86_400_000L).toInt()
    }

    private fun MerchantNotificationRecord.normalized(): MerchantNotificationRecord = copy(
        id = id.orEmpty(),
        merchantEmail = merchantEmail.orEmpty(),
        type = type.orEmpty(),
        plotName = plotName.orEmpty(),
        farmerName = farmerName.orEmpty(),
        milestoneKey = milestoneKey.orEmpty(),
        amountLabel = amountLabel.orEmpty(),
        extensionDate = extensionDate.orEmpty(),
        title = title.orEmpty(),
        body = body.orEmpty(),
    )

    fun toDomain(record: PlotRecord): Plot = Plot(
        id = record.id,
        name = record.name,
        crop = record.crop,
        area = record.area,
        harvestDate = record.harvestDate,
        place = record.place,
        stage = record.stage,
        progress = record.progress,
        status = record.status,
        amount = record.amount,
        cycle = record.cycle,
        milestones = record.milestones,
        imageUri = record.imageUri,
        ownerEmail = record.ownerEmail,
        ownerName = record.ownerName,
        fundedByEmail = record.fundedByEmail,
        latitude = record.latitude,
        longitude = record.longitude,
        accuracyMeters = record.accuracyMeters,
        publishedAt = record.publishedAt,
        milestoneStates = record.milestoneStates,
        visitOutcome = record.visitOutcome,
        visitDate = record.visitDate,
    )

    private fun UserRecord.normalized(): UserRecord = UserRecord(
        email = email.orEmpty(),
        password = password.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        role = role.orEmpty().ifBlank { "farmer" },
        phone = phone.orEmpty(),
        birthdate = birthdate.orEmpty(),
    )

    private fun MilestoneState.normalized(): MilestoneState {
        val statusValue = status.orEmpty().ifBlank { MilestoneState.STATUS_PENDING }
        var submittedMillis = submittedAtMillis
        if (submittedMillis <= 0L &&
            (
                statusValue == MilestoneState.STATUS_SUBMITTED ||
                    statusValue == MilestoneState.STATUS_QUEUED ||
                    statusValue == MilestoneState.STATUS_APPROVED ||
                    statusValue == MilestoneState.STATUS_NEEDS_CORRECTION
                )
        ) {
            submittedMillis = PlotFinance.parseDate(recordedAt.orEmpty())?.time ?: 0L
        }
        return MilestoneState(
        key = key.orEmpty().ifBlank { MilestoneState.KEY_PLANTING },
        status = statusValue,
        dueLabel = dueLabel.orEmpty(),
        photoUri = photoUri.orEmpty(),
        inputUsed = inputUsed.orEmpty(),
        amountApplied = amountApplied.orEmpty(),
        notes = notes.orEmpty(),
        recordedAt = recordedAt.orEmpty(),
        submittedAtMillis = submittedMillis,
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracyMeters,
        extensionDate = extensionDate.orEmpty(),
        extensionReason = extensionReason.orEmpty(),
    )
    }

    private fun PlotRecord.normalized(): PlotRecord = PlotRecord(
        id = id,
        name = name.orEmpty(),
        crop = crop.orEmpty(),
        area = area.orEmpty(),
        harvestDate = harvestDate.orEmpty(),
        place = place.orEmpty(),
        stage = stage.orEmpty(),
        progress = progress,
        status = status.orEmpty(),
        amount = amount.orEmpty(),
        cycle = cycle.orEmpty(),
        milestones = milestones.orEmpty(),
        imageUri = imageUri,
        ownerEmail = ownerEmail.orEmpty(),
        ownerName = ownerName.orEmpty(),
        fundedByEmail = fundedByEmail,
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracyMeters,
        publishedAt = publishedAt.orEmpty(),
        milestoneStates = (milestoneStates ?: emptyList()).map { it.normalized() },
        visitOutcome = visitOutcome.orEmpty(),
        visitDate = visitDate.orEmpty(),
    )

    private fun stageTitle(key: String): String = when (key) {
        MilestoneState.KEY_PLANTING -> "Planting"
        MilestoneState.KEY_IRRIGATION -> "Irrigation"
        MilestoneState.KEY_FERTILIZATION -> "Fertilization"
        MilestoneState.KEY_HARVEST -> "Harvest"
        else -> key
    }

    fun addUser(user: UserRecord) {
        users.add(user)
        persist()
    }

    fun addPlot(plot: PlotRecord) {
        plots.add(0, plot)
        persist()
    }

    fun updatePlot(id: Int, update: PlotUpdatePayload, requesterEmail: String): PlotRecord? {
        val index = plots.indexOfFirst { it.id == id }
        if (index < 0) return null
        val current = plots[index]
        if (!current.ownerEmail.equals(requesterEmail, ignoreCase = true)) return null
        if (current.status == "funded") return null
        val updated = current.copy(
            name = update.name.ifBlank { current.name },
            crop = update.crop.ifBlank { current.crop },
            area = update.area.ifBlank { current.area },
            harvestDate = update.harvestDate.ifBlank { current.harvestDate },
            place = update.place.ifBlank { current.place },
            amount = update.amount.ifBlank { current.amount },
            cycle = update.cycle.ifBlank { current.cycle },
            milestones = update.milestones.ifBlank { current.milestones },
            imageUri = update.imageUri ?: current.imageUri,
            latitude = update.latitude ?: current.latitude,
            longitude = update.longitude ?: current.longitude,
            accuracyMeters = update.accuracyMeters ?: current.accuracyMeters,
        )
        plots[index] = updated
        persist()
        return updated
    }

    fun fundPlot(id: Int, merchantEmail: String): PlotRecord? {
        val index = plots.indexOfFirst { it.id == id }
        if (index < 0) return null
        val current = plots[index]
        val updated = current.copy(
            status = "funded",
            stage = "Planting",
            progress = 10,
            fundedByEmail = merchantEmail,
            milestoneStates = current.milestoneStates.ifEmpty {
                MilestoneCatalog.seed("Planting", 10)
            },
        )
        plots[index] = updated
        persist()
        return updated
    }

    fun ownerName(email: String): String {
        return users.find { it.email.equals(email, ignoreCase = true) }?.displayName.orEmpty()
    }

    fun nextPlotId(): Int = (plots.maxOfOrNull { it.id } ?: 0) + 1

    fun newToken(): String = UUID.randomUUID().toString()

    private fun realignSeedOwners() {
        val index = plots.indexOfFirst {
            it.name.contains("Álamos", ignoreCase = true) ||
                it.name.contains("Alamos", ignoreCase = true)
        }
        if (index >= 0 && plots[index].ownerEmail.equals(Environment.DemoCredentials.FARMER_EMAIL, true)) {
            plots[index] = plots[index].copy(
                ownerEmail = "carlos.mamani@muyu.pe",
                ownerName = "Carlos Mamani",
            )
            persist()
        }
    }

    private fun seedCredentialsIfMissing() {
        seedUsers().forEach { demo ->
            if (users.none { it.email.equals(demo.email, ignoreCase = true) }) {
                users.add(demo)
            }
        }
        persist()
    }

    private fun seedUsers() = listOf(
        UserRecord(
            email = Environment.DemoCredentials.FARMER_EMAIL,
            password = Environment.DemoCredentials.PASSWORD,
            firstName = "María",
            lastName = "Quispe",
            role = "farmer",
            phone = "987654321",
            birthdate = "12 Mar 1988",
        ),
        UserRecord(
            email = Environment.DemoCredentials.MERCHANT_EMAIL,
            password = Environment.DemoCredentials.PASSWORD,
            firstName = "José",
            lastName = "Arturo",
            role = "merchant",
            phone = "912345678",
            birthdate = "04 Jul 1990",
        ),
        UserRecord(
            email = "carlos.mamani@muyu.pe",
            password = Environment.DemoCredentials.PASSWORD,
            firstName = "Carlos",
            lastName = "Mamani",
            role = "farmer",
            phone = "999111222",
            birthdate = "22 Sep 1985",
        ),
    )

    private fun seedPlots() = listOf(
        PlotRecord(
            id = 1,
            name = "El Rosal Plot",
            crop = "Coffee",
            area = "2.5 ha",
            harvestDate = "15 Feb 2027",
            place = "Chachapoyas",
            stage = "Fertilization",
            progress = 65,
            status = "funded",
            amount = "S/ 8,500",
            cycle = "6 months",
            milestones = "4",
            imageUri = null,
            ownerEmail = Environment.DemoCredentials.FARMER_EMAIL,
            ownerName = "María Quispe",
            fundedByEmail = Environment.DemoCredentials.MERCHANT_EMAIL,
        ),
        PlotRecord(
            id = 2,
            name = "Huayco Plot",
            crop = "Yellow corn",
            area = "1.2 ha",
            harvestDate = "10 Mar 2027",
            place = "Cuzco",
            stage = "Planting",
            progress = 20,
            status = "approved",
            amount = "S/ 4,200",
            cycle = "6 months",
            milestones = "4",
            imageUri = null,
            ownerEmail = Environment.DemoCredentials.FARMER_EMAIL,
            ownerName = "María Quispe",
        ),
        PlotRecord(
            id = 3,
            name = "Molinos Farm",
            crop = "Quinoa",
            area = "1.2 ha",
            harvestDate = "01 Apr 2027",
            place = "Apurímac",
            stage = "Published",
            progress = 5,
            status = "pending",
            amount = "S/ 3,800",
            cycle = "3 months",
            milestones = "3",
            imageUri = null,
            ownerEmail = Environment.DemoCredentials.FARMER_EMAIL,
            ownerName = "María Quispe",
        ),
        PlotRecord(
            id = 4,
            name = "Esperanza Plot",
            crop = "Huayro potato",
            area = "3.5 ha",
            harvestDate = "15 Feb 2027",
            place = "Cuzco",
            stage = "Available",
            progress = 0,
            status = "available",
            amount = "S/ 8,500",
            cycle = "6 months",
            milestones = "4",
            imageUri = null,
            ownerEmail = Environment.DemoCredentials.FARMER_EMAIL,
            ownerName = "María Quispe",
        ),
        PlotRecord(
            id = 5,
            name = "Los Álamos Plot",
            crop = "Purple corn",
            area = "2 ha",
            harvestDate = "20 Mar 2027",
            place = "Apurímac",
            stage = "Available",
            progress = 0,
            status = "available",
            amount = "S/ 4,200",
            cycle = "6 months",
            milestones = "4",
            imageUri = null,
            ownerEmail = "carlos.mamani@muyu.pe",
            ownerName = "Carlos Mamani",
        ),
    )
}
