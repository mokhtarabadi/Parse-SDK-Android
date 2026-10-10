# AGENTS.md — Parse-SDK-Android (Cognitive Executor)

> Stack: Android multi-module library (Java 8 + Kotlin 1.9.22, AGP 9.2.1, compileSdk 36 / minSdk 23 / targetSdk 35, AndroidX + Jetifier). Core module `parse` (Java, ~156 files) on `bolts-tasks` Task API; adapters `google`, `facebook`, `twitter`, `fcm`; Kotlin surfaces `ktx`, `coroutines`, `rxjava`.

## 1. Entry & Spec Binding

- Entry point: read this file first. Bound specs: `docs/architecture.md`, `docs/data_model.md`, `docs/conventions.md`, `opencode.json`, `.opencode/memory/index.md`.
- Absent-File Policy: if a bound file is missing, skip gracefully — never halt, never hallucinate its contents.
- Cite-before-act: rejections, gate verdicts, and architectural claims cite file + section.

## 2. Module Boundaries (never violate silently)

- `parse` ← `bolts-tasks` (`api project(':bolts-tasks')`). No reverse deps.
- Every adapter (`google`, `facebook`, `twitter`, `fcm`, `ktx`, `coroutines`, `rxjava`) → `implementation project(':parse')` only. Adapters never depend on each other.
- Public API surface: `Parse.initialize(Configuration)`, `ParseObject`, `ParseQuery`, `ParseUser`, `ParseFile`, `ParseInstallation`, `ParseConfig`, `ParseCloud`. Keep backward compat; no renames without a dated ADR in the module `DECISIONS.md`.

## 3. Formatting & Quality Gates (CI-blocking)

- Spotless is law: `googleJavaFormat(AOSP)` for Java, `ktlint` (`no-wildcard-imports` disabled rule) for Kotlin. Run `./gradlew spotlessCheck --no-daemon` before claiming done; fix with `./gradlew spotlessApply --no-daemon`.
- Tests: `./gradlew --no-daemon clean jacocoTestReport` is the CI entry, but root `jacocoTestReport` does not fan out to modules — the real unit gate is `./gradlew :parse:testDebugUnitTest --no-daemon` (JDK 17; Robolectric 4.14.1). Verification runs start with the `rtk test` prefix. Do not claim completion without a successful exit code recorded in the task file.
- `lint { abortOnError false }` — lint never blocks, Spotless and tests do.

## 4. Kanban Lifecycle (V6, WIP ≤ 3)

- Lanes: `tasks/backlog/` → `tasks/in-progress/` (max 3) → `tasks/qa/` (max 3) → `tasks/completed/`; `tasks/archive/` for milestone compaction.
- Implementation tasks: verify file is in `tasks/in-progress/` before writing code (`git mv` from backlog first).
- After staging: move to `tasks/qa/`, update `**File:**` header, re-lint + re-stage. Closure only on explicit "Approved for closure" / "Close task" via `custom_context_commit_and_clean_task`.
- Zero-Autonomous-Commit: never run `git add/commit/checkout/push`. Stage via `custom_context_stage_and_inject_diff`, unstage via `custom_context_unstage_files`.

## 5. Living Folder Docs

- Each Gradle module keeps `README.md` + `DECISIONS.md`. Read siblings before editing; append dated ADRs when domain contracts or layer boundaries change. Never silently alter slice architecture.

## 6. Memory & Context

- Read `.opencode/memory/index.md` at task start; `search_memory`/`read_memory` for stack quirks and test commands. `store_memory` only for new explicit project rules or reusable quirks — never task progress or code snippets.
- Heavy discovery goes to the `cognitive-discovery` subagent (code-search skill); executor keeps context for implementation. Fallback to direct tools when subagents are unavailable.
- Keep the task file holding: active id + lane, `[fed-context]` citations, persona/seat, mode lock, staged diff hash, blockers, next action.

## 7. Skill Loading

- Auto-load by stack: Android/Kotlin → `android-kotlin`. New task files → `task-generator`; closure review → `archive-tasks` + `task-lint`; complex bug/deadlock/silent failure → `debug-instrumentation`; memory read/write → `project-memory`.
- An XML task's `<context_phase>` names win over this matrix. Never guess a seat or skill on low confidence — stop and ask.

## 8. Halt & Circuit Breakers

- Halt (ask, don't guess): ambiguous/fragmented direct orders; destructive or irreversible next action with no rollback; missing credential only the Manager holds; self-contradicting task file.
- Circuit breakers (stop + surface): same tool 5+ times with near-identical args; actions drifting from the task goal; disk state diverging from assumptions; 50+ steps without measurable progress.
- Buffer isolation: heavy discovery goes to subagents via direct tools when available; never paste large dumps inline — cite file + line.
