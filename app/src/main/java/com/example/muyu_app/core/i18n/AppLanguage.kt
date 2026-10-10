package com.example.muyu_app.core.i18n

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val tag: String,
) {
    English("EN", "en"),
    SpanishLatin("ES", "es-419");

    fun toLocale(): Locale = Locale.forLanguageTag(tag)

    fun toggled(): AppLanguage = if (this == English) SpanishLatin else English
}

class LanguagePreferences {
    var farmer by mutableStateOf(AppLanguage.English)
    var merchant by mutableStateOf(AppLanguage.English)

    fun current(isMerchant: Boolean): AppLanguage = if (isMerchant) merchant else farmer

    fun toggle(isMerchant: Boolean) {
        if (isMerchant) {
            merchant = merchant.toggled()
        } else {
            farmer = farmer.toggled()
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.English }

val LocalToggleAppLanguage = staticCompositionLocalOf { {} }

@Composable
fun ProvideAppLocale(
    language: AppLanguage,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    val baseContext = LocalContext.current
    val viewConfig = LocalConfiguration.current
    val locale = language.toLocale()
    val localizedContext = remember(baseContext, language) {
        baseContext.wrapLocale(locale)
    }
    val localizedConfig = remember(viewConfig, language) {
        Configuration(viewConfig).apply {
            setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLocales(android.os.LocaleList(locale))
            }
        }
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalResources provides localizedContext.resources,
        LocalConfiguration provides localizedConfig,
        LocalAppLanguage provides language,
        LocalToggleAppLanguage provides onToggle,
        content = content,
    )
}

fun Context.wrapLocale(locale: Locale): Context {
    val activity = unwrapActivity()
    val config = Configuration(activity.resources.configuration)
    config.setLocale(locale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.setLocales(android.os.LocaleList(locale))
    }
    val localized = activity.createConfigurationContext(config)
    return LocaleAwareContextWrapper(activity, localized)
}

private fun Context.unwrapActivity(): Context {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is android.app.Activity) return ctx
        ctx = ctx.baseContext
    }
    return this
}

private class LocaleAwareContextWrapper(
    base: Context,
    private val localized: Context,
) : ContextWrapper(base) {
    override fun getResources(): Resources = localized.resources

    override fun getAssets(): AssetManager = localized.assets
}
