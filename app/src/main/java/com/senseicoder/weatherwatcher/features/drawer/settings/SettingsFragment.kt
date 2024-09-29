package com.senseicoder.weatherwatcher.features.drawer.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.FragmentSettingsBinding
import com.senseicoder.weatherwatcher.utils.global.Constants
import java.util.Locale


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    lateinit var settings: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings = requireActivity().getSharedPreferences(Constants.SharedPrefs.Settings.SETTINGS, Context.MODE_PRIVATE)
        binding.apply {

            languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                Constants.SharedPrefs.Settings.Language.Values.let {
                    val editor = settings.edit()
                    if (checkedId == binding.settingsEnglishRadioButton.id) {
                        editor.putString(Constants.SharedPrefs.Settings.Location.LOCATION, it.EN)
                        changLanguage(it.EN)
                    } else {
                        editor.putString(Constants.SharedPrefs.Settings.Location.LOCATION, it.AR)
                        changLanguage(it.AR)
                    }
                    editor.commit()
                }
            }

            temperatureRadioGroup.setOnCheckedChangeListener{ _, checkedId ->
                Constants.SharedPrefs.Settings.Temperature.Values.let {
                    val editor = settings.edit()
                    when (checkedId) {
                        binding.settingsCelsiusRadioButton.id -> {
                            editor.putString(Constants.SharedPrefs.Settings.Temperature.TEMPERATURE, it.CELSIUS)
                        }
                        binding.settingsKelvinRadioButton.id -> {
                            editor.putString(Constants.SharedPrefs.Settings.Temperature.TEMPERATURE, it.KELVIN)
                        }
                        else -> {
                            editor.putString(Constants.SharedPrefs.Settings.Temperature.TEMPERATURE, it.FAHRENHEIT)
                        }
                    }
                    editor.commit()
                }
            }

            windSpeedRadioGroup.setOnCheckedChangeListener{ _, checkedId ->
                Constants.SharedPrefs.Settings.WindSpeed.Values.let {
                    val editor = settings.edit()
                    if (checkedId == binding.settingsMeterPerSecRadioButton.id) {
                        editor.putString(Constants.SharedPrefs.Settings.WindSpeed.WIND_SPEED, it.METER_PER_SECOND)
                    }
                    else {
                        editor.putString(Constants.SharedPrefs.Settings.WindSpeed.WIND_SPEED, it.METER_PER_SECOND)
                    }
                    editor.commit()
                }
            }

            notificationsRadioGroup.setOnCheckedChangeListener{ _, checkedId ->
                Constants.SharedPrefs.Settings.Notifications.Values.let {
                    val editor = settings.edit()
                    if (checkedId == binding.settingsEnableRadioButton.id) {
                        editor.putString(Constants.SharedPrefs.Settings.Notifications.NOTIFICAITONS, it.ENABLE)
                    }
                    else {
                        editor.putString(Constants.SharedPrefs.Settings.Notifications.NOTIFICAITONS, it.DISABLE)
                    }
                    editor.commit()
                }
            }

        }
    }

    private fun changLanguage(code: String) {
        val local = Locale(code)
        Locale.setDefault(local)
        val config = Configuration()
        config.setLocale(local)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

}