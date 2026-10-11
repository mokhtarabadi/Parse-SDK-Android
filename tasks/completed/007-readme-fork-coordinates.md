**File:** `tasks/completed/007-readme-fork-coordinates.md`
**Status:** closed
**Source:** manager direct order (create and close)
**Type:** docs
**Seat:** Senior Programmer
**Mode:** manual

# Task 007: README fork coordinates

## Goal

Point README dependency coordinates, CI badge, and releases link at the fork (mokhtarabadi) with 4.4.1. Upstream service/history links intentionally kept.

## Context

- Prior: badges retargeted (task 004), group id + CI badge + releases link retargeted this round.
- Verification: `spotlessCheck` green after edit.

## Local TODOs

- [x] 8 dependency coords → fork group id
- [x] CI badge + releases link → fork
- [x] spotlessCheck green

## Acceptance Criteria

- [x] 12 fork refs, remaining upstream refs only services/history
- [x] Task lint passes

## Risk & Rollback

- Risk: negligible (docs-only). Rollback: revert README hunk.

## Verification Evidence

- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.

## Execution Log & Reasoning

- Created and closed on explicit Manager order in one turn.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `f2c23bcf5a741f501d9e4fdf4b90694875a215e2`
<!-- END_GIT_DIFF -->
