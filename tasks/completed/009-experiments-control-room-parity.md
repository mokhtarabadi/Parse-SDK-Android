**File:** `tasks/completed/009-experiments-control-room-parity.md`
**Status:** closed
**Source:** manager (server A/B completion follow-up)
**Type:** feature
**Seat:** Senior Programmer
**Mode:** autopilot (locked 2026-10-11: implement 009)

# Task 009: Complete SDK experiments surface to server parity

## Goal

Bring the Android SDK's A/B surface to full parity with the completed parse-server A/B contract, keeping local-first evaluation and adding opt-in server verdicts plus idiomatic wrappers.

## Context

- Server truth (parse-server task 02, closed): shared fail-open resolver; REST `GET /config?abEvaluate=true` verdicts; GraphQL `clientConfig`; PUT strips echoed `abEvaluated`; legacy REST `[]` vs GraphQL `null`; vectors `vip_for_new_10`/`vftE0NKr0C` → 86.
- SDK today: `parse/src/main/java/com/parse/ParseExperiments.java:35` — pure-static `bucketFor`/`getExperiment`/`isVariant`, zero network, covered by `parse/src/test/java/com/parse/ParseExperimentsTest.java` (parity vectors incl. 81/75/8/20/88). Documented in `docs/data_model.md:32` and `docs/architecture.md:47` (fork commit `17da089`).
- Gaps found by graph + read: (G1) `ParseRESTConfigCommand.java:27` GET sends no params — `abEvaluate` server verdicts unreachable; (G2) no bulk `evaluateAll` equivalent (server/JS have it); (G3) new-vs-old targeting left to callers with no helper (server gates on `user_joined` vs `start_iso`); (G4) no `ktx`/`coroutines` wrappers (no Config/Experiment symbols in `ktx/src`); (G5) `@NonNull`/`@Nullable` audit missing on the experiments API (conventions §Language & API).
- Out of scope: Push experiments, `Parse-Dashboard` server, changing default fetch behavior (stays local-first, offline-safe).

## Local TODOs

- [x] G1: opt-in `abEvaluate` fetch overload on `ParseConfig.getInBackground` (default path unchanged; verdicts exposed read-only, never cached as params)
- [x] G2: `ParseExperiments.evaluateAll` bulk API mirroring server semantics (input order, skip malformed, fail-closed entries)
- [x] G3: `isVariantForNewUser`-style targeting helper taking joined-date + `start_iso` (mirrors server gate; caller keeps user-joined semantics)
- [x] G4: `ktx` (+ `coroutines` if matching existing wrapper style) suspend wrappers; never remove Task/callback originals
- [x] G5: androidx nullability annotations on new/changed public APIs
- [x] Tests mirroring main (`*Test` naming, `ParseTestUtils` helpers): vectors ✅, bulk order ✅, fetch overload ✅ (mocked-client captor, no live calls); targeting gate ✅ Phase 3
- [x] Gates: `./gradlew :parse:testDebugUnitTest --no-daemon` (JDK 17) + `spotlessCheck`; record `rtk test`-prefixed evidence
- [x] Docs: module `README.md` + dated ADR in module `DECISIONS.md` (public API additions); never rename without ADR

## Acceptance Criteria

- [x] Local-first default fetch behavior byte-identical (offline-safe, disk cache untouched)
- [x] Opt-in fetch returns server verdicts; echoed verdicts never persist (server strips on PUT)
- [x] New APIs covered by unit tests incl. shared cross-runtime vectors
- [x] Spotless + unit gate green with exit codes recorded below; no adapter depends on another (AGENTS.md §2)

## Risk & Rollback

- Risk: medium-low (public API additions only; no behavior change unless opted in). Rollback: revert new overloads/wrappers; core evaluator untouched.
- Module boundaries: `parse` ← `bolts-tasks` only; adapters → `parse` only.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:testDebugUnitTest --tests "com.parse.ParseExperimentsTest" --tests "com.parse.ParseConfigAbEvaluateTest" --no-daemon`
- Expected: exit 0. Actual: exit 0 (repair 1: server-URL setup added to new test). Exit code: 0.
- Command: `rtk test ./gradlew :parse:testDebugUnitTest :ktx:testDebugUnitTest :coroutines:testDebugUnitTest --no-daemon` (hotfix final)
- Expected: exit 0, 836 green. Actual: exit 0 — 830 + 4 + 2, 0 failures. Exit code: 0.
- Command: `rtk test ./gradlew :ktx:assembleDebug :coroutines:assembleDebug --no-daemon` (P4)
- Expected: exit 0. Actual: exit 0. Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon` (P4 final)
- Expected: exit 0. Actual: exit 0 (after one spotlessApply for P4 edits). Exit code: 0.

## Execution Log & Reasoning

- REVIEW: QA_PASSED then Code Reviewer technical approval → PO_REVIEW_PENDING (relayed to Manager; file stays in qa awaiting exact close word).

- HOTFIX (QA_REJECTED triage, task back in-progress): FIXED V1 (defensive copy in `getAbEvaluated`); FIXED V2 (null keys skipped, javadoc + tests updated); FIXED V3 (null-guard resume with `IllegalStateException` fallback); FIXED V4 (pure wrappers desuspended to plain top-level funs); FIXED M1 (junit test source sets in ktx/coroutines: 4 + 2 tests — receiver style dropped to top-level funs because `ParseConfig` ctors are package-private, keeping receivers would leave untestable API); FIXED M2 (null-input/empty/unmodifiable/offline-error tests); FIXED M3 by construction (null keys skipped, KTX strict types forbid null elements). `fetchAbEvaluateConfig` network path stays indirectly covered (parse controller tests) — it needs SDK init no unit module can provide; logged.
- P4 (docs + sweep) from Brain XML; one spotlessApply for P4 edits, otherwise zero repairs.
- Step 2 sweep: REST factory annotated (`@NonNull` return, `@Nullable` session); `ParseConfig`/`ParseExperiments` new APIs already annotated in P2–P3; Kotlin wrappers carry strict nullability in types (no androidx annotations needed — type system covers).
- Step 3–4: `parse/README.md` parity section + `parse/DECISIONS.md` 2026-10-11 ADR (append-only).
- CHANGELOG check: `### Added` already holds all three lines (fetch, bulk, targeting/wrappers); no `### Changed` churn, no duplicate header.

- P3 (targeting + wrappers) from Brain XML; repairs used: 2 (test server-URL setup carried from P2 run, ParseException throws decl, coroutines resume imports — 3 total this phase).
- Step 2 repair applied: `evaluateAll` now guards via `getExperiment` (missing/malformed → -1); javadoc corrected.
- Step 3 `isVariantForNewUser(config, key, userId, joinedAt)` added with `start_iso` gate via `ParseDateFormat`; Step 4 `ktx/ParseExperiments.kt` + `coroutines/ParseConfigCoroutinesExtensions.kt` added (Task originals retained, adapters still depend only on parse).
- P2 implemented from Brain XML with two recorded deviations, both required by observed base-class behavior: (D1) params travel in the method-override POST body, never the query string (`ParseRESTCommand.java:115-127`), so the fetch carries `abEvaluate=true` in-body and tests assert the body, not `?userId=`; invented `userId`/`keys` wire params dropped — the server authenticates via session token. (D2) bulk returns per-key buckets with `-1` fail-closed markers, not `0` — `0` is a valid in-variant bucket, `-1` is the codebase's documented control marker. Signatures: factory + controller + `getInBackgroundAbEvaluate` Task/callback + `evaluateAll(config, keys, userId)` + transient `getAbEvaluated`; nullability annotated on all new public APIs; default fetch path byte-identical (null params, controller persist path untouched).
- Memory hits: stack-quirks (module graph, Spotless rules), test-commands (real gate `:parse:testDebugUnitTest`, JDK 17).
- Sibling docs deferred to Phase 4 (no component README/DECISIONS edit this run); CHANGELOG Unreleased `### Added` appended (no duplicate header; Toolchain bullets left in place — moving them is churn with zero release-note value, logged here).
- 2026-10-11: authored from parse/src graph (2626 nodes) + full read of `ParseExperiments.java:1-151`, `ParseRESTConfigCommand.java:27`, `ParseConfig.java:50-86`; ktx has no Config/Experiment symbols.
- Assumption A1: server-verdict fetch stays opt-in (offline-first SDK must not hard-depend on `abEvaluate`).
- Autopilot lock recorded (Mode header); vector 86 reproduced locally, evaluator untouched.

## P1 Map (contract lock, read-only)

- Graph: `context-reports/graph_20261011_081013_ddcf7f5c.*` — 3326 nodes / 7474 links. Tree: `context-reports/tree_report_20261011_081012_5819af9e.md`. DESIGN.md absent (skipped per policy). Signatures: `context-reports/signatures_report_20261011_081027_ea48e712.md` (ParseConfig), `..._8b58fba5.md` (ParseExperiments). Discovery subagents unavailable on this tier — Steps 1–3 ran on direct tools, logged here.
- G1: `ParseRESTConfigCommand.java:25-27` — `fetchConfigCommand(sessionToken)` builds GET on `"config"` with null params; no query-param plumbing exists, so `abEvaluate` verdicts are unreachable. `ParseConfig.java:5-6` — `getInBackground(ConfigCallback)` + `getInBackground()` are the only fetch entries.
- G2: no bulk API — `ParseExperiments.java:2-4` exposes only `bucketFor`, `getExperiment`, `isVariant`; no `evaluateAll`.
- G3: no joined-date helper — evaluator judges enabled + killed + percent + bucket only; caller owns new-vs-old semantics.
- G4: zero Config/Experiment symbols in `ktx/src` (grep clean); coroutines style = `suspend fun` + `suspendCoroutine` wrappers (`ParseQueryCoroutinesExtensions.kt:11`, `ParseTaskExtensions.kt:15`), no Config wrappers anywhere.
- G5: `ParseExperiments.java:2-4` public methods carry no `@NonNull`/`@Nullable` (conventions language rule applies to new/changed APIs).
- Locked decisions: default fetch stays byte-identical; verdicts read-only, never cached as params; core hash untouched (vector 86 verified); adapters depend only on parse.
- Test targets for later phases: existing `ParseExperimentsTest` vectors (81/75/8/20/88) + MockWebServer pattern (`ParseHttpClientTest` style) for the fetch overload.
- Deferred with reason: docstrings/verbose comments (no new public code this run); syntax check via tooling (nothing to check); CHANGELOG entry (no behavior change); sibling README/DECISIONS edits (no component touched — ADR lands in Phase 4).

## P2–P5 Entry Criteria

- REST plumbing: how `ParseRESTConfigCommand` accepts query params (builder shape at lines 25–27).
- Bulk semantics: preserve input order, skip malformed entries, fail-closed per entry.
- Targeting semantics: joined-date vs `start_iso` comparison rule (quote server contract, do not invent).
- Wrapper signatures: `suspend fun ParseConfig` fetch + experiment evaluate mirrors; Task/callback originals retained.
- Annotation list: all new/changed public methods across parse + ktx + coroutines.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
**Factual Git Diff:** Stored in Commit Hash: `2dec0b5fa47523ada62b5ec921e8523750b1c76a`
<!-- END_GIT_DIFF -->
