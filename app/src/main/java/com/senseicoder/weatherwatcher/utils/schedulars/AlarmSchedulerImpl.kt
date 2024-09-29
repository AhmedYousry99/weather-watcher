package com.senseicoder.weatherwatcher.utils.schedulars

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.utils.broadcast_receivers.AlarmReceiver
import java.time.ZoneId

class AlarmSchedulerImpl (private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
     fun scheduleAlarm(alertItem: AlertDTO) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlertDTO.ENTITY_NAME, alertItem)
        }
         Log.d(TAG, "scheduleAlarm: $alertItem")
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alertItem.fromTimeLDT.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                alertItem.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ))
    }

    fun cancelAlarm(alarmItem: AlertDTO) {
        alarmManager.cancel(PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        ))
    }

    companion object{
        private const val TAG = "AlarmSchedulerImpl"
    }
}