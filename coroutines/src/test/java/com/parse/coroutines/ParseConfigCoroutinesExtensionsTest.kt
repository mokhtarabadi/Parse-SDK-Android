package com.parse.coroutines

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the pure experiments wrappers. The network suspend wrapper
 * (`fetchAbEvaluateConfig`) needs SDK init and is covered indirectly by the
 * controller tests in `:parse`. */
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
