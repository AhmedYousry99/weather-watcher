package com.senseicoder.weatherwatcher.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.features.Main.MainActivity

object NotificationUtils {

    const val ALARM_CHANNAL_ID : String = "alarm_channel_id"
    const val ACTION : String = "com.senseicoder.MyAction"
    const val NOTIFICATION_PERM = 1234

    fun createAlertNotification(context: Context): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.alert_channel_name)
             val description = context.getString(R.string.alert_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ALARM_CHANNAL_ID, name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, ALARM_CHANNAL_ID)
            .setSmallIcon(R.drawable.cloud_placeholder)
            .setContentTitle(context.getString(R.string.hi))
            .setContentText(context.getString(R.string.dont_forget_to_check_weather))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        return builder
    }

    fun createAlertNotificationAndShowIt(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.alert_channel_name)
            val description = context.getString(R.string.alert_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ALARM_CHANNAL_ID, name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, ALARM_CHANNAL_ID)
            .setSmallIcon(R.drawable.cloud_placeholder)
            .setContentTitle(context.getString(R.string.hi))
            .setContentText(context.getString(R.string.dont_forget_to_check_weather))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent).build()
        notificationManager.notify(NOTIFICATION_PERM, notification)
    }
}