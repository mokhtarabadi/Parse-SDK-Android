**File:** `tasks/completed/003-upstream-compare-docs-jitpack-bump.md`
**Status:** closed
**Source:** manager direct order (upstream + JitPack)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** manual

# Task 003: Upstream compare, docs upgrade, JitPack patch bump

## Goal

Add `upstream` (parse-community/Parse-SDK-Android) remote and fetch; diff fork vs upstream to list fork-only work; upgrade project docs from findings; bump patch version for JitPack publish (4.4.0 → 4.4.1). No commit or tag without explicit approval.

## Context

- Fork: `mokhtarabadi/Parse-SDK-Android`, local `master` at `17da089` tracking `origin/master`.
- Upstream: `parse-community/Parse-SDK-Android`, branch `master`, HEAD `9e6e18d` (verified via `gh repo view` + `ls-remote`).
- Fork history shows 3+ local commits ahead (`17da089` sticky bucketing, `1b42f79` hardening, `890ceaf` bug fixes).
- JitPack coordinates: `com.github.parse-community.Parse-SDK-Android:<module>:<version>` (`README.md:82-102`); version lives in `gradle.properties:1`.
- Phase 0 note: it ran as direct Manager orders with no task file, so there is no Phase 0 file to close.

## Local TODOs

- [ ] `git remote add upstream https://github.com/parse-community/Parse-SDK-Android.git` + fetch
- [ ] Diff `upstream/master...origin/master` (commits + files) and record fork-only work
- [ ] Upgrade `docs/architecture.md`, `docs/data_model.md`, module `DECISIONS.md` from findings
- [ ] Bump `gradle.properties` version to 4.4.1 and verify JitPack pointer docs

## Acceptance Criteria

- [ ] `upstream` remote fetches cleanly; fork-only commit list recorded in Execution Log
- [ ] Docs changes staged for review, no silent architecture edits
- [ ] Version bump verified (gradle parses, no commit/tag pushed without approval)

## Risk & Rollback

- Risk: low (remote add + fetch are read-only; edits are text-only). Rollback: `git remote remove upstream`; revert worktree edits.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:assembleDebug --no-daemon`
- Expected: exit 0. Actual: exit 0 (only deprecation notes in bolts-tasks/parse). Exit code: 0.

## Execution Log & Reasoning

- Upstream added: `upstream https://github.com/parse-community/Parse-SDK-Android.git`, fetched (`master` → `9e6e18d`). Fork `origin/master` = `17da089`, 3 commits ahead.
- Fork-only commits: `890ceaf` toolchain + cancellable HTTP + `saveEventuallyInBackground`; `1b42f79` token cache + proguard; `17da089` `ParseExperiments` + parity tests. Full detail in `docs/architecture.md` §Fork Divergence.
- Assumption A1: version bump 4.4.0→4.4.1 in `gradle.properties:1` only; JitPack builds the `4.4.1` tag on demand — tag/push left for explicit approval.
- Docs touched: `docs/architecture.md`, `docs/data_model.md`, `parse/DECISIONS.md` (append-only ADRs, no history rewritten).

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `5b5b05a0563f2087363d46da0e0d14abfc0f64bd`
<!-- END_GIT_DIFF -->
