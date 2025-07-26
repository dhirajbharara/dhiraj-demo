package com.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED) {
            
            // Restore any active alarms
            val sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val isAlarmSet = sharedPreferences.getBoolean(MainActivity.ALARM_SET_KEY, false)
            val alarmTime = sharedPreferences.getLong(MainActivity.ALARM_TIME_KEY, 0)
            
            if (isAlarmSet && alarmTime > System.currentTimeMillis()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    MainActivity.ALARM_REQUEST_CODE,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
    }
}