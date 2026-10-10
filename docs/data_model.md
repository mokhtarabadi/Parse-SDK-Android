# Data Model — Parse-SDK-Android

> Client-side model of Parse Server objects. Wire format is Parse REST JSON; see `ParseEncoder` / `ParseDecoder` / `PointerEncoder` (`parse/src/main/java/com/parse/`).

## Core Types

| Class | Server class | Key fields | Notes |
|---|---|---|---|
| `ParseObject` | any (`ParseClassName`) | `objectId`, `createdAt`, `updatedAt`, `ACL`, subclass fields via `ParseOperationSet` | Dirty-tracking state machine; `saveInBackground` / `saveEventually` (via `EventuallyPin` + `ParsePinningEventuallyQueue`) |
| `ParseUser` | `_User` | `username`, `password` (write-only), `email`, `sessionToken`, `authData` | `logInInBackground` / `signUpInBackground`; current user via `ParseCurrentUserController` + `CachedCurrentUserController` |
| `ParseSession` | `_Session` | `sessionToken`, `user` (Pointer), `expiresAt` | Revocable sessions; `NetworkSessionController` |
| `ParseRole` | `_Role` | `name`, `users` (Relation), `roles` (Relation) | ACL-gated; `ParseRoleTest` |
| `ParseInstallation` | `_Installation` | `installationId`, `deviceToken`, `channels`, `pushType`, `GCMSenderId` | Cached via `CachedCurrentInstallationController`; FCM adapter writes `deviceToken` |
| `ParseConfig` | `Config` (singleton) | arbitrary JSON params | `ParseCurrentConfigController`; `getInBackground` |
| `ParseFile` | `File` | `name`, `url`, `__type: File` | Upload via `ParseFileController` + `ParseRESTFileCommand`; URI/ByteArray/File bodies |
| `ParseGeoPoint` | inline type | `latitude`, `longitude`, `__type: GeoPoint` | Query `$nearSphere` support |
| `ParsePolygon` | inline type | `coordinates`, `__type: Polygon` | `$geoWithin` support |
| `ParseACL` | inline map | per-user/role read+write, `publicRead/WriteAccess` | Parcelable; enforced server-side |
| `ParseRelation<T>` | inline | `__type: Relation`, `className` | Lazy query backing |
| `ParsePush` | push payload | `channels`, `where`, `data`, `expiration_*` | Client send (legacy) + `PushRouter` receive |

## Wire Encoding (`__type` tags)

- `Date` → `{__type: Date, iso: …}` (`ParseDateFormat`, imprecise variant covered).
- `Bytes` → `{__type: Bytes, base64: …}`; `Pointer` → `{__type: Pointer, className, objectId}`; `File` similar.
- `OfflineStore` / `ParseSQLiteDatabase` persist objects + `EventuallyPin` queue in on-device SQLite (LDS). Cache layer: `ParseKeyValueCache` with `maxKeyValueCacheBytes/Files` (4.4.0, `Parse.Configuration`).

## Local Identity

- `installationId` (`InstallationId`, persisted); `LocalIdManager` for unsaved offline objects (`localId` → `objectId` remap on save).

## Fork Addition: `ParseExperiments` (not a server class)

- Pure-static client helper (`parse/src/main/java/com/parse/ParseExperiments.java`, fork commit `17da089`): sticky A/B bucketing evaluated locally from `ParseConfig` experiment definitions — `bucketFor(key, userId)` = `SHA-256(key + "\n" + userId)` head mod 100, `-1` on bad input (treat as control). Zero network, zero state, any-thread safe. Widening `traffic_pct` only admits higher buckets (early users never evicted); shrinking corrupts reads — rotate the key instead.
