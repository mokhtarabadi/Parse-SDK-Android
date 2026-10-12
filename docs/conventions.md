# Conventions — Parse-SDK-Android

## Formatting (CI-enforced via Spotless, JDK 17)

- Java: `googleJavaFormat(AOSP)`, 4-space indent, `removeUnusedImports`, trim trailing whitespace, end with newline. Target `**/*.java`.
- Kotlin: `ktlint` with `no-wildcard-imports` disabled; same whitespace rules. Target `**/*.kt`.
- Gradle/Markdown/gitignore: `misc` format block; XML: `xml` format block.
- Commands: check `./gradlew spotlessCheck --no-daemon`; fix `./gradlew spotlessApply --no-daemon`.
- Test runtime: Robolectric 4.14.1 (supports current targetSdk); Mockito 4.11.0, MockWebServer following `$okhttpVersion`. Real unit gate: `:parse:testDebugUnitTest`.

## Language & API

- Core `parse` module is Java 8 (`sourceCompatibility = targetCompatibility = 1.8`); Kotlin only in adapter/surface modules (`google`, `ktx`, `coroutines`, `rxjava`).
- Public API keeps Bolts `Task<T>` + callback overloads (`SaveCallback`, `FindCallback`, `LogInCallback`, …). Kotlin modules add suspend/Rx wrappers — never remove the Task/callback originals.
- Nullability: annotate new public Java APIs with `androidx.annotation` (`@NonNull`/`@Nullable`); Kotlin surfaces use strict nullability.
- No Guava, no new networking stack: HTTP stays on OkHttp 5.4.0 via `ParseHttpClient`/`ParseRESTCommand`.

## Naming & Layout

- Package per module: `com.parse` (core), `com.parse.boltsinternal` (tasks), `com.parse.<adapter>` (google/facebook/twitter/fcm/ktx/coroutines/rxjava).
- Tests mirror main: `parse/src/test/java/com/parse/*Test.java` with `ParseTestUtils`/`TestHelper`/`TaskQueueTestHelper` helpers. New tests follow existing `*Test` naming.
- Resources: `includeAndroidResources = true` for unit tests; `consumerProguardFiles release-proguard.pro`.

## Versioning & Releases

- Semantic versioning via semantic-release (`release.config.js`, `.releaserc/`); `gradle.properties` holds the version (currently 4.4.5). CHANGELOG.md is generated — do not hand-edit release entries.
- JitPack coordinates: `com.github.mokhtarabadi.Parse-SDK-Android:<module>:<version>`.

## Task Files & Memory

- Task template: canonical headers (`**File:**`, status, seats, mode). Every change records `rtk test`-prefixed verification evidence with exit code.
- Memories: `android-sdk/stack-quirks`, `android-sdk/test-commands`. Store only explicit reusable rules.
