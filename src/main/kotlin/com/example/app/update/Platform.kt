package com.example.app.update

import java.nio.file.Files
import java.nio.file.Path

enum class OsFamily {
    Windows,
    MacOs,
    Linux;

    companion object {
        val current: OsFamily by lazy {
            val name = System.getProperty("os.name").lowercase()
            when {
                "win" in name -> Windows
                "mac" in name || "darwin" in name -> MacOs
                else -> Linux
            }
        }
    }
}

enum class Arch(val tokens: Set<String>) {
    X64(setOf("x64", "x86_64", "amd64")),
    Arm64(setOf("arm64", "aarch64"));

    companion object {
        val current: Arch by lazy {
            when (System.getProperty("os.arch").lowercase()) {
                "aarch64", "arm64" -> Arm64
                else -> X64
            }
        }
    }
}

object LinuxDistro {
    private val debianFamily = setOf("debian", "ubuntu", "linuxmint", "pop", "elementary")
    private val rpmFamily = setOf("fedora", "rhel", "centos", "rocky", "almalinux", "suse", "opensuse", "opensuse-leap", "sles")

    fun packageExtensions(osRelease: Path = Path.of("/etc/os-release")): List<String> {
        val ids = runCatching { Files.readAllLines(osRelease) }.getOrDefault(emptyList())
            .filter { it.startsWith("ID=") || it.startsWith("ID_LIKE=") }
            .flatMap { it.substringAfter('=').trim('"').split(' ') }
            .map { it.lowercase() }
        return when {
            ids.any { it in debianFamily } -> listOf("deb")
            ids.any { it in rpmFamily } -> listOf("rpm")
            else -> emptyList()
        }
    }
}
