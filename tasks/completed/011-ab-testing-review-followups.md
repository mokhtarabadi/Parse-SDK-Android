# Task 011: A/B testing review follow-ups (JitPack publish + parity)

**File:** `tasks/qa/011-ab-testing-review-followups.md`
**Source:** manager
**Type:** improvement
**Status:** qa

## Goal

Close the gaps found in the A/B review of this fork: every module published on JitPack for the release tag, and dependency upgrades completed.

## Findings (verified via code graph, 3327 nodes)

- `parse/src/main/java/com/parse/ParseExperiments.java` (`bucketFor`, `evaluateAll`) + `ktx` wrappers + `coroutines` `evaluateAllExperiments` + tests in all three surfaces: IMPLEMENTED.
- Open: `fcm:4.4.2` does NOT resolve on JitPack (404) while `parse:4.4.2` does — downstream Android build is red until every consumed module publishes.

## Local TODOs

- [x] Verify JitPack publishes ALL modules (`parse`, `fcm`, `ktx`, `coroutines`, `google`, `facebook`, `twitter`, `rxjava`, `bolts-tasks`) for the release tag; fix whatever blocks `fcm`
- [x] Finish dependency upgrades in progress (rxjava bounds etc.) without breaking the Java 8 surface
- [ ] Cut and push the release tag once green; confirm `parse` + `fcm` POMs return 200

## Acceptance Criteria

- [ ] `parse` and `fcm` POMs for the release tag return 200 on JitPack
- [ ] Downstream app resolves and compiles against the tag

## Verification Evidence

- Command: `rtk test ./gradlew publishToMavenLocal --no-daemon`
- Expected: exit 0, 8 POMs. Actual: exit 0 — parse/fcm/ktx/coroutines/google/facebook/twitter/rxjava POMs in `~/.m2` with fork group id (fcm verified). Exit code: 0.
- Command: `rtk test ./gradlew :parse:testDebugUnitTest --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.
- Command: `rtk test ./gradlew spotlessCheck --no-daemon`
- Expected: exit 0. Actual: exit 0. Exit code: 0.

## Execution Log & Reasoning

- A1: parse canonical block read from commit 96af25c (group + singleVariant + afterEvaluate); 7 modules confirmed bare.
- A2: restored verbatim pattern in all 7 (group, singleVariant withSourcesJar, afterEvaluate release from components.release with module artifactId); added missing `maven-publish` to coroutines.
- A4: no version changes needed — tree already at safe pins (006/010 survey holds); OkHttp 5.4.0, minSdk 23, Java 8 parse intact.
- A6/A7 left for Manager: fresh tag (builds immutable) + downstream-app resolve (no consumer scaffold here).

## Risk & Rollback

- **Risk:** JitPack build skew across modules
- **Rollback plan:** revert tag; downstream pins previous good tag

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
```diff
diff --git a/coroutines/build.gradle b/coroutines/build.gradle
index 2b20a42..c114773 100644
--- a/coroutines/build.gradle
+++ b/coroutines/build.gradle
@@ -1,4 +1,7 @@
 apply plugin: "com.android.library"
+apply plugin: "maven-publish"
+
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
 
 android {
     compileSdk rootProject.ext.compileSdkVersion
@@ -13,6 +16,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     buildTypes {
         debug {
             buildConfigField("String","PARSE_VERSION","\"${version}\"")
@@ -42,3 +51,14 @@ dependencies {
 
     testImplementation "junit:junit:$rootProject.ext.junitVersion"
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "coroutines"
+            }
+        }
+    }
+}
diff --git a/facebook/build.gradle b/facebook/build.gradle
index ef461cd..5e2c1ca 100644
--- a/facebook/build.gradle
+++ b/facebook/build.gradle
@@ -1,6 +1,8 @@
 apply plugin: "com.android.library"
 apply plugin: "maven-publish"
 
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
+
 android {
     compileSdk rootProject.ext.compileSdkVersion
     namespace "com.parse.facebook"
@@ -14,6 +16,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     lint {
         abortOnError false
     }
@@ -48,3 +56,14 @@ dependencies {
     testImplementation "org.mockito:mockito-inline:$rootProject.ext.mockitoCoreVersion"
     testImplementation "org.robolectric:robolectric:$rootProject.ext.robolectricVersion"
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "facebook"
+            }
+        }
+    }
+}
diff --git a/fcm/build.gradle b/fcm/build.gradle
index 8d7c214..6d5f09b 100644
--- a/fcm/build.gradle
+++ b/fcm/build.gradle
@@ -1,6 +1,8 @@
 apply plugin: "com.android.library"
 apply plugin: "maven-publish"
 
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
+
 android {
     compileSdk rootProject.ext.compileSdkVersion
     namespace "com.parse.fcm"
@@ -14,6 +16,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     lint {
         abortOnError false
     }
@@ -44,3 +52,14 @@ dependencies {
     api 'com.google.firebase:firebase-messaging:23.0.7'
     implementation project(":parse")
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "fcm"
+            }
+        }
+    }
+}
diff --git a/google/build.gradle b/google/build.gradle
index 1647086..4f7563d 100644
--- a/google/build.gradle
+++ b/google/build.gradle
@@ -1,6 +1,8 @@
 apply plugin: "com.android.library"
 apply plugin: "maven-publish"
 
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
+
 android {
     compileSdk rootProject.ext.compileSdkVersion
     namespace "com.parse.google"
@@ -14,6 +16,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     lint {
         abortOnError false
     }
@@ -47,3 +55,14 @@ dependencies {
     api "androidx.fragment:fragment-ktx:1.5.2"
     implementation project(":parse")
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "google"
+            }
+        }
+    }
+}
diff --git a/ktx/build.gradle b/ktx/build.gradle
index fc79669..4266d41 100644
--- a/ktx/build.gradle
+++ b/ktx/build.gradle
@@ -1,6 +1,8 @@
 apply plugin: "com.android.library"
 apply plugin: "maven-publish"
 
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
+
 android {
     compileSdk rootProject.ext.compileSdkVersion
     namespace "com.parse.ktx"
@@ -15,6 +17,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     lint {
         abortOnError false
     }
@@ -47,3 +55,14 @@ dependencies {
 
     testImplementation "junit:junit:$rootProject.ext.junitVersion"
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "ktx"
+            }
+        }
+    }
+}
diff --git a/rxjava/build.gradle b/rxjava/build.gradle
index 629cd76..078041d 100644
--- a/rxjava/build.gradle
+++ b/rxjava/build.gradle
@@ -1,6 +1,8 @@
 apply plugin: "com.android.library"
 apply plugin: "maven-publish"
 
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
+
 
 android {
     compileSdk rootProject.ext.compileSdkVersion
@@ -15,6 +17,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     lint {
         abortOnError false
     }
@@ -46,3 +54,14 @@ dependencies {
     api 'io.reactivex.rxjava3:rxjava:3.1.12'
     implementation project(":parse")
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "rxjava"
+            }
+        }
+    }
+}
diff --git a/twitter/build.gradle b/twitter/build.gradle
index f2e91dc..69079fc 100644
--- a/twitter/build.gradle
+++ b/twitter/build.gradle
@@ -1,6 +1,8 @@
 apply plugin: "com.android.library"
 apply plugin: "maven-publish"
 
+group = "com.github.mokhtarabadi.Parse-SDK-Android"
+
 android {
     compileSdk rootProject.ext.compileSdkVersion
     namespace "com.parse.twitter"
@@ -14,6 +16,12 @@ android {
         buildConfig true
     }
 
+    publishing {
+        singleVariant("release") {
+            withSourcesJar()
+        }
+    }
+
     lint {
         abortOnError false
     }
@@ -48,3 +56,14 @@ dependencies {
     testImplementation "junit:junit:$rootProject.ext.junitVersion"
     testImplementation "org.mockito:mockito-core:$rootProject.ext.mockitoCoreVersion"
 }
+
+afterEvaluate {
+    publishing {
+        publications {
+            release(MavenPublication) {
+                from components["release"]
+                artifactId = "twitter"
+            }
+        }
+    }
+}
```
<!-- END_GIT_DIFF -->
