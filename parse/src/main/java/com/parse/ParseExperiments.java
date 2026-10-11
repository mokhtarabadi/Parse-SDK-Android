/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Sticky experiment bucketing over {@link ParseConfig} definitions.
 *
 * <p>A/B testing needs per-user variation, but {@code ParseConfig} is global (one payload for all
 * clients) and disk-cached. This helper therefore never invents per-user Config: it reads
 * experiment <em>definitions</em> from the shared Config and evaluates each user locally with a
 * deterministic hash. Same user, same bucket — every process, every launch, no assignment table.
 *
 * <p>Bucket math: {@code SHA-256(experimentKey + "\n" + userId)}, first 4 bytes as an unsigned int,
 * {@code mod 100}. The user is in-variant iff {@code bucket < traffic_pct}. Widening the percent
 * only admits higher buckets, so early users are never evicted mid-experiment. Shrinking mid-flight
 * corrupts the read — rotate the experiment key instead (the key is part of the hash, so arms
 * reshuffle cleanly).
 *
 * <p>Pure static functions, zero network, zero state. Safe to call from any thread.
 */
public final class ParseExperiments {

    private ParseExperiments() {
        // Static helper — do not instantiate.
    }

    /**
     * Returns the stable bucket 0-99 for a user in an experiment, or -1 when the inputs are
     * unusable (null/empty key or user id, hashing failure). Callers must treat -1 as control.
     *
     * @param experimentKey the experiment key (salt + identity, e.g. "vip_for_new_10")
     * @param userId the stable user id (e.g. the _User objectId)
     * @return bucket in [0, 100), or -1 on invalid input
     */
    public static int bucketFor(String experimentKey, String userId) {
        if (experimentKey == null
                || experimentKey.isEmpty()
                || userId == null
                || userId.isEmpty()) {
            return -1;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash =
                    digest.digest((experimentKey + "\n" + userId).getBytes(StandardCharsets.UTF_8));
            long head =
                    ((hash[0] & 0xFFL) << 24)
                            | ((hash[1] & 0xFFL) << 16)
                            | ((hash[2] & 0xFFL) << 8)
                            | (hash[3] & 0xFFL);
            return (int) (head % 100L);
        } catch (NoSuchAlgorithmException e) {
            return -1;
        }
    }

    /**
     * Finds an experiment definition by key inside the {@code ab_experiments} Config value. Accepts
     * both {@link JSONArray} and {@link List} of maps (Parse decodes arrays either way).
     *
     * @param config the fetched config (null-safe, returns null)
     * @param experimentKey the experiment key to find
     * @return the definition object, or null when absent or malformed
     */
    public static JSONObject getExperiment(ParseConfig config, String experimentKey) {
        if (config == null || experimentKey == null || experimentKey.isEmpty()) {
            return null;
        }
        Object raw;
        try {
            raw = config.get("ab_experiments");
        } catch (Exception e) {
            return null;
        }
        if (raw instanceof JSONArray) {
            JSONArray array = (JSONArray) raw;
            for (int i = 0; i < array.length(); i++) {
                JSONObject entry = array.optJSONObject(i);
                if (entry != null && experimentKey.equals(entry.optString("key", null))) {
                    return entry;
                }
            }
            return null;
        }
        if (raw instanceof List) {
            List<?> list = (List<?>) raw;
            for (Object item : list) {
                if (item instanceof Map) {
                    Object key = ((Map<?, ?>) item).get("key");
                    if (experimentKey.equals(key)) {
                        return new JSONObject((Map<?, ?>) item);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Evaluates whether a user is in-variant. Fail-closed at every step: null config, global
     * kill-switch off, missing/killed experiment, unreadable percent, or invalid bucket all yield
     * {@code false} (control). New-vs-old targeting stays with the caller (it owns the user-joined
     * semantics); this method judges only enabled + killed + percent + bucket.
     *
     * @param config the fetched config (null-safe)
     * @param experimentKey the experiment key
     * @param userId the stable user id
     * @return true only when the user is in-variant
     */
    public static boolean isVariant(ParseConfig config, String experimentKey, String userId) {
        if (config == null) {
            return false;
        }
        boolean globalOn;
        try {
            globalOn = config.getBoolean("ab_enabled", false);
        } catch (Exception e) {
            return false;
        }
        if (!globalOn) {
            return false;
        }
        JSONObject experiment = getExperiment(config, experimentKey);
        if (experiment == null || experiment.optBoolean("killed", false)) {
            return false;
        }
        double pct = experiment.optDouble("traffic_pct", Double.NaN);
        if (Double.isNaN(pct) || pct <= 0.0) {
            return false;
        }
        if (pct > 100.0) {
            pct = 100.0;
        }
        int bucket = bucketFor(experimentKey, userId);
        return bucket >= 0 && bucket < pct;
    }

    /**
     * Evaluates every requested experiment key against one fetched config, preserving the input key
     * order. Each entry first checks definition existence with the same lookup {@link
     * #getExperiment} uses: a missing or malformed definition records {@code -1} without hashing.
     * Null keys are skipped (a null key can never match a definition and would poison downstream
     * lookups); empty-string keys hash normally and fail closed per {@link #bucketFor} rules.
     * Present definitions delegate to {@link #bucketFor}; any failure there records {@code -1} —
     * the documented fail-closed control marker, never a valid in-variant bucket. A null config or
     * null key list yields an empty map (not -1 per key).
     *
     * @param config the fetched config (null-safe, yields empty map)
     * @param keys experiment keys in the order the caller wants back (null-safe)
     * @param userId the stable user id (null-safe per key)
     * @return an unmodifiable input-ordered map of key to bucket in [0, 100), or -1 per failed key.
     */
    @NonNull
    public static Map<String, Integer> evaluateAll(
            @Nullable ParseConfig config, @Nullable List<String> keys, @Nullable String userId) {
        Map<String, Integer> out = new LinkedHashMap<>();
        if (config == null || keys == null) {
            return Collections.unmodifiableMap(out);
        }
        for (String key : keys) {
            if (key == null) {
                continue;
            }
            int bucket;
            try {
                // Definition guard: unknown or malformed keys never reach the hash.
                if (getExperiment(config, key) == null) {
                    bucket = -1;
                } else {
                    bucket = bucketFor(key, userId);
                }
            } catch (Exception e) {
                bucket = -1;
            }
            out.put(key, bucket);
        }
        return Collections.unmodifiableMap(out);
    }

    /**
     * Evaluates whether a user who joined at {@code joinedAt} is in-variant, mirroring the server
     * targeting gate ({@code user_joined >= start_iso}). When the experiment definition carries no
     * {@code start_iso}, there is no gate and this delegates straight to {@link #isVariant}.
     * Fail-closed: any null input, unparseable date, or joined-before-start yields {@code false}.
     *
     * @param config the fetched config (null-safe)
     * @param experimentKey the experiment key (null-safe)
     * @param userId the stable user id (null-safe)
     * @param joinedAt the user's join date (null-safe)
     * @return true only when the gate passes and the user is in-variant.
     */
    public static boolean isVariantForNewUser(
            @Nullable ParseConfig config,
            @Nullable String experimentKey,
            @Nullable String userId,
            @Nullable java.util.Date joinedAt) {
        if (config == null || experimentKey == null || userId == null || joinedAt == null) {
            return false;
        }
        JSONObject definition = getExperiment(config, experimentKey);
        if (definition != null) {
            String startIso = definition.optString("start_iso", null);
            if (startIso != null && !startIso.isEmpty()) {
                java.util.Date start = ParseDateFormat.getInstance().parse(startIso);
                // Unparseable or not-yet-joined: fail closed.
                if (start == null || joinedAt.before(start)) {
                    return false;
                }
            }
        }
        return isVariant(config, experimentKey, userId);
    }
}
