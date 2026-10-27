package com.example.app.settings

import java.util.prefs.Preferences

object AppSettings {
    private val preferences: Preferences = Preferences.userNodeForPackage(AppSettings::class.java)

    var language: String?
        get() = preferences.get("language", null)
        set(value) {
            if (value == null) preferences.remove("language") else preferences.put("language", value)
        }
}
