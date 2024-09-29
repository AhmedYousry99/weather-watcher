package com.senseicoder.weatherwatcher.features.drawer.favorites

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.FragmentFavoritesBinding
import com.senseicoder.weatherwatcher.db.AppDataBase
import com.senseicoder.weatherwatcher.db.LocalDataSourceImpl
import com.senseicoder.weatherwatcher.features.drawer.favorites.adapters.FavoriteAdapter
import com.senseicoder.weatherwatcher.features.drawer.favorites.viewmodels.FavoriteViewModel
import com.senseicoder.weatherwatcher.features.drawer.favorites.viewmodels.FavoriteViewModelFactory
import com.senseicoder.weatherwatcher.features.map.MapActivity
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import com.senseicoder.weatherwatcher.models.repositories.LocalRepositoryImpl
import com.senseicoder.weatherwatcher.utils.global.Constants
import com.senseicoder.weatherwatcher.utils.global.showSnackbar
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.launch


class FavoritesFragment : Fragment() {

    lateinit var binding: FragmentFavoritesBinding

    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel

    private lateinit var settings: SharedPreferences

    val registerMapActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            val editor = settings.edit()
            val name = result.data!!.getStringExtra("${Constants.PACKAGE_NAME}.${Constants.NAME}")
            val latitude = result.data!!.getStringExtra("${Constants.PACKAGE_NAME}.${Constants.LATITUDE}")
            val longitude = result.data!!.getStringExtra("${Constants.PACKAGE_NAME}.${Constants.LONGITUDE}")
            Log.d(TAG, "registerMapActivity: putting latitude and longitude: $latitude , $longitude ; name: $name")
            editor.putString(Constants.LONGITUDE, longitude)
                .putString(Constants.LATITUDE, latitude)
                .putString(Constants.NAME, name)
            editor.commit()
            favoriteViewModel.insertFavorite(
                FavoriteDTO(
                    name!!,
                    longitude!!,
                    latitude!!,
                )
            )
        }else{
            binding.root.showSnackbar(getString(R.string.add_aborted))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = FavoriteViewModelFactory(
            LocalRepositoryImpl.getInstance(
                LocalDataSourceImpl(
                    AppDataBase.getInstance(requireContext()).weatherDAO
                )
            ),
            requireActivity().application
        )
        favoriteAdapter = FavoriteAdapter({favoriteViewModel.deleteFavorite(it.location)}){
        }

        binding.apply {
            favoriteFAB.setOnClickListener{
                registerMapActivity.launch(
                    Intent(requireContext(), MapActivity::class.java)
                )
            }
            favoriteRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            favoriteRecycler.adapter = favoriteAdapter
        }

        favoriteViewModel = ViewModelProvider(this,
            factory)[FavoriteViewModel::class]

        settings = requireActivity().getSharedPreferences(Constants.SharedPrefs.Settings.SETTINGS, Context.MODE_PRIVATE)

        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        lifecycleScope.launch {
            favoriteViewModel.favorites.collect{
                    res ->
                when(res){
                    is CurrentState.Failure -> {
                        binding.favoriteProgressBar.visibility = View.GONE
                        binding.root.showSnackbar(getString(R.string.couldnt_save_to_db))
                    }
                    is CurrentState.Loading -> {
                        binding.apply {
                            favoriteProgressBar.visibility = View.VISIBLE
                            favoriteRecycler.visibility = View.GONE
                            favoriteRecyclerEmpty.visibility = View.GONE
                        }
                    }
                    is CurrentState.Success -> {
                        binding.apply {
                            favoriteProgressBar.visibility = View.GONE
                            favoriteAdapter.submitList(res.data)
                            if(res.data.isEmpty()){
                                favoriteRecycler.visibility = View.GONE
                                favoriteRecyclerEmpty.visibility = View.VISIBLE
                            }else{
                                favoriteRecycler.visibility = View.VISIBLE
                                favoriteRecyclerEmpty.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    companion object{
        private const val TAG = "FavoritesFragment"
    }
}