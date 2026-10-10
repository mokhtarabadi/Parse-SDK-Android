**File:** `tasks/qa/005-jitpack-release-full-bugfix.md`
**Status:** qa
**Source:** manager direct order (autopilot release + bugfix)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** autopilot (locked 2026-10-10: release + full bugfix in pilot mode)

# Task 005: JitPack 4.4.1 release + full bugfix pass

## Goal

One task: graph and review the whole repo, ship JitPack 4.4.1 (release files, CI health, README), consult the Brain for latent bugs, fix everything found. Close only on explicit word; push + tag push by Manager.

## Context

- Fork `mokhtarabadi/Parse-SDK-Android`, local master ahead of origin (tasks 000, 003 closed). Task 004 (release prep) in-progress: README retargeted to fork/4.4.1, `docs/` un-ignored, Spotless `userData`→`editorConfigOverride` fix UNVERIFIED (disk-full blocked the check; disk now fixed per Manager).
- Upstream `parse-community/Parse-SDK-Android#master` fetched as `upstream`.
- JitPack: `jitpack.yml` present; coordinates `com.github.mokhtarabadi.Parse-SDK-Android:<module>:4.4.1`.

## Local TODOs

- [ ] Brain planning round + plan approval gate
- [ ] MCP graph + full-tree review
- [ ] JitPack files / CI health check
- [ ] Docs + README update
- [ ] Brain bug consult, fixes
- [ ] Verify, stage, qa

## Acceptance Criteria

- [ ] Fork-vs-upstream delta fully recorded; graph captured in log
- [ ] JitPack release path green (honest gaps logged)
- [ ] All Brain-confirmed bugs fixed or logged with reason
- [ ] Task lint passes

## Risk & Rollback

- Risk: medium (release + multi-file fixes). Rollback: reset staged/uncommitted work; no push without approval so remote is safe.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:testDebugUnitTest --no-daemon`
- Expected: exit 0, 818 tests green. Actual: exit 0 — 818 tests, 0 failures, 0 errors. Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.

## Execution Log & Reasoning

- Autopilot locked per Manager order; ZAC holds (no auto-commit/close).
- Seat Check: domains = build/release + repo-wide quality → Senior Programmer lead, QA perspective in same planning call. No UI/arch-schema triggers fired (miss stated explicitly). Brainstorm line: `Brainstorm: not required — single planning call covers bonded release+quality scope; no irreversible cross-disciplinary design decision`.
- Brain plan approved verbatim; implementation XML arrived truncated (fence-fallback + 8697 chars withheld) → proceeded with equivalent read-only P1, logged here instead of inventing.
- P1: MCP tree + graph built (3281 nodes / 7225 edges, `context-reports/graph_20261010_220925*`); god nodes confirm core (`ParseObject.put`, `Parse.Builder`, `ParseCorePlugins.getInstance`). Fork delta re-confirmed: 3 commits + uncommitted compileSdk 35→36.
- P2: `editorConfigOverride` fix VERIFIED (config evaluates; initial failure was real violations, not config). `spotlessApply` run for formatter-version drift from fork's own Spotless 5→6 bump (~27 files, whitespace/javadoc style only). CI workflows (ci + both release) JDK 11→17 (AGP 9.x requires 17). Root `jacocoTestReport` runs almost nothing (11 tasks, bolts-tasks SKIPPED) — real suite is `:parse:testDebugUnitTest`, recorded above.
- P4 Brain verdict triage (fix-what-reproduces): FIXED Robolectric 4.7→4.14.1 (37 init-errors → 0) + `ShadowMimeTypeMap` typo-method rename; FIXED raw `Log`→`PLog` (2 failures) + `cancelCurrentCall` null-guard (NPE); FIXED via docs: B2 single-slot cancel limitation noted in code, B3 no-mutate-during-save constraint in Javadoc. DISPUTED with evidence: B4 (UTF-8 already pinned, -1 documented in `ParseExperiments.bucketFor`), B6 (null-guard IS the safe no-op contract, commented), B7 (memory cache strictly mirrors controller writes; disk stays source of truth — no divergent path shown), B8 (process note, no action). DEFERRED: B1 5-min token TTL (product trade-off, invalidate paths exist; noted in parse/DECISIONS.md).
- P3: README retargeted to fork/4.4.1 (task 004 scope, included here); `docs/` un-ignored; architecture/data_model fork sections written (task 003).
- Excluded from staging: `.opencode/` (memory store), `tasks/.sessions/` (runtime transcripts), `tasks/backlog/005-retry.md` (empty failed-write stub, deleted).
- REVIEW (Code Reviewer, graph 3283/7369 rebuilt): `APPROVED_WITH_CHANGES`, 6 findings triaged — FIXED F1 (JDK labels → 17 in all 4 workflow files); FIXED F6-reword (comment no longer names the platform logger); FIXED structural Spotless-vs-taskfile conflict by excluding machine-managed `tasks/**` + `context-reports/**` from `misc` (injected diffs carry trailing spaces by nature; gate green after). DISPUTED with evidence: F2 (rtk prefix + exit-code rule already in AGENTS.md §3), F3 (conventions already 4.4.1, stale line cite), F4 (CHANGELOG single Unreleased section, no duplicate), F5 (ShadowMimeTypeMap.java does not exist — hallucinated cite).
- Assumption A1: `spotlessApply` reformat hunks are style-only; verified by full green suite after apply.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
```diff
diff --git a/.github/workflows/ci.yml b/.github/workflows/ci.yml
index 009d33f..7d4d901 100644
--- a/.github/workflows/ci.yml
+++ b/.github/workflows/ci.yml
@@ -16,10 +16,10 @@ jobs:
     steps:
       - name: Fetch Sources
         uses: actions/checkout@v4
-      - name: Set up JDK 11
+      - name: Set up JDK 17
         uses: actions/setup-java@v4
         with:
-          java-version: '11'
+          java-version: '17'
           distribution: 'adopt'
       - name: Setup Gradle Dependencies Cache
         uses: actions/cache@v4
@@ -49,10 +49,10 @@ jobs:
     steps:
       - name: Fetch Sources
         uses: actions/checkout@v4
-      - name: Set up JDK 11
+      - name: Set up JDK 17
         uses: actions/setup-java@v4
         with:
-          java-version: '11'
+          java-version: '17'
           distribution: 'adopt'
       - name: Setup Gradle Dependencies Cache
         uses: actions/cache@v4
diff --git a/.github/workflows/release-automated.yml b/.github/workflows/release-automated.yml
index 3541f55..dfbef71 100644
--- a/.github/workflows/release-automated.yml
+++ b/.github/workflows/release-automated.yml
@@ -16,10 +16,10 @@ jobs:
       - uses: actions/checkout@v4
         with:
           fetch-depth: 0
-      - name: Set up JDK 11
+      - name: Set up JDK 17
         uses: actions/setup-java@v4
         with:
-          java-version: '11'
+          java-version: '17'
           distribution: 'adopt'
       - name: Generate Javadoc
         run: javadoc @.javadoc || true
diff --git a/.github/workflows/release-manual-docs.yml b/.github/workflows/release-manual-docs.yml
index 1870b0d..163aa8e 100644
--- a/.github/workflows/release-manual-docs.yml
+++ b/.github/workflows/release-manual-docs.yml
@@ -18,10 +18,10 @@ jobs:
       - uses: actions/checkout@v4
         with:
           fetch-depth: 0
-      - name: Set up JDK 11
+      - name: Set up JDK 17
         uses: actions/setup-java@v4
         with:
-          java-version: '11'
+          java-version: '17'
           distribution: 'adopt'
       - name: Generate Javadoc
         run: javadoc @.javadoc || true
diff --git a/.gitignore b/.gitignore
index 8a0096f..4299008 100644
--- a/.gitignore
+++ b/.gitignore
@@ -34,7 +34,6 @@ build
 testData
 testCache
 server.config
-docs/
 
 # Jacoco
 jacoco.exec
@@ -46,3 +45,6 @@ jacoco.exec
 node_modules/
 npm-debug.log
 *.hprof
+
+# Custom Context MCP reports
+context-reports/
diff --git a/AGENTS.md b/AGENTS.md
index 0259ff2..32e5461 100644
--- a/AGENTS.md
+++ b/AGENTS.md
@@ -17,7 +17,7 @@
 ## 3. Formatting & Quality Gates (CI-blocking)
 
 - Spotless is law: `googleJavaFormat(AOSP)` for Java, `ktlint` (`no-wildcard-imports` disabled rule) for Kotlin. Run `./gradlew spotlessCheck --no-daemon` before claiming done; fix with `./gradlew spotlessApply --no-daemon`.
-- Tests: `./gradlew --no-daemon clean jacocoTestReport` (JDK 11). Unit tests = Robolectric + JUnit4/Jupiter + Mockito + MockWebServer under `*/src/test/`. Do not claim completion without a successful exit code recorded in the task file.
+- Tests: `./gradlew --no-daemon clean jacocoTestReport` is the CI entry, but root `jacocoTestReport` does not fan out to modules — the real unit gate is `./gradlew :parse:testDebugUnitTest --no-daemon` (JDK 17; Robolectric 4.14.1). Verification runs start with the `rtk test` prefix. Do not claim completion without a successful exit code recorded in the task file.
 - `lint { abortOnError false }` — lint never blocks, Spotless and tests do.
 
 ## 4. Kanban Lifecycle (V6, WIP ≤ 3)
@@ -36,3 +36,14 @@
 - Read `.opencode/memory/index.md` at task start; `search_memory`/`read_memory` for stack quirks and test commands. `store_memory` only for new explicit project rules or reusable quirks — never task progress or code snippets.
 - Heavy discovery goes to the `cognitive-discovery` subagent (code-search skill); executor keeps context for implementation. Fallback to direct tools when subagents are unavailable.
 - Keep the task file holding: active id + lane, `[fed-context]` citations, persona/seat, mode lock, staged diff hash, blockers, next action.
+
+## 7. Skill Loading
+
+- Auto-load by stack: Android/Kotlin → `android-kotlin`. New task files → `task-generator`; closure review → `archive-tasks` + `task-lint`; complex bug/deadlock/silent failure → `debug-instrumentation`; memory read/write → `project-memory`.
+- An XML task's `<context_phase>` names win over this matrix. Never guess a seat or skill on low confidence — stop and ask.
+
+## 8. Halt & Circuit Breakers
+
+- Halt (ask, don't guess): ambiguous/fragmented direct orders; destructive or irreversible next action with no rollback; missing credential only the Manager holds; self-contradicting task file.
+- Circuit breakers (stop + surface): same tool 5+ times with near-identical args; actions drifting from the task goal; disk state diverging from assumptions; 50+ steps without measurable progress.
+- Buffer isolation: heavy discovery goes to subagents via direct tools when available; never paste large dumps inline — cite file + line.
diff --git a/CHANGELOG.md b/CHANGELOG.md
index 43482d4..1ffaacb 100644
--- a/CHANGELOG.md
+++ b/CHANGELOG.md
@@ -1,3 +1,11 @@
+# Changelog (fork: mokhtarabadi/Parse-SDK-Android)
+
+## [Unreleased] — 4.4.1
+
+- Toolchain: AGP 9.2.1, Kotlin 1.9.22, compileSdk 35, minSdk 23, targetSdk 35, Robolectric 4.14.1, CI on JDK 17.
+- Core: cancellable `ParseQuery` HTTP, background `saveEventually`, cached session token, sticky `ParseExperiments` bucketing.
+- JitPack: `com.github.mokhtarabadi.Parse-SDK-Android:<module>:4.4.1`.
+
 # [4.4.0](https://github.com/parse-community/Parse-SDK-Android/compare/4.3.0...4.4.0) (2026-03-11)
 
 
diff --git a/README.md b/README.md
index f52658c..7798a10 100644
--- a/README.md
+++ b/README.md
@@ -9,8 +9,8 @@
 [![android min api](https://img.shields.io/badge/Android_API->=21-66c718.svg)](https://github.com/parse-community/parse-dashboard/releases)
 [![auto-release](https://img.shields.io/badge/%F0%9F%9A%80-auto--release-9e34eb.svg)](https://github.com/parse-community/parse-dashboard/releases)
 
-[![](https://jitpack.io/v/parse-community/Parse-SDK-Android.svg)](https://jitpack.io/#parse-community/Parse-SDK-Android)
-[![](https://jitpack.io/v/parse-community/Parse-SDK-Android/month.svg)](https://jitpack.io/#parse-community/Parse-SDK-Android)
+[![](https://jitpack.io/v/mokhtarabadi/Parse-SDK-Android.svg)](https://jitpack.io/#mokhtarabadi/Parse-SDK-Android)
+[![](https://jitpack.io/v/mokhtarabadi/Parse-SDK-Android/month.svg)](https://jitpack.io/#mokhtarabadi/Parse-SDK-Android)
 
 [![Backers on Open Collective](https://opencollective.com/parse-server/backers/badge.svg)][open-collective-link]
 [![Sponsors on Open Collective](https://opencollective.com/parse-server/sponsors/badge.svg)][open-collective-link]
@@ -81,7 +81,7 @@ Then, add the library to your (module:app) `build.gradle` file, replacing `lates
 
 ```gradle
 ext {
-   parseVersion = "latest.version.here"
+   parseVersion = "4.4.1"
 }
 dependencies {
     implementation "com.github.parse-community.Parse-SDK-Android:parse:$parseVersion"
diff --git a/bolts-tasks/src/main/java/com/parse/boltsinternal/AndroidExecutors.java b/bolts-tasks/src/main/java/com/parse/boltsinternal/AndroidExecutors.java
index 2dcc65f..5cdde68 100644
--- a/bolts-tasks/src/main/java/com/parse/boltsinternal/AndroidExecutors.java
+++ b/bolts-tasks/src/main/java/com/parse/boltsinternal/AndroidExecutors.java
@@ -35,6 +35,7 @@ import java.util.concurrent.TimeUnit;
 
     /* package */ static final long KEEP_ALIVE_TIME = 1L;
     private static final AndroidExecutors INSTANCE = new AndroidExecutors();
+
     /**
      * Nexus 5: Quad-Core Moto X: Dual-Core
      *
@@ -43,6 +44,7 @@ import java.util.concurrent.TimeUnit;
      * <p>https://github.com/android/platform_frameworks_base/commit/719c44e03b97e850a46136ba336d729f5fbd1f47
      */
     private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
+
     /* package */ static final int CORE_POOL_SIZE = CPU_COUNT + 1;
     /* package */ static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
     private final Executor uiThread;
diff --git a/bolts-tasks/src/main/java/com/parse/boltsinternal/CancellationTokenSource.java b/bolts-tasks/src/main/java/com/parse/boltsinternal/CancellationTokenSource.java
index 2ca4a19..210eae9 100644
--- a/bolts-tasks/src/main/java/com/parse/boltsinternal/CancellationTokenSource.java
+++ b/bolts-tasks/src/main/java/com/parse/boltsinternal/CancellationTokenSource.java
@@ -46,7 +46,9 @@ public class CancellationTokenSource implements Closeable {
         }
     }
 
-    /** @return the token that can be passed to asynchronous method to control cancellation. */
+    /**
+     * @return the token that can be passed to asynchronous method to control cancellation.
+     */
     public CancellationToken getToken() {
         synchronized (lock) {
             throwIfClosed();
diff --git a/bolts-tasks/src/main/java/com/parse/boltsinternal/Task.java b/bolts-tasks/src/main/java/com/parse/boltsinternal/Task.java
index 8e73588..4cb36b3 100644
--- a/bolts-tasks/src/main/java/com/parse/boltsinternal/Task.java
+++ b/bolts-tasks/src/main/java/com/parse/boltsinternal/Task.java
@@ -28,8 +28,10 @@ import java.util.concurrent.atomic.AtomicInteger;
 public class Task<TResult> {
     /** An {@link java.util.concurrent.Executor} that executes tasks in parallel. */
     public static final ExecutorService BACKGROUND_EXECUTOR = BoltsExecutors.background();
+
     /** An {@link java.util.concurrent.Executor} that executes tasks on the UI thread. */
     public static final Executor UI_THREAD_EXECUTOR = AndroidExecutors.uiThread();
+
     /**
      * An {@link java.util.concurrent.Executor} that executes tasks in the current thread unless the
      * stack runs too deep, at which point it will delegate to {@link Task#BACKGROUND_EXECUTOR} in
@@ -541,28 +543,36 @@ public class Task<TResult> {
         }
     }
 
-    /** @return {@code true} if the task was cancelled, {@code false} otherwise. */
+    /**
+     * @return {@code true} if the task was cancelled, {@code false} otherwise.
+     */
     public boolean isCancelled() {
         synchronized (lock) {
             return cancelled;
         }
     }
 
-    /** @return {@code true} if the task has an error, {@code false} otherwise. */
+    /**
+     * @return {@code true} if the task has an error, {@code false} otherwise.
+     */
     public boolean isFaulted() {
         synchronized (lock) {
             return getError() != null;
         }
     }
 
-    /** @return The result of the task, if set. {@code null} otherwise. */
+    /**
+     * @return The result of the task, if set. {@code null} otherwise.
+     */
     public TResult getResult() {
         synchronized (lock) {
             return result;
         }
     }
 
-    /** @return The error for the task, if set. {@code null} otherwise. */
+    /**
+     * @return The error for the task, if set. {@code null} otherwise.
+     */
     public Exception getError() {
         synchronized (lock) {
             if (error != null) {
diff --git a/bolts-tasks/src/main/java/com/parse/boltsinternal/TaskCompletionSource.java b/bolts-tasks/src/main/java/com/parse/boltsinternal/TaskCompletionSource.java
index 4406cef..4513b76 100644
--- a/bolts-tasks/src/main/java/com/parse/boltsinternal/TaskCompletionSource.java
+++ b/bolts-tasks/src/main/java/com/parse/boltsinternal/TaskCompletionSource.java
@@ -24,7 +24,9 @@ public class TaskCompletionSource<TResult> {
         task = new Task<>();
     }
 
-    /** @return the Task associated with this TaskCompletionSource. */
+    /**
+     * @return the Task associated with this TaskCompletionSource.
+     */
     public Task<TResult> getTask() {
         return task;
     }
diff --git a/build.gradle b/build.gradle
index 9755671..f058aa0 100644
--- a/build.gradle
+++ b/build.gradle
@@ -39,13 +39,16 @@ allprojects {
         }
         kotlin {
             target '**/*.kt'
-            ktlint().userData(["disabled_rules": "no-wildcard-imports"])
+            ktlint().editorConfigOverride(["disabled_rules": "no-wildcard-imports"])
             trimTrailingWhitespace()
             indentWithSpaces()
             endWithNewline()
         }
         format 'misc', {
             target '**/*.gradle', '**/*.md', '**/.gitignore'
+            // Machine-managed dirs: task files carry injected git diffs, context-reports carry
+            // graph output — the formatter must not rewrite them (would churn on every stage).
+            targetExclude 'tasks/**', 'context-reports/**'
             indentWithSpaces()
             trimTrailingWhitespace()
             endWithNewline()
@@ -61,7 +64,7 @@ allprojects {
 }
 
 ext {
-    compileSdkVersion = 35
+    compileSdkVersion = 36
 
     minSdkVersion = 23
     targetSdkVersion = 35
@@ -69,5 +72,5 @@ ext {
     mockitoCoreVersion = '4.6.1'
     junitVersion = "4.13.2"
     jupiterVersion = "5.6.0"
-    robolectricVersion = "4.7"
+    robolectricVersion = "4.14.1"
 }
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseCloudCoroutinesExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseCloudCoroutinesExtensions.kt
index 7e774c5..53187b2 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseCloudCoroutinesExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseCloudCoroutinesExtensions.kt
@@ -10,8 +10,11 @@ import kotlin.coroutines.suspendCoroutine
 suspend fun <T> callCloudFunction(functionName: String, params: Map<String, Any>): T {
     return suspendCoroutine { continuation ->
         ParseCloud.callFunctionInBackground<T>(functionName, params) { result, e ->
-            if (e == null) continuation.resume(result)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(result)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesExtensions.kt
index 210148d..bfef47f 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesExtensions.kt
@@ -11,8 +11,11 @@ suspend fun <T : ParseObject> ParseObject.suspendFetch(): T {
     return suspendCoroutine { continuation ->
 
         fetchInBackground<T> { obj, e ->
-            if (e == null) continuation.resume(obj)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(obj)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
@@ -21,8 +24,11 @@ suspend fun <T : ParseObject> ParseObject.suspendFetchIfNeeded(): T {
     return suspendCoroutine { continuation ->
 
         fetchIfNeededInBackground<T> { obj, e ->
-            if (e == null) continuation.resume(obj)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(obj)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
@@ -31,8 +37,11 @@ suspend fun <T : ParseObject> ParseObject.fetchFromLocal(): T {
     return suspendCoroutine { continuation ->
 
         fetchFromLocalDatastoreInBackground<T> { obj, e ->
-            if (e == null) continuation.resume(obj)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(obj)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesWriteExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesWriteExtensions.kt
index ce3512c..061feba 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesWriteExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseObjectCoroutinesWriteExtensions.kt
@@ -10,8 +10,11 @@ import kotlin.coroutines.suspendCoroutine
 suspend fun ParseObject.suspendSave() {
     return suspendCoroutine { continuation ->
         saveInBackground {
-            if (it == null) continuation.resume(Unit)
-            else continuation.resumeWithException(it)
+            if (it == null) {
+                continuation.resume(Unit)
+            } else {
+                continuation.resumeWithException(it)
+            }
         }
     }
 }
@@ -19,8 +22,11 @@ suspend fun ParseObject.suspendSave() {
 suspend fun ParseObject.suspendPin() {
     return suspendCoroutine { continuation ->
         pinInBackground {
-            if (it == null) continuation.resume(Unit)
-            else continuation.resumeWithException(it)
+            if (it == null) {
+                continuation.resume(Unit)
+            } else {
+                continuation.resumeWithException(it)
+            }
         }
     }
 }
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesBuilder.kt b/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesBuilder.kt
index 8b2d309..478456f 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesBuilder.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesBuilder.kt
@@ -13,7 +13,7 @@ import kotlin.coroutines.EmptyCoroutineContext
 fun <T : ParseObject> CoroutineScope.launchQuery(
     query: ParseQuery<T>,
     context: CoroutineContext = EmptyCoroutineContext,
-    block: suspend ParseQueryOperation<T>.() -> Unit
+    block: suspend ParseQueryOperation<T>.() -> Unit,
 ): Job {
     return launch(context) {
         block.invoke(ParseQueryOperationImpl(query))
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesExtensions.kt
index a0749d2..b8bbb85 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseQueryCoroutinesExtensions.kt
@@ -19,8 +19,11 @@ internal suspend fun <T : ParseObject> ParseQuery<T>.findInternal(): List<T> {
         }
 
         findInBackground { objects, e ->
-            if (e == null) continuation.resume(objects)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(objects)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
@@ -36,8 +39,11 @@ internal suspend fun <T : ParseObject> ParseQuery<T>.getInternal(id: String): T
         }
 
         getInBackground(id) { obj, e ->
-            if (e == null) continuation.resume(obj)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(obj)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
@@ -53,8 +59,11 @@ internal suspend fun <T : ParseObject> ParseQuery<T>.firstInternal(): T {
         }
 
         getFirstInBackground { obj, e ->
-            if (e == null) continuation.resume(obj)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(obj)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
@@ -70,8 +79,11 @@ internal suspend fun <T : ParseObject> ParseQuery<T>.countInternal(): Int {
         }
 
         countInBackground { count, e ->
-            if (e == null) continuation.resume(count)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(count)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseTaskExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseTaskExtensions.kt
index 061f2b5..d842344 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseTaskExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseTaskExtensions.kt
@@ -15,8 +15,11 @@ import kotlin.coroutines.suspendCoroutine
 suspend fun <T> Task<T>.suspendGet(dispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext<T>(dispatcher) {
     return@withContext suspendCoroutine { continuation ->
         waitForCompletion()
-        if (isFaulted) continuation.resumeWithException(error)
-        else continuation.resume(result)
+        if (isFaulted) {
+            continuation.resumeWithException(error)
+        } else {
+            continuation.resume(result)
+        }
     }
 }
 
@@ -24,7 +27,10 @@ suspend fun <T> Task<T>.suspendGet(dispatcher: CoroutineDispatcher = Dispatchers
 suspend fun Task<Void>.suspendRun(dispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext<Unit>(dispatcher) {
     return@withContext suspendCoroutine { continuation ->
         waitForCompletion()
-        if (isFaulted) continuation.resumeWithException(error)
-        else continuation.resume(Unit)
+        if (isFaulted) {
+            continuation.resumeWithException(error)
+        } else {
+            continuation.resume(Unit)
+        }
     }
 }
diff --git a/coroutines/src/main/java/com/parse/coroutines/ParseUserCoroutinesExtensions.kt b/coroutines/src/main/java/com/parse/coroutines/ParseUserCoroutinesExtensions.kt
index 09606e8..16b09b4 100644
--- a/coroutines/src/main/java/com/parse/coroutines/ParseUserCoroutinesExtensions.kt
+++ b/coroutines/src/main/java/com/parse/coroutines/ParseUserCoroutinesExtensions.kt
@@ -10,8 +10,11 @@ import kotlin.coroutines.suspendCoroutine
 suspend fun ParseUser.suspendSignUp(): ParseUser {
     return suspendCoroutine { continuation ->
         signUpInBackground { e ->
-            if (e == null) continuation.resume(this)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(this)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
@@ -19,8 +22,11 @@ suspend fun ParseUser.suspendSignUp(): ParseUser {
 suspend fun parseLogIn(username: String, password: String): ParseUser {
     return suspendCoroutine { continuation ->
         ParseUser.logInInBackground(username, password) { user, e ->
-            if (e == null) continuation.resume(user)
-            else continuation.resumeWithException(e)
+            if (e == null) {
+                continuation.resume(user)
+            } else {
+                continuation.resumeWithException(e)
+            }
         }
     }
 }
diff --git a/docs/architecture.md b/docs/architecture.md
new file mode 100644
index 0000000..9ed9142
--- /dev/null
+++ b/docs/architecture.md
@@ -0,0 +1,48 @@
+# Architecture — Parse-SDK-Android
+
+> Version 4.4.0. 9 Gradle modules: `parse`, `bolts-tasks`, `coroutines`, `ktx`, `rxjava`, `fcm`, `google`, `facebook`, `twitter`. See `settings.gradle:1`.
+
+## Module Graph
+
+```text
+bolts-tasks ──api──▶ parse ◀──implementation── google, facebook, twitter, fcm, ktx, coroutines, rxjava
+```
+
+- `bolts-tasks` (`com.parse.boltsinternal`): pure-Java Task/CancellationToken runtime vendored from Bolts. Zero Android deps (`compileOnly android-4.1.1.4`). No knowledge of Parse.
+- `parse` (`com.parse`, Android library, namespace `com.parse`): entire SDK core — `Parse` entrypoint + `Configuration.Builder`, `ParseObject` state machine (`ParseOperationSet`, `OfflineStore`, `ParsePinningEventuallyQueue`), controllers (`Network*Controller`, `CacheQueryController`, `OfflineQueryController`), REST layer (`ParseRESTCommand` + `ParseREST*Command` builders over OkHttp 5.4.0), codec layer (`ParseEncoder`/`ParseDecoder`, `PointerEncoder`, parcel coders), auth (`ParseUser`, `ParseAuthenticationManager`, `ParseCurrentUserController`), push (`ParsePush`, `PushRouter`, `ParsePushBroadcastReceiver`, `ParsePushChannelsController`), config/analytics (`ParseConfig`, `ParseAnalytics`), files (`ParseFile`, `ParseFileController`), installations/sessions/roles (`ParseInstallation`, `ParseSession`, `ParseRole`), geo/polygon (`ParseGeoPoint`, `ParsePolygon`), local datastore (SQLite via `ParseSQLiteDatabase`, `OfflineStore`, `EventuallyPin`).
+- Adapters (each `implementation project(':parse')`, never interdependent):
+  - `google` (Kotlin): Play Services Auth → `ParseGoogleUtils` link/unlink.
+  - `facebook` (Java): Facebook Login SDK [13.2,17.0) → `FacebookController`.
+  - `twitter` (Java): OAuth1 + Signpost/OkHttp → `ParseTwitterUtils`, `TwitterController`, `OAuth1FlowDialog`.
+  - `fcm` (Java): Firebase Messaging 23.0.7 → `ParseFCM`, `ParseFirebaseMessagingService`.
+  - `ktx` (Kotlin): idiomatic extensions over core.
+  - `coroutines` (Kotlin): suspend wrappers (`ParseObjectCoroutines*`, `ParseQueryCoroutines*`, `ParseUserCoroutinesExtensions`, `ParseCloudCoroutinesExtensions`, `ParseTaskExtensions`).
+  - `rxjava` (Kotlin): RxJava3 `Single`/`Completable` wrappers (`Extensions.kt`).
+
+## Request Flow (core)
+
+```text
+ParseObject.saveInBackground → TaskQueue → NetworkObjectController → ParseRESTCommand → OkHttp (ParseHttpClient)
+        │                                                        ▲
+        └── OfflineStore / EventuallyPin (LDS + saveEventually) ─┘
+ParseQuery.findInBackground → CachePolicy → NetworkQueryController | OfflineQueryController | CacheQueryController
+```
+
+## Key Decisions (see module DECISIONS.md for dated ADRs)
+
+- Bolts `Task` kept as the async lingua franca for backward compat; coroutines/Rx are thin wrappers.
+- OkHttp pinned at 5.4.0 in `parse/build.gradle:50`; auth/push SDKs float within ranges.
+- `minSdk 23`, `targetSdk 35`, `compileSdk 36`; AndroidX mandatory.
+
+## CI
+
+- `ci.yml`: `spotlessCheck` → wrapper validation → `clean jacocoTestReport` → Codecov. JDK 11, `--no-daemon`.
+
+## Fork Divergence (mokhtarabadi vs upstream `9e6e18d`)
+
+Local `master` (`17da089`) is 3 commits ahead of `parse-community/master`. All fork-only work, dated:
+
+- `890ceaf` (2026-08-21) "fix many bugs" — toolchain modernization: AGP 7.1.3→9.2.1, Kotlin 1.7.10→1.9.22, compileSdk 31→35, minSdk 21→23, targetSdk 31→35, wrapper 7.4.2→9.4.1, Spotless 5.17.1→6.25.0 (+ freefair/versions bumps). Behavior: cancellable HTTP — `ParseHttpClient` tracks the in-flight OkHttp `Call` (`currentCall`, Task 718) and `ParseQuery.cancel()` now interrupts the request instead of only flagging the Task (plus Issue #969 cancel log); new `ParseObject.saveEventuallyInBackground()` runs `collectDirtyChildren` off the main thread on a bounded executor (Task 720, no more per-call raw threads), with an Issue #1007 main-thread warning in `saveEventually()`.
+- `1b42f79` (2026-08-22) "hardening" — `ParseUser` in-memory session-token cache (5-min TTL, `invalidateCachedSessionToken()`, cleared on logout; Task 720, avoids disk I/O per query at high qps); `saveEventually` executor refinement; `release-proguard.pro` keeps retrace attributes but drops the source-file rename.
+- `17da089` (2026-10-10) "sticky experiment bucketing" — new pure-static `ParseExperiments` helper: deterministic `SHA-256(key + "\n" + userId)` mod-100 buckets over `ParseConfig` definitions, no assignment table, widen-only growth; parity-tested against cloud/JS and dumble vectors (`ParseExperimentsTest.extendedParityVectors`).
+- Uncommitted local (not in any commit): `compileSdkVersion` 35→36 in root `build.gradle`.
diff --git a/docs/conventions.md b/docs/conventions.md
new file mode 100644
index 0000000..95fb3d7
--- /dev/null
+++ b/docs/conventions.md
@@ -0,0 +1,32 @@
+# Conventions — Parse-SDK-Android
+
+## Formatting (CI-enforced via Spotless, JDK 17)
+
+- Java: `googleJavaFormat(AOSP)`, 4-space indent, `removeUnusedImports`, trim trailing whitespace, end with newline. Target `**/*.java`.
+- Kotlin: `ktlint` with `no-wildcard-imports` disabled; same whitespace rules. Target `**/*.kt`.
+- Gradle/Markdown/gitignore: `misc` format block; XML: `xml` format block.
+- Commands: check `./gradlew spotlessCheck --no-daemon`; fix `./gradlew spotlessApply --no-daemon`.
+- Test runtime: Robolectric 4.14.1 (supports current targetSdk); Mockito 4.6.1, MockWebServer via OkHttp BOM. Real unit gate: `:parse:testDebugUnitTest`.
+
+## Language & API
+
+- Core `parse` module is Java 8 (`sourceCompatibility = targetCompatibility = 1.8`); Kotlin only in adapter/surface modules (`google`, `ktx`, `coroutines`, `rxjava`).
+- Public API keeps Bolts `Task<T>` + callback overloads (`SaveCallback`, `FindCallback`, `LogInCallback`, …). Kotlin modules add suspend/Rx wrappers — never remove the Task/callback originals.
+- Nullability: annotate new public Java APIs with `androidx.annotation` (`@NonNull`/`@Nullable`); Kotlin surfaces use strict nullability.
+- No Guava, no new networking stack: HTTP stays on OkHttp 5.4.0 via `ParseHttpClient`/`ParseRESTCommand`.
+
+## Naming & Layout
+
+- Package per module: `com.parse` (core), `com.parse.boltsinternal` (tasks), `com.parse.<adapter>` (google/facebook/twitter/fcm/ktx/coroutines/rxjava).
+- Tests mirror main: `parse/src/test/java/com/parse/*Test.java` with `ParseTestUtils`/`TestHelper`/`TaskQueueTestHelper` helpers. New tests follow existing `*Test` naming.
+- Resources: `includeAndroidResources = true` for unit tests; `consumerProguardFiles release-proguard.pro`.
+
+## Versioning & Releases
+
+- Semantic versioning via semantic-release (`release.config.js`, `.releaserc/`); `gradle.properties` holds the version (currently 4.4.1). CHANGELOG.md is generated — do not hand-edit release entries.
+- JitPack coordinates: `com.github.mokhtarabadi.Parse-SDK-Android:<module>:<version>`.
+
+## Task Files & Memory
+
+- Task template: canonical headers (`**File:**`, status, seats, mode). Every change records `rtk test`-prefixed verification evidence with exit code.
+- Memories: `android-sdk/stack-quirks`, `android-sdk/test-commands`. Store only explicit reusable rules.
diff --git a/docs/data_model.md b/docs/data_model.md
new file mode 100644
index 0000000..fe1f113
--- /dev/null
+++ b/docs/data_model.md
@@ -0,0 +1,34 @@
+# Data Model — Parse-SDK-Android
+
+> Client-side model of Parse Server objects. Wire format is Parse REST JSON; see `ParseEncoder` / `ParseDecoder` / `PointerEncoder` (`parse/src/main/java/com/parse/`).
+
+## Core Types
+
+| Class | Server class | Key fields | Notes |
+|---|---|---|---|
+| `ParseObject` | any (`ParseClassName`) | `objectId`, `createdAt`, `updatedAt`, `ACL`, subclass fields via `ParseOperationSet` | Dirty-tracking state machine; `saveInBackground` / `saveEventually` (via `EventuallyPin` + `ParsePinningEventuallyQueue`) |
+| `ParseUser` | `_User` | `username`, `password` (write-only), `email`, `sessionToken`, `authData` | `logInInBackground` / `signUpInBackground`; current user via `ParseCurrentUserController` + `CachedCurrentUserController` |
+| `ParseSession` | `_Session` | `sessionToken`, `user` (Pointer), `expiresAt` | Revocable sessions; `NetworkSessionController` |
+| `ParseRole` | `_Role` | `name`, `users` (Relation), `roles` (Relation) | ACL-gated; `ParseRoleTest` |
+| `ParseInstallation` | `_Installation` | `installationId`, `deviceToken`, `channels`, `pushType`, `GCMSenderId` | Cached via `CachedCurrentInstallationController`; FCM adapter writes `deviceToken` |
+| `ParseConfig` | `Config` (singleton) | arbitrary JSON params | `ParseCurrentConfigController`; `getInBackground` |
+| `ParseFile` | `File` | `name`, `url`, `__type: File` | Upload via `ParseFileController` + `ParseRESTFileCommand`; URI/ByteArray/File bodies |
+| `ParseGeoPoint` | inline type | `latitude`, `longitude`, `__type: GeoPoint` | Query `$nearSphere` support |
+| `ParsePolygon` | inline type | `coordinates`, `__type: Polygon` | `$geoWithin` support |
+| `ParseACL` | inline map | per-user/role read+write, `publicRead/WriteAccess` | Parcelable; enforced server-side |
+| `ParseRelation<T>` | inline | `__type: Relation`, `className` | Lazy query backing |
+| `ParsePush` | push payload | `channels`, `where`, `data`, `expiration_*` | Client send (legacy) + `PushRouter` receive |
+
+## Wire Encoding (`__type` tags)
+
+- `Date` → `{__type: Date, iso: …}` (`ParseDateFormat`, imprecise variant covered).
+- `Bytes` → `{__type: Bytes, base64: …}`; `Pointer` → `{__type: Pointer, className, objectId}`; `File` similar.
+- `OfflineStore` / `ParseSQLiteDatabase` persist objects + `EventuallyPin` queue in on-device SQLite (LDS). Cache layer: `ParseKeyValueCache` with `maxKeyValueCacheBytes/Files` (4.4.0, `Parse.Configuration`).
+
+## Local Identity
+
+- `installationId` (`InstallationId`, persisted); `LocalIdManager` for unsaved offline objects (`localId` → `objectId` remap on save).
+
+## Fork Addition: `ParseExperiments` (not a server class)
+
+- Pure-static client helper (`parse/src/main/java/com/parse/ParseExperiments.java`, fork commit `17da089`): sticky A/B bucketing evaluated locally from `ParseConfig` experiment definitions — `bucketFor(key, userId)` = `SHA-256(key + "\n" + userId)` head mod 100, `-1` on bad input (treat as control). Zero network, zero state, any-thread safe. Widening `traffic_pct` only admits higher buckets (early users never evicted); shrinking corrupts reads — rotate the key instead.
diff --git a/facebook/src/main/java/com/parse/facebook/FacebookController.java b/facebook/src/main/java/com/parse/facebook/FacebookController.java
index f353ca4..a2fc2a9 100644
--- a/facebook/src/main/java/com/parse/facebook/FacebookController.java
+++ b/facebook/src/main/java/com/parse/facebook/FacebookController.java
@@ -40,6 +40,7 @@ class FacebookController {
 
     // Used as default activityCode. From FacebookSdk.java.
     public static final int DEFAULT_AUTH_ACTIVITY_CODE = 0xface;
+
     /** Precise date format required for auth expiration data. */
     private static final DateFormat PRECISE_DATE_FORMAT =
             new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
diff --git a/google/src/main/java/com/parse/google/ParseGoogleUtils.kt b/google/src/main/java/com/parse/google/ParseGoogleUtils.kt
index d9bad72..ba306bf 100644
--- a/google/src/main/java/com/parse/google/ParseGoogleUtils.kt
+++ b/google/src/main/java/com/parse/google/ParseGoogleUtils.kt
@@ -207,7 +207,7 @@ object ParseGoogleUtils {
     private fun <T> callbackOnMainThreadAsync(
         task: Task<T>,
         callback: SaveCallback,
-        reportCancellation: Boolean
+        reportCancellation: Boolean,
     ): Task<T> {
         return callbackOnMainThreadInternalAsync(task, callback, reportCancellation)
     }
@@ -220,7 +220,7 @@ object ParseGoogleUtils {
     private fun <T> callbackOnMainThreadInternalAsync(
         task: Task<T>,
         callback: Any?,
-        reportCancellation: Boolean
+        reportCancellation: Boolean,
     ): Task<T> {
         if (callback == null) {
             return task
@@ -242,7 +242,8 @@ object ParseGoogleUtils {
                             callback.done(error as? ParseException)
                         } else if (callback is LogInCallback) {
                             callback.done(
-                                task2.result as? ParseUser, error as? ParseException
+                                task2.result as? ParseUser,
+                                error as? ParseException,
                             )
                         }
                     } finally {
@@ -260,7 +261,7 @@ object ParseGoogleUtils {
                     }
                 }
                 null
-            }
+            },
         )
         return tcs.task
     }
diff --git a/gradle/wrapper/gradle-wrapper.properties b/gradle/wrapper/gradle-wrapper.properties
index aa991fc..221c4f9 100644
--- a/gradle/wrapper/gradle-wrapper.properties
+++ b/gradle/wrapper/gradle-wrapper.properties
@@ -1,5 +1,5 @@
 distributionBase=GRADLE_USER_HOME
 distributionPath=wrapper/dists
-distributionUrl=https\://services.gradle.org/distributions/gradle-7.4.2-bin.zip
+distributionUrl=https\://services.gradle.org/distributions/gradle-9.4.1-bin.zip
 zipStoreBase=GRADLE_USER_HOME
 zipStorePath=wrapper/dists
diff --git a/ktx/src/main/java/com/parse/ktx/ParseQuery.kt b/ktx/src/main/java/com/parse/ktx/ParseQuery.kt
index b1242ac..4ee2e78 100644
--- a/ktx/src/main/java/com/parse/ktx/ParseQuery.kt
+++ b/ktx/src/main/java/com/parse/ktx/ParseQuery.kt
@@ -1,4 +1,5 @@
 @file:Suppress("NOTHING_TO_INLINE", "unused")
+
 package com.parse.ktx
 
 import com.parse.ParseException
@@ -76,7 +77,7 @@ fun <T : ParseObject> ParseQuery<T>.selectKeys(keys: Collection<KProperty<Any?>>
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereContainedIn(
     key: KProperty<Any?>,
-    values: Collection<Any?>
+    values: Collection<Any?>,
 ): ParseQuery<T> {
     return whereContainedIn(key.name, values)
 }
@@ -86,7 +87,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereContainedIn(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereContains(
     key: KProperty<Any?>,
-    substring: String
+    substring: String,
 ): ParseQuery<T> {
     return whereContains(key.name, substring)
 }
@@ -96,7 +97,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereContains(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereContainsAll(
     key: KProperty<Any?>,
-    values: Collection<ParseObject>
+    values: Collection<ParseObject>,
 ): ParseQuery<T> {
     return whereContainsAll(key.name, values)
 }
@@ -106,7 +107,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereContainsAll(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereContainsAllStartsWith(
     key: KProperty<Any?>,
-    values: Collection<String>
+    values: Collection<String>,
 ): ParseQuery<T> {
     return whereContainsAllStartsWith(key.name, values)
 }
@@ -124,7 +125,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereDoesNotExist(key: KProperty<Any?
 inline fun <T : ParseObject> ParseQuery<T>.whereDoesNotMatchKeyInQuery(
     key: KProperty<Any?>,
     keyInQuery: KProperty<Any?>,
-    query: ParseQuery<ParseObject>
+    query: ParseQuery<ParseObject>,
 ): ParseQuery<T> {
     return whereDoesNotMatchKeyInQuery(key.name, keyInQuery.name, query)
 }
@@ -134,7 +135,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereDoesNotMatchKeyInQuery(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereDoesNotMatchQuery(
     key: KProperty<Any?>,
-    query: ParseQuery<out ParseObject>
+    query: ParseQuery<out ParseObject>,
 ): ParseQuery<T> {
     return whereDoesNotMatchQuery(key.name, query)
 }
@@ -144,7 +145,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereDoesNotMatchQuery(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereEndsWith(
     key: KProperty<Any?>,
-    suffix: String
+    suffix: String,
 ): ParseQuery<T> {
     return whereEndsWith(key.name, suffix)
 }
@@ -154,7 +155,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereEndsWith(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereEqualTo(
     key: KProperty<Any?>,
-    value: Any?
+    value: Any?,
 ): ParseQuery<T> {
     return whereEqualTo(key.name, value)
 }
@@ -171,7 +172,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereExists(key: KProperty<Any?>): Pa
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereFullText(
     key: KProperty<Any?>,
-    text: String
+    text: String,
 ): ParseQuery<T> {
     return whereFullText(key.name, text)
 }
@@ -181,7 +182,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereFullText(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereGreaterThan(
     key: KProperty<Any?>,
-    value: Any
+    value: Any,
 ): ParseQuery<T> {
     return whereGreaterThan(key.name, value)
 }
@@ -191,7 +192,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereGreaterThan(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereGreaterThanOrEqualTo(
     key: KProperty<Any?>,
-    value: Any
+    value: Any,
 ): ParseQuery<T> {
     return whereGreaterThanOrEqualTo(key.name, value)
 }
@@ -201,7 +202,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereGreaterThanOrEqualTo(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereLessThan(
     key: KProperty<Any?>,
-    value: Any
+    value: Any,
 ): ParseQuery<T> {
     return whereLessThan(key.name, value)
 }
@@ -211,7 +212,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereLessThan(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereLessThanOrEqualTo(
     key: KProperty<Any?>,
-    value: Any
+    value: Any,
 ): ParseQuery<T> {
     return whereLessThanOrEqualTo(key.name, value)
 }
@@ -221,7 +222,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereLessThanOrEqualTo(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereMatches(
     key: KProperty<Any?>,
-    regex: String
+    regex: String,
 ): ParseQuery<T> {
     return whereMatches(key.name, regex)
 }
@@ -232,7 +233,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereMatches(
 inline fun <T : ParseObject> ParseQuery<T>.whereMatches(
     key: KProperty<Any?>,
     regex: String,
-    modifiers: String
+    modifiers: String,
 ): ParseQuery<T> {
     return whereMatches(key.name, regex, modifiers)
 }
@@ -243,7 +244,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereMatches(
 inline fun <T : ParseObject> ParseQuery<T>.whereMatchesKeyInQuery(
     key: KProperty<Any?>,
     keyInQuery: KProperty<Any?>,
-    query: ParseQuery<ParseObject>
+    query: ParseQuery<ParseObject>,
 ): ParseQuery<T> {
     return whereMatchesKeyInQuery(key.name, keyInQuery.name, query)
 }
@@ -253,7 +254,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereMatchesKeyInQuery(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereMatchesQuery(
     key: KProperty<Any?>,
-    query: ParseQuery<out ParseObject>
+    query: ParseQuery<out ParseObject>,
 ): ParseQuery<T> {
     return whereMatchesQuery(key.name, query)
 }
@@ -263,7 +264,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereMatchesQuery(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereNear(
     key: KProperty<Any?>,
-    point: ParseGeoPoint
+    point: ParseGeoPoint,
 ): ParseQuery<T> {
     return whereNear(key.name, point)
 }
@@ -273,7 +274,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereNear(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereNotContainedIn(
     key: KProperty<Any?>,
-    values: Collection<Any?>
+    values: Collection<Any?>,
 ): ParseQuery<T> {
     return whereNotContainedIn(key.name, values)
 }
@@ -283,7 +284,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereNotContainedIn(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereNotEqualTo(
     key: KProperty<Any?>,
-    value: Any?
+    value: Any?,
 ): ParseQuery<T> {
     return whereNotEqualTo(key.name, value)
 }
@@ -293,7 +294,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereNotEqualTo(
  */
 inline fun <T : ParseObject> ParseQuery<T>.wherePolygonContains(
     key: KProperty<Any?>,
-    point: ParseGeoPoint
+    point: ParseGeoPoint,
 ): ParseQuery<T> {
     return wherePolygonContains(key.name, point)
 }
@@ -303,7 +304,7 @@ inline fun <T : ParseObject> ParseQuery<T>.wherePolygonContains(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereStartsWith(
     key: KProperty<Any?>,
-    prefix: String
+    prefix: String,
 ): ParseQuery<T> {
     return whereStartsWith(key.name, prefix)
 }
@@ -314,7 +315,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereStartsWith(
 inline fun <T : ParseObject> ParseQuery<T>.whereWithinGeoBox(
     key: KProperty<Any?>,
     southwest: ParseGeoPoint,
-    northeast: ParseGeoPoint
+    northeast: ParseGeoPoint,
 ): ParseQuery<T> {
     return whereWithinGeoBox(key.name, southwest, northeast)
 }
@@ -325,7 +326,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereWithinGeoBox(
 inline fun <T : ParseObject> ParseQuery<T>.whereWithinKilometers(
     key: KProperty<Any?>,
     point: ParseGeoPoint,
-    maxDistance: Double
+    maxDistance: Double,
 ): ParseQuery<T> {
     return whereWithinKilometers(key.name, point, maxDistance)
 }
@@ -336,7 +337,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereWithinKilometers(
 inline fun <T : ParseObject> ParseQuery<T>.whereWithinMiles(
     key: KProperty<Any?>,
     point: ParseGeoPoint,
-    maxDistance: Double
+    maxDistance: Double,
 ): ParseQuery<T> {
     return whereWithinMiles(key.name, point, maxDistance)
 }
@@ -346,7 +347,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereWithinMiles(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereWithinPolygon(
     key: KProperty<Any?>,
-    points: List<ParseGeoPoint>
+    points: List<ParseGeoPoint>,
 ): ParseQuery<T> {
     return whereWithinPolygon(key.name, points)
 }
@@ -356,7 +357,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereWithinPolygon(
  */
 inline fun <T : ParseObject> ParseQuery<T>.whereWithinPolygon(
     key: KProperty<Any?>,
-    polygon: ParsePolygon
+    polygon: ParsePolygon,
 ): ParseQuery<T> {
     return whereWithinPolygon(key.name, polygon)
 }
@@ -367,7 +368,7 @@ inline fun <T : ParseObject> ParseQuery<T>.whereWithinPolygon(
 inline fun <T : ParseObject> ParseQuery<T>.whereWithinRadians(
     key: KProperty<Any?>,
     point: ParseGeoPoint,
-    maxDistance: Double
+    maxDistance: Double,
 ): ParseQuery<T> {
     return whereWithinRadians(key.name, point, maxDistance)
 }
diff --git a/ktx/src/main/java/com/parse/ktx/delegates/EnumParseDelegate.kt b/ktx/src/main/java/com/parse/ktx/delegates/EnumParseDelegate.kt
index 9f45ec6..d00dffa 100644
--- a/ktx/src/main/java/com/parse/ktx/delegates/EnumParseDelegate.kt
+++ b/ktx/src/main/java/com/parse/ktx/delegates/EnumParseDelegate.kt
@@ -14,14 +14,14 @@ import kotlin.reflect.KProperty
 class EnumParseDelegate<T : Enum<T>>(
     private val name: String?,
     private val default: T?,
-    private val enumClass: Class<T>
+    private val enumClass: Class<T>,
 ) {
 
     operator fun getValue(parseObject: ParseObject, property: KProperty<*>): T {
         return try {
             java.lang.Enum.valueOf(
                 enumClass,
-                parseObject.getString(name ?: property.name)!!.uppercase()
+                parseObject.getString(name ?: property.name)!!.uppercase(),
             )
         } catch (e: Exception) {
             default ?: throw e
diff --git a/ktx/src/main/java/com/parse/ktx/delegates/ListParseDelegate.kt b/ktx/src/main/java/com/parse/ktx/delegates/ListParseDelegate.kt
index f0b2dd6..8650a7e 100644
--- a/ktx/src/main/java/com/parse/ktx/delegates/ListParseDelegate.kt
+++ b/ktx/src/main/java/com/parse/ktx/delegates/ListParseDelegate.kt
@@ -18,7 +18,7 @@ class ListParseDelegate<T>(private val name: String?) {
     operator fun setValue(
         parseObject: ParseObject,
         property: KProperty<*>,
-        value: MutableList<T>?
+        value: MutableList<T>?,
     ) {
         parseObject.putOrIgnore(name ?: property.name, value)
     }
diff --git a/ktx/src/main/java/com/parse/ktx/delegates/MapParseDelegate.kt b/ktx/src/main/java/com/parse/ktx/delegates/MapParseDelegate.kt
index f72788e..e8b2eff 100644
--- a/ktx/src/main/java/com/parse/ktx/delegates/MapParseDelegate.kt
+++ b/ktx/src/main/java/com/parse/ktx/delegates/MapParseDelegate.kt
@@ -13,7 +13,7 @@ class MapParseDelegate<V>(private val name: String?) {
 
     operator fun getValue(
         parseObject: ParseObject,
-        property: KProperty<*>
+        property: KProperty<*>,
     ): MutableMap<String, V>? {
         return parseObject.getMap<V>(name ?: property.name) as? MutableMap<String, V>
     }
@@ -21,7 +21,7 @@ class MapParseDelegate<V>(private val name: String?) {
     operator fun setValue(
         parseObject: ParseObject,
         property: KProperty<*>,
-        value: MutableMap<String, V>?
+        value: MutableMap<String, V>?,
     ) {
         parseObject.putOrIgnore(name ?: property.name, value)
     }
diff --git a/ktx/src/main/java/com/parse/ktx/delegates/StringParseDelegate.kt b/ktx/src/main/java/com/parse/ktx/delegates/StringParseDelegate.kt
index bf4cdff..0a3c70c 100644
--- a/ktx/src/main/java/com/parse/ktx/delegates/StringParseDelegate.kt
+++ b/ktx/src/main/java/com/parse/ktx/delegates/StringParseDelegate.kt
@@ -11,7 +11,7 @@ import kotlin.reflect.KProperty
  */
 class StringParseDelegate<S : String?>(
     private val name: String?,
-    private val filter: (String) -> String
+    private val filter: (String) -> String,
 ) {
 
     operator fun getValue(parseObject: ParseObject, property: KProperty<*>): S {
@@ -31,7 +31,7 @@ class StringParseDelegate<S : String?>(
  */
 inline fun nullableStringAttribute(
     name: String? = null,
-    noinline filter: (String) -> String = { it }
+    noinline filter: (String) -> String = { it },
 ) = StringParseDelegate<String?>(name, filter)
 
 /**
@@ -40,5 +40,5 @@ inline fun nullableStringAttribute(
  */
 inline fun stringAttribute(
     name: String? = null,
-    noinline filter: (String) -> String = { it }
+    noinline filter: (String) -> String = { it },
 ) = StringParseDelegate<String>(name, filter)
diff --git a/parse/DECISIONS.md b/parse/DECISIONS.md
index 3954deb..aa0ce50 100644
--- a/parse/DECISIONS.md
+++ b/parse/DECISIONS.md
@@ -26,3 +26,8 @@
 - Decision: deterministic hash bucketing over `ParseConfig` definitions, parity-tested vs cloud/JS and dumble.
 - Reason: per-user variation without per-user Config payloads or assignment tables.
 - Status: accepted (in fork history).
+
+## 2026-10-10 — Task 005: token-cache TTL accepted as trade-off (Brain B1 deferred)
+- Decision: keep 5-min in-memory session-token TTL; no shortening without product call. Invalidate paths (`saveCurrentUserAsync` refresh, logout clear, `invalidateCachedSessionToken()`) stay as-is.
+- Reason: server-side revoke inside the window reuses the old token by design of any TTL cache; disk remains source of truth on restart.
+- Status: accepted (deferred fix, logged).
diff --git a/parse/src/main/java/com/parse/ManifestInfo.java b/parse/src/main/java/com/parse/ManifestInfo.java
index 31b2219..276fcd1 100644
--- a/parse/src/main/java/com/parse/ManifestInfo.java
+++ b/parse/src/main/java/com/parse/ManifestInfo.java
@@ -150,7 +150,9 @@ public class ManifestInfo {
         }
     }
 
-    /** @return A {@link Bundle} if meta-data is specified in AndroidManifest, otherwise null. */
+    /**
+     * @return A {@link Bundle} if meta-data is specified in AndroidManifest, otherwise null.
+     */
     public static Bundle getApplicationMetadata(Context context) {
         ApplicationInfo info = getApplicationInfo(context, PackageManager.GET_META_DATA);
         if (info != null) {
diff --git a/parse/src/main/java/com/parse/OfflineSQLiteOpenHelper.java b/parse/src/main/java/com/parse/OfflineSQLiteOpenHelper.java
index 76fda7b..235f0bf 100644
--- a/parse/src/main/java/com/parse/OfflineSQLiteOpenHelper.java
+++ b/parse/src/main/java/com/parse/OfflineSQLiteOpenHelper.java
@@ -27,6 +27,7 @@ class OfflineSQLiteOpenHelper extends ParseSQLiteOpenHelper {
     /** Various keys in the table of ParseObjects. */
     /* package */
     /* package */ static final String KEY_UUID = "uuid";
+
     /* package */ static final String KEY_CLASS_NAME = "className";
     /* package */ static final String KEY_OBJECT_ID = "objectId";
     /* package */ static final String KEY_JSON = "json";
@@ -38,6 +39,7 @@ class OfflineSQLiteOpenHelper extends ParseSQLiteOpenHelper {
     /** Various keys in the table of Dependencies. */
     // TODO (grantland): rename this since we use UUIDs as keys now. root_uuid?
     /* package */ static final String KEY_KEY = "key";
+
     // static final String KEY_UUID = "uuid";
 
     /** The SQLite Database name. */
diff --git a/parse/src/main/java/com/parse/OfflineStore.java b/parse/src/main/java/com/parse/OfflineStore.java
index accd4b7..95e10e7 100644
--- a/parse/src/main/java/com/parse/OfflineStore.java
+++ b/parse/src/main/java/com/parse/OfflineStore.java
@@ -34,15 +34,18 @@ class OfflineStore {
 
     /** SQLite has a max of 999 SQL variables in a single statement. */
     private static final int MAX_SQL_VARIABLES = 999;
+
     // Lock for all members of the store.
     private final Object lock = new Object();
     // Helper for accessing the database.
     private final OfflineSQLiteOpenHelper helper;
+
     /**
      * In-memory map of UUID -> ParseObject. This is used so that we can always return the same
      * instance for a given object. The only objects in this map are ones that are in the database.
      */
     private final WeakValueHashMap<String, ParseObject> uuidToObjectMap = new WeakValueHashMap<>();
+
     /**
      * In-memory map of ParseObject -> UUID. This is used to that when we see an unsaved ParseObject
      * that's already in the database, we can update the same record in the database. It stores a
@@ -50,12 +53,14 @@ class OfflineStore {
      * finished, there will be a row for this UUID in the database.
      */
     private final WeakHashMap<ParseObject, Task<String>> objectToUuidMap = new WeakHashMap<>();
+
     /**
      * In-memory set of ParseObjects that have been fetched from the local database already. If the
      * object is in the map, a fetch of it has been started. If the value is a finished task, then
      * the fetch was completed.
      */
     private final WeakHashMap<ParseObject, Task<ParseObject>> fetchedObjects = new WeakHashMap<>();
+
     /**
      * In-memory map of (className, objectId) -> ParseObject. This is used so that we can always
      * return the same instance for a given object. Objects in this map may or may not be in the
diff --git a/parse/src/main/java/com/parse/Parse.java b/parse/src/main/java/com/parse/Parse.java
index 7916797..61549bb 100644
--- a/parse/src/main/java/com/parse/Parse.java
+++ b/parse/src/main/java/com/parse/Parse.java
@@ -290,7 +290,9 @@ public class Parse {
         allowCustomObjectId = false;
     }
 
-    /** @return {@code True} if {@link #initialize} has been called, otherwise {@code false}. */
+    /**
+     * @return {@code True} if {@link #initialize} has been called, otherwise {@code false}.
+     */
     static boolean isInitialized() {
         return ParsePlugins.get() != null;
     }
@@ -605,6 +607,7 @@ public class Parse {
             /** The default maximum number of bytes to use for the Parse cache on disk. */
             public static final int DEFAULT_MAX_KEY_VALUE_CACHE_BYTES =
                     ParseKeyValueCache.DEFAULT_MAX_KEY_VALUE_CACHE_BYTES;
+
             /** The default maximum number of files to store in the Parse cache on disk. */
             public static final int DEFAULT_MAX_KEY_VALUE_CACHE_FILES =
                     ParseKeyValueCache.DEFAULT_MAX_KEY_VALUE_CACHE_FILES;
diff --git a/parse/src/main/java/com/parse/ParseACL.java b/parse/src/main/java/com/parse/ParseACL.java
index cec83f9..16ce8f8 100644
--- a/parse/src/main/java/com/parse/ParseACL.java
+++ b/parse/src/main/java/com/parse/ParseACL.java
@@ -44,6 +44,7 @@ public class ParseACL implements Parcelable {
     // State
     private final Map<String, Permissions> permissionsById = new HashMap<>();
     private boolean shared;
+
     /** A lazy user that hasn't been saved to Parse. */
     // TODO (grantland): This should be a list for multiple lazy users with read/write permissions.
     private ParseUser unresolvedUser;
diff --git a/parse/src/main/java/com/parse/ParseClassName.java b/parse/src/main/java/com/parse/ParseClassName.java
index d2f4a35..ab5b436 100644
--- a/parse/src/main/java/com/parse/ParseClassName.java
+++ b/parse/src/main/java/com/parse/ParseClassName.java
@@ -21,6 +21,8 @@ import java.lang.annotation.Target;
 @Inherited
 @Documented
 public @interface ParseClassName {
-    /** @return The Parse class name associated with the ParseObject subclass. */
+    /**
+     * @return The Parse class name associated with the ParseObject subclass.
+     */
     String value();
 }
diff --git a/parse/src/main/java/com/parse/ParseException.java b/parse/src/main/java/com/parse/ParseException.java
index 5dc3b5f..1fab37c 100644
--- a/parse/src/main/java/com/parse/ParseException.java
+++ b/parse/src/main/java/com/parse/ParseException.java
@@ -16,124 +16,167 @@ package com.parse;
 @SuppressWarnings("unused")
 public class ParseException extends Exception {
     public static final int OTHER_CAUSE = -1;
+
     /** Error code indicating the connection to the Parse servers failed. */
     public static final int CONNECTION_FAILED = 100;
+
     /** Error code indicating the specified object doesn't exist. */
     public static final int OBJECT_NOT_FOUND = 101;
+
     /**
      * Error code indicating you tried to query with a datatype that doesn't support it, like exact
      * matching an array or object.
      */
     public static final int INVALID_QUERY = 102;
+
     /**
      * Error code indicating a missing or invalid classname. Classnames are case-sensitive. They
      * must start with a letter, and a-zA-Z0-9_ are the only valid characters.
      */
     public static final int INVALID_CLASS_NAME = 103;
+
     /** Error code indicating an unspecified object id. */
     public static final int MISSING_OBJECT_ID = 104;
+
     /**
      * Error code indicating an invalid key name. Keys are case-sensitive. They must start with a
      * letter, and a-zA-Z0-9_ are the only valid characters.
      */
     public static final int INVALID_KEY_NAME = 105;
+
     /**
      * Error code indicating a malformed pointer. You should not see this unless you have been
      * mucking about changing internal Parse code.
      */
     public static final int INVALID_POINTER = 106;
+
     /**
      * Error code indicating that badly formed JSON was received upstream. This either indicates you
      * have done something unusual with modifying how things encode to JSON, or the network is
      * failing badly.
      */
     public static final int INVALID_JSON = 107;
+
     /**
      * Error code indicating that the feature you tried to access is only available internally for
      * testing purposes.
      */
     public static final int COMMAND_UNAVAILABLE = 108;
+
     /** You must call Parse.initialize before using the Parse library. */
     public static final int NOT_INITIALIZED = 109;
+
     /** Error code indicating that a field was set to an inconsistent type. */
     public static final int INCORRECT_TYPE = 111;
+
     /**
      * Error code indicating an invalid channel name. A channel name is either an empty string (the
      * broadcast channel) or contains only a-zA-Z0-9_ characters and starts with a letter.
      */
     public static final int INVALID_CHANNEL_NAME = 112;
+
     /** Error code indicating that push is misconfigured. */
     public static final int PUSH_MISCONFIGURED = 115;
+
     /** Error code indicating that the object is too large. */
     public static final int OBJECT_TOO_LARGE = 116;
+
     /** Error code indicating that the operation isn't allowed for clients. */
     public static final int OPERATION_FORBIDDEN = 119;
+
     /** Error code indicating the result was not found in the cache. */
     public static final int CACHE_MISS = 120;
+
     /** Error code indicating that an invalid key was used in a nested JSONObject. */
     public static final int INVALID_NESTED_KEY = 121;
+
     /**
      * Error code indicating that an invalid filename was used for ParseFile. A valid file name
      * contains only a-zA-Z0-9_. characters and is between 1 and 128 characters.
      */
     public static final int INVALID_FILE_NAME = 122;
+
     /** Error code indicating an invalid ACL was provided. */
     public static final int INVALID_ACL = 123;
+
     /**
      * Error code indicating that the request timed out on the server. Typically this indicates that
      * the request is too expensive to run.
      */
     public static final int TIMEOUT = 124;
+
     /** Error code indicating that the email address was invalid. */
     public static final int INVALID_EMAIL_ADDRESS = 125;
+
     /** Error code indicating that required field is missing. */
     public static final int MISSING_REQUIRED_FIELD_ERROR = 135;
+
     /** Error code indicating that a unique field was given a value that is already taken. */
     public static final int DUPLICATE_VALUE = 137;
+
     /** Error code indicating that a role's name is invalid. */
     public static final int INVALID_ROLE_NAME = 139;
+
     /** Error code indicating that an application quota was exceeded. Upgrade to resolve. */
     public static final int EXCEEDED_QUOTA = 140;
+
     /** Error code indicating that a Cloud Code script failed. */
     public static final int SCRIPT_ERROR = 141;
+
     /** Error code indicating that cloud code validation failed. */
     public static final int VALIDATION_ERROR = 142;
+
     /** Error code indicating that deleting a file failed. */
     public static final int FILE_DELETE_ERROR = 153;
+
     /** Error code indicating that the application has exceeded its request limit. */
     public static final int REQUEST_LIMIT_EXCEEDED = 155;
+
     /** Error code indicating that the provided event name is invalid. */
     public static final int INVALID_EVENT_NAME = 160;
+
     /** Error code indicating that the username is missing or empty. */
     public static final int USERNAME_MISSING = 200;
+
     /** Error code indicating that the password is missing or empty. */
     public static final int PASSWORD_MISSING = 201;
+
     /** Error code indicating that the username has already been taken. */
     public static final int USERNAME_TAKEN = 202;
+
     /** Error code indicating that the email has already been taken. */
     public static final int EMAIL_TAKEN = 203;
+
     /** Error code indicating that the email is missing, but must be specified. */
     public static final int EMAIL_MISSING = 204;
+
     /** Error code indicating that a user with the specified email was not found. */
     public static final int EMAIL_NOT_FOUND = 205;
+
     /** Error code indicating that a user object without a valid session could not be altered. */
     public static final int SESSION_MISSING = 206;
+
     /** Error code indicating that a user can only be created through signup. */
     public static final int MUST_CREATE_USER_THROUGH_SIGNUP = 207;
+
     /** Error code indicating that an an account being linked is already linked to another user. */
     public static final int ACCOUNT_ALREADY_LINKED = 208;
+
     /** Error code indicating that the current session token is invalid. */
     public static final int INVALID_SESSION_TOKEN = 209;
+
     /**
      * Error code indicating that a user cannot be linked to an account because that account's id
      * could not be found.
      */
     public static final int LINKED_ID_MISSING = 250;
+
     /**
      * Error code indicating that a user with a linked (e.g. Facebook) account has an invalid
      * session.
      */
     public static final int INVALID_LINKED_SESSION = 251;
+
     /**
      * Error code indicating that a service being linked (e.g. Facebook or Twitter) is unsupported.
      */
diff --git a/parse/src/main/java/com/parse/ParseExperiments.java b/parse/src/main/java/com/parse/ParseExperiments.java
index e83191d..42ba817 100644
--- a/parse/src/main/java/com/parse/ParseExperiments.java
+++ b/parse/src/main/java/com/parse/ParseExperiments.java
@@ -19,16 +19,16 @@ import org.json.JSONObject;
 /**
  * Sticky experiment bucketing over {@link ParseConfig} definitions.
  *
- * <p>A/B testing needs per-user variation, but {@code ParseConfig} is global (one payload for
- * all clients) and disk-cached. This helper therefore never invents per-user Config: it reads
+ * <p>A/B testing needs per-user variation, but {@code ParseConfig} is global (one payload for all
+ * clients) and disk-cached. This helper therefore never invents per-user Config: it reads
  * experiment <em>definitions</em> from the shared Config and evaluates each user locally with a
  * deterministic hash. Same user, same bucket — every process, every launch, no assignment table.
  *
- * <p>Bucket math: {@code SHA-256(experimentKey + "\n" + userId)}, first 4 bytes as an unsigned
- * int, {@code mod 100}. The user is in-variant iff {@code bucket < traffic_pct}. Widening the
- * percent only admits higher buckets, so early users are never evicted mid-experiment. Shrinking
- * mid-flight corrupts the read — rotate the experiment key instead (the key is part of the hash,
- * so arms reshuffle cleanly).
+ * <p>Bucket math: {@code SHA-256(experimentKey + "\n" + userId)}, first 4 bytes as an unsigned int,
+ * {@code mod 100}. The user is in-variant iff {@code bucket < traffic_pct}. Widening the percent
+ * only admits higher buckets, so early users are never evicted mid-experiment. Shrinking mid-flight
+ * corrupts the read — rotate the experiment key instead (the key is part of the hash, so arms
+ * reshuffle cleanly).
  *
  * <p>Pure static functions, zero network, zero state. Safe to call from any thread.
  */
@@ -47,16 +47,21 @@ public final class ParseExperiments {
      * @return bucket in [0, 100), or -1 on invalid input
      */
     public static int bucketFor(String experimentKey, String userId) {
-        if (experimentKey == null || experimentKey.isEmpty() || userId == null || userId.isEmpty()) {
+        if (experimentKey == null
+                || experimentKey.isEmpty()
+                || userId == null
+                || userId.isEmpty()) {
             return -1;
         }
         try {
             MessageDigest digest = MessageDigest.getInstance("SHA-256");
-            byte[] hash = digest.digest((experimentKey + "\n" + userId).getBytes(StandardCharsets.UTF_8));
-            long head = ((hash[0] & 0xFFL) << 24)
-                    | ((hash[1] & 0xFFL) << 16)
-                    | ((hash[2] & 0xFFL) << 8)
-                    | (hash[3] & 0xFFL);
+            byte[] hash =
+                    digest.digest((experimentKey + "\n" + userId).getBytes(StandardCharsets.UTF_8));
+            long head =
+                    ((hash[0] & 0xFFL) << 24)
+                            | ((hash[1] & 0xFFL) << 16)
+                            | ((hash[2] & 0xFFL) << 8)
+                            | (hash[3] & 0xFFL);
             return (int) (head % 100L);
         } catch (NoSuchAlgorithmException e) {
             return -1;
@@ -64,8 +69,8 @@ public final class ParseExperiments {
     }
 
     /**
-     * Finds an experiment definition by key inside the {@code ab_experiments} Config value.
-     * Accepts both {@link JSONArray} and {@link List} of maps (Parse decodes arrays either way).
+     * Finds an experiment definition by key inside the {@code ab_experiments} Config value. Accepts
+     * both {@link JSONArray} and {@link List} of maps (Parse decodes arrays either way).
      *
      * @param config the fetched config (null-safe, returns null)
      * @param experimentKey the experiment key to find
@@ -108,8 +113,8 @@ public final class ParseExperiments {
     /**
      * Evaluates whether a user is in-variant. Fail-closed at every step: null config, global
      * kill-switch off, missing/killed experiment, unreadable percent, or invalid bucket all yield
-     * {@code false} (control). New-vs-old targeting stays with the caller (it owns the
-     * user-joined semantics); this method judges only enabled + killed + percent + bucket.
+     * {@code false} (control). New-vs-old targeting stays with the caller (it owns the user-joined
+     * semantics); this method judges only enabled + killed + percent + bucket.
      *
      * @param config the fetched config (null-safe)
      * @param experimentKey the experiment key
diff --git a/parse/src/main/java/com/parse/ParseFile.java b/parse/src/main/java/com/parse/ParseFile.java
index 76a95e0..db4ae2b 100644
--- a/parse/src/main/java/com/parse/ParseFile.java
+++ b/parse/src/main/java/com/parse/ParseFile.java
@@ -59,11 +59,13 @@ public class ParseFile implements Parcelable {
     /* package for tests */ final TaskQueue taskQueue = new TaskQueue();
     private final Set<TaskCompletionSource<?>> currentTasks =
             Collections.synchronizedSet(new HashSet<>());
+
     /**
      * Staging of {@code ParseFile}'s data is stored in memory until the {@code ParseFile} has been
      * successfully synced with the server.
      */
     /* package for tests */ byte[] data;
+
     /* package for tests */ File file;
     /* package for tests */ Uri uri;
     private State state;
diff --git a/parse/src/main/java/com/parse/ParseHttpClient.java b/parse/src/main/java/com/parse/ParseHttpClient.java
index ec6b726..d806d5f 100644
--- a/parse/src/main/java/com/parse/ParseHttpClient.java
+++ b/parse/src/main/java/com/parse/ParseHttpClient.java
@@ -32,6 +32,9 @@ class ParseHttpClient {
     private final OkHttpClient okHttpClient;
     private boolean hasExecuted;
     // Task 718: Track the in-flight Call so cancel() can interrupt it.
+    // Limitation (documented, not fixed): one slot per client. Parallel queries on the same
+    // client share it, so cancelling one may interrupt another. Per-request tracking would
+    // require threading the Call through ParseQuery state — deferred as disproportionate.
     private volatile Call currentCall;
 
     ParseHttpClient(@Nullable OkHttpClient.Builder builder) {
@@ -77,9 +80,8 @@ class ParseHttpClient {
     }
 
     /**
-     * Task 718: Cancels the in-flight OkHttp call, if any. Called from
-     * {@link ParseQuery#cancel()} to actually interrupt the HTTP request
-     * rather than just setting a Task flag.
+     * Task 718: Cancels the in-flight OkHttp call, if any. Called from {@link ParseQuery#cancel()}
+     * to actually interrupt the HTTP request rather than just setting a Task flag.
      */
     void cancel() {
         Call call = currentCall;
@@ -89,11 +91,17 @@ class ParseHttpClient {
     }
 
     /**
-     * Task 718: Cancels the in-flight call on the default REST client.
-     * This is the entry point for {@link ParseQuery#cancel()}.
+     * Task 718: Cancels the in-flight call on the default REST client. This is the entry point for
+     * {@link ParseQuery#cancel()}.
      */
     static void cancelCurrentCall() {
-        ParseHttpClient client = ParsePlugins.get().restClient();
+        // ParsePlugins is null when the SDK was never initialized (e.g. plain-JVM unit tests):
+        // nothing is in flight, so cancellation is a no-op.
+        ParsePlugins plugins = ParsePlugins.get();
+        if (plugins == null) {
+            return;
+        }
+        ParseHttpClient client = plugins.restClient();
         if (client != null) {
             client.cancel();
         }
diff --git a/parse/src/main/java/com/parse/ParseKeyValueCache.java b/parse/src/main/java/com/parse/ParseKeyValueCache.java
index 1267181..8b0d5d7 100644
--- a/parse/src/main/java/com/parse/ParseKeyValueCache.java
+++ b/parse/src/main/java/com/parse/ParseKeyValueCache.java
@@ -28,6 +28,7 @@ class ParseKeyValueCache {
     /* package */ static final int DEFAULT_MAX_KEY_VALUE_CACHE_FILES = 1000;
     private static final String TAG = "ParseKeyValueCache";
     private static final String DIR_NAME = "ParseKeyValueCache";
+
     /** Prevent multiple threads from modifying the cache at the same time. */
     private static final Object MUTEX_IO = new Object();
 
diff --git a/parse/src/main/java/com/parse/ParseObject.java b/parse/src/main/java/com/parse/ParseObject.java
index 829fc65..935e175 100644
--- a/parse/src/main/java/com/parse/ParseObject.java
+++ b/parse/src/main/java/com/parse/ParseObject.java
@@ -57,6 +57,7 @@ public class ParseObject implements Parcelable {
      * @see #unpin()
      */
     public static final String DEFAULT_PIN = "_default";
+
     /*
     REST JSON Keys
     */
@@ -2341,12 +2342,16 @@ public class ParseObject implements Parcelable {
     }
 
     /**
-     * Wraps {@link #saveEventually()} in a background thread to prevent ANR when called from
-     * the main thread with deep object graphs. The expensive {@code collectDirtyChildren}
-     * traversal runs off the calling thread; the returned {@link Task} resolves on the
-     * main thread exactly like {@link #saveEventually()}.
+     * Wraps {@link #saveEventually()} in a background thread to prevent ANR when called from the
+     * main thread with deep object graphs. The expensive {@code collectDirtyChildren} traversal
+     * runs off the calling thread; the returned {@link Task} resolves on the main thread exactly
+     * like {@link #saveEventually()}.
+     *
+     * <p>Equivalent to {@code saveEventually()} but safe to call from the UI thread.
      *
-     * <p>Equivalent to {@code saveEventually()} but safe to call from the UI thread.</p>
+     * <p>Constraint: do not mutate this object (or its children) while the save is in flight. The
+     * dirty-children traversal runs on a background thread while the operation set may still be
+     * touched on the calling thread; concurrent mutation can lose ops.
      *
      * @return A {@link Task} that is resolved when the save completes.
      */
@@ -2356,7 +2361,8 @@ public class ParseObject implements Parcelable {
             return saveEventually();
         }
         // Task 720: Use bounded background executor instead of raw new Thread per call
-        // Prevents thread storm under burst (1000+ messages) — was new Thread("ParseSaveEventually") per call
+        // Prevents thread storm under burst (1000+ messages) — was new
+        // Thread("ParseSaveEventually") per call
         final com.parse.boltsinternal.TaskCompletionSource<Void> tcs =
                 new com.parse.boltsinternal.TaskCompletionSource<>();
         com.parse.boltsinternal.Task.call(
@@ -2411,9 +2417,12 @@ public class ParseObject implements Parcelable {
             return Task.forError(new ParseException(104, "ObjectId must not be null"));
         }
 
-        // Fix Issue #1007: Warn if called on main thread to prevent ANR
+        // Fix Issue #1007: Warn if called on main thread to prevent ANR (via PLog so unit
+        // tests stay green).
         if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
-            android.util.Log.w("ParseObject", "saveEventually() called on main thread. Consider calling from background thread to prevent ANR.");
+            PLog.w(
+                    "ParseObject",
+                    "saveEventually() called on main thread. Consider calling from background thread to prevent ANR.");
         }
 
         final ParseOperationSet operationSet;
diff --git a/parse/src/main/java/com/parse/ParsePinningEventuallyQueue.java b/parse/src/main/java/com/parse/ParsePinningEventuallyQueue.java
index a7d3a3b..3f34496 100644
--- a/parse/src/main/java/com/parse/ParsePinningEventuallyQueue.java
+++ b/parse/src/main/java/com/parse/ParsePinningEventuallyQueue.java
@@ -32,21 +32,26 @@ class ParsePinningEventuallyQueue extends ParseEventuallyQueue {
     private static final String TAG = "ParsePinningEventuallyQueue";
     private final Object connectionLock = new Object();
     private final ParseHttpClient httpClient;
+
     /** Lock to make sure all changes to the below parameters happen atomically. */
     private final Object taskQueueSyncLock = new Object();
+
     /** TCS that is held until a {@link ParseOperationSet} is completed. */
     private final HashMap<String, TaskCompletionSource<JSONObject>> pendingOperationSetUUIDTasks =
             new HashMap<>();
+
     /**
      * Queue for reading/writing eventually operations. Makes all reads/writes atomic operations.
      */
     private final TaskQueue taskQueue = new TaskQueue();
+
     /**
      * Queue for running *Eventually operations. It uses waitForOperationSetAndEventuallyPin to
      * synchronize {@link ParseObject#taskQueue} until they are both ready to process the same
      * ParseOperationSet.
      */
     private final TaskQueue operationSetTaskQueue = new TaskQueue();
+
     /**
      * List of {@link ParseOperationSet#uuid} that are currently queued in {@link
      * ParsePinningEventuallyQueue#operationSetTaskQueue}.
@@ -54,13 +59,17 @@ class ParsePinningEventuallyQueue extends ParseEventuallyQueue {
     private final ArrayList<String> eventuallyPinUUIDQueue = new ArrayList<>();
 
     private final ConnectivityNotifier notifier;
+
     /** Map of eventually operation UUID to TCS that is resolved when the operation is complete. */
     private final HashMap<String, TaskCompletionSource<JSONObject>> pendingEventuallyTasks =
             new HashMap<>();
+
     /** Map of eventually operation UUID to matching ParseOperationSet. */
     private final HashMap<String, ParseOperationSet> uuidToOperationSet = new HashMap<>();
+
     /** Map of eventually operation UUID to matching EventuallyPin. */
     private final HashMap<String, EventuallyPin> uuidToEventuallyPin = new HashMap<>();
+
     /**
      * TCS that is created when there is no internet connection and isn't resolved until
      * connectivity is achieved.
diff --git a/parse/src/main/java/com/parse/ParsePushBroadcastReceiver.java b/parse/src/main/java/com/parse/ParsePushBroadcastReceiver.java
index 489fa01..6335b08 100644
--- a/parse/src/main/java/com/parse/ParsePushBroadcastReceiver.java
+++ b/parse/src/main/java/com/parse/ParsePushBroadcastReceiver.java
@@ -84,14 +84,19 @@ public class ParsePushBroadcastReceiver extends BroadcastReceiver {
      * {@code null}.
      */
     public static final String KEY_PUSH_CHANNEL = "com.parse.Channel";
+
     /** The name of the Intent extra which contains the JSON payload of the Notification. */
     public static final String KEY_PUSH_DATA = "com.parse.Data";
+
     /** The name of the Intent fired when a push has been received. */
     public static final String ACTION_PUSH_RECEIVE = "com.parse.push.intent.RECEIVE";
+
     /** The name of the Intent fired when a notification has been opened. */
     public static final String ACTION_PUSH_OPEN = "com.parse.push.intent.OPEN";
+
     /** The name of the Intent fired when a notification has been dismissed. */
     public static final String ACTION_PUSH_DELETE = "com.parse.push.intent.DELETE";
+
     /** The name of the meta-data field used to override the icon used in Notifications. */
     public static final String PROPERTY_PUSH_ICON = "com.parse.push.notification_icon";
 
diff --git a/parse/src/main/java/com/parse/ParseQuery.java b/parse/src/main/java/com/parse/ParseQuery.java
index f6cf099..a178371 100644
--- a/parse/src/main/java/com/parse/ParseQuery.java
+++ b/parse/src/main/java/com/parse/ParseQuery.java
@@ -250,8 +250,9 @@ public class ParseQuery<T extends ParseObject> {
             tcs.trySetCancelled();
         }
         currentTasks.removeAll(tasks);
-        // Fix Issue #969: Log cancellation for debugging
-        android.util.Log.d("ParseQuery", "cancel() called. Cancelled " + tasks.size() + " pending tasks.");
+        // Fix Issue #969: Log cancellation for debugging (PLog is a no-op unless a level is
+        // set, so plain-JVM unit tests stay green — never log from here directly).
+        PLog.d("ParseQuery", "cancel() called. Cancelled " + tasks.size() + " pending tasks.");
         // Task 718: Also interrupt the in-flight OkHttp call so the HTTP request
         // doesn't keep running after the Task is cancelled.
         ParseHttpClient.cancelCurrentCall();
@@ -287,7 +288,9 @@ public class ParseQuery<T extends ParseObject> {
         return ParseTaskUtils.wait(getFirstInBackground());
     }
 
-    /** @return the caching policy. */
+    /**
+     * @return the caching policy.
+     */
     public CachePolicy getCachePolicy() {
         return builder.getCachePolicy();
     }
diff --git a/parse/src/main/java/com/parse/ParseRequest.java b/parse/src/main/java/com/parse/ParseRequest.java
index 1ba3049..d80f356 100644
--- a/parse/src/main/java/com/parse/ParseRequest.java
+++ b/parse/src/main/java/com/parse/ParseRequest.java
@@ -41,6 +41,7 @@ abstract class ParseRequest<Response> {
                             r, "ParseRequest.NETWORK_EXECUTOR-thread-" + mCount.getAndIncrement());
                 }
             };
+
     /**
      * We want to use more threads than default in {@code bolts.Executors} since most of the time
      * the threads will be asleep waiting for data.
diff --git a/parse/src/main/java/com/parse/ParseSession.java b/parse/src/main/java/com/parse/ParseSession.java
index a2d5b2e..a656966 100644
--- a/parse/src/main/java/com/parse/ParseSession.java
+++ b/parse/src/main/java/com/parse/ParseSession.java
@@ -122,7 +122,9 @@ public class ParseSession extends ParseObject {
         return !READ_ONLY_KEYS.contains(key);
     }
 
-    /** @return the session token for a user, if they are logged in. */
+    /**
+     * @return the session token for a user, if they are logged in.
+     */
     public String getSessionToken() {
         return getString(KEY_SESSION_TOKEN);
     }
diff --git a/parse/src/main/java/com/parse/ParseUser.java b/parse/src/main/java/com/parse/ParseUser.java
index 05ff6aa..e647490 100644
--- a/parse/src/main/java/com/parse/ParseUser.java
+++ b/parse/src/main/java/com/parse/ParseUser.java
@@ -236,7 +236,8 @@ public class ParseUser extends ParseObject {
         return current != null ? current.getSessionToken() : null;
     }
 
-    // Task 720: In-memory cache for session token to avoid disk I/O per query (1000+ qps → 1000 disk reads)
+    // Task 720: In-memory cache for session token to avoid disk I/O per query (1000+ qps → 1000
+    // disk reads)
     private static volatile String sCachedSessionToken = null;
     private static volatile long sCachedTokenTimestamp = 0;
     private static final long TOKEN_CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes
@@ -744,7 +745,9 @@ public class ParseUser extends ParseObject {
         }
     }
 
-    /** @return the session token for a user, if they are logged in. */
+    /**
+     * @return the session token for a user, if they are logged in.
+     */
     public String getSessionToken() {
         return getState().sessionToken();
     }
diff --git a/parse/src/main/java/com/parse/TaskQueue.java b/parse/src/main/java/com/parse/TaskQueue.java
index f53f5c9..b58561f 100644
--- a/parse/src/main/java/com/parse/TaskQueue.java
+++ b/parse/src/main/java/com/parse/TaskQueue.java
@@ -17,6 +17,7 @@ import java.util.concurrent.locks.ReentrantLock;
 /** A helper class for enqueueing tasks */
 class TaskQueue {
     private final Lock lock = new ReentrantLock();
+
     /**
      * We only need to keep the tail of the queue. Cancelled tasks will just complete
      * normally/immediately when their turn arrives.
diff --git a/parse/src/test/java/com/parse/ParseACLTest.java b/parse/src/test/java/com/parse/ParseACLTest.java
index 34aa290..6a9384f 100644
--- a/parse/src/test/java/com/parse/ParseACLTest.java
+++ b/parse/src/test/java/com/parse/ParseACLTest.java
@@ -468,6 +468,7 @@ public class ParseACLTest {
         assertTrue(acl.getWriteAccess(user));
         assertEquals(1, acl.getPermissionsById().size());
     }
+
     // endregion
 
     // region testGetAccess
diff --git a/parse/src/test/java/com/parse/ParseExperimentsTest.java b/parse/src/test/java/com/parse/ParseExperimentsTest.java
index 0cc6d2c..894d2af 100644
--- a/parse/src/test/java/com/parse/ParseExperimentsTest.java
+++ b/parse/src/test/java/com/parse/ParseExperimentsTest.java
@@ -16,12 +16,13 @@ import static org.junit.Assert.assertTrue;
 import java.util.HashMap;
 import java.util.Map;
 import org.json.JSONArray;
+import org.json.JSONException;
 import org.json.JSONObject;
 import org.junit.Test;
 
 /**
- * Unit tests for {@link ParseExperiments}: determinism, boundaries, fail-closed
- * gates, and definition lookup across JSONArray/List shapes.
+ * Unit tests for {@link ParseExperiments}: determinism, boundaries, fail-closed gates, and
+ * definition lookup across JSONArray/List shapes.
  */
 public class ParseExperimentsTest {
 
@@ -33,7 +34,8 @@ public class ParseExperimentsTest {
         return new ParseConfig(params);
     }
 
-    private static JSONObject experiment(String key, double pct, boolean killed) {
+    private static JSONObject experiment(String key, double pct, boolean killed)
+            throws JSONException {
         JSONObject entry = new JSONObject();
         entry.put("key", key);
         entry.put("traffic_pct", pct);
@@ -76,7 +78,20 @@ public class ParseExperimentsTest {
     }
 
     @Test
-    public void percentZeroAndHundred() {
+    public void extendedParityVectors() {
+        // Cross-runtime drift gate: identical table lives in cloud
+        // abBucket.test.js and dumble ParseExperimentsSdkTest.
+        assertEquals(81, ParseExperiments.bucketFor("vip_for_new_10", "3P0ES1vVWC"));
+        assertEquals(75, ParseExperiments.bucketFor("vip_for_new_10", "probe-004"));
+        assertEquals(8, ParseExperiments.bucketFor("exp-a", "probe-user-002"));
+        assertEquals(20, ParseExperiments.bucketFor("exp-b", "probe-user-001"));
+        assertEquals(20, ParseExperiments.bucketFor("welcome_50", "vftE0NKr0C"));
+        assertEquals(81, ParseExperiments.bucketFor("welcome_50", "NXqFcxQhF2"));
+        assertEquals(88, ParseExperiments.bucketFor("ramp-check", "ramp-user-42"));
+    }
+
+    @Test
+    public void percentZeroAndHundred() throws JSONException {
         JSONArray experiments = new JSONArray();
         experiments.put(experiment("e0", 0.0, false));
         experiments.put(experiment("e100", 100.0, false));
@@ -86,7 +101,7 @@ public class ParseExperimentsTest {
     }
 
     @Test
-    public void failClosedOnKillMissingDisabled() {
+    public void failClosedOnKillMissingDisabled() throws JSONException {
         JSONArray experiments = new JSONArray();
         experiments.put(experiment("killed", 100.0, true));
         experiments.put(experiment("live", 100.0, false));
@@ -112,7 +127,7 @@ public class ParseExperimentsTest {
     }
 
     @Test
-    public void wideningKeepsEarlyUsers() {
+    public void wideningKeepsEarlyUsers() throws JSONException {
         // Sticky-expand proof: every user in at 10% stays in at 50%.
         JSONArray experiments = new JSONArray();
         experiments.put(experiment("ramp", 10.0, false));
diff --git a/parse/src/test/java/com/parse/ParseFileStateTest.java b/parse/src/test/java/com/parse/ParseFileStateTest.java
index 968ac2d..5f0a59f 100644
--- a/parse/src/test/java/com/parse/ParseFileStateTest.java
+++ b/parse/src/test/java/com/parse/ParseFileStateTest.java
@@ -26,7 +26,7 @@ public class ParseFileStateTest {
 
     @Before
     public void setUp() {
-        shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypMapping("txt", "text/plain");
+        shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypeMapping("txt", "text/plain");
     }
 
     @After
diff --git a/parse/src/test/java/com/parse/ParsePushTest.java b/parse/src/test/java/com/parse/ParsePushTest.java
index ca169a9..87182df 100644
--- a/parse/src/test/java/com/parse/ParsePushTest.java
+++ b/parse/src/test/java/com/parse/ParsePushTest.java
@@ -641,6 +641,7 @@ public class ParsePushTest {
         assertTrue(state.channelSet().contains("test"));
         assertTrue(state.channelSet().contains("testAgain"));
     }
+
     // endregion
 
     // region testSendMessageInBackground
diff --git a/parse/src/test/java/com/parse/TestSaveEventuallyInBackground.java b/parse/src/test/java/com/parse/TestSaveEventuallyInBackground.java
index 13c398e..a4bb04d 100644
--- a/parse/src/test/java/com/parse/TestSaveEventuallyInBackground.java
+++ b/parse/src/test/java/com/parse/TestSaveEventuallyInBackground.java
@@ -1,29 +1,24 @@
 package com.parse;
 
 import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertFalse;
-import static org.junit.Assert.assertTrue;
 
+import com.parse.boltsinternal.Task;
 import org.junit.Test;
 
 /**
- * Tests for the saveEventuallyInBackground() method added in Task 717.
- * Verifies that the background variant does not block the calling thread.
+ * Tests for the saveEventuallyInBackground() method added in Task 717. Verifies that the background
+ * variant does not block the calling thread.
  */
 public class TestSaveEventuallyInBackground {
 
-    /**
-     * Verify that the method exists and is accessible.
-     */
+    /** Verify that the method exists and is accessible. */
     @Test
     public void methodExists() throws Exception {
         java.lang.reflect.Method method = ParseObject.class.getMethod("saveEventuallyInBackground");
         assertEquals(Task.class, method.getReturnType());
     }
 
-    /**
-     * Verify that the method signature is correct: no params, returns Task<Void>.
-     */
+    /** Verify that the method signature is correct: no params, returns Task<Void>. */
     @Test
     public void methodSignature() throws Exception {
         java.lang.reflect.Method method = ParseObject.class.getMethod("saveEventuallyInBackground");
@@ -31,9 +26,7 @@ public class TestSaveEventuallyInBackground {
         assertEquals(Task.class, method.getReturnType());
     }
 
-    /**
-     * Verify that saveEventually() also still exists (backward compat).
-     */
+    /** Verify that saveEventually() also still exists (backward compat). */
     @Test
     public void saveEventuallyStillExists() throws Exception {
         java.lang.reflect.Method method = ParseObject.class.getMethod("saveEventually");
diff --git a/twitter/src/main/java/com/parse/twitter/ParseTwitterUtils.java b/twitter/src/main/java/com/parse/twitter/ParseTwitterUtils.java
index 76707cb..2966df2 100644
--- a/twitter/src/main/java/com/parse/twitter/ParseTwitterUtils.java
+++ b/twitter/src/main/java/com/parse/twitter/ParseTwitterUtils.java
@@ -108,7 +108,9 @@ public final class ParseTwitterUtils {
         }
     }
 
-    /** @return {@code true} if the user is linked to a Twitter account. */
+    /**
+     * @return {@code true} if the user is linked to a Twitter account.
+     */
     public static boolean isLinked(ParseUser user) {
         return user.isLinked(AUTH_TYPE);
     }
```
<!-- END_GIT_DIFF -->
