**File:** `tasks/completed/001-readme-compat-table-drift.md`
**Status:** closed
**Source:** manager (Phase 0 init order)
**Type:** docs-fix
**Seat:** Senior Programmer
**Mode:** manual

# Task 001: README compatibility table drift

## Goal

Align root `README.md` compatibility table with actual build config (`build.gradle`, `gradle.properties`).

## Context

- `README.md:38-44` — table lists SDK 2.1 / min API 21 / Gradle Plugin 7.0.3 as latest; repo version is 4.4.0 (`gradle.properties:1`).
- `build.gradle:63-67` — `compileSdkVersion = 36`, `minSdkVersion = 23`, `targetSdkVersion = 35`; AGP `9.2.1` (`build.gradle:9`).
- `README.md:9` badge still claims `Android_API->=21`.

## Local TODOs

- [x] Add 4.x row to compatibility table (min API 23, target 35, AGP 9.2.1)
- [x] Fix min-API badge (>=21 → >=23)
- [x] Run `spotlessCheck` (misc block covers `**/*.md`)

## Acceptance Criteria

- [ ] Table documents the 4.x line matching `build.gradle:63-67` and `gradle.properties:1`
- [ ] `./gradlew spotlessCheck --no-daemon` exits 0

## Risk & Rollback

- Risk: negligible (docs-only). Rollback: revert the `README.md` hunk.

## Verification Evidence

- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.

## Execution Log & Reasoning

- QA_PASSED (QA Engineer, Brain): docs-only, no vulns, spotless covers scope.

- Assumption A1: table is hand-maintained (no generator found in repo) — edit in place.
- Next: move to `tasks/in-progress/` on pickup.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `e8c2377b2fab4334e7bd05c1750ff6770eef92b1`
<!-- END_GIT_DIFF -->
