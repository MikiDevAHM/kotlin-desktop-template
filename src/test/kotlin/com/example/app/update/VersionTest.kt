package com.example.app.update

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VersionTest {
    @Test
    fun parsesPlainAndPrefixedVersions() {
        assertEquals(Version.parseOrNull("1.2.3"), Version(1, 2, 3))
        assertEquals(Version.parseOrNull("v1.2.3"), Version(1, 2, 3))
        assertEquals(Version.parseOrNull("v1.2.3-beta.1+build.5"), Version(1, 2, 3, "beta.1"))
    }

    @Test
    fun rejectsInvalidVersions() {
        assertNull(Version.parseOrNull("latest"))
        assertNull(Version.parseOrNull("1.2"))
        assertNull(Version.parseOrNull(""))
    }

    @Test
    fun ordersByCoreNumbers() {
        assertTrue(Version(1, 10, 0) > Version(1, 9, 9))
        assertTrue(Version(2, 0, 0) > Version(1, 99, 99))
        assertTrue(Version(1, 0, 1) > Version(1, 0, 0))
    }

    @Test
    fun releaseBeatsPreRelease() {
        assertTrue(Version(1, 0, 0) > Version(1, 0, 0, "rc.1"))
        assertTrue(Version(1, 0, 0, "rc.2") > Version(1, 0, 0, "rc.1"))
        assertTrue(Version(1, 0, 0, "rc.10") > Version(1, 0, 0, "rc.9"))
        assertTrue(Version(1, 0, 0, "rc") > Version(1, 0, 0, "beta"))
        assertTrue(Version(1, 0, 0, "rc.1") > Version(1, 0, 0, "rc"))
    }
}
