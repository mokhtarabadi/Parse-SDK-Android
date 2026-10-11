@file:Suppress("unused")

package com.parse.ktx

import com.parse.ParseConfig
import com.parse.ParseExperiments
import java.util.Date

/**
 * Evaluates every requested experiment key against a fetched config, preserving input order.
 * Top-level (not a receiver) so it stays unit-testable without SDK init. See
 * [ParseExperiments.evaluateAll]. Fail-closed entries read `-1`.
 */
fun evaluateAll(
    config: ParseConfig?,
    keys: List<String>?,
    userId: String?,
): Map<String, Int> = ParseExperiments.evaluateAll(config, keys, userId)

/**
 * Variant check with the new-user targeting gate (`user_joined >= start_iso`).
 * See [ParseExperiments.isVariantForNewUser].
 */
fun isVariantForNewUser(
    config: ParseConfig?,
    key: String?,
    userId: String?,
    joinedAt: Date?,
): Boolean = ParseExperiments.isVariantForNewUser(config, key, userId, joinedAt)

/**
 * Plain local variant check on a fetched config. See [ParseExperiments.isVariant].
 */
fun isVariant(config: ParseConfig?, key: String?, userId: String?): Boolean =
    ParseExperiments.isVariant(config, key, userId)
