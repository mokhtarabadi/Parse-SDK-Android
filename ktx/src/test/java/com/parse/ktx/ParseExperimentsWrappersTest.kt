package com.parse.ktx

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the experiments wrappers: delegation without SDK init. */
class ParseExperimentsWrappersTest {

    @Test
    fun evaluateAllNullConfigIsEmpty() {
        assertTrue(evaluateAll(null, listOf("a"), "u").isEmpty())
    }

    @Test
    fun evaluateAllNullKeysIsEmpty() {
        assertTrue(evaluateAll(null, null, "u").isEmpty())
    }

    @Test
    fun isVariantNullSafe() {
        assertFalse(isVariant(null, "k", "u"))
    }

    @Test
    fun isVariantForNewUserNullSafe() {
        assertFalse(isVariantForNewUser(null, "k", "u", null))
        assertEquals(false, isVariantForNewUser(null, null, null, null))
    }
}
