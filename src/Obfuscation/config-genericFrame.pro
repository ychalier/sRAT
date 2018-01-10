-obfuscationdictionary ./examples/dictionaries/windows.txt
-libraryjars /Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/lib/rt.jar
-injars rs.jar
-outjar rs-obs.jar
-dontshrink
-dontskipnonpubliclibraryclassmembers

-overloadaggressively
-allowaccessmodification 
-keepattributes !LocalVariableTable,!LocalVariableTypeTable

-optimizations !code/simplification/arithmetic

-printmapping mapping.txt

-optimizationpasses 20
-verbose 

-dontwarn org.junit.**
-keep class com.backblaze.erasure.GaloisTest
-keep public class proguard.ProGuard {
public static void main(java.lang.String[]);
}
