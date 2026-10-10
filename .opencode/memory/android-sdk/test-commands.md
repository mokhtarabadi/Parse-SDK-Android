---
created_at: '2026-10-10T19:40:07.179036+00:00'
status: active
tags: []
updated_at: '2026-10-10T19:40:07.179047+00:00'
---

# Parse-SDK-Android test commands

- Full CI: `./gradlew --no-daemon clean jacocoTestReport` (JDK 11, ubuntu-latest).
- Lint gate: `./gradlew spotlessCheck --no-daemon` must pass before tests.
- Unit tests: Robolectric + JUnit4/Jupiter + Mockito + MockWebServer, live under `parse/src/test/java/com/parse/` (~88 files). Module tests: `facebook`, `twitter` have own tests.
- Coverage uploaded to Codecov (fail_ci_if_error=false).
- Tags: testing, ci, gradle
