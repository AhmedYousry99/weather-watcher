package com.senseicoder.weatherwatcher.utils.dialogs

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.senseicoder.weatherwatcher.databinding.FragmentInitialSettingsDialogBinding
import com.senseicoder.weatherwatcher.features.Main.MainActivity
import com.senseicoder.weatherwatcher.features.map.MapActivity
import com.senseicoder.weatherwatcher.utils.global.Constants

class InitialSettingsDialogFragment(private val okFunc : (location: String, notifications: String)-> Unit) : DialogFragment() {

    lateinit var binding: FragmentInitialSettingsDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitialSettingsDialogBinding.inflate(
            layoutInflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioButtonMap.setOnClickListener{
            (requireActivity() as MainActivity).registerMapActivity.launch(
                Intent(requireContext(), MapActivity::class.java)
            )
        }
        binding.initialSetupOkButton.setOnClickListener{
            val location = if(binding.radioButtonGPS.isChecked) Constants.SharedPrefs.Settings.Location.Values.GPS else Constants.SharedPrefs.Settings.Location.Values.MAP
            val notifications = if(binding.locationSwitch.isChecked) Constants.SharedPrefs.Settings.Notifications.Values.ENABLE else Constants.SharedPrefs.Settings.Notifications.Values.DISABLE
            okFunc.invoke(location, notifications)
            this@InitialSettingsDialogFragment.dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        requireActivity().finish()
    }
}