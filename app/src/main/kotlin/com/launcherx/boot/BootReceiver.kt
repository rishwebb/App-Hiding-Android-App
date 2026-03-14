package com.launcherx.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.launcherx.LauncherActivity
import com.launcherx.lockscreen.LockScreenService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            // Start lock screen service
            LockScreenService.start(context)

            // Start launcher activity
            val launcherIntent = Intent(context, LauncherActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(launcherIntent)
        }
    }
}
