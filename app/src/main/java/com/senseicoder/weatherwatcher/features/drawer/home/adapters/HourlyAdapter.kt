package com.senseicoder.weatherwatcher.features.drawer.home.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.HomeHourlyListTileBinding
import com.senseicoder.weatherwatcher.features.drawer.home.adapters.DailyAdapter.Companion
import com.senseicoder.weatherwatcher.models.WeatherDTO
import com.senseicoder.weatherwatcher.utils.global.toDateTime
import com.senseicoder.weatherwatcher.utils.global.toDrawable

class HourlyDiffUtilClass: DiffUtil.ItemCallback<WeatherDTO>(){
    override fun areItemsTheSame(oldItem: WeatherDTO, newItem: WeatherDTO): Boolean {
        return oldItem.date == newItem.date && oldItem.location == newItem.location
    }

    override fun areContentsTheSame(oldItem: WeatherDTO, newItem: WeatherDTO): Boolean {
        return oldItem == newItem
    }
}

class HourlyAdapter() :
    ListAdapter<WeatherDTO, HourlyViewHolder>(HourlyDiffUtilClass()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = HomeHourlyListTileBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val weather: WeatherDTO = getItem(position)
        holder.binding.apply {
            time.text = weather.date.toDateTime("h a")
            temp.text = weather.temperature
            icon.setImageResource(weather.tempIcon.toDrawable())
        }
    }

    companion object {
        private const val TAG = "HourlyAdapter"
    }
}


class HourlyViewHolder(val binding: HomeHourlyListTileBinding): RecyclerView.ViewHolder(binding.root)