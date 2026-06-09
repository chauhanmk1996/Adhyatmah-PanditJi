package com.app.panditji.utils.lang

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

object LocalizationUtility {
    fun applyLocalizationContext(baseContext: Context): Context {
        val baseLocale = getLocaleFromConfiguration(baseContext.resources.configuration)
        val currentLocale = LanguageSetting.getLanguageWithDefault(baseContext, LanguageSetting.getDefaultLanguage(baseContext))
        if (!baseLocale.toString().equals(currentLocale.toString(), ignoreCase = true)) {
            val context = LocalizationContext(baseContext)
           // val config = context.resources.configuration
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    val configuration = Configuration()
                    /* configuration.setLocale(locale)
                     configuration.setLayoutDirection(locale)
     */
                    configuration.setLocale(currentLocale)
                    val localeList = LocaleList(currentLocale)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)
                    context.createConfigurationContext(configuration)
                    /*config.setLocale(currentLocale)
                    val localeList = LocaleList(currentLocale)
                    LocaleList.setDefault(localeList)
                    config.setLocales(localeList)
                    context.createConfigurationContext( config)*/
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                    val configuration = Configuration()
                    configuration.setLocale(currentLocale)
                    context.createConfigurationContext(configuration)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    val configuration = Configuration()

                    configuration.locale = currentLocale
                    @Suppress("DEPRECATION")
                    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
                    context
                }
            }
        } else {
            return baseContext
        }
    }

    @Suppress("DEPRECATION")
    fun getLocaleFromConfiguration(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            configuration.locales.get(0)
        } else {
            configuration.locale
        }
    }
}
