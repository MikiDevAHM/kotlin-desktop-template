package com.example.app.ui.icons

@JvmInline
value class LucideGlyph(val codePoint: Int) {
    val text: String get() = codePoint.toChar().toString()
}
