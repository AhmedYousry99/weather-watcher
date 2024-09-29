package com.senseicoder.weatherwatcher.features.drawer.alerts.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.AlertListTileBinding
import com.senseicoder.weatherwatcher.models.AlertDTO

class AlertDiffUtilClass: DiffUtil.ItemCallback<AlertDTO>(){
    override fun areItemsTheSame(oldItem: AlertDTO, newItem: AlertDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlertDTO, newItem: AlertDTO): Boolean {
        return oldItem == newItem
    }
}

class AlertAdapter(private val deleteFunc: (alert:AlertDTO) -> Unit) : ListAdapter<AlertDTO, AlertViewHolder>(AlertDiffUtilClass()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = AlertListTileBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert: AlertDTO = getItem(position)
        holder.binding.run {
            alertFromTime.text = alert.fromTime
            alertToTime.text = alert.toTime
            alertToDay.text = alert.toDate
            alertFromDay.text = alert.fromDate
            alertItemMenu.setOnClickListener{
                val popup = PopupMenu(root.context.applicationContext, it)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.alert_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.menu_delete -> {
                            deleteFunc.invoke(alert)
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    companion object {
        private const val TAG = "RecyclerView.AlertAdapter"
    }
}


class AlertViewHolder(val binding: AlertListTileBinding): RecyclerView.ViewHolder(binding.root)