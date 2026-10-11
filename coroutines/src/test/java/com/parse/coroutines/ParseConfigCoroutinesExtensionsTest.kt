package com.parse.coroutines

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the pure offline experiments wrappers. No network involved. */
class ParseConfigCoroutinesExtensionsTest {

    @Test
    fun evaluateAllExperimentsNullSafe() {
        assertTrue(evaluateAllExperiments(null, listOf("a"), "u").isEmpty())
        assertTrue(evaluateAllExperiments(null, null, "u").isEmpty())
    }

    @Test
    fun isVariantForNewUserNullSafe() {
        assertFalse(isVariantForNewUser(null, "k", "u", null))
    }
}
