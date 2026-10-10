# MUYU — bounded contexts (Android)

Each folder under `features/` is a bounded context. Shared infrastructure lives in `core/`.

## `core/`

| Path | Responsibility |
|------|----------------|
| `core/di/` | App container, composition locals, network module |
| `core/designsystem/` | Shared UI components (bottom bars, headers) |
| `core/image/` | Asset loading (Coil) |
| `core/i18n/` | Language selection |
| `core/location/` | GPS permissions, geocoding, map WebView (`LocationSupport`, `PlotLocationMap`) |
| `core/network/` | Fake API store, interceptor, connectivity (`NetworkConnectivity`) |
| `core/plot/` | Selected plot session in memory (`SelectedPlotSupport`: `liveFundedPlot`, `rememberLiveFundedPlot`) |

## `features/user_management/`

Login, PIN, profiles, auth API DTOs, `User` and `AuthRepository`.

## `features/plot_approval/`

Farmer listing lifecycle: create/update plot, publish wizard, farmer home/my plots, **shared domain** `Plot`, `MilestoneState`, `PlotFinance`, milestone queries (`PlotMilestoneExtensions`).

| Layer | Examples |
|-------|----------|
| `application/` | `CreatePlotUseCase`, `UpdatePlotUseCase` |
| `domain/` | `Plot`, `PlotRepository`, milestone extensions |
| `presentation/` | `PublishPlot*`, `FarmerHomeScreen`, `MyPlotScreen` |

## `features/plot_financing/`

Merchant catalog, fund plot, financed plot UI, milestone review, field visit outcome.

| Layer | Examples |
|-------|----------|
| `application/` | `FundPlotUseCase` |
| `presentation/catalog/` | Merchant catalog |
| `presentation/fund/` | Funding flow |
| `presentation/evidence/` | Approve/reject, milestone evidence |
| `presentation/visit/` | `VisitOutcomeScreen` |
| `presentation/myplots/` | Merchant “my plots” helpers |

## `features/labor_execution/`

Farmer execution after funding: funded plot detail, record evidence, sync queue, extensions.

| Path | Examples |
|------|----------|
| `presentation/evidence/` | `FundedPlotScreen`, `RecordEvidenceScreen`, `EvidenceLocationSupport` |
| `presentation/sync/` | `SyncScreen`, `SyncConnectedScreen`, `EvidenceSyncSupport` |

## `features/climate_monitoring/`

Weather onboarding and alerts (Open-Meteo).

## `features/escrow_custody/`

Payments and escrow UI: `PaymentDetailsScreen`, `MerchantPaymentHistoryScreen`.

## `navigation/` + `ui/`

App-wide navigation graph (`AppNavHost`) and theme/splash entry points.

## Cross-context rules

- **Plot aggregate** stays in `plot_approval/domain` (used by all contexts).
- **GPS/maps** → `core/location` (not publish wizard only).
- **Selected plot refresh** → `core/plot` (UI session, not labor-specific).
- **Evidence geo validation** → `labor_execution` (`EvidenceLocationSupport`).
- **Funding use case** → `plot_financing/application`, not `plot_approval`.
