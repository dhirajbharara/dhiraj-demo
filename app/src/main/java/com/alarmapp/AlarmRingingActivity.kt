package com.alarmapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alarmapp.databinding.ActivityAlarmRingingBinding
import java.text.SimpleDateFormat
import java.util.*

class AlarmRingingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmRingingBinding
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashlightOn = false
    private var flashlightToggleCount = 0
    private val requiredToggles = 5
    private val timeHandler = Handler(Looper.getMainLooper())
    private var timeRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmRingingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFlashlight()
        setupUI()
        startTimeUpdater()
    }

    private fun setupFlashlight() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                cameraId = cameraManager.cameraIdList[0]
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupUI() {
        updateFlashlightCount()
        
        binding.flashlightButton.setOnClickListener {
            toggleFlashlight()
        }

        binding.snoozeButton.setOnClickListener {
            snoozeAlarm()
        }

        binding.stopButton.setOnClickListener {
            stopAlarm()
        }
    }

    private fun toggleFlashlight() {
        cameraId?.let { id ->
            try {
                cameraManager.setTorchMode(id, !isFlashlightOn)
                isFlashlightOn = !isFlashlightOn
                flashlightToggleCount++
                
                updateFlashlightCount()
                
                if (flashlightToggleCount >= requiredToggles) {
                    binding.stopButton.isEnabled = true
                    binding.stopButton.text = "STOP ALARM (UNLOCKED)"
                    Toast.makeText(this, "Alarm can now be stopped!", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: CameraAccessException) {
                e.printStackTrace()
                Toast.makeText(this, "Error controlling flashlight", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Flashlight not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFlashlightCount() {
        binding.flashlightCountText.text = getString(R.string.flashlight_count, flashlightToggleCount)
    }

    private fun snoozeAlarm() {
        // Stop the alarm service
        val serviceIntent = Intent(this, AlarmService::class.java)
        stopService(serviceIntent)

        // Set a new alarm for 5 minutes from now
        val snoozeIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            this,
            MainActivity.ALARM_REQUEST_CODE + 1,
            snoozeIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000) // 5 minutes

        try {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                snoozeTime,
                pendingIntent
            )
            
            Toast.makeText(this, getString(R.string.alarm_snoozed), Toast.LENGTH_SHORT).show()
            finish()
            
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error setting snooze", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopAlarm() {
        if (flashlightToggleCount >= requiredToggles) {
            // Stop the alarm service
            val serviceIntent = Intent(this, AlarmService::class.java)
            stopService(serviceIntent)

            // Clear alarm from shared preferences
            val sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putBoolean(MainActivity.ALARM_SET_KEY, false)
                apply()
            }

            Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Toggle flashlight ${requiredToggles - flashlightToggleCount} more times", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimeUpdater() {
        timeRunnable = object : Runnable {
            override fun run() {
                updateCurrentTime()
                timeHandler.postDelayed(this, 1000)
            }
        }
        timeRunnable?.let { timeHandler.post(it) }
    }

    private fun updateCurrentTime() {
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        binding.currentTime.text = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()
        timeRunnable?.let { timeHandler.removeCallbacks(it) }
        
        // Turn off flashlight if it's on
        if (isFlashlightOn) {
            cameraId?.let { id ->
                try {
                    cameraManager.setTorchMode(id, false)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        // Prevent back button from closing the alarm
        Toast.makeText(this, "Use flashlight toggle or snooze to dismiss alarm", Toast.LENGTH_SHORT).show()
    }
}