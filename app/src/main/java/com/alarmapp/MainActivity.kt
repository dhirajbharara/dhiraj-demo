package com.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alarmapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmManager: AlarmManager

    companion object {
        const val ALARM_REQUEST_CODE = 1001
        const val PREFS_NAME = "AlarmPrefs"
        const val ALARM_TIME_KEY = "alarm_time"
        const val ALARM_SET_KEY = "alarm_set"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setupUI()
        updateAlarmStatus()
    }

    private fun setupUI() {
        binding.setAlarmButton.setOnClickListener {
            setAlarm()
        }

        binding.cancelAlarmButton.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun setAlarm() {
        val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has already passed today, set for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            // Save alarm details
            sharedPreferences.edit().apply {
                putLong(ALARM_TIME_KEY, calendar.timeInMillis)
                putBoolean(ALARM_SET_KEY, true)
                apply()
            }

            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val alarmTimeString = timeFormat.format(calendar.time)
            
            Toast.makeText(this, getString(R.string.alarm_set_for, alarmTimeString), Toast.LENGTH_LONG).show()
            updateAlarmStatus()

        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission required to set exact alarms", Toast.LENGTH_LONG).show()
        }
    }

    private fun cancelAlarm() {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        // Clear saved alarm details
        sharedPreferences.edit().apply {
            remove(ALARM_TIME_KEY)
            putBoolean(ALARM_SET_KEY, false)
            apply()
        }

        Toast.makeText(this, getString(R.string.alarm_cancelled), Toast.LENGTH_SHORT).show()
        updateAlarmStatus()
    }

    private fun updateAlarmStatus() {
        val isAlarmSet = sharedPreferences.getBoolean(ALARM_SET_KEY, false)
        
        if (isAlarmSet) {
            val alarmTime = sharedPreferences.getLong(ALARM_TIME_KEY, 0)
            if (alarmTime > 0) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val alarmTimeString = timeFormat.format(Date(alarmTime))
                
                binding.alarmStatusText.text = getString(R.string.alarm_set_for, alarmTimeString)
                binding.cancelAlarmButton.visibility = android.view.View.VISIBLE
            }
        } else {
            binding.alarmStatusText.text = getString(R.string.no_alarm_set)
            binding.cancelAlarmButton.visibility = android.view.View.GONE
        }
    }
}