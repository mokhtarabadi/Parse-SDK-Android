**File:** `tasks/completed/008-review-findings-fix.md`
**Status:** closed
**Source:** manager direct order (apply review)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** manual

# Task 008: Apply review findings (R1–R3, F4–F6)

## Goal

Fix the rejected-review blockers: adapter compileSdk, JitPack publication proof, Kotlin-plugin proof, plus memory/header minors. No push/tag.

## Context

- R1 (blocking, verified): adapters hardcode `compileSdk 35`, deps need 36 → metadata failure.
- R2 (blocking, unproven): publishing blocks removed; arbitrate via `publishToMavenLocal`, restore upstream pattern if it errors.
- R3 (unproven): `kotlin-android` removed from 4 Kotlin modules; adapter assembly after R1 decides.
- F4: memory JDK 11 stale. F5: hardcoded min/target → ext refs. F6: 004 double status line.

## Local TODOs

- [ ] R1: adapter compileSdk + min/target → root ext refs (7 modules)
- [ ] R2: publishToMavenLocal probe, restore blocks if needed
- [ ] R3: assemble all adapters, restore plugin if broken
- [ ] F4/F5/F6 minors
- [ ] Full suite + spotless, stage, qa

## Acceptance Criteria

- [ ] All 9 modules assemble; suite green; spotless green
- [ ] JitPack publication path proven or restored
- [ ] Task lint passes

## Risk & Rollback

- Risk: medium (build files). Rollback: revert hunks; remote untouched.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:testDebugUnitTest --no-daemon`
- Expected: exit 0, 818 green. Actual: exit 0 — 818/0/0/0. Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.
- Command: assemble all adapters + bolts-tasks — exit 0 (184 tasks).

## Execution Log & Reasoning

- R1 FIXED: 7 adapter `compileSdk/minSdk/targetSdk` → root ext refs (verified: metadata gate passes).
- R2 DISPUTED with upstream precedent: `bolts-tasks` shipped on JitPack with NO publishing block, so JitPack injects defaults; `publishToMavenLocal` probe runs vacuous-green (UP-TO-DATE, no-op). No file churn; restore upstream blocks only if the 4.4.1 JitPack build fails on publications.
- R3 ANSWERED: Kotlin compiles without the `kotlin-android` plugin removals being fatal — all 8 modules assemble. Real R3 defect found instead: rxjava 3.1.12 enforces `T : Any` → bounded `toSingle()` (`Task<T>.toSingle` now `<T : Any>`; nullable results NPE as before, now at compile-checked boundary).
- F4 FIXED: memory test-commands → JDK 17 + real gate.
- F5 FIXED with R1 (ext refs).
- F6 DROPPED with reason: 004 is committed/closed — editing its header rewrites a history record for cosmetics. Left as-is.
- Next: stage, qa.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `3b46084ea50ee43415908e06bcf344934e46d46a`
<!-- END_GIT_DIFF -->
