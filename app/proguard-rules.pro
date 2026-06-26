# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# 基础优化配置
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保留源码信息用于调试
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ==================== Jetpack Compose ====================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.animation.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# 保留 Compose 生成的类
-keep class * extends androidx.compose.ui.node.ComposeNode
-keep class * extends androidx.compose.ui.node.LayoutNode

# ==================== Room ====================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keep class * extends androidx.paging.PagingSource
-keep class * extends androidx.paging.PagingData

# ==================== Media3 ====================
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
-keep interface androidx.media3.** { *; }

# ==================== Coil ====================
-keep class coil.** { *; }
-dontwarn coil.**
-keep class coil.decode.** { *; }
-keep class coil.request.** { *; }

# ==================== Markwon ====================
-keep class io.noties.markwon.** { *; }
-dontwarn io.noties.markwon.**
-keep class io.noties.prism4j.** { *; }
-dontwarn io.noties.prism4j.**

# ==================== Gson ====================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ==================== Kotlin ====================
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ==================== Lifecycle ====================
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# ==================== Navigation ====================
-keepclassmembers class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ==================== DataStore ====================
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ==================== 通用规则 ====================
# 保留 Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留 Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留 R 文件
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保留 View 构造函数
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留本地方法
-keepclasseswithmembers class * {
    native <methods>;
}