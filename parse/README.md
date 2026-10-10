# parse — Core SDK Module

> Android library (`com.parse`, namespace `com.parse`). Java 8, ~156 source files. Depends on `bolts-tasks` (`api project(':bolts-tasks')`); third-party: OkHttp 5.4.0, AndroidX annotation/core.

## Entry Points

| Class | File | Role |
|---|---|---|
| `Parse` / `Configuration.Builder` | `src/main/java/com/parse/Parse.java` | `Parse.initialize(...)` startup |
| `ParseObject` | `src/main/java/com/parse/ParseObject.java` | save/fetch/pin state machine (`saveInBackground`, `saveEventually`) |
| `ParseQuery<T>` | `src/main/java/com/parse/ParseQuery.java` | `findInBackground`, `getInBackground`, cache policy |
| `ParseUser` | `src/main/java/com/parse/ParseUser.java` | login/signup/current-user (`logInInBackground`) |
| `ParseFile` | `src/main/java/com/parse/ParseFile.java` | file upload/download |
| `ParseInstallation` | `src/main/java/com/parse/ParseInstallation.java` | push installation record |
| `ParseConfig` | `src/main/java/com/parse/ParseConfig.java` | remote config singleton |
| `ParseCloud` | `src/main/java/com/parse/ParseCloud.java` | Cloud Code calls |
| `ParseRESTCommand` | `src/main/java/com/parse/ParseRESTCommand.java` | REST builder over OkHttp |

## Subsystems

- Controllers: `NetworkObjectController`, `NetworkQueryController`, `NetworkUserController`, `NetworkSessionController`, `CacheQueryController`, `OfflineQueryController`, `OfflineStore`, `ParsePinningEventuallyQueue`.
- Codec: `ParseEncoder`, `ParseDecoder`, `PointerEncoder`, `ParseParcelEncoder/Decoder`, `ParseObjectCurrentCoder`.
- Tests: `src/test/java/com/parse/*Test.java` (~88 files, Robolectric + Mockito + MockWebServer). Run `./gradlew :parse:testDebugUnitTest --no-daemon`.

See `DECISIONS.md` (sibling) and root `docs/architecture.md`, `docs/data_model.md`.
