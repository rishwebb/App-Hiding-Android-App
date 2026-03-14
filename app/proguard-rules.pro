# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class com.launcherx.data.entities.** { *; }
-keep class com.launcherx.data.db.** { *; }

# Suppress missing annotation-only Error Prone types referenced by Tink/security-crypto.
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi
