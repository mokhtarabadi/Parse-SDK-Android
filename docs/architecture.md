# Architecture — Parse-SDK-Android

> Version 4.4.5. 9 Gradle modules: `parse`, `bolts-tasks`, `coroutines`, `ktx`, `rxjava`, `fcm`, `google`, `facebook`, `twitter`. See `settings.gradle:1`.

## Module Graph

```text
bolts-tasks ──api──▶ parse ◀──implementation── google, facebook, twitter, fcm, ktx, coroutines, rxjava
```

- `bolts-tasks` (`com.parse.boltsinternal`): pure-Java Task/CancellationToken runtime vendored from Bolts. Zero Android deps (`compileOnly android-4.1.1.4`). No knowledge of Parse.
- `parse` (`com.parse`, Android library, namespace `com.parse`): entire SDK core — `Parse` entrypoint + `Configuration.Builder`, `ParseObject` state machine (`ParseOperationSet`, `OfflineStore`, `ParsePinningEventuallyQueue`), controllers (`Network*Controller`, `CacheQueryController`, `OfflineQueryController`), REST layer (`ParseRESTCommand` + `ParseREST*Command` builders over OkHttp 5.4.0), codec layer (`ParseEncoder`/`ParseDecoder`, `PointerEncoder`, parcel coders), auth (`ParseUser`, `ParseAuthenticationManager`, `ParseCurrentUserController`), push (`ParsePush`, `PushRouter`, `ParsePushBroadcastReceiver`, `ParsePushChannelsController`), config/analytics (`ParseConfig`, `ParseAnalytics`), files (`ParseFile`, `ParseFileController`), installations/sessions/roles (`ParseInstallation`, `ParseSession`, `ParseRole`), geo/polygon (`ParseGeoPoint`, `ParsePolygon`), local datastore (SQLite via `ParseSQLiteDatabase`, `OfflineStore`, `EventuallyPin`).
- Adapters (each `implementation project(':parse')`, never interdependent):
  - `google` (Kotlin): Play Services Auth → `ParseGoogleUtils` link/unlink.
  - `facebook` (Java): Facebook Login SDK [13.2,17.0) → `FacebookController`.
  - `twitter` (Java): OAuth1 + Signpost/OkHttp → `ParseTwitterUtils`, `TwitterController`, `OAuth1FlowDialog`.
  - `fcm` (Java): Firebase Messaging 23.0.7 → `ParseFCM`, `ParseFirebaseMessagingService`.
  - `ktx` (Kotlin): idiomatic extensions over core.
  - `coroutines` (Kotlin): suspend wrappers (`ParseObjectCoroutines*`, `ParseQueryCoroutines*`, `ParseUserCoroutinesExtensions`, `ParseCloudCoroutinesExtensions`, `ParseTaskExtensions`).
  - `rxjava` (Kotlin): RxJava3 `Single`/`Completable` wrappers (`Extensions.kt`).

## Request Flow (core)

```text
ParseObject.saveInBackground → TaskQueue → NetworkObjectController → ParseRESTCommand → OkHttp (ParseHttpClient)
        │                                                        ▲
        └── OfflineStore / EventuallyPin (LDS + saveEventually) ─┘
ParseQuery.findInBackground → CachePolicy → NetworkQueryController | OfflineQueryController | CacheQueryController
```

## Key Decisions (see module DECISIONS.md for dated ADRs)

- Bolts `Task` kept as the async lingua franca for backward compat; coroutines/Rx are thin wrappers.
- OkHttp pinned at 5.4.0 in `parse/build.gradle:50`; auth/push SDKs float within ranges.
- `minSdk 23`, `targetSdk 35`, `compileSdk 36`; AndroidX mandatory.

## CI

- `ci.yml`: `spotlessCheck` → wrapper validation → `clean jacocoTestReport` → Codecov. JDK 17, `--no-daemon`.

## Fork Divergence (mokhtarabadi vs upstream `9e6e18d`)

Local `master` (`17da089`) is 3 commits ahead of `parse-community/master`. All fork-only work, dated:

- `890ceaf` (2026-08-21) "fix many bugs" — toolchain modernization: AGP 7.1.3→9.2.1, Kotlin 1.7.10→1.9.22, compileSdk 31→35, minSdk 21→23, targetSdk 31→35, wrapper 7.4.2→9.4.1, Spotless 5.17.1→6.25.0 (+ freefair/versions bumps). Behavior: cancellable HTTP — `ParseHttpClient` tracks the in-flight OkHttp `Call` (`currentCall`, Task 718) and `ParseQuery.cancel()` now interrupts the request instead of only flagging the Task (plus Issue #969 cancel log); new `ParseObject.saveEventuallyInBackground()` runs `collectDirtyChildren` off the main thread on a bounded executor (Task 720, no more per-call raw threads), with an Issue #1007 main-thread warning in `saveEventually()`.
- `1b42f79` (2026-08-22) "hardening" — `ParseUser` in-memory session-token cache (5-min TTL, `invalidateCachedSessionToken()`, cleared on logout; Task 720, avoids disk I/O per query at high qps); `saveEventually` executor refinement; `release-proguard.pro` keeps retrace attributes but drops the source-file rename.
- `17da089` (2026-10-10) "sticky experiment bucketing" — new pure-static `ParseExperiments` helper: deterministic `SHA-256(key + "\n" + userId)` mod-100 buckets over `ParseConfig` definitions, no assignment table, widen-only growth; parity-tested against cloud/JS and dumble vectors (`ParseExperimentsTest.extendedParityVectors`).
- Uncommitted local (not in any commit): `compileSdkVersion` 35→36 in root `build.gradle`.
