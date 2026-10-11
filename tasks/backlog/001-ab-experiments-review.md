# Task 001: A/B experiments review — verify, fix, release

**File:** `tasks/backlog/001-ab-experiments-review.md`
**Source:** manager
**Type:** review
**Status:** open

## Goal

Verify the native A/B implementation in this SDK fork is complete and correct, fix anything missing, cut a release, push.

## Review checklist (from graph + code review)

- [ ] `parse/.../ParseExperiments.java`: `bucketFor` (SHA-256 mod 100), `isVariant`, `isVariantForNewUser`, `evaluateAll`, `getExperiment` — verify fail-closed on null/empty, killed flag, percent bounds 0/100, target new/all, start_iso gating
- [ ] Cross-runtime parity: vectors 86/56/60 (+ extended table) identical in `ParseExperimentsTest`, cloud `abBucket.test.js`, dumble `ParseExperimentsSdkTest` — add any missing vector
- [ ] `ktx/ParseExperimentsWrappersTest` + coroutines `ParseConfigCoroutinesExtensionsTest` green
- [ ] Bucket key is ALWAYS the stable `_User` objectId — never installationId (would flip variant on login)
- [ ] Wrapper repair already applied (Gradle 9.4.1, compileSdk 36) — keep green, do not regress
- [ ] JitPack: verify EVERY module publishes (`parse`, `fcm`, `ktx`, `coroutines`, `rxjava`, `google`, `facebook`, `twitter`, `bolts-tasks`) — **known gap: `fcm:4.4.2` 404s on JitPack while `parse:4.4.2` resolves; dumble Android build is blocked on it**
- [ ] Cut release tag, push, confirm JitPack build log green for all modules

## Verification Evidence

- **Test command:** `./gradlew :parse:testDebugUnitTest --tests "com.parse.ParseExperimentsTest"`
- **Expected result:** all green
- **Actual result:** _(fill during execution)_
- **Exit code:** _(fill)_

## Definition of Done

- [ ] Tests green, JitPack serves all modules for the new tag, tag pushed

## Risk & Rollback

- **Risk:** breaking composite/consumer builds
- **Rollback plan:** revert to previous tag

## Execution Log & Reasoning

_(log here)_

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
<!-- END_GIT_DIFF -->
