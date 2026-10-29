package com.example.muyu_app.navigation

import android.app.Activity
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.muyu_app.core.di.FarmerPlotReturn
import com.example.muyu_app.core.di.LocalAppContainer
import com.example.muyu_app.core.di.MerchantEvidenceOrigin
import com.example.muyu_app.core.di.MerchantPlotReturn
import com.example.muyu_app.core.i18n.ProvideAppLocale
import com.example.muyu_app.core.designsystem.components.BlankMerchantTabScreen
import com.example.muyu_app.core.designsystem.components.BlankTabScreen
import com.example.muyu_app.core.designsystem.components.MerchantBottomBar
import com.example.muyu_app.core.designsystem.components.MerchantTab
import com.example.muyu_app.core.designsystem.components.MuyuTab
import com.example.muyu_app.features.climate_monitoring.presentation.alert.WeatherAlertScreen
import com.example.muyu_app.features.climate_monitoring.presentation.onboarding.WeatherOnboardingScreen
import com.example.muyu_app.features.escrow_custody.presentation.onboarding.EscrowOnboardingScreen
import com.example.muyu_app.features.escrow_custody.presentation.payments.PaymentDetailsScreen
import com.example.muyu_app.features.escrow_custody.presentation.payments.PaymentHistoryScreen
import com.example.muyu_app.features.labor_execution.presentation.evidence.FundedPlotScreen
import com.example.muyu_app.features.labor_execution.presentation.evidence.RecordEvidenceScreen
import com.example.muyu_app.features.labor_execution.presentation.evidence.RequestExtensionScreen
import com.example.muyu_app.features.labor_execution.presentation.evidence.SelectMilestoneScreen
import com.example.muyu_app.features.plot_financing.presentation.visit.VisitOutcomeScreen
import com.example.muyu_app.features.labor_execution.presentation.sync.SyncConnectedScreen
import com.example.muyu_app.features.labor_execution.presentation.sync.SyncScreen
import com.example.muyu_app.features.plot_approval.presentation.home.FarmerHomeScreen
import com.example.muyu_app.features.plot_approval.presentation.myplots.FarmerMyPlotsScreen
import com.example.muyu_app.features.plot_approval.presentation.onboarding.ListPlotOnboardingScreen
import com.example.muyu_app.features.plot_approval.presentation.plotdetail.MyPlotScreen
import com.example.muyu_app.features.plot_approval.presentation.plotdetail.PlotDetailScreen
import com.example.muyu_app.features.plot_approval.presentation.visits.VisitRequestsScreen
import com.example.muyu_app.features.plot_approval.presentation.publish.PublishSuccessScreen
import com.example.muyu_app.features.plot_approval.presentation.publish.PublishPlotInfoScreen
import com.example.muyu_app.features.plot_approval.presentation.publish.PublishPlotLocationScreen
import com.example.muyu_app.features.plot_financing.presentation.alerts.MerchantAlertsScreen
import com.example.muyu_app.features.plot_financing.presentation.evidence.MerchantApproveRejectScreen
import com.example.muyu_app.features.plot_financing.presentation.evidence.MerchantConfirmApprovalScreen
import com.example.muyu_app.features.plot_financing.presentation.evidence.MerchantMilestoneApprovedScreen
import com.example.muyu_app.features.plot_financing.presentation.evidence.MerchantMilestoneEvidenceScreen
import com.example.muyu_app.features.plot_financing.presentation.evidence.MerchantRejectionReasonScreen
import com.example.muyu_app.features.plot_financing.presentation.evidence.MerchantReviewOutcomeScreen
import com.example.muyu_app.features.plot_financing.presentation.catalog.MerchantCatalogScreen
import com.example.muyu_app.features.plot_financing.presentation.fund.FundPlotScreen
import com.example.muyu_app.features.plot_financing.presentation.fund.FundSuccessScreen
import com.example.muyu_app.features.escrow_custody.presentation.payments.MerchantPaymentHistoryScreen
import com.example.muyu_app.features.plot_financing.presentation.myplots.MerchantMyPlotsScreen
import com.example.muyu_app.features.plot_financing.presentation.plotdetail.MerchantPlotDetailScreen
import com.example.muyu_app.features.plot_financing.presentation.publish.PublishPlotFinancingScreen
import com.example.muyu_app.features.user_management.presentation.loading.LoadingScreen
import com.example.muyu_app.features.user_management.presentation.login.LoginScreen
import com.example.muyu_app.features.user_management.presentation.pin.SafetyPinScreen
import com.example.muyu_app.features.user_management.presentation.profile.MerchantProfileScreen
import com.example.muyu_app.features.user_management.presentation.profile.ProfileScreen
import com.example.muyu_app.features.user_management.presentation.register.RegisterScreen
import com.example.muyu_app.features.user_management.presentation.resetpassword.ResetPasswordScreen
import com.example.muyu_app.features.user_management.presentation.role.RoleSelectionScreen
import com.example.muyu_app.features.user_management.presentation.legal.TermsAndConditionsScreen
import com.example.muyu_app.ui.splash.SplashScreen
import kotlinx.coroutines.delay

private enum class WalkthroughStep {
    Splash,
    Login,
    Register,
    ResetPassword,
    SafetyPin,
    Loading,
    OnboardingPlot,
    OnboardingEscrow,
    OnboardingWeather,
    Role,
    PlotDetail,
    FarmerHome,
    FarmerMyPlots,
    PublishInfo,
    PublishLocation,
    PublishFinancing,
    PublishSuccess,
    MyPlot,
    VisitRequests,
    FundedPlot,
    VisitOutcome,
    RequestExtension,
    SelectMilestone,
    RecordEvidence,
    WeatherAlert,
    Sync,
    SyncConnected,
    Profile,
    PaymentHistory,
    PaymentDetails,
    MerchantCatalog,
    MerchantPlotDetail,
    FundPlot,
    FundSuccess,
    MerchantMyPlots,
    MerchantAlerts,
    MerchantMilestoneEvidence,
    MerchantApproveReject,
    MerchantConfirmApproval,
    MerchantMilestoneApproved,
    MerchantRejectionReason,
    MerchantReviewOutcome,
    Terms,
}

private val PublicSteps = setOf(
    WalkthroughStep.Splash,
    WalkthroughStep.Login,
    WalkthroughStep.Register,
    WalkthroughStep.ResetPassword,
    WalkthroughStep.SafetyPin,
    WalkthroughStep.Terms,
)

@Composable
fun AppNavHost() {
    var step by rememberSaveable { mutableStateOf(WalkthroughStep.Splash) }
    var tab by rememberSaveable { mutableStateOf<String?>(null) }
    var isMerchant by rememberSaveable { mutableStateOf(false) }
    var termsReturnStep by rememberSaveable { mutableStateOf(WalkthroughStep.Register) }
    val container = LocalAppContainer.current
    val languages = container.languages
    val appLanguage = languages.current(isMerchant)
    val view = LocalView.current
    val currentTab = tab?.let { name -> MuyuTab.entries.find { it.name == name } }

    val selectMerchantTab: (MerchantTab) -> Unit = { selected ->
        tab = null
        step = when (selected) {
            MerchantTab.Catalog -> WalkthroughStep.MerchantCatalog
            MerchantTab.MyPlots -> WalkthroughStep.MerchantMyPlots
            MerchantTab.Alerts -> WalkthroughStep.MerchantAlerts
            MerchantTab.Payments -> WalkthroughStep.PaymentHistory
            MerchantTab.Profile -> WalkthroughStep.Profile
        }
    }

    val merchantBar: @Composable BoxScope.(MerchantTab) -> Unit = { selected ->
        MerchantBottomBar(
            selectedTab = selected,
            onTabSelected = selectMerchantTab,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    val selectTab: (MuyuTab) -> Unit = { selected ->
        when (selected) {
            MuyuTab.Home -> {
                tab = null
                step = if (isMerchant) {
                    WalkthroughStep.MerchantCatalog
                } else {
                    WalkthroughStep.FarmerHome
                }
            }
            MuyuTab.Sync -> {
                tab = null
                step = WalkthroughStep.Sync
            }
            MuyuTab.Payments -> {
                tab = null
                step = WalkthroughStep.PaymentHistory
            }
            MuyuTab.Profile -> {
                tab = null
                step = WalkthroughStep.Profile
            }
            MuyuTab.Plot -> {
                tab = null
                step = WalkthroughStep.FarmerMyPlots
            }
            else -> {
                tab = selected.name
            }
        }
    }

    val goToLogin: () -> Unit = {
        container.clearSession()
        tab = null
        isMerchant = false
        step = WalkthroughStep.Login
    }

    val greenHeader = step in setOf(
        WalkthroughStep.PlotDetail,
        WalkthroughStep.FarmerHome,
        WalkthroughStep.FarmerMyPlots,
        WalkthroughStep.PublishInfo,
        WalkthroughStep.PublishLocation,
        WalkthroughStep.PublishFinancing,
        WalkthroughStep.PublishSuccess,
        WalkthroughStep.MyPlot,
        WalkthroughStep.FundedPlot,
        WalkthroughStep.VisitOutcome,
        WalkthroughStep.RequestExtension,
        WalkthroughStep.SelectMilestone,
        WalkthroughStep.RecordEvidence,
        WalkthroughStep.WeatherAlert,
        WalkthroughStep.Sync,
        WalkthroughStep.SyncConnected,
        WalkthroughStep.Profile,
        WalkthroughStep.PaymentHistory,
        WalkthroughStep.PaymentDetails,
        WalkthroughStep.MerchantCatalog,
        WalkthroughStep.MerchantPlotDetail,
        WalkthroughStep.FundPlot,
        WalkthroughStep.FundSuccess,
        WalkthroughStep.MerchantMyPlots,
        WalkthroughStep.MerchantAlerts,
        WalkthroughStep.MerchantMilestoneEvidence,
        WalkthroughStep.MerchantApproveReject,
        WalkthroughStep.MerchantConfirmApproval,
        WalkthroughStep.MerchantMilestoneApproved,
        WalkthroughStep.MerchantRejectionReason,
        WalkthroughStep.MerchantReviewOutcome,
    )

    val darkSheet = (
            step in setOf(
                WalkthroughStep.Login,
                WalkthroughStep.Register,
                WalkthroughStep.ResetPassword,
                WalkthroughStep.SafetyPin,
            ) || greenHeader
            ) && currentTab == null

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !greenHeader
            controller.isAppearanceLightNavigationBars = !darkSheet
        }
    }

    ProvideAppLocale(
        language = appLanguage,
        onToggle = { languages.toggle(isMerchant) },
    ) {
    val needsLogin = step !in PublicSteps && container.fakeStore.currentUser == null
    LaunchedEffect(needsLogin) {
        if (needsLogin) {
            tab = null
            isMerchant = false
            step = WalkthroughStep.Login
        }
    }
    if (needsLogin) return@ProvideAppLocale

    val afterSplash = if (container.fakeStore.currentUser != null) {
        WalkthroughStep.Role
    } else {
        WalkthroughStep.Login
    }

    when (step) {
        WalkthroughStep.Splash -> {
            LaunchedEffect(Unit) {
                delay(1500)
                step = afterSplash
            }
            SplashScreen(onContinue = { step = afterSplash })
        }
        WalkthroughStep.Login -> LoginScreen(
            onLoginSuccess = { step = WalkthroughStep.Loading },
            onRegister = { step = WalkthroughStep.Register },
            onForgotPassword = { step = WalkthroughStep.ResetPassword },
        )
        WalkthroughStep.Register -> RegisterScreen(
            onRegister = goToLogin,
            onLogin = goToLogin,
            onTerms = {
                termsReturnStep = WalkthroughStep.Register
                step = WalkthroughStep.Terms
            },
        )
        WalkthroughStep.Terms -> TermsAndConditionsScreen(onBack = { step = termsReturnStep })
        WalkthroughStep.ResetPassword -> ResetPasswordScreen(
            onNext = { step = WalkthroughStep.SafetyPin },
        )
        WalkthroughStep.SafetyPin -> SafetyPinScreen(
            onAccept = goToLogin,
        )
        WalkthroughStep.Loading -> LoadingScreen(
            onFinished = { step = WalkthroughStep.OnboardingPlot },
        )
        WalkthroughStep.OnboardingPlot -> ListPlotOnboardingScreen(
            onNext = { step = WalkthroughStep.OnboardingEscrow },
        )
        WalkthroughStep.OnboardingEscrow -> EscrowOnboardingScreen(
            onNext = { step = WalkthroughStep.OnboardingWeather },
        )
        WalkthroughStep.OnboardingWeather -> WeatherOnboardingScreen(
            onStart = { step = WalkthroughStep.Role },
        )
        WalkthroughStep.Role -> RoleSelectionScreen(
            onNext = { step = WalkthroughStep.PlotDetail },
            onFarmerSelected = {
                isMerchant = false
                step = WalkthroughStep.FarmerHome
            },
            onMerchantSelected = {
                isMerchant = true
                step = WalkthroughStep.MerchantCatalog
            },
        )
        WalkthroughStep.PlotDetail -> TabOrContent(currentTab, selectTab) {
            PlotDetailScreen(onTabSelected = selectTab)
        }
        WalkthroughStep.FarmerHome -> TabOrContent(currentTab, selectTab) {
            FarmerHomeScreen(
                onWeather = { step = WalkthroughStep.WeatherAlert },
                onTabSelected = selectTab,
                onPlotSelected = {
                    container.farmerPlotReturn = FarmerPlotReturn.Home
                    step = WalkthroughStep.FundedPlot
                },
            )
        }
        WalkthroughStep.FarmerMyPlots -> TabOrContent(currentTab, selectTab) {
            FarmerMyPlotsScreen(
                onPublish = {
                    container.plotDraft.clear()
                    step = WalkthroughStep.PublishInfo
                },
                onWeatherAlert = { step = WalkthroughStep.WeatherAlert },
                onTabSelected = selectTab,
                onViewPlot = { plot ->
                    container.selectedPlot = plot
                    container.farmerPlotReturn = FarmerPlotReturn.MyPlots
                    step = if (plot.status == "funded") {
                        WalkthroughStep.FundedPlot
                    } else {
                        WalkthroughStep.MyPlot
                    }
                },
            )
        }
        WalkthroughStep.PublishInfo -> TabOrContent(currentTab, selectTab) {
            PublishPlotInfoScreen(
                onNextLocation = { step = WalkthroughStep.PublishLocation },
                onBack = {
                    step = if (container.plotDraft.isEditing) {
                        WalkthroughStep.MyPlot
                    } else {
                        WalkthroughStep.FarmerMyPlots
                    }
                },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.PublishLocation -> TabOrContent(currentTab, selectTab) {
            PublishPlotLocationScreen(
                onNextFinancing = { step = WalkthroughStep.PublishFinancing },
                onBack = { step = WalkthroughStep.PublishInfo },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.PublishFinancing -> TabOrContent(currentTab, selectTab) {
            PublishPlotFinancingScreen(
                onBack = { step = WalkthroughStep.PublishLocation },
                onTabSelected = selectTab,
                onPublished = { step = WalkthroughStep.PublishSuccess },
                onUpdated = { step = WalkthroughStep.MyPlot },
            )
        }
        WalkthroughStep.PublishSuccess -> TabOrContent(currentTab, selectTab) {
            PublishSuccessScreen(
                onViewPlot = { step = WalkthroughStep.MyPlot },
                onHome = { step = WalkthroughStep.FarmerHome },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.MyPlot -> TabOrContent(currentTab, selectTab) {
            MyPlotScreen(
                onBack = {
                    step = when (container.farmerPlotReturn) {
                        FarmerPlotReturn.Home -> WalkthroughStep.FarmerHome
                        FarmerPlotReturn.MyPlots -> WalkthroughStep.FarmerMyPlots
                    }
                },
                onEditPlot = {
                    container.selectedPlot?.let { plot ->
                        container.plotDraft.clear()
                        container.plotDraft.loadFrom(plot)
                        step = WalkthroughStep.PublishInfo
                    }
                },
                onTabSelected = selectTab,
                onViewVisitRequests = { step = WalkthroughStep.VisitRequests },
                fromHome = container.farmerPlotReturn == FarmerPlotReturn.Home,
            )
        }
        WalkthroughStep.VisitRequests -> TabOrContent(currentTab, selectTab) {
            VisitRequestsScreen(
                onBack = { step = WalkthroughStep.MyPlot },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.FundedPlot -> TabOrContent(currentTab, selectTab) {
            val returnStep = when (container.farmerPlotReturn) {
                FarmerPlotReturn.Home -> WalkthroughStep.FarmerHome
                FarmerPlotReturn.MyPlots -> WalkthroughStep.FarmerMyPlots
            }
            val bottomTab = when (container.farmerPlotReturn) {
                FarmerPlotReturn.Home -> MuyuTab.Home
                FarmerPlotReturn.MyPlots -> MuyuTab.Plot
            }
            FundedPlotScreen(
                onBack = { step = returnStep },
                onRecordEvidence = { step = WalkthroughStep.SelectMilestone },
                onRequestExtension = { step = WalkthroughStep.RequestExtension },
                onTabSelected = selectTab,
                selectedBottomTab = bottomTab,
            )
        }
        WalkthroughStep.VisitOutcome -> TabOrContent(currentTab, selectTab) {
            VisitOutcomeScreen(
                onBack = { step = WalkthroughStep.FundedPlot },
                onSaved = { step = WalkthroughStep.FundedPlot },
                onReleased = { step = WalkthroughStep.FarmerHome },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.RequestExtension -> TabOrContent(currentTab, selectTab) {
            RequestExtensionScreen(
                onBack = { step = WalkthroughStep.FundedPlot },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.SelectMilestone -> TabOrContent(currentTab, selectTab) {
            SelectMilestoneScreen(
                onBack = { step = WalkthroughStep.FundedPlot },
                onMilestoneSelected = { step = WalkthroughStep.RecordEvidence },
            )
        }
        WalkthroughStep.RecordEvidence -> TabOrContent(currentTab, selectTab) {
            RecordEvidenceScreen(
                onBack = { step = WalkthroughStep.SelectMilestone },
                onSaved = { step = WalkthroughStep.FundedPlot },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.WeatherAlert -> TabOrContent(currentTab, selectTab) {
            WeatherAlertScreen(
                onBack = {
                    step = when (currentTab) {
                        MuyuTab.Plot -> WalkthroughStep.FarmerMyPlots
                        else -> WalkthroughStep.FarmerHome
                    }
                },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.Sync -> TabOrContent(currentTab, selectTab) {
            SyncScreen(
                onBack = { step = WalkthroughStep.FarmerHome },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.SyncConnected -> TabOrContent(currentTab, selectTab) {
            SyncScreen(
                onBack = { step = WalkthroughStep.FarmerHome },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.Profile -> TabOrContent(currentTab, selectTab) {
            if (isMerchant) {
                MerchantProfileScreen(
                    onTabSelected = selectMerchantTab,
                    onLogout = goToLogin,
                    onTerms = {
                        termsReturnStep = WalkthroughStep.Profile
                        step = WalkthroughStep.Terms
                    },
                )
            } else {
                ProfileScreen(
                    onTabSelected = selectTab,
                    onLogout = goToLogin,
                    onTerms = {
                        termsReturnStep = WalkthroughStep.Profile
                        step = WalkthroughStep.Terms
                    },
                )
            }
        }
        WalkthroughStep.PaymentHistory -> TabOrContent(currentTab, selectTab) {
            if (isMerchant) {
                MerchantPaymentHistoryScreen(
                    onBack = { step = WalkthroughStep.MerchantCatalog },
                    onTabSelected = selectMerchantTab,
                    onPaymentClick = { step = WalkthroughStep.PaymentDetails },
                )
            } else {
                PaymentHistoryScreen(
                    onBack = { step = WalkthroughStep.FarmerHome },
                    onTabSelected = selectTab,
                    onPaymentClick = { step = WalkthroughStep.PaymentDetails },
                )
            }
        }
        WalkthroughStep.PaymentDetails -> TabOrContent(currentTab, selectTab) {
            PaymentDetailsScreen(
                onBack = { step = WalkthroughStep.PaymentHistory },
                onTabSelected = selectTab,
            )
        }
        WalkthroughStep.MerchantCatalog -> MerchantCatalogScreen(
            onPlotClick = {
                container.merchantPlotReturn = MerchantPlotReturn.Catalog
                step = WalkthroughStep.MerchantPlotDetail
            },
            onAlertsClick = { step = WalkthroughStep.MerchantAlerts },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantPlotDetail -> MerchantPlotDetailScreen(
            onBack = {
                step = when (container.merchantPlotReturn) {
                    MerchantPlotReturn.Catalog -> WalkthroughStep.MerchantCatalog
                    MerchantPlotReturn.MyPlots -> WalkthroughStep.MerchantMyPlots
                    MerchantPlotReturn.Alerts -> WalkthroughStep.MerchantAlerts
                }
            },
            onFundPlot = { step = WalkthroughStep.FundPlot },
            onViewEvidence = { step = WalkthroughStep.MerchantMilestoneEvidence },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.FundPlot -> FundPlotScreen(
            onBack = { step = WalkthroughStep.MerchantPlotDetail },
            onAcceptTerms = { step = WalkthroughStep.FundSuccess },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.FundSuccess -> FundSuccessScreen(
            onBack = { step = WalkthroughStep.FundPlot },
            onViewPlot = { step = WalkthroughStep.MerchantPlotDetail },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantMyPlots -> MerchantMyPlotsScreen(
            onPlotClick = {
                container.merchantPlotReturn = MerchantPlotReturn.MyPlots
                step = WalkthroughStep.MerchantPlotDetail
            },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantAlerts -> MerchantAlertsScreen(
            onBack = { step = WalkthroughStep.MerchantCatalog },
            onPlotClick = {
                container.merchantPlotReturn = MerchantPlotReturn.Alerts
                step = WalkthroughStep.MerchantPlotDetail
            },
            onEvidenceClick = { step = WalkthroughStep.MerchantMilestoneEvidence },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantMilestoneEvidence -> MerchantMilestoneEvidenceScreen(
            onBack = {
                step = when (container.merchantEvidenceOrigin) {
                    MerchantEvidenceOrigin.MyPlots -> WalkthroughStep.MerchantMyPlots
                    MerchantEvidenceOrigin.Alerts -> WalkthroughStep.MerchantAlerts
                    MerchantEvidenceOrigin.PlotDetail -> WalkthroughStep.MerchantPlotDetail
                }
            },
            onReview = { step = WalkthroughStep.MerchantApproveReject },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantApproveReject -> MerchantApproveRejectScreen(
            onBack = { step = WalkthroughStep.MerchantMilestoneEvidence },
            onApprove = { step = WalkthroughStep.MerchantConfirmApproval },
            onReject = { step = WalkthroughStep.MerchantRejectionReason },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantConfirmApproval -> MerchantConfirmApprovalScreen(
            onBack = { step = WalkthroughStep.MerchantApproveReject },
            onConfirmed = { step = WalkthroughStep.MerchantMilestoneApproved },
            onBackToEvidence = { step = WalkthroughStep.MerchantMilestoneEvidence },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantMilestoneApproved -> MerchantMilestoneApprovedScreen(
            onBack = {
                step = when (container.merchantEvidenceOrigin) {
                    MerchantEvidenceOrigin.MyPlots -> WalkthroughStep.MerchantMyPlots
                    MerchantEvidenceOrigin.Alerts -> WalkthroughStep.MerchantAlerts
                    MerchantEvidenceOrigin.PlotDetail -> WalkthroughStep.MerchantPlotDetail
                }
            },
            onViewPlot = {
                // Abre la parcela seleccionada y conserva el origen para regresar.
                container.merchantPlotReturn = when (container.merchantEvidenceOrigin) {
                    MerchantEvidenceOrigin.MyPlots -> MerchantPlotReturn.MyPlots
                    MerchantEvidenceOrigin.Alerts -> MerchantPlotReturn.Alerts
                    MerchantEvidenceOrigin.PlotDetail -> container.merchantPlotReturn
                }
                step = WalkthroughStep.MerchantPlotDetail
            },
            onViewReceipt = { step = WalkthroughStep.PaymentHistory },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantRejectionReason -> MerchantRejectionReasonScreen(
            onBack = { step = WalkthroughStep.MerchantApproveReject },
            onConfirmed = { step = WalkthroughStep.MerchantReviewOutcome },
            onCancelAndReview = { step = WalkthroughStep.MerchantMilestoneEvidence },
            onTabSelected = selectMerchantTab,
        )
        WalkthroughStep.MerchantReviewOutcome -> MerchantReviewOutcomeScreen(
            onBack = {
                step = when (container.merchantEvidenceOrigin) {
                    MerchantEvidenceOrigin.MyPlots -> WalkthroughStep.MerchantMyPlots
                    MerchantEvidenceOrigin.Alerts -> WalkthroughStep.MerchantAlerts
                    MerchantEvidenceOrigin.PlotDetail -> WalkthroughStep.MerchantPlotDetail
                }
            },
            onViewMilestone = { step = WalkthroughStep.MerchantMilestoneEvidence },
            onBackToPlot = {
                step = when (container.merchantEvidenceOrigin) {
                    MerchantEvidenceOrigin.MyPlots -> WalkthroughStep.MerchantMyPlots
                    MerchantEvidenceOrigin.Alerts -> WalkthroughStep.MerchantAlerts
                    MerchantEvidenceOrigin.PlotDetail -> WalkthroughStep.MerchantPlotDetail
                }
            },
            onTabSelected = selectMerchantTab,
        )
    }
    }
}

@Composable
private fun TabOrContent(
    currentTab: MuyuTab?,
    onTabSelected: (MuyuTab) -> Unit,
    content: @Composable () -> Unit,
) {
    if (currentTab == null) {
        content()
    } else {
        BlankTabScreen(
            tab = currentTab,
            onTabSelected = onTabSelected,
        )
    }
}
