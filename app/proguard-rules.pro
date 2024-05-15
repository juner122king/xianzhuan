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


#网易网盾sdk开始
 -keep class com.netease.htprotect.**{*;}
 -keep class com.netease.mobsec.**{*;}
#网易网盾sdk结束



#友盟开始
-keep class com.umeng.** {*;}
-keep class org.repackage.** {*;}
-keep class com.uyumao.** { *; }
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#友盟结束

#web 开始

#自己的包名即可
-keep class com.github.lzyzsd.jsbridge.**{*;}
-keep interface com.github.lzyzsd.jsbridge.** { *; }

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keep class android.webkit.JavascriptInterface {*;}

##保持JavascriptInterface这个不能别调用 //
#-keepclassmembers class com.lelezu.app.xianzhuan.ui.views.WebViewActivity{
#  public *;
#}


#鲸鸿动能SDK https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/identifier-service-integrating-sdk-0000001056460552#section187948541792
#-keep class com.huawei.hms.ads.** {*; }
#-keep interface com.huawei.hms.ads.** {*; }