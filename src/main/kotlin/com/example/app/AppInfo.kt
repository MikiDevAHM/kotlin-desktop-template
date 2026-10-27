package com.example.app

import com.example.app.update.Version
import java.util.*

object AppInfo {
    private val properties = Properties().apply {
        AppInfo::class.java.getResourceAsStream("/app.properties")?.use { load(it) }
    }

    val name: String = properties.getProperty("name", "App")
    val version: Version = Version.parseOrNull(properties.getProperty("version").orEmpty()) ?: Version(0, 0, 0)
    val githubOwner: String = properties.getProperty("githubOwner").orEmpty()
    val githubRepo: String = properties.getProperty("githubRepo").orEmpty()
}
