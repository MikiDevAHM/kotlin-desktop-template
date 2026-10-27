package com.example.app.update

object AssetPicker {
    private val allArchTokens = Arch.entries.flatMap { it.tokens }.toSet()

    fun extensions(os: OsFamily, linuxExtensions: List<String>): List<String> = when (os) {
        OsFamily.Windows -> listOf("msi", "exe")
        OsFamily.MacOs -> listOf("dmg", "pkg")
        OsFamily.Linux -> linuxExtensions
    }

    fun pick(
        assets: List<GitHubAsset>,
        os: OsFamily = OsFamily.current,
        arch: Arch = Arch.current,
        linuxExtensions: List<String> = LinuxDistro.packageExtensions(),
    ): GitHubAsset? {
        val candidates = extensions(os, linuxExtensions).flatMap { extension ->
            assets.filter { it.name.endsWith(".$extension", ignoreCase = true) }
        }
        return candidates.firstOrNull { hasToken(it.name, arch.tokens) }
            ?: candidates.firstOrNull { !hasToken(it.name, allArchTokens) }
    }

    private fun hasToken(name: String, tokens: Set<String>): Boolean {
        val lower = name.lowercase()
        return tokens.any { token ->
            Regex("(?<![a-z0-9])${Regex.escape(token)}(?![a-z0-9])").containsMatchIn(lower)
        }
    }
}
