# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-android
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * implements java.io.Serializable
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}


-keep class in.edu.ssn.ssnapp.models.** {*;}
-keepclasseswithmembers class * {
    @in.edu.ssn.ssnapp.models.* <methods>;
}

-keepattributes *Annotation*, Signature, Exception
-keepattributes SourceFile, LineNumberTable
-keepattributes LocalVariableTable, LocalVariableTypeTable


# lottie library
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** {*;}

# picasso
-dontwarn com.squareup.okhttp.**



# sql cipher
-keep public class net.sqlcipher.** {
    *;
}
-keep public class net.sqlcipher.database.** {
    *;
}

-dontnote net.sqlcipher.*
-keep class net.sqlcipher.database.** {
    *;
}
-dontwarn net.sqlcipher.**
-keeppackagenames net.sqlcipher
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** {
    *;
}

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**