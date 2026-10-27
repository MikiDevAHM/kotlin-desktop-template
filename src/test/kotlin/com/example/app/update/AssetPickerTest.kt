package com.example.app.update

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AssetPickerTest {
    private fun asset(name: String) = GitHubAsset(name = name, downloadUrl = "https://example.com/$name")

    private val assets = listOf(
        asset("windows-x64-App-1.0.0.msi"),
        asset("macos-arm64-App-1.0.0.dmg"),
        asset("macos-x64-App-1.0.0.dmg"),
        asset("linux-x64-app_1.0.0-1_amd64.deb"),
        asset("linux-x64-app-1.0.0-1.x86_64.rpm"),
        asset("notes.txt"),
    )

    @Test
    fun picksWindowsInstaller() {
        assertEquals("windows-x64-App-1.0.0.msi", AssetPicker.pick(assets, OsFamily.Windows, Arch.X64)?.name)
    }

    @Test
    fun picksMacAssetMatchingArchitecture() {
        assertEquals("macos-arm64-App-1.0.0.dmg", AssetPicker.pick(assets, OsFamily.MacOs, Arch.Arm64)?.name)
        assertEquals("macos-x64-App-1.0.0.dmg", AssetPicker.pick(assets, OsFamily.MacOs, Arch.X64)?.name)
    }

    @Test
    fun picksLinuxPackageForDistroFamily() {
        assertEquals("linux-x64-app_1.0.0-1_amd64.deb", AssetPicker.pick(assets, OsFamily.Linux, Arch.X64, listOf("deb"))?.name)
        assertEquals("linux-x64-app-1.0.0-1.x86_64.rpm", AssetPicker.pick(assets, OsFamily.Linux, Arch.X64, listOf("rpm"))?.name)
    }

    @Test
    fun returnsNullWhenNothingMatches() {
        assertNull(AssetPicker.pick(assets, OsFamily.Linux, Arch.X64, emptyList()))
        assertNull(AssetPicker.pick(assets, OsFamily.Windows, Arch.Arm64))
    }
}
