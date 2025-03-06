package fr.isen.ines.isensmartcompanion.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

object NotificationHelper {
    fun scheduleNotification(context: Context, eventName: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("eventName", eventName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10_000,
            pendingIntent
        )
    }
}
