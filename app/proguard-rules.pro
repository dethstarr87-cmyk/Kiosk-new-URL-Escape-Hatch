# Keep all kiosk app classes
-keep class com.kiosk.app.** { *; }

-keepattributes *Annotation*
-keep public class * extends android.app.Activity
-keep public class * extends android.content.BroadcastReceiver
