# DECISIONS — `rxjava`

> Living ADR log. Append dated entries; never rewrite history. Root context: `docs/architecture.md`, `docs/conventions.md`.

## 2026-10-10 — Phase 0 init (Cognitive Executor scaffold)
- Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
- Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
- Status: accepted.

## 2026-10-11 — `toSingle` nullability bound (task 012)
- Decision: keep `fun <T : Any> Task<T>.toSingle()` (rxjava 3.1.12 `Single.just` rejects null at compile time instead of NPE at runtime).
- Reason: the old unbounded `<T>` only moved the same null crash to runtime; the bound documents the pre-existing constraint. No behavior change for non-null results.
- Status: accepted.
