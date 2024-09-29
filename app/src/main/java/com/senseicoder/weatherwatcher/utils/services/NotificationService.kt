package com.senseicoder.weatherwatcher.utils.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.senseicoder.weatherwatcher.utils.NotificationUtils.createAlertNotification


class NotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onHandleIntent: onHandleIntentBeginning")
        startForeground(10, createAlertNotification(this).build())
        Log.d(TAG, "onHandleIntent: onHandleIntentEnd")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    companion object{
        private const val TAG = "NotificationService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}