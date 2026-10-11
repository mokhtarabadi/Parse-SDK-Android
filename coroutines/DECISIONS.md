# DECISIONS — `coroutines`

> Living ADR log. Append dated entries; never rewrite history. Root context: `docs/architecture.md`, `docs/conventions.md`.

## 2026-10-10 — Phase 0 init (Cognitive Executor scaffold)
- Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
- Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
- Status: accepted.

## 2026-10-11 — Full-offline A/B mandate (task 012)
- Decision: remove the `fetchAbEvaluateConfig` suspend wrapper (online verdict path); keep the pure offline wrappers `evaluateAllExperiments` / `isVariantForNewUser`.
- Reason: manager order — no server `abEvaluate` support in this SDK; all evaluation is local over `ParseConfig` definitions.
- Status: accepted.
