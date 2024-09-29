package com.senseicoder.weatherwatcher.features.drawer.favorites.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.FavoriteListTileBinding
import com.senseicoder.weatherwatcher.models.FavoriteDTO


class FavoriteDiffUtilClass: DiffUtil.ItemCallback<FavoriteDTO>(){
    override fun areItemsTheSame(oldItem: FavoriteDTO, newItem: FavoriteDTO): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FavoriteDTO, newItem: FavoriteDTO): Boolean {
        return oldItem == newItem
    }
}

class FavoriteAdapter(private val deleteFunc: (favorite: FavoriteDTO) -> Unit, private val onClickFunc: (favorite: FavoriteDTO) -> Unit) : ListAdapter<FavoriteDTO, FavoriteViewHolder>(FavoriteDiffUtilClass()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = FavoriteListTileBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite: FavoriteDTO = getItem(position)
        holder.binding.run {
            favoriteLocation.text = favorite.location
            favoriteItemMenu.setOnClickListener{
                val popup = PopupMenu(root.context.applicationContext, it)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.favorite_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.menu_delete -> {
                            deleteFunc.invoke(favorite)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
            favoriteTileCardView.setOnClickListener{

            }
        }
    }

    companion object {
        private const val TAG = "RecyclerView.FavoriteAdapter"
    }
}


class FavoriteViewHolder(val binding: FavoriteListTileBinding): RecyclerView.ViewHolder(binding.root)