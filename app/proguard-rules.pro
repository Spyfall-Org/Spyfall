-dontwarn org.apiguardian.api.API$Status
-dontwarn org.apiguardian.api.API
-dontwarn com.google.auto.service.AutoService


# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Ads SDK adds alot of beef,
#-keep public class com.google.android.gms.ads.** {
#    public *;
#}
#
#-keep public class com.google.ads.** {
#    public *;
#}
#
#-dontwarn com.google.android.gms.**