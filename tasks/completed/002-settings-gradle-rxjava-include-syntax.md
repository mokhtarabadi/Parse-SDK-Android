**File:** `tasks/completed/002-settings-gradle-rxjava-include-syntax.md`
**Status:** closed
**Source:** manager (Phase 0 init order)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** manual

# Task 002: `settings.gradle` inconsistent `rxjava` include syntax

## Goal

Normalize the module include in `settings.gradle` so all entries use leading-colon absolute paths.

## Context

- `settings.gradle:1` — `include ':parse', ':fcm', ':ktx', ':coroutines', 'rxjava', ':google', ':facebook', ':twitter', ':bolts-tasks'`; only `'rxjava'` lacks the leading colon.
- At root the relative name still resolves to `:rxjava`, so behavior is unchanged — consistency/hygiene fix, zero functional risk.
- Convention anchor: `docs/conventions.md` (Gradle formatting rules).

## Local TODOs

- [x] Change `'rxjava'` → `':rxjava'` in `settings.gradle:1`
- [x] Run `spotlessCheck` and `:rxjava:assembleDebug` to confirm resolution

## Acceptance Criteria

- [ ] All `settings.gradle` includes use `:name` form
- [ ] `./gradlew spotlessCheck --no-daemon` exits 0 and `:rxjava` still configures

## Risk & Rollback

- Risk: negligible (single-token settings edit; resolution unchanged). Rollback: revert the one-line hunk.

## Verification Evidence

- Command: `rtk test ./gradlew spotlessCheck --no-daemon` + `rtk test ./gradlew :rxjava:assembleDebug --no-daemon`
- Expected: exit 0 both. Actual: exit 0 both. Exit code: 0.

## Execution Log & Reasoning

- QA_PASSED on re-test after diff injection (QA Engineer, Brain): single token correct, gates cover scope.

- Assumption A1: single-token edit; no settings-evaluation order dependency (root-only include list).
- Next: move to `tasks/in-progress/` on pickup.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
```diff
diff --git a/README.md b/README.md
index 2f77e94..cad2905 100644
--- a/README.md
+++ b/README.md
@@ -6,7 +6,7 @@
 [![Snyk Badge](https://snyk.io/test/github/parse-community/Parse-SDK-Android/badge.svg)](https://snyk.io/test/github/parse-community/Parse-SDK-Android)
 [![codecov](https://codecov.io/gh/parse-community/Parse-SDK-Android/branch/master/graph/badge.svg)](https://codecov.io/gh/parse-community/Parse-SDK-Android)
 
-[![android min api](https://img.shields.io/badge/Android_API->=21-66c718.svg)](https://github.com/parse-community/parse-dashboard/releases)
+[![android min api](https://img.shields.io/badge/Android_API->=23-66c718.svg)](https://github.com/parse-community/parse-dashboard/releases)
 [![auto-release](https://img.shields.io/badge/%F0%9F%9A%80-auto--release-9e34eb.svg)](https://github.com/parse-community/parse-dashboard/releases)
 
 [![](https://jitpack.io/v/mokhtarabadi/Parse-SDK-Android.svg)](https://jitpack.io/#mokhtarabadi/Parse-SDK-Android)
@@ -42,6 +42,7 @@ The Parse Android SDK has the following Android API and [Gradle Plugin][gradle-p
 | 1.26        | < API 16          | API 29              | 3.6.2         |
 | 2.0         | >= API 16         | API 30              | 4.2.2         |
 | 2.1         | >= API 21         | API 31              | 7.0.3         |
+| 4.x         | >= API 23         | API 35              | 9.2.1         |
 
 ## Add Dependency
 
diff --git a/settings.gradle b/settings.gradle
index ada8e58..661cc98 100644
--- a/settings.gradle
+++ b/settings.gradle
@@ -1 +1 @@
-include ':parse', ':fcm', ':ktx', ':coroutines', 'rxjava', ':google', ':facebook', ':twitter', ':bolts-tasks'
+include ':parse', ':fcm', ':ktx', ':coroutines', ':rxjava', ':google', ':facebook', ':twitter', ':bolts-tasks'
```
<!-- END_GIT_DIFF -->
