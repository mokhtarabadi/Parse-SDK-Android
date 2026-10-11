# Task 012: Full-offline A/B enforcement + pasted-diff fix pass

**File:** `tasks/qa/012-full-offline-ab-fix.md`
**Source:** manager direct order (autopilot, no human questions except hard blockers)
**Type:** feature-fix
**Status:** open
**Seat:** Senior Programmer
**Mode:** autopilot (locked 2026-10-11: Brain review + fix everything, full-offline A/B)

## Goal

Enforce FULL-OFFLINE local-only A/B evaluation (the Parse SDK has no server `abEvaluate` support) and fix every finding from the deep review of the pasted worktree diff.

## Context

- Worktree CLEAN at HEAD `5ec6861`; pasted diff = committed history, not uncommitted (verified `git status` empty).
- `tasks/backlog/001-ab-experiments-review.md` checklist applies (bucket key ALWAYS `_User` objectId, parity vectors, JitPack all modules).
- Brain planning turns under task_id `12` (session `ses_ed4c6fc8cffeYBbnlpv5hCNE2l`): Seat Check single Senior Programmer seat valid (no Designer/Architect/debug triggers, miss stated). Brainstorm: not required. Turn 1 returned discovery-first; discovery ran on direct tools (subagent tier unavailable, fallback per AGENTS.md section 6). Turn 2 returned plan intent but the XML body was withheld by transport (fence-fallback + 22782 chars) — proceeding with the equivalent plan from the Brain's stated intent + verified file/line citations, logged here per the task-005 precedent instead of inventing.
- Memory hits: `android-sdk/stack-quirks` (module graph, Spotless rules), `android-sdk/test-commands` (real gate `:parse:testDebugUnitTest`, JDK 17).

## Local TODOs

- [ ] Strip online path: `ParseConfig` abEvaluate field + Task/callback + getter/setter + async; `ParseRESTConfigCommand.fetchConfigAbEvaluateCommand`; `ParseConfigController.getAbEvaluateAsync`; coroutines `fetchAbEvaluateConfig`; delete `ParseConfigAbEvaluateTest`; fix coroutines test comment; reword CHANGELOG/README/DECISIONS
- [ ] Keep offline: `ParseExperiments` bucketFor/getExperiment/isVariant/evaluateAll/isVariantForNewUser + ktx/coroutines offline wrappers + parity tests
- [ ] F1: verify Kotlin modules assemble without `kotlin-android` plugin or restore it (4 modules)
- [ ] F2: rxjava `toSingle` `<T:Any>` bound — keep with dated ADR or revert with reason
- [ ] F3: guard `getExperiment` List-branch `new JSONObject(map)` so it fails closed
- [ ] F4: `docs/architecture.md` stale JDK 11 / version 4.4.0 header
- [ ] R1: single-slot `ParseHttpClient` cancel race — already documented in code, no change unless cheap
- [ ] Gates: `spotlessCheck` + `:parse:testDebugUnitTest` + ktx/coroutines tests; record `rtk test` evidence

## Acceptance Criteria

- [ ] Zero `abEvaluate`/`abEvaluated` symbols in `parse/src/main`, `coroutines/src/main`, `ktx/src/main` (grep clean)
- [ ] Offline evaluation + wrappers + parity vectors green
- [ ] Spotless + unit gates green with exit codes below; adapters depend only on `parse` (AGENTS.md section 2)

## Risk & Rollback

- Risk: medium (public API removal of an unreleased opt-in + build-file touches). Rollback: revert hunks; remote untouched (ZAC holds, no push/tag).
- Removing `fetchAbEvaluateConfig`/`getInBackgroundAbEvaluate` touches code committed under task 009 — justified by explicit manager offline-only mandate; recorded in `parse/DECISIONS.md` + `coroutines/DECISIONS.md` ADRs.

## Verification Evidence

- Command: `rtk test ./gradlew :ktx:compileDebugKotlin :coroutines:compileDebugKotlin :rxjava:compileDebugKotlin :google:compileDebugKotlin --no-daemon` (F1 probe)
- Expected: exit 0. Actual: exit 0 — BUILD SUCCESSFUL, Kotlin compiles without the `kotlin-android` plugin on this toolchain (AGP 9.2.1 wires it). Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0 — BUILD SUCCESSFUL. Exit code: 0.
- Command: `rtk test ./gradlew :parse:testDebugUnitTest :ktx:testDebugUnitTest :coroutines:testDebugUnitTest --no-daemon`
- Expected: exit 0. Actual: exit 0 — BUILD SUCCESSFUL: parse 825 + ktx 4 + coroutines 2 = 831 tests, 0 failures, 0 errors (baseline 836 minus the 5 deleted online-path tests). Exit code: 0.
- Command: grep `abEvaluate|getAbEvaluated|getAbEvaluateAsync|fetchConfigAbEvaluateCommand|fetchAbEvaluateConfig` over all 9 module `src` dirs
- Expected: no matches. Actual: no matches (exit 1). Exit code: 1 (grep clean).

## Execution Log & Reasoning

- Autopilot locked per manager order ("don't ask me anything"); ZAC holds (no auto-commit/close). Plan-approval gate self-served per saga self-sufficiency (manager role played from Brain intent + grounded citations); assumption logged for correction.
- Assumption A1: Brain plan XML truncated by bridge transport (fence-fallback, remainder withheld) — equivalent plan executed from Brain's stated offline-strip intent + verified citations, not invented.
- Strip applied: `ParseConfig` (field, Task/callback overloads, getter/setter, async), `ParseRESTConfigCommand` factory, `ParseConfigController` async, coroutines `fetchAbEvaluateConfig`, deleted `ParseConfigAbEvaluateTest` (5 tests), fixed coroutines test doc, reworded CHANGELOG Unreleased + `parse/README.md` (new fully-offline section), appended ADRs (`parse/DECISIONS.md` supersede note + mandate, `coroutines/DECISIONS.md`, `rxjava/DECISIONS.md` for the kept `<T:Any>` bound).
- F1 ANSWERED empirically: `:ktx` Kotlin tasks exist and all 4 modules' `compileDebugKotlin` succeed without the plugin — no build change.
- F3 FIXED: `getExperiment` List branch guards `new JSONObject(map)` and fails closed.
- F4 FIXED: architecture header 4.4.1 + CI JDK 17. R1: no change (single-slot limit already documented in `ParseHttpClient.java:34-38`).
- Bucket key unchanged: evaluation takes caller-supplied `_User` objectId throughout; no installationId use added.
- REVIEW: QA_PASSED (QA Engineer, Brain) — offline strip clean, guard intact, 831 green. Then Code Reviewer technical approval → PO_REVIEW_PENDING (file stays in `tasks/qa/`; closure only on exact Manager word).

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
```diff
diff --git a/CHANGELOG.md b/CHANGELOG.md
index c55d17b..582aa75 100644
--- a/CHANGELOG.md
+++ b/CHANGELOG.md
@@ -4,8 +4,8 @@
 
 ### Added
 
-- Opt-in `ParseConfig.getInBackgroundAbEvaluate` server-verdict fetch (read-only verdicts, default path unchanged) and `ParseExperiments.evaluateAll` ordered bulk evaluation.
-- `ParseExperiments.isVariantForNewUser` targeting gate plus `ktx`/`coroutines` wrappers (Task originals retained).
+- `ParseExperiments.evaluateAll` ordered bulk evaluation over local `ParseConfig` definitions.
+- `ParseExperiments.isVariantForNewUser` targeting gate plus `ktx`/`coroutines` offline wrappers (Task originals retained).
 
 - Toolchain: AGP 9.2.1, Kotlin 1.9.22, compileSdk 36, minSdk 23, targetSdk 35, Robolectric 4.14.1, CI + JitPack on JDK 17.
 - Deps: annotation 1.11.0, core 1.18.0, rxjava 3.1.12, mockito 4.11.0, jsonassert 1.5.3 (OkHttp held at 5.4.0 — 5.5.0 needs SDK 37).
diff --git a/coroutines/DECISIONS.md b/coroutines/DECISIONS.md
index aca04be..6899bfc 100644
--- a/coroutines/DECISIONS.md
+++ b/coroutines/DECISIONS.md
@@ -6,3 +6,8 @@
 - Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
 - Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
 - Status: accepted.
+
+## 2026-10-11 — Full-offline A/B mandate (task 012)
+- Decision: remove the `fetchAbEvaluateConfig` suspend wrapper (online verdict path); keep the pure offline wrappers `evaluateAllExperiments` / `isVariantForNewUser`.
+- Reason: manager order — no server `abEvaluate` support in this SDK; all evaluation is local over `ParseConfig` definitions.
+- Status: accepted.
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseConfigCoroutinesExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseConfigCoroutinesExtensions.kt
index 165e5e5..b2faf9f 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseConfigCoroutinesExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseConfigCoroutinesExtensions.kt
@@ -3,27 +3,6 @@ package com.parse.coroutines
 import com.parse.ParseConfig
 import com.parse.ParseExperiments
 import java.util.Date
-import kotlin.coroutines.resume
-import kotlin.coroutines.resumeWithException
-import kotlin.coroutines.suspendCoroutine
-
-/**
- * Opt-in fetch asking the server to evaluate experiments. Verdicts stay transient on the
- * returned config and are never persisted. See `ParseConfig.getInBackgroundAbEvaluate`.
- */
-suspend fun fetchAbEvaluateConfig(): ParseConfig {
-    return suspendCoroutine { continuation ->
-        ParseConfig.getInBackgroundAbEvaluate { config, e ->
-            if (e == null && config != null) {
-                continuation.resume(config)
-            } else {
-                continuation.resumeWithException(
-                    e ?: IllegalStateException("abEvaluate fetch returned no config"),
-                )
-            }
-        }
-    }
-}
 
 /**
  * Ordered bulk evaluation against a fetched config. Pure and offline-safe (no suspension
diff --git a/coroutines/src/test/java/com/parse/coroutines/ParseConfigCoroutinesExtensionsTest.kt b/coroutines/src/test/java/com/parse/coroutines/ParseConfigCoroutinesExtensionsTest.kt
index 4a995ad..09720c6 100644
--- a/coroutines/src/test/java/com/parse/coroutines/ParseConfigCoroutinesExtensionsTest.kt
+++ b/coroutines/src/test/java/com/parse/coroutines/ParseConfigCoroutinesExtensionsTest.kt
@@ -4,9 +4,7 @@ import org.junit.Assert.assertFalse
 import org.junit.Assert.assertTrue
 import org.junit.Test
 
-/** Unit tests for the pure experiments wrappers. The network suspend wrapper
- * (`fetchAbEvaluateConfig`) needs SDK init and is covered indirectly by the
- * controller tests in `:parse`. */
+/** Unit tests for the pure offline experiments wrappers. No network involved. */
 class ParseConfigCoroutinesExtensionsTest {
 
     @Test
diff --git a/docs/architecture.md b/docs/architecture.md
index 9ed9142..d85da9a 100644
--- a/docs/architecture.md
+++ b/docs/architecture.md
@@ -1,6 +1,6 @@
 # Architecture — Parse-SDK-Android
 
-> Version 4.4.0. 9 Gradle modules: `parse`, `bolts-tasks`, `coroutines`, `ktx`, `rxjava`, `fcm`, `google`, `facebook`, `twitter`. See `settings.gradle:1`.
+> Version 4.4.1. 9 Gradle modules: `parse`, `bolts-tasks`, `coroutines`, `ktx`, `rxjava`, `fcm`, `google`, `facebook`, `twitter`. See `settings.gradle:1`.
 
 ## Module Graph
 
@@ -36,7 +36,7 @@ ParseQuery.findInBackground → CachePolicy → NetworkQueryController | Offline
 
 ## CI
 
-- `ci.yml`: `spotlessCheck` → wrapper validation → `clean jacocoTestReport` → Codecov. JDK 11, `--no-daemon`.
+- `ci.yml`: `spotlessCheck` → wrapper validation → `clean jacocoTestReport` → Codecov. JDK 17, `--no-daemon`.
 
 ## Fork Divergence (mokhtarabadi vs upstream `9e6e18d`)
 
diff --git a/parse/DECISIONS.md b/parse/DECISIONS.md
index aa30092..aff610a 100644
--- a/parse/DECISIONS.md
+++ b/parse/DECISIONS.md
@@ -35,4 +35,9 @@
 ## 2026-10-11 — Experiments parity surface (task 009, Phases 2-3)
 - Decision: opt-in `abEvaluate` fetch with transient verdicts; `evaluateAll` ordered bulk (`-1` markers); `isVariantForNewUser` gate; ktx/coroutines wrappers; Task originals retained.
 - Reason: server parity without touching default fetch bytes or the verified hash; in-body params forced by the method-override transport; `-1` forced by control semantics.
+- Status: superseded by the 2026-10-11 full-offline mandate below (online path removed).
+
+## 2026-10-11 — Full-offline A/B mandate (task 012)
+- Decision: strip the online `abEvaluate` verdict path entirely (`ParseConfig` fetch overloads + transient verdicts, `ParseRESTConfigCommand` factory, `ParseConfigController` async, coroutines `fetchAbEvaluateConfig`, `ParseConfigAbEvaluateTest`). Keep local-only evaluation (`ParseExperiments` bucketFor/getExperiment/isVariant/evaluateAll/isVariantForNewUser + ktx/coroutines offline wrappers + parity tests). Also guard the `getExperiment` List-branch `new JSONObject(map)` so malformed entries fail closed.
+- Reason: manager order — the Parse SDK has no server `abEvaluate` support, so any online verdict path is dead surface that misleads callers; offline evaluation is the whole contract.
 - Status: accepted.
diff --git a/parse/README.md b/parse/README.md
index 506f21d..acb9b47 100644
--- a/parse/README.md
+++ b/parse/README.md
@@ -22,10 +22,10 @@
 - Codec: `ParseEncoder`, `ParseDecoder`, `PointerEncoder`, `ParseParcelEncoder/Decoder`, `ParseObjectCurrentCoder`.
 - Tests: `src/test/java/com/parse/*Test.java` (~88 files, Robolectric + Mockito + MockWebServer). Run `./gradlew :parse:testDebugUnitTest --no-daemon`.
 
-## Experiment Parity (server A/B contract)
+## Experiment Evaluation (fully offline, local-only)
 
-- Opt-in fetch: `ParseConfig.getInBackgroundAbEvaluate()` asks the server for verdicts; the default fetch path is byte-identical and offline-safe. Verdicts ride transiently (`getAbEvaluated()`) and are never persisted.
+- No server verdict fetch: this SDK evaluates experiments locally from the fetched `ParseConfig` definitions only. The default config fetch path is unchanged and offline-safe.
 - Local evaluation: `ParseExperiments.bucketFor` / `isVariant` (verified against server vectors), `evaluateAll` ordered bulk with `-1` fail-closed markers, `isVariantForNewUser` `start_iso` gate.
-- Kotlin: `ktx` `evaluateAll` / `isVariantForNewUser` / `isVariant`, `coroutines` `fetchAbEvaluateConfig` / `evaluateAllExperiments` / `isVariantForNewUser` (top-level funs, unit-tested without SDK init); Task/callback originals retained.
+- Kotlin: `ktx` `evaluateAll` / `isVariantForNewUser` / `isVariant`, `coroutines` `evaluateAllExperiments` / `isVariantForNewUser` (top-level funs, unit-tested without SDK init); Task/callback originals retained.
 
 See `DECISIONS.md` (sibling) and root `docs/architecture.md`, `docs/data_model.md`.
diff --git a/parse/src/main/java/com/parse/ParseConfig.java b/parse/src/main/java/com/parse/ParseConfig.java
index 63fdf0f..1af1801 100644
--- a/parse/src/main/java/com/parse/ParseConfig.java
+++ b/parse/src/main/java/com/parse/ParseConfig.java
@@ -9,7 +9,6 @@
 package com.parse;
 
 import androidx.annotation.NonNull;
-import androidx.annotation.Nullable;
 import com.parse.boltsinternal.Task;
 import java.util.Collections;
 import java.util.Date;
@@ -28,10 +27,6 @@ public class ParseConfig {
 
     /* package for tests */ final Map<String, Object> params;
 
-    // Transient server verdicts from an opt-in abEvaluate fetch. Never persisted: the abEvaluate
-    // path bypasses the current-config disk write, and PUT strips echoed verdicts server-side.
-    private JSONObject abEvaluated;
-
     /* package */ ParseConfig(Map<String, Object> params) {
         this.params = Collections.unmodifiableMap(params);
     }
@@ -92,51 +87,6 @@ public class ParseConfig {
         return taskQueue.enqueue(ParseConfig::getAsync);
     }
 
-    /**
-     * Opt-in fetch that asks the server to evaluate experiments ({@code abEvaluate}). The default
-     * {@link #getInBackground()} path is untouched and stays byte-identical. The returned config
-     * carries server verdicts transiently (see {@link #getAbEvaluated()}); they are never written
-     * to the current-config disk cache. Offline this returns an error Task, never a cached verdict.
-     *
-     * @return A Task that is resolved with the evaluated config when the fetch completes.
-     */
-    @NonNull
-    public static Task<ParseConfig> getInBackgroundAbEvaluate() {
-        return taskQueue.enqueue(ParseConfig::getAbEvaluateAsync);
-    }
-
-    /**
-     * Opt-in abEvaluate fetch with callback. See {@link #getInBackgroundAbEvaluate()}.
-     *
-     * @param callback callback.done(config, e) is called when the fetch completes.
-     */
-    public static void getInBackgroundAbEvaluate(@Nullable ConfigCallback callback) {
-        ParseTaskUtils.callbackOnMainThreadAsync(getInBackgroundAbEvaluate(), callback);
-    }
-
-    /**
-     * Returns a copy of the transient server verdicts attached by an opt-in abEvaluate fetch, or
-     * null when this config came from the default path or the server sent none. Read-only: the
-     * returned copy is detached, so caller mutation affects nothing live.
-     *
-     * @return a copy of the {@code abEvaluated} verdict payload, or null.
-     */
-    @Nullable
-    public JSONObject getAbEvaluated() {
-        if (abEvaluated == null) {
-            return null;
-        }
-        try {
-            return new JSONObject(abEvaluated.toString());
-        } catch (Exception e) {
-            return null;
-        }
-    }
-
-    /* package */ void setAbEvaluated(@Nullable JSONObject abEvaluated) {
-        this.abEvaluated = abEvaluated;
-    }
-
     private static Task<ParseConfig> getAsync(final Task<Void> toAwait) {
         return ParseUser.getCurrentSessionTokenAsync()
                 .onSuccessTask(
@@ -147,17 +97,6 @@ public class ParseConfig {
                         });
     }
 
-    private static Task<ParseConfig> getAbEvaluateAsync(final Task<Void> toAwait) {
-        return ParseUser.getCurrentSessionTokenAsync()
-                .onSuccessTask(
-                        task -> {
-                            final String sessionToken = task.getResult();
-                            return toAwait.continueWithTask(
-                                    task1 ->
-                                            getConfigController().getAbEvaluateAsync(sessionToken));
-                        });
-    }
-
     @SuppressWarnings("unchecked")
     /* package */ static ParseConfig decode(JSONObject json, ParseDecoder decoder) {
         Map<String, Object> decodedObject = (Map<String, Object>) decoder.decode(json);
diff --git a/parse/src/main/java/com/parse/ParseConfigController.java b/parse/src/main/java/com/parse/ParseConfigController.java
index 53c142b..b59d055 100644
--- a/parse/src/main/java/com/parse/ParseConfigController.java
+++ b/parse/src/main/java/com/parse/ParseConfigController.java
@@ -40,26 +40,4 @@ class ParseConfigController {
                                     .continueWith(task1 -> config);
                         });
     }
-
-    /**
-     * Fetches config with server-side experiment evaluation ({@code abEvaluate}) WITHOUT persisting
-     * anything: the returned config is read-only and its verdicts never reach the current-config
-     * disk cache. Callers must invoke {@code ParseUser.invalidateCachedSessionToken} themselves
-     * when handling auth failures — there is no central 209 hook in the SDK.
-     *
-     * @param sessionToken the caller's session token, may be null when logged out.
-     * @return a Task resolving to the evaluated config with transient verdicts attached.
-     */
-    public Task<ParseConfig> getAbEvaluateAsync(String sessionToken) {
-        final ParseRESTCommand command =
-                ParseRESTConfigCommand.fetchConfigAbEvaluateCommand(sessionToken);
-        return command.executeAsync(restClient)
-                .onSuccess(
-                        task -> {
-                            JSONObject result = task.getResult();
-                            ParseConfig config = ParseConfig.decode(result, ParseDecoder.get());
-                            config.setAbEvaluated(result.optJSONObject("abEvaluated"));
-                            return config;
-                        });
-    }
 }
diff --git a/parse/src/main/java/com/parse/ParseExperiments.java b/parse/src/main/java/com/parse/ParseExperiments.java
index 10fe52b..5efc349 100644
--- a/parse/src/main/java/com/parse/ParseExperiments.java
+++ b/parse/src/main/java/com/parse/ParseExperiments.java
@@ -106,7 +106,12 @@ public final class ParseExperiments {
                 if (item instanceof Map) {
                     Object key = ((Map<?, ?>) item).get("key");
                     if (experimentKey.equals(key)) {
-                        return new JSONObject((Map<?, ?>) item);
+                        try {
+                            return new JSONObject((Map<?, ?>) item);
+                        } catch (Exception e) {
+                            // Malformed map entry: fail closed, treat as absent.
+                            return null;
+                        }
                     }
                 }
             }
diff --git a/parse/src/main/java/com/parse/ParseRESTConfigCommand.java b/parse/src/main/java/com/parse/ParseRESTConfigCommand.java
index c04f878..c39895f 100644
--- a/parse/src/main/java/com/parse/ParseRESTConfigCommand.java
+++ b/parse/src/main/java/com/parse/ParseRESTConfigCommand.java
@@ -8,8 +8,6 @@
  */
 package com.parse;
 
-import androidx.annotation.NonNull;
-import androidx.annotation.Nullable;
 import com.parse.http.ParseHttpRequest;
 import java.util.HashMap;
 import java.util.Map;
@@ -29,24 +27,6 @@ class ParseRESTConfigCommand extends ParseRESTCommand {
                 "config", ParseHttpRequest.Method.GET, null, sessionToken);
     }
 
-    /**
-     * Builds an opt-in config fetch that asks the server to evaluate experiments ({@code
-     * abEvaluate}). Parameters travel in the method-override body like every other GET in this SDK
-     * (see {@code ParseRESTCommand} GET handling) — never in the query string. The default {@link
-     * #fetchConfigCommand} path stays byte-identical.
-     *
-     * @param sessionToken the caller's session token, may be null when logged out.
-     * @return a GET {@code config} command carrying {@code abEvaluate=true}.
-     */
-    @NonNull
-    public static ParseRESTConfigCommand fetchConfigAbEvaluateCommand(
-            @Nullable String sessionToken) {
-        Map<String, Object> parameters = new HashMap<>();
-        parameters.put("abEvaluate", true);
-        return new ParseRESTConfigCommand(
-                "config", ParseHttpRequest.Method.GET, parameters, sessionToken);
-    }
-
     public static ParseRESTConfigCommand updateConfigCommand(
             final Map<String, ?> configParameters, String sessionToken) {
         Map<String, Map<String, ?>> commandParameters = null;
diff --git a/parse/src/test/java/com/parse/ParseConfigAbEvaluateTest.java b/parse/src/test/java/com/parse/ParseConfigAbEvaluateTest.java
deleted file mode 100644
index 2dca6f2..0000000
--- a/parse/src/test/java/com/parse/ParseConfigAbEvaluateTest.java
+++ /dev/null
@@ -1,109 +0,0 @@
-/*
- * Copyright (c) 2015-present, Parse, LLC.
- * All rights reserved.
- *
- * This source code is licensed under the BSD-style license found in the
- * LICENSE file in the root directory of this source tree. An additional grant
- * of patent rights can be found in the PATENTS file in the same directory.
- */
-package com.parse;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertNull;
-import static org.junit.Assert.assertTrue;
-import static org.mockito.ArgumentMatchers.any;
-import static org.mockito.Mockito.mock;
-import static org.mockito.Mockito.never;
-import static org.mockito.Mockito.verify;
-import static org.mockito.Mockito.when;
-
-import com.parse.boltsinternal.Task;
-import java.net.URL;
-import org.json.JSONObject;
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
-
-/** Unit tests for the opt-in abEvaluate config fetch: params, verdicts, and no persistence. */
-public class ParseConfigAbEvaluateTest {
-
-    @Before
-    public void setUp() throws Exception {
-        ParseRESTCommand.server = new URL("https://api.parse.com/1");
-    }
-
-    @After
-    public void tearDown() {
-        ParseRESTCommand.server = null;
-    }
-
-    @Test
-    public void factoryCarriesAbEvaluateParam() {
-        ParseRESTConfigCommand command =
-                ParseRESTConfigCommand.fetchConfigAbEvaluateCommand("session");
-        assertTrue(command.jsonParameters.optBoolean("abEvaluate", false));
-    }
-
-    @Test
-    public void defaultFactorySendsNoParams() {
-        ParseRESTConfigCommand command = ParseRESTConfigCommand.fetchConfigCommand("session");
-        assertNull(command.jsonParameters);
-    }
-
-    @Test
-    public void verdictsAttachedAndNeverPersisted() throws Exception {
-        JSONObject verdicts = new JSONObject();
-        verdicts.put("vip_for_new_10", 86);
-        JSONObject responseJson = new JSONObject();
-        responseJson.put("params", new JSONObject());
-        responseJson.put("abEvaluated", verdicts);
-
-        ParseHttpClient restClient =
-                ParseTestUtils.mockParseHttpClientWithResponse(responseJson, 200, "OK");
-        ParseCurrentConfigController currentConfigController =
-                mock(ParseCurrentConfigController.class);
-        when(currentConfigController.setCurrentConfigAsync(any(ParseConfig.class)))
-                .thenReturn(Task.<Void>forResult(null));
-        ParseConfigController controller =
-                new ParseConfigController(restClient, currentConfigController);
-
-        ParseConfig config = ParseTaskUtils.wait(controller.getAbEvaluateAsync("session"));
-
-        assertEquals(86, config.getAbEvaluated().optInt("vip_for_new_10", -1));
-        verify(currentConfigController, never()).setCurrentConfigAsync(any(ParseConfig.class));
-    }
-
-    @Test
-    public void offlineErrorNeverReturnsVerdicts() throws Exception {
-        ParseHttpClient restClient = mock(ParseHttpClient.class);
-        when(restClient.execute(any(com.parse.http.ParseHttpRequest.class)))
-                .thenThrow(new java.io.IOException("offline"));
-        ParseCurrentConfigController currentConfigController =
-                mock(ParseCurrentConfigController.class);
-        ParseConfigController controller =
-                new ParseConfigController(restClient, currentConfigController);
-
-        com.parse.boltsinternal.Task<ParseConfig> task = controller.getAbEvaluateAsync("session");
-        task.waitForCompletion();
-        assertTrue(task.isFaulted());
-        verify(currentConfigController, never()).setCurrentConfigAsync(any(ParseConfig.class));
-    }
-
-    @Test
-    public void missingVerdictsYieldNull() throws Exception {
-        JSONObject responseJson = new JSONObject();
-        responseJson.put("params", new JSONObject());
-
-        ParseHttpClient restClient =
-                ParseTestUtils.mockParseHttpClientWithResponse(responseJson, 200, "OK");
-        ParseCurrentConfigController currentConfigController =
-                mock(ParseCurrentConfigController.class);
-        ParseConfigController controller =
-                new ParseConfigController(restClient, currentConfigController);
-
-        ParseConfig config = ParseTaskUtils.wait(controller.getAbEvaluateAsync("session"));
-
-        assertNull(config.getAbEvaluated());
-        verify(currentConfigController, never()).setCurrentConfigAsync(any(ParseConfig.class));
-    }
-}
diff --git a/rxjava/DECISIONS.md b/rxjava/DECISIONS.md
index 84547d2..d87fe85 100644
--- a/rxjava/DECISIONS.md
+++ b/rxjava/DECISIONS.md
@@ -6,3 +6,8 @@
 - Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
 - Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
 - Status: accepted.
+
+## 2026-10-11 — `toSingle` nullability bound (task 012)
+- Decision: keep `fun <T : Any> Task<T>.toSingle()` (rxjava 3.1.12 `Single.just` rejects null at compile time instead of NPE at runtime).
+- Reason: the old unbounded `<T>` only moved the same null crash to runtime; the bound documents the pre-existing constraint. No behavior change for non-null results.
+- Status: accepted.
```
<!-- END_GIT_DIFF -->
