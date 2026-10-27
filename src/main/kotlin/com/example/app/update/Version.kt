package com.example.app.update

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null,
) : Comparable<Version> {

    override fun compareTo(other: Version): Int {
        compareValuesBy(this, other, Version::major, Version::minor, Version::patch).let { if (it != 0) return it }
        return when {
            preRelease == other.preRelease -> 0
            preRelease == null -> 1
            other.preRelease == null -> -1
            else -> comparePreRelease(preRelease, other.preRelease)
        }
    }

    override fun toString(): String = "$major.$minor.$patch" + (preRelease?.let { "-$it" } ?: "")

    companion object {
        private val pattern = Regex("""^[vV]?(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z.-]+))?(?:\+[0-9A-Za-z.-]+)?$""")

        fun parseOrNull(raw: String): Version? {
            val match = pattern.matchEntire(raw.trim()) ?: return null
            val (major, minor, patch, preRelease) = match.destructured
            return Version(major.toInt(), minor.toInt(), patch.toInt(), preRelease.ifEmpty { null })
        }

        private fun comparePreRelease(a: String, b: String): Int {
            val left = a.split('.')
            val right = b.split('.')
            for (index in 0 until minOf(left.size, right.size)) {
                val x = left[index]
                val y = right[index]
                val xn = x.toLongOrNull()
                val yn = y.toLongOrNull()
                val result = when {
                    xn != null && yn != null -> xn.compareTo(yn)
                    xn != null -> -1
                    yn != null -> 1
                    else -> x.compareTo(y)
                }
                if (result != 0) return result
            }
            return left.size.compareTo(right.size)
        }
    }
}
