package com.senseicoder.weatherwatcher.features.drawer.home.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.HomeDailyListTileBinding
import com.senseicoder.weatherwatcher.models.WeatherDTO
import com.senseicoder.weatherwatcher.utils.global.toDateTime
import com.senseicoder.weatherwatcher.utils.global.toDrawable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyDiffUtilClass: DiffUtil.ItemCallback<WeatherDTO>(){
    override fun areItemsTheSame(oldItem: WeatherDTO, newItem: WeatherDTO): Boolean {
        return oldItem.date == newItem.date && oldItem.location == newItem.location
    }

    override fun areContentsTheSame(oldItem: WeatherDTO, newItem: WeatherDTO): Boolean {
        return oldItem == newItem
    }
}

class DailyAdapter() :
    ListAdapter<WeatherDTO, DailyViewHolder>(DailyDiffUtilClass()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = HomeDailyListTileBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val weather: WeatherDTO = getItem(position)
        Log.d(TAG, "onBindViewHolder: $itemCount")
        holder.binding.apply {
            maxMinTemp.text = "${weather.maxTemperature}/${weather.minTemperature}"
            description.text = weather.description
            day.text = weather.date.toDateTime("EEEE")
            icon.setImageResource(weather.tempIcon.toDrawable())
            if(position == 0){
                card.setBackgroundResource(R.drawable.primary_gradient_circular_radius_background)
            }
        }
    }

    companion object {
        private const val TAG = "DailyAdapter"
    }


}


class DailyViewHolder(val binding: HomeDailyListTileBinding): RecyclerView.ViewHolder(binding.root)