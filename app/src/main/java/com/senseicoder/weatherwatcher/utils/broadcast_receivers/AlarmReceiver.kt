package com.senseicoder.weatherwatcher.utils.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.utils.NotificationUtils.createAlertNotificationAndShowIt
import com.senseicoder.weatherwatcher.utils.services.AlarmOverlayService

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var alarmItem : AlertDTO

    override fun onReceive(context: Context, intent: Intent) {
        alarmItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AlertDTO.ENTITY_NAME, AlertDTO::class.java)!!
        }else{
            intent.getParcelableExtra(AlertDTO.ENTITY_NAME)!!
        }
        if (true){
            createAlertNotificationAndShowIt(context)
            if (Settings.canDrawOverlays(context)){
                Log.d(TAG, "onReceive: ${alarmItem.id}")
                AlarmOverlayService.startAlarm(context, alarmItem)
            }
        }
        else createAlertNotificationAndShowIt(context)
    }

    companion object{
        private const val TAG = "AlarmReceiver"
    }
}