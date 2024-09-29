package com.senseicoder.weatherwatcher.utils.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.AlertOverlayBinding
import com.senseicoder.weatherwatcher.models.AlertDTO

class AlarmOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var alarmItem: AlertDTO

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        alarmItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent!!.getParcelableExtra(AlertDTO.ENTITY_NAME, AlertDTO::class.java)!!
        }else{
            intent!!.getParcelableExtra(AlertDTO.ENTITY_NAME)!!
        }
        Log.d(TAG, "onStartCommand: $alarmItem")
        val binding = AlertOverlayBinding.inflate(LayoutInflater.from(this))
        overlayView = binding.root
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.TOP
        val margin = ViewGroup.MarginLayoutParams(layoutParams)
        margin.setMargins(20, 50, 50, 20)
        overlayView.layoutParams = margin
//        binding.msgTxt.text = alarmItem.message
        windowManager.addView(overlayView, layoutParams)

        mediaPlayer.start()
        binding.alertDialogDismiss.setOnClickListener {
            mediaPlayer.stop()
            stopSelf()
        }
        overlayView.setOnTouchListener { _, _ -> // Close overlay on touch outside
            stopSelf()
            true
        }
        return START_STICKY
    }
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true
        Log.d(TAG, "onCreate: ")
    }
    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(overlayView)
        mediaPlayer.stop()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object{
        private const val TAG = "AlarmOverlayService"

        fun startAlarm(context: Context, alertDTO: AlertDTO) {
            val intent = Intent(context, AlarmOverlayService::class.java)
            intent.apply {
                putExtra(AlertDTO.ENTITY_NAME, alertDTO)
            }
            context.startService(intent)
        }
    }
}