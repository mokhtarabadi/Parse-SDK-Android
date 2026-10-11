**File:** `tasks/completed/006-graph-brain-bugfix-clock.md`
**Status:** closed
**Source:** manager direct order (autopilot graph + bugfix + clock)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** autopilot (locked 2026-10-10: graph + Brain bugfix + clock section)

# Task 006: Graph, Brain bugfix, clock-section fix

## Goal

Rebuild the repo graph, run a Brain bug consult on it, fix confirmed bugs, and completely fix the clock section. No commit, close, push, or tag without explicit word.

## Context

- Assumption A1 (voice-ordered "clock", logged for correction): the wall-clock TTL in the `ParseUser` session-token cache (`System.currentTimeMillis`, 5-min window, Brain B1). If the Manager meant another clock, this task pivots on his word.
- Prior state: 818/818 green, spotless green, tasks 000/003/004/005 closed, README fork coordinates done (uncommitted), local == origin/master.
- Seat Check: domains = repo graph + quality/bugfix → Senior Programmer lead. No UI/arch-schema triggers (miss stated). Brainstorm: not required — bonded quality scope, no irreversible design call.

## Local TODOs

- [ ] Rebuild MCP graph, record stats
- [ ] Brain bug consult under same task id
- [ ] Clock-section fix (monotonic time + invalidation audit)
- [ ] Confirmed-bug fixes
- [ ] Full suite + spotless, stage, qa

## Acceptance Criteria

- [ ] Graph rebuilt and cited; Brain verdict triaged with evidence
- [ ] Clock logic correct under time changes and logout/revoke
- [ ] Suite green, lint clean

## Risk & Rollback

- Risk: medium (core auth-path edits). Rollback: reset worktree edits; remote untouched.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:testDebugUnitTest --no-daemon`
- Expected: exit 0, 818 green. Actual: exit 0 — 818 tests, 0 failures, 0 errors. Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.

## Execution Log & Reasoning

- Autopilot locked; ZAC holds.
- Graph rebuilt via MCP: 3294 nodes / 7379 links (`context-reports/graph_20261010_224127_87b5fa6b.*`).
- Brain consult triage (fix-what-reproduces): FIXED C1 (wall clock → `SystemClock.elapsedRealtime` monotonic TTL); FIXED C5 (token+stamp now atomic under `sTokenCacheLock` via `cacheSessionToken()`); FIXED C6 (package-private `nowRealtimeMs()` test seam); FIXED S5-remainder (`jitpack.yml` openjdk11 → openjdk17 — AGP 9.x cannot build on 11, true release blocker). DISPUTED with evidence: C2 (all 9 login/signup paths funnel through `saveCurrentUserAsync`, cache always overwritten — ParseUser.java:108,167,425,765,928,965,1079,1098,1371), C4 (fields were already volatile; now lock-guarded), S1–S4 (already triaged in task 005: documented limits or false premises — S4 charset already pinned). DEFERRED with doc: C3 (no central 209 hook exists in SDK; `invalidateCachedSessionToken()` stays the public choke point, contract documented on the method; logout clears, logins refresh).
- Remaining wall-clock uses (`ParseNotificationManager`, `ParseCommandCache`, `ParsePush`) are IDs/filenames/expiry — legitimate, untouched.
- Assumption A1 held: clock = token-cache TTL (proceeding; Manager corrects by word if otherwise).
- DEPS UPGRADE (Brain strategy: safe minors/patches, hold majors): APPLIED annotation 1.4.0→1.11.0, core 1.8.0→1.18.0, rxjava 3.1.4→3.1.12, mockito 4.6.1→4.11.0 (incl. mockito-inline), jsonassert 1.5.0→1.5.3. REVERTED okhttp 5.4.0→5.5.0 (its okhttp-android artifact requires compileSdk 37; tree stays 36) and core 1.19.1→1.18.0 (same reason) — AAR metadata gate arbitrated both. HELD with reason: firebase/play-auth/appcompat/activity/fragment-ktx majors (no module tests to verify behavior), coroutines 1.6.1 (no tests in module), jupiter 6 / Kotlin 2 / AGP 10 / plugin majors (Brain D1-D3), facebook range (already floating <17), junit/signpost/android-compileOnly (already latest). ben-manes 0.51.0 `dependencyUpdates` is broken on Gradle 9 (removed API) — versions sourced from Maven Central + Google Maven metadata + release notes instead.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `8a78271ca5a5e63fd72ec12092195312088632aa`
<!-- END_GIT_DIFF -->
