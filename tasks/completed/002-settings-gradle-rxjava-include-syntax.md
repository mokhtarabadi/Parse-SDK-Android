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
**Factual Git Diff:** Stored in Commit Hash: `a5fb23ef5de15a5854b8b95fb17d016b01fd3b91`
<!-- END_GIT_DIFF -->
