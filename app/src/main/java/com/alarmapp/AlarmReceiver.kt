package com.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Start the alarm service
        val serviceIntent = Intent(context, AlarmService::class.java)
        context.startForegroundService(serviceIntent)
        
        // Start the alarm ringing activity
        val activityIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(activityIntent)
    }
}