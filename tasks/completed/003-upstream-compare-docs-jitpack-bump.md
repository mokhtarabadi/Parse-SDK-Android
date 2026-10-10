**File:** `tasks/qa/003-upstream-compare-docs-jitpack-bump.md`
**Status:** qa
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
```diff
diff --git a/gradle.properties b/gradle.properties
index fabf5a9..15b56b3 100644
--- a/gradle.properties
+++ b/gradle.properties
@@ -1,3 +1,3 @@
-version = 4.4.0
+version = 4.4.1
 android.enableJetifier = true
 android.useAndroidX = true
diff --git a/parse/DECISIONS.md b/parse/DECISIONS.md
new file mode 100644
index 0000000..3954deb
--- /dev/null
+++ b/parse/DECISIONS.md
@@ -0,0 +1,28 @@
+# DECISIONS — `parse`
+
+> Living ADR log. Append dated entries; never rewrite history. Root context: `docs/architecture.md`, `docs/conventions.md`.
+
+## 2026-10-10 — Phase 0 init (Cognitive Executor scaffold)
+- Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
+- Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
+- Status: accepted.
+
+## 2026-10-10 — Bolts Task API stays canonical in core
+- Decision: `parse` keeps Bolts `Task<T>` + callback overloads as canonical async; coroutines/Rx remain thin wrappers in their own modules.
+- Reason: backward compat for Java consumers; `bolts-tasks/README.md` vendoring rationale.
+- Status: accepted.
+
+## 2026-08-21 — Fork: toolchain modernization + cancellable HTTP (890ceaf)
+- Decision: modernize build (AGP 9.2.1, Kotlin 1.9.22, SDK 35/23/35, wrapper 9.4.1); make `ParseHttpClient` calls cancellable and wire `ParseQuery.cancel()` through to OkHttp; add `saveEventuallyInBackground()` on a bounded executor.
+- Reason: upstream pins (AGP 7.1.3, SDK 31/21/31) predate store requirements; cancelled queries kept burning network; `saveEventually()` on UI thread with deep graphs caused ANR.
+- Status: accepted (in fork history).
+
+## 2026-08-22 — Fork: session-token cache + proguard retrace (1b42f79)
+- Decision: 5-min in-memory session-token cache in `ParseUser`, invalidated on logout; keep `LineNumberTable,SourceFile` retrace attributes, drop source-file rename.
+- Reason: disk read per query at 1000+ qps; retrace needs line numbers without renamed sources.
+- Status: accepted (in fork history).
+
+## 2026-10-10 — Fork: sticky experiment bucketing (17da089)
+- Decision: deterministic hash bucketing over `ParseConfig` definitions, parity-tested vs cloud/JS and dumble.
+- Reason: per-user variation without per-user Config payloads or assignment tables.
+- Status: accepted (in fork history).
```
<!-- END_GIT_DIFF -->
