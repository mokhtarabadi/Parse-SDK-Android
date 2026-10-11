# DECISIONS — `parse`

> Living ADR log. Append dated entries; never rewrite history. Root context: `docs/architecture.md`, `docs/conventions.md`.

## 2026-10-10 — Phase 0 init (Cognitive Executor scaffold)
- Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
- Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
- Status: accepted.

## 2026-10-10 — Bolts Task API stays canonical in core
- Decision: `parse` keeps Bolts `Task<T>` + callback overloads as canonical async; coroutines/Rx remain thin wrappers in their own modules.
- Reason: backward compat for Java consumers; `bolts-tasks/README.md` vendoring rationale.
- Status: accepted.

## 2026-08-21 — Fork: toolchain modernization + cancellable HTTP (890ceaf)
- Decision: modernize build (AGP 9.2.1, Kotlin 1.9.22, SDK 35/23/35, wrapper 9.4.1); make `ParseHttpClient` calls cancellable and wire `ParseQuery.cancel()` through to OkHttp; add `saveEventuallyInBackground()` on a bounded executor.
- Reason: upstream pins (AGP 7.1.3, SDK 31/21/31) predate store requirements; cancelled queries kept burning network; `saveEventually()` on UI thread with deep graphs caused ANR.
- Status: accepted (in fork history).

## 2026-08-22 — Fork: session-token cache + proguard retrace (1b42f79)
- Decision: 5-min in-memory session-token cache in `ParseUser`, invalidated on logout; keep `LineNumberTable,SourceFile` retrace attributes, drop source-file rename.
- Reason: disk read per query at 1000+ qps; retrace needs line numbers without renamed sources.
- Status: accepted (in fork history).

## 2026-10-10 — Fork: sticky experiment bucketing (17da089)
- Decision: deterministic hash bucketing over `ParseConfig` definitions, parity-tested vs cloud/JS and dumble.
- Reason: per-user variation without per-user Config payloads or assignment tables.
- Status: accepted (in fork history).

## 2026-10-10 — Task 005: token-cache TTL accepted as trade-off (Brain B1 deferred)
- Decision: keep 5-min in-memory session-token TTL; no shortening without product call. Invalidate paths (`saveCurrentUserAsync` refresh, logout clear, `invalidateCachedSessionToken()`) stay as-is.
- Reason: server-side revoke inside the window reuses the old token by design of any TTL cache; disk remains source of truth on restart.
- Status: accepted (deferred fix, logged).

## 2026-10-11 — Experiments parity surface (task 009, Phases 2-3)
- Decision: opt-in `abEvaluate` fetch with transient verdicts; `evaluateAll` ordered bulk (`-1` markers); `isVariantForNewUser` gate; ktx/coroutines wrappers; Task originals retained.
- Reason: server parity without touching default fetch bytes or the verified hash; in-body params forced by the method-override transport; `-1` forced by control semantics.
- Status: accepted.
