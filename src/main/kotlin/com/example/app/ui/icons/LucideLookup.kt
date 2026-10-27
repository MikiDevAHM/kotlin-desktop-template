package com.example.app.ui.icons

import kotlinx.serialization.json.Json

private const val CODEPOINTS_RESOURCE = "/fonts/lucide-codepoints.json"

private val codePoints: Map<String, Int> by lazy {
    val text = LucideFont::class.java.getResourceAsStream(CODEPOINTS_RESOURCE)?.use { it.readBytes().decodeToString() }
        ?: error("Missing $CODEPOINTS_RESOURCE, run: groovy scripts/generate-lucide.groovy")
    Json.decodeFromString<Map<String, Int>>(text)
}

val Lucide.names: Set<String> get() = codePoints.keys

fun Lucide.byName(name: String): LucideGlyph? = codePoints[name]?.let(::LucideGlyph)
