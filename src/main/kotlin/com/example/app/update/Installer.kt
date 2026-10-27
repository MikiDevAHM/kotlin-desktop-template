package com.example.app.update

import java.awt.Desktop
import java.net.URI
import java.nio.file.Path

object Installer {
    fun launch(file: Path, os: OsFamily = OsFamily.current) {
        val path = file.toString()
        val command = when (os) {
            OsFamily.Windows -> if (path.endsWith(".msi", ignoreCase = true)) listOf("msiexec", "/i", path) else listOf(path)
            OsFamily.MacOs -> listOf("open", path)
            OsFamily.Linux -> listOf("xdg-open", path)
        }
        ProcessBuilder(command).inheritIO().start()
    }

    fun openInBrowser(url: String, os: OsFamily = OsFamily.current) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI.create(url))
            return
        }
        val command = when (os) {
            OsFamily.Windows -> listOf("rundll32", "url.dll,FileProtocolHandler", url)
            OsFamily.MacOs -> listOf("open", url)
            OsFamily.Linux -> listOf("xdg-open", url)
        }
        ProcessBuilder(command).start()
    }
}
