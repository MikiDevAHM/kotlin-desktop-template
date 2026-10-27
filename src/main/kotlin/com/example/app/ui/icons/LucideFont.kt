package com.example.app.ui.icons

import androidx.compose.ui.text.font.FontFamily
import java.util.concurrent.ConcurrentHashMap
import org.jetbrains.skia.Data
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.Typeface
import androidx.compose.ui.text.platform.Font as ComposeFont

object LucideFont {
    private const val RESOURCE = "fonts/lucide.ttf"
    private val fonts = ConcurrentHashMap<Int, Font>()

    val typeface: Typeface by lazy {
        val bytes = LucideFont::class.java.getResourceAsStream("/$RESOURCE")?.use { it.readBytes() }
            ?: error("Missing /$RESOURCE, run: groovy scripts/generate-lucide.groovy")
        requireNotNull(FontMgr.default.makeFromData(Data.makeFromBytes(bytes))) { "Could not load /$RESOURCE as a font" }
    }

    val family: FontFamily by lazy { FontFamily(ComposeFont(resource = RESOURCE)) }

    fun font(sizePx: Float): Font {
        val key = (sizePx * 4).toInt().coerceAtLeast(1)
        if (fonts.size > 64) fonts.clear()
        return fonts.getOrPut(key) { Font(typeface, key / 4f).apply { isSubpixel = true } }
    }
}
