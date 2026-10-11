package com.parse.coroutines

import com.parse.ParseConfig
import com.parse.ParseExperiments
import java.util.Date

/**
 * Ordered bulk evaluation against a fetched config. Pure and offline-safe (no suspension
 * needed, kept here for namespace grouping). See `ParseExperiments.evaluateAll`.
 */
fun evaluateAllExperiments(
    config: ParseConfig?,
    keys: List<String>?,
    userId: String?,
): Map<String, Int> = ParseExperiments.evaluateAll(config, keys, userId)

/**
 * New-user targeting gate on a fetched config. Pure; see
 * `ParseExperiments.isVariantForNewUser`.
 */
fun isVariantForNewUser(
    config: ParseConfig?,
    key: String?,
    userId: String?,
    joinedAt: Date?,
): Boolean = ParseExperiments.isVariantForNewUser(config, key, userId, joinedAt)
