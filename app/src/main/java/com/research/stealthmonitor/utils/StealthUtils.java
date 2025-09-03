package com.research.stealthmonitor.utils;

import android.os.Debug;
import android.os.Build;

public class StealthUtils {
    
    public static boolean isDebugging() {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger();
    }
    
    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic") ||
               Build.FINGERPRINT.startsWith("unknown") ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.MODEL.contains("Android SDK") ||
               Build.MANUFACTURER.contains("Genymotion") ||
               Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
               "google_sdk".equals(Build.PRODUCT);
    }
    
    public static void antiAnalysisDelay() {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long end = System.currentTimeMillis();
        
        if ((end - start) > 200) {
            System.exit(0);
        }
    }
}