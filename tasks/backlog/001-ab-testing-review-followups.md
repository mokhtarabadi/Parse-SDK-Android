# Task 001: A/B testing review follow-ups (JitPack publish + parity)

**File:** `tasks/backlog/001-ab-testing-review-followups.md`
**Source:** manager
**Type:** improvement
**Status:** open

## Goal

Close the gaps found in the A/B review of this fork: every module published on JitPack for the release tag, and dependency upgrades completed.

## Findings (verified via code graph, 3327 nodes)

- `parse/src/main/java/com/parse/ParseExperiments.java` (`bucketFor`, `evaluateAll`) + `ktx` wrappers + `coroutines` `evaluateAllExperiments` + tests in all three surfaces: IMPLEMENTED.
- Open: `fcm:4.4.2` does NOT resolve on JitPack (404) while `parse:4.4.2` does — downstream Android build is red until every consumed module publishes.

## Local TODOs

- [ ] Verify JitPack publishes ALL modules (`parse`, `fcm`, `ktx`, `coroutines`, `google`, `facebook`, `twitter`, `rxjava`, `bolts-tasks`) for the release tag; fix whatever blocks `fcm`
- [ ] Finish dependency upgrades in progress (rxjava bounds etc.) without breaking the Java 8 surface
- [ ] Cut and push the release tag once green; confirm `parse` + `fcm` POMs return 200

## Acceptance Criteria

- [ ] `parse` and `fcm` POMs for the release tag return 200 on JitPack
- [ ] Downstream app resolves and compiles against the tag

## Verification Evidence

- **Test command:** curl POM URLs + downstream Gradle resolve
- **Actual result:** _(fill during execution)_
- **Exit code:** _(fill during execution)_

## Risk & Rollback

- **Risk:** JitPack build skew across modules
- **Rollback plan:** revert tag; downstream pins previous good tag

## Execution Log & Reasoning

_(fill during execution)_

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->

_(injected by tooling — do not hand-edit)_

<!-- END_GIT_DIFF -->
