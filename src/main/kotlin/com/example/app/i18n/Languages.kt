package com.example.app.i18n

import com.example.app.settings.AppSettings
import java.util.*

object Languages {
    val supported: List<Locale> = listOf(
        Locale.ENGLISH,
        Locale.of("pt", "BR"),
        Locale.GERMAN,
    )

    fun restore() {
        AppSettings.language?.let { Locale.setDefault(Locale.forLanguageTag(it)) }
    }

    fun apply(locale: Locale) {
        Locale.setDefault(locale)
        AppSettings.language = locale.toLanguageTag()
    }

    fun isSelected(locale: Locale): Boolean = Locale.getDefault().language == locale.language

    fun displayName(locale: Locale): String =
        locale.getDisplayLanguage(locale).replaceFirstChar { it.titlecase(locale) }
}
