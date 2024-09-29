package com.senseicoder.weatherwatcher.utils.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.weatherwatcher.databinding.FragmentAlertTimePickerDialogBinding
import com.senseicoder.weatherwatcher.db.AppDataBase
import com.senseicoder.weatherwatcher.db.LocalDataSourceImpl
import com.senseicoder.weatherwatcher.features.drawer.alerts.viewmodels.AlertViewModel
import com.senseicoder.weatherwatcher.features.drawer.alerts.viewmodels.AlertViewModelFactory
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.repositories.LocalRepositoryImpl
import com.senseicoder.weatherwatcher.utils.global.toDateTime
import com.senseicoder.weatherwatcher.utils.schedulars.AlarmSchedulerImpl
import java.time.LocalDateTime
import java.util.Calendar

class AlertTimePickerDialogFragment() : DialogFragment() {

    private lateinit var binding: FragmentAlertTimePickerDialogBinding

    private lateinit var calendar : Calendar

    private lateinit var selectedDateTimeForDataFrom: LocalDateTime
    private lateinit var selectedDateTimeForDataTo: LocalDateTime


    private lateinit var alertViewModel: AlertViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlertTimePickerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        val factory = AlertViewModelFactory(
            LocalRepositoryImpl.getInstance(
                LocalDataSourceImpl(
                    AppDataBase.getInstance(requireContext()).weatherDAO
                ),
            ),
            requireActivity().application
        )
        alertViewModel = ViewModelProvider(this, factory)[AlertViewModel::class]

        initializeDates()
        initializeListeners()
    }

    private fun initializeListeners() {
        binding.apply {
            dateTimePickerToBtn.setOnClickListener{
                showDatePicker()
            }
            dateTimePickerFromBtn.setOnClickListener{
                showDatePicker()
            }
            dateTimeToCardView.setOnClickListener{
                showDatePicker(false)
            }
            dateTimeFromCardView.setOnClickListener{
                showDatePicker(false)
            }
            dateTimePickerSaveBtn.setOnClickListener{
                Log.d(TAG, "initializeListeners: clicked")
                if(selectedDateTimeForDataFrom.isBefore(LocalDateTime.now())){
                    selectedDateTimeForDataFrom = LocalDateTime.now().plusMinutes(1)
                }
                if (selectedDateTimeForDataTo.isBefore(selectedDateTimeForDataFrom)){
                    selectedDateTimeForDataTo = selectedDateTimeForDataFrom.plusMinutes(60)
                }
                alertViewModel.insertAlert(
                    AlertDTO(
                        fromTime = selectedDateTimeForDataFrom.toDateTime("h:mm a"),
                        toTime = selectedDateTimeForDataTo.toDateTime("h:mm a"),
                        fromDate = selectedDateTimeForDataFrom.toDateTime("dd, MMM yyyy"),
                        toDate = selectedDateTimeForDataTo.toDateTime("dd, MMM yyyy"),
                        fromTimeLDT = selectedDateTimeForDataFrom,
                        toTimeLDT = selectedDateTimeForDataTo,
                    )
                )
                AlarmSchedulerImpl(requireActivity().application).scheduleAlarm(
                    AlertDTO(
                        fromTime = selectedDateTimeForDataFrom.toDateTime("h:mm a"),
                        toTime = selectedDateTimeForDataTo.toDateTime("h:mm a"),
                        fromDate = selectedDateTimeForDataFrom.toDateTime("dd, MMM yyyy"),
                        toDate = selectedDateTimeForDataTo.toDateTime("dd, MMM yyyy"),
                        fromTimeLDT = selectedDateTimeForDataFrom,
                        toTimeLDT = selectedDateTimeForDataTo,
                    )
                )
                dismiss()
            }
        }
    }

    private fun showDatePicker(isFrom: Boolean = true){
        calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePicker =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                    if(isFrom){
                        selectedDateTimeForDataFrom = LocalDateTime.of(
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay,
                            selectedHour,
                            selectedMinute
                        )
                        binding.apply {
                            timePickerDialogFrom.text = selectedDateTimeForDataFrom.toDateTime("h:mm a")
                            timePickerDialogFromDate.text = selectedDateTimeForDataFrom.toDateTime("dd, MMM yyyy")
                        }
                        Log.d(TAG, "showDatePicker: $selectedDateTimeForDataFrom")
                    }else{
                        selectedDateTimeForDataTo = LocalDateTime.of(
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay,
                            selectedHour,
                            selectedMinute
                        )
                        binding.apply {
                            timePickerDialogTo.text = selectedDateTimeForDataTo.toDateTime("h:mm a")
                            timePickerDialogToDate.text = selectedDateTimeForDataTo.toDateTime("dd, MMM yyyy")
                        }
                        Log.d(TAG, "showDatePicker: $selectedDateTimeForDataTo")
                    }
                    Log.d(TAG, "abc: $selectedYear/${selectedMonth + 1}/$selectedDay $selectedHour:$selectedMinute")
                }, hour, minute, false).show()
            }, year, month, day)
        datePicker.datePicker.minDate = calendar.timeInMillis
        datePicker.show()
    }



    private fun initializeDates(){
        selectedDateTimeForDataFrom = LocalDateTime.now()
        selectedDateTimeForDataTo = LocalDateTime.now().plusMinutes(60)

        binding.apply {
            timePickerDialogFrom.text = selectedDateTimeForDataFrom.toDateTime("h:mm a")
            timePickerDialogFromDate.text = selectedDateTimeForDataFrom.toDateTime("dd, MMM yyyy")
            timePickerDialogTo.text = selectedDateTimeForDataTo.toDateTime("h:mm a")
            timePickerDialogToDate.text = selectedDateTimeForDataTo.toDateTime("dd, MMM yyyy")
        }
    }

    /*fun toDateTime(pattern: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.format.DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .format(Instant.ofEpochSecond(this).atZone(ZoneId.of("UTC")))
        } else {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            val netDate = Date(this * 1000)
            sdf.format(netDate)
        }
    }*/
    companion object {
        private const val TAG = "AlertTimePickerDialogFr"
    }
}