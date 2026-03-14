package com.launcherx.lockscreen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.launcherx.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockScreenService : LifecycleService() {

    private var screenReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        registerScreenReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "LauncherX Lock Screen",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps lock screen service running"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LauncherX")
            .setContentText("Lock screen active")
            .setSmallIcon(R.drawable.ios_settings)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun registerScreenReceiver() {
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        // Send broadcast to show lock screen overlay
                        val lockIntent = Intent("com.launcherx.SHOW_LOCK_SCREEN")
                        context.sendBroadcast(lockIntent)
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenReceiver, filter)
    }

    override fun onDestroy() {
        screenReceiver?.let { unregisterReceiver(it) }
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "launcherx_lockscreen"
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, LockScreenService::class.java)
            context.startForegroundService(intent)
        }
    }
}
