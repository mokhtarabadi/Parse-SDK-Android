---
created_at: '2026-10-10T19:40:06.955346+00:00'
status: active
tags: []
updated_at: '2026-10-10T19:40:06.955363+00:00'
---

# Parse-SDK-Android stack quirks

- Multi-module Android library: core `parse` (Java, 156 files) + `bolts-tasks` (Task API) + Kotlin extensions (`ktx`, `coroutines`, `rxjava`) + auth/push adapters (`google`, `facebook`, `twitter`, `fcm`).
- `parse` depends on `bolts-tasks` via `api project(':bolts-tasks')`; all adapter modules use `implementation project(':parse')`. No reverse deps.
- Java 8 sourceCompat, Kotlin 1.9.22, AGP 9.2.1, compileSdk 36 / minSdk 23 / targetSdk 35, AndroidX required (`android.useAndroidX=true`, Jetifier on).
- Core public API: `Parse.initialize(Configuration)`, `ParseObject.saveInBackground`, `ParseQuery.findInBackground`, `ParseUser.logInInBackground`, `ParseFile`, `ParseInstallation`, `ParseConfig`, `ParseCloud`.
- Formatting enforced by Spotless: `googleJavaFormat(AOSP)` for Java, `ktlint` (no-wildcard-imports) for Kotlin. CI runs `spotlessCheck`.
- Tags: android, parse-sdk, gradle, multi-module
