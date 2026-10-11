---
created_at: '2026-10-11T05:39:34.259882+00:00'
status: active
tags: []
updated_at: '2026-10-11T05:39:34.259894+00:00'
---

# Parse-SDK-Android test commands

- CI entry: `./gradlew --no-daemon clean jacocoTestReport` (JDK 17, ubuntu-latest) — note root `jacocoTestReport` does not fan out; real unit gate is `:parse:testDebugUnitTest`.
- Lint gate: `./gradlew spotlessCheck --no-daemon` must pass before tests.
- Unit tests: Robolectric 4.14.1 + JUnit4/Jupiter + Mockito 4.11 + MockWebServer, live under `parse/src/test/java/com/parse/` (818 tests). Module tests: `facebook`, `twitter` have own tests.
- Coverage uploaded to Codecov (fail_ci_if_error=false).
- Tags: testing, ci, gradle
