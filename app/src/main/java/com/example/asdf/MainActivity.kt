package com.example.asdf

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }
        
        statusText = TextView(this).apply {
            text = "状态：无障碍服务未开启"
            textSize = 18f
            setPadding(0, 0, 0, 30)
        }
        
        val openAccessibilityBtn = Button(this).apply {
            text = "1. 开启无障碍服务"
            setOnClickListener { openAccessibilitySettings() }
        }
        
        val openOverlayBtn = Button(this).apply {
            text = "2. 开启悬浮窗权限"
            setOnClickListener { requestOverlayPermission() }
        }
        
        layout.addView(statusText)
        layout.addView(openAccessibilityBtn)
        layout.addView(openOverlayBtn)
        setContentView(layout)
    }
    
    override fun onResume() {
        super.onResume()
        checkServiceStatus()
    }
    
    private fun checkServiceStatus() {
        val isEnabled = isAccessibilityServiceEnabled()
        statusText.text = if (isEnabled) {
            "状态：✅ 无障碍服务已开启\n点击悬浮按钮开始自动点击"
        } else {
            "状态：❌ 请先开启无障碍服务"
        }
    }
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = "${packageName}/${TapService::class.java.canonicalName}"
        try {
            val enabledServices = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return enabledServices?.contains(service) == true
        } catch (e: Exception) {
            return false
        }
    }
    
    private fun openAccessibilitySettings() {
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            startActivity(this)
        }
    }
    
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                    startActivity(this)
                }
            }
        }
    }
}
