/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Unit tests for {@link ParseExperiments}: determinism, boundaries, fail-closed gates, and
 * definition lookup across JSONArray/List shapes.
 */
public class ParseExperimentsTest {

    private static ParseConfig configOf(Object... pairs) {
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            params.put((String) pairs[i], pairs[i + 1]);
        }
        return new ParseConfig(params);
    }

    private static JSONObject experiment(String key, double pct, boolean killed)
            throws JSONException {
        JSONObject entry = new JSONObject();
        entry.put("key", key);
        entry.put("traffic_pct", pct);
        entry.put("killed", killed);
        return entry;
    }

    @Test
    public void bucketIsDeterministicAndBounded() {
        int first = ParseExperiments.bucketFor("vip_for_new_10", "vftE0NKr0C");
        assertTrue(first >= 0 && first < 100);
        assertEquals(first, ParseExperiments.bucketFor("vip_for_new_10", "vftE0NKr0C"));
    }

    @Test
    public void bucketRejectsInvalidInput() {
        assertEquals(-1, ParseExperiments.bucketFor(null, "uid"));
        assertEquals(-1, ParseExperiments.bucketFor("", "uid"));
        assertEquals(-1, ParseExperiments.bucketFor("key", null));
        assertEquals(-1, ParseExperiments.bucketFor("key", ""));
    }

    @Test
    public void bucketDiffersPerKey() {
        // Per-experiment independence: same user lands differently across keys
        // with overwhelming probability over this probe set.
        String uid = "probe-user-001";
        int same = 0;
        String[] keys = {"exp-a", "exp-b", "exp-c", "exp-d", "exp-e", "exp-f", "exp-g", "exp-h"};
        int[] buckets = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            buckets[i] = ParseExperiments.bucketFor(keys[i], uid);
        }
        for (int i = 1; i < buckets.length; i++) {
            if (buckets[i] == buckets[0]) {
                same++;
            }
        }
        assertTrue(same < buckets.length - 1);
    }

    @Test
    public void extendedParityVectors() {
        // Cross-runtime drift gate: identical table lives in cloud
        // abBucket.test.js and dumble ParseExperimentsSdkTest.
        assertEquals(81, ParseExperiments.bucketFor("vip_for_new_10", "3P0ES1vVWC"));
        assertEquals(75, ParseExperiments.bucketFor("vip_for_new_10", "probe-004"));
        assertEquals(8, ParseExperiments.bucketFor("exp-a", "probe-user-002"));
        assertEquals(20, ParseExperiments.bucketFor("exp-b", "probe-user-001"));
        assertEquals(20, ParseExperiments.bucketFor("welcome_50", "vftE0NKr0C"));
        assertEquals(81, ParseExperiments.bucketFor("welcome_50", "NXqFcxQhF2"));
        assertEquals(88, ParseExperiments.bucketFor("ramp-check", "ramp-user-42"));
    }

    @Test
    public void percentZeroAndHundred() throws JSONException {
        JSONArray experiments = new JSONArray();
        experiments.put(experiment("e0", 0.0, false));
        experiments.put(experiment("e100", 100.0, false));
        ParseConfig config = configOf("ab_enabled", true, "ab_experiments", experiments);
        assertFalse(ParseExperiments.isVariant(config, "e0", "any-user"));
        assertTrue(ParseExperiments.isVariant(config, "e100", "any-user"));
    }

    @Test
    public void failClosedOnKillMissingDisabled() throws JSONException {
        JSONArray experiments = new JSONArray();
        experiments.put(experiment("killed", 100.0, true));
        experiments.put(experiment("live", 100.0, false));
        ParseConfig on = configOf("ab_enabled", true, "ab_experiments", experiments);
        ParseConfig off = configOf("ab_enabled", false, "ab_experiments", experiments);
        assertFalse(ParseExperiments.isVariant(on, "killed", "u1"));
        assertFalse(ParseExperiments.isVariant(on, "missing", "u1"));
        assertFalse(ParseExperiments.isVariant(off, "live", "u1"));
        assertFalse(ParseExperiments.isVariant(null, "live", "u1"));
    }

    @Test
    public void readsListShapedDefinitions() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("key", "list-exp");
        entry.put("traffic_pct", 100.0);
        entry.put("killed", false);
        java.util.List<Object> list = new java.util.ArrayList<>();
        list.add(entry);
        ParseConfig config = configOf("ab_enabled", true, "ab_experiments", list);
        assertNotNull(ParseExperiments.getExperiment(config, "list-exp"));
        assertTrue(ParseExperiments.isVariant(config, "list-exp", "u9"));
    }

    @Test
    public void wideningKeepsEarlyUsers() throws JSONException {
        // Sticky-expand proof: every user in at 10% stays in at 50%.
        JSONArray experiments = new JSONArray();
        experiments.put(experiment("ramp", 10.0, false));
        ParseConfig narrow = configOf("ab_enabled", true, "ab_experiments", experiments);
        JSONArray wideArr = new JSONArray();
        wideArr.put(experiment("ramp", 50.0, false));
        ParseConfig wide = configOf("ab_enabled", true, "ab_experiments", wideArr);
        for (int i = 0; i < 200; i++) {
            String uid = "ramp-user-" + i;
            if (ParseExperiments.isVariant(narrow, "ramp", uid)) {
                assertTrue(ParseExperiments.isVariant(wide, "ramp", uid));
            }
        }
    }
}
