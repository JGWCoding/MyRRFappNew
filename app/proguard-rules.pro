# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in G:\android\adt-bundle-windows-x86_64-20140321\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

################### region for xUtils
-keepattributes Signature,*Annotation*
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers @org.xutils.db.annotation.* class * {*;}
-keepclassmembers @org.xutils.http.annotation.* class * {*;}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event <methods>;
}
#################### end region

#################### region for glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#################### end region

#################### region for PhotoView
-dontwarn uk.co.senab.photoview.**
-keep public class uk.co.senab.photoview.**{*;}
#################### end region

#################### region for bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#################### end region
#################### region for ksoap2
-dontwarn org.kobjects.**
-keep class org.kobjects.** { *;}
-dontwarn org.ksoap2.**
-keep class org.ksoap2.** { *;}
-dontwarn org.kxml2.**
-keep class org.kxml2.** { *;}
-dontwarn org.xmlpull.v1.**
-keep class org.xmlpull.v1.** { *;}
#################### end region
-dontwarn okio.**
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
#################### region region dialog样式
-keep class com.dou361.** {
*;
}
#################### end region
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}