package com.senseicoder.weatherwatcher.utils.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.FragmentLocationDialogBinding
import com.senseicoder.weatherwatcher.models.CitiesResponse

class LocationDialogFragment(var location: CitiesResponse.CityResponseItem, private val okFunc : ()-> Unit) : DialogFragment() {

    lateinit var binding: FragmentLocationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLocationDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.locationText.text = location.name
        binding.locationOkButton.setOnClickListener{
            okFunc.invoke()
        }
    }


}