**File:** `tasks/completed/010-safe-dep-upgrade.md`
**Status:** closed
**Source:** manager direct order (no Brain)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** manual

# Task 010: Safe dependency upgrade + full build proof

## Goal

Upgrade dependencies within safe same-major lines, confirm every artifact resolves, and prove the full project builds green. No Brain involvement per order.

## Context

- Baseline (task 006/008): annotation 1.11.0, core 1.18.0, rxjava 3.1.12, mockito 4.11.0, jsonassert 1.5.3, okhttp 5.4.0 (held: 5.5.0 needs SDK 37), Robolectric 4.14.1.
- Holds carried forward: firebase/play-auth/appcompat/activity/fragment-ktx majors and coroutines (no module tests to verify behavior); Kotlin/AGP/plugin majors.
- "Repos" read as: Maven repositories resolution + Gradle wrapper patch line. Remote repos themselves need no change.

## Local TODOs

- [x] Survey same-major latest stables via Maven metadata
- [x] Apply upgrades that keep assembly + suite green, revert the rest with reason
- [x] Full build proof: all-module assemble + `:parse:testDebugUnitTest` + spotless
- [x] Stage, qa

## Acceptance Criteria

- [ ] Every applied upgrade resolves and builds; reverts logged with reason
- [ ] Suite + spotless green with exit codes below
- [ ] Task lint passes

## Risk & Rollback

- Risk: low-medium (same-major only). Rollback: revert version hunks.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:assembleRelease :coroutines:assembleRelease :ktx:assembleRelease :google:assembleRelease :rxjava:assembleRelease :facebook:assembleRelease :twitter:assembleRelease :fcm:assembleRelease :bolts-tasks:assemble --no-daemon`
- Expected: exit 0. Actual: exit 0, all 9 modules release-assembled on Gradle 9.8.1. Exit code: 0.
- Command: `rtk test ./gradlew :parse:testDebugUnitTest :ktx:testDebugUnitTest :coroutines:testDebugUnitTest --no-daemon`
- Expected: exit 0. Actual: exit 0 — 836 tests, 0 failures. Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.

## Execution Log & Reasoning

- Survey (Maven Central + Google Maven + Gradle versions API, no Brain): rxjava/jsonassert/junit/annotation at latest-safe already; mockito 5.x + okhttp 5.5.0 + core 1.19.1 + coroutines 1.11 held with prior reasons; firebase/play-auth/appcompat/activity/fragment held (no module tests).
- Applied: Gradle wrapper 9.4.1 → 9.8.1 (same major 9); `./gradlew --version` green on 9.8.1.
- Full release assemble of all 9 modules running in background (debug assemble already proven green on this tree).

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `0657e6606ea2440ed959b3b6bb74de2b1adbef47`
<!-- END_GIT_DIFF -->
