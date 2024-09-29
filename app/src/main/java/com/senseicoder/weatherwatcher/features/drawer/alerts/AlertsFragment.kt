package com.senseicoder.weatherwatcher.features.drawer.alerts

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.FragmentAlertsBinding
import com.senseicoder.weatherwatcher.db.AppDataBase
import com.senseicoder.weatherwatcher.db.LocalDataSourceImpl
import com.senseicoder.weatherwatcher.features.drawer.alerts.adapters.AlertAdapter
import com.senseicoder.weatherwatcher.features.drawer.alerts.viewmodels.AlertViewModel
import com.senseicoder.weatherwatcher.features.drawer.alerts.viewmodels.AlertViewModelFactory
import com.senseicoder.weatherwatcher.models.repositories.LocalRepositoryImpl
import com.senseicoder.weatherwatcher.utils.dialogs.AlertTimePickerDialogFragment
import com.senseicoder.weatherwatcher.utils.global.Constants
import com.senseicoder.weatherwatcher.utils.global.Constants.SharedPrefs.Settings.Notifications.Values.DISABLE
import com.senseicoder.weatherwatcher.utils.global.Constants.SharedPrefs.Settings.Notifications.Values.ENABLE
import com.senseicoder.weatherwatcher.utils.global.showSnackbar
import com.senseicoder.weatherwatcher.utils.schedulars.AlarmSchedulerImpl
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.launch


class AlertsFragment : Fragment() {

    lateinit var binding: FragmentAlertsBinding
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertTimePickerDialogFragment: AlertTimePickerDialogFragment
    private lateinit var settings: SharedPreferences

    private val notificationsPermissionRegister = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        // To log permissions:
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            if (granted) {
                if (isAlarmAllowed()) {
                    requestAlertPermissions()
                } else {
                    binding.run {
                        notificationsPermissionDeniedGroup.visibility = View.GONE
                        notificationPermissionGrantedGroup.visibility = View.VISIBLE
                        alertsProgressBar.visibility = View.GONE
                    }
                    alertViewModel.getAlerts()
                }
            } else {
                binding.run {
                    notificationsPermissionDeniedGroup.visibility = View.VISIBLE
                    notificationPermissionGrantedGroup.visibility = View.GONE
                }
                binding.root.showSnackbar(getString(R.string.permissions_denied), 3000)
            }
        }
    }

    private val alertPermissionsRegister = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d(TAG, "alertPermissionsRegister: $granted")
        if (granted) {
            binding.run {
                notificationsPermissionDeniedGroup.visibility = View.GONE
                alertPermissionsDeniedGroup.visibility = View.GONE
                notificationPermissionGrantedGroup.visibility = View.VISIBLE
            }
            alertViewModel.getAlerts()
        } else {
            binding.run {
                notificationsPermissionDeniedGroup.visibility = View.GONE
                alertPermissionsDeniedGroup.visibility = View.VISIBLE
                notificationPermissionGrantedGroup.visibility = View.GONE
            }
            binding.root.showSnackbar(getString(R.string.permissions_denied), 3000)
        }

    }

    private fun requestAlertPermissions() {
        alertPermissionsRegister.launch(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = AlertViewModelFactory(
            LocalRepositoryImpl.getInstance(
                LocalDataSourceImpl(
                    AppDataBase.getInstance(requireContext()).weatherDAO
                )
            ),
            requireActivity().application
        )

        settings = requireActivity().getSharedPreferences(
            Constants.SharedPrefs.Settings.SETTINGS,
            Context.MODE_PRIVATE
        )

        alertViewModel = ViewModelProvider(
            this,
            factory
        )[AlertViewModel::class]

        binding.alertEnableNotificationsButton.setOnClickListener {
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            }
        }
        alertAdapter = AlertAdapter {
            alertViewModel.deleteAlert(it.id)
            AlarmSchedulerImpl(requireContext()).cancelAlarm(it)
        }
        binding.alertRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = alertAdapter
        }
        binding.alertFAB.setOnClickListener {
            openDateTimeDialog()
        }
        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        lifecycleScope.launch {
            alertViewModel.alert.collect { res ->
                when (res) {
                    is CurrentState.Failure -> {
                        binding.alertsProgressBar.visibility = View.GONE
                        binding.root.showSnackbar(res.msg)
                    }

                    is CurrentState.Loading -> {
                        binding.apply {
                            alertsProgressBar.visibility = View.VISIBLE
                            alertRecyclerEmpty.visibility = View.GONE
                            alertRecycler.visibility = View.VISIBLE
                        }
                    }

                    is CurrentState.Success -> {
                        alertAdapter.submitList(res.data)
                        binding.apply {
                            binding.alertsProgressBar.visibility = View.GONE
                            if (res.data.isEmpty()) {
                                alertRecyclerEmpty.visibility = View.VISIBLE
                                alertRecycler.visibility = View.GONE
                            } else {
                                alertRecyclerEmpty.visibility = View.GONE
                                alertRecycler.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openDateTimeDialog() {
        if (::alertTimePickerDialogFragment.isInitialized) {
            alertTimePickerDialogFragment.dismiss()
        }
        alertTimePickerDialogFragment = AlertTimePickerDialogFragment().also {
            it.show(
                requireActivity().supportFragmentManager, null
            )
        }
    }


    private fun isAlarmAllowed(): Boolean {
        return settings.getString(
            Constants.SharedPrefs.Settings.Notifications.NOTIFICAITONS,
            DISABLE
        )!! == ENABLE
    }

    override fun onStart() {
        super.onStart()
        val allowAlarm: Boolean = isAlarmAllowed()
        Constants.SharedPrefs.Settings.Notifications.Values.run {
            if ((VERSION.SDK_INT >= VERSION_CODES.TIRAMISU)) {
                if (areNotificationsEnabled()) {
                    if(allowAlarm){
                        if(isShowOnOtherAppsPermissionGranted()){
                            Log.d(TAG, "onStart: working")
                            binding.run {
                                notificationsPermissionDeniedGroup.visibility = View.GONE
                                alertPermissionsDeniedGroup.visibility = View.GONE
                                notificationPermissionGrantedGroup.visibility = View.VISIBLE
                            }
                        }else{
                            requestAlertPermissions()
                        }
                    }else{
                        binding.run {
                            notificationsPermissionDeniedGroup.visibility = View.GONE
                            alertPermissionsDeniedGroup.visibility = View.GONE
                            notificationPermissionGrantedGroup.visibility = View.VISIBLE
                        }
                    }
                    alertViewModel.getAlerts()
                } else {
                    requestNotificationPermission()
                }
            } else {
                alertViewModel.getAlerts()
            }
        }


    }

    @RequiresApi(VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission() {
        notificationsPermissionRegister.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun areNotificationsEnabled(): Boolean {
        val notificationManager: NotificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }

    fun isShowOnOtherAppsPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(requireContext())
    }


    companion object {
        private const val TAG = "AlertsFragment"
    }

}