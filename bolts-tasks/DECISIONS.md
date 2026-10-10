# DECISIONS — `bolts-tasks`

> Living ADR log. Append dated entries; never rewrite history. Root context: `docs/architecture.md`, `docs/conventions.md`.

## 2026-10-10 — Phase 0 init (Cognitive Executor scaffold)
- Decision: adopt living-folder-docs pattern for this module; keep existing public API frozen.
- Reason: Phase 0 repo onboarding; backward compat is a hard constraint (AGENTS.md §2).
- Status: accepted.

## 2026-10-10 — Vendored Bolts fork stays dependency-free
- Decision: `bolts-tasks` keeps zero Android runtime deps (`compileOnly android-4.1.1.4`); no Parse imports allowed.
- Reason: preserves the fork rationale in `bolts-tasks/README.md`; keeps core testable on JVM.
- Status: accepted.
