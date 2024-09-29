package com.senseicoder.weatherwatcher.features.drawer.alerts.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.repositories.LocalRepository
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val localRepository: LocalRepository, val application: Application): ViewModel() {

    private val _alerts = MutableStateFlow<CurrentState<List<AlertDTO>>>(CurrentState.Loading())
    val alert = _alerts.asStateFlow()


    fun getAlerts(){
        viewModelScope.launch(Dispatchers.IO){
            try{
                localRepository.getAlerts().collect{
                    Log.d(TAG, "getAlerts: $it")
                    _alerts.value = CurrentState.Success(it)
                }
            }catch (e: Exception){
                Log.e(TAG, "getAlerts: ", e)
                _alerts.value = CurrentState.Failure(application.getString(R.string.couldnt_get_data_from_db))
            }
        }
    }

    fun insertAlert(alertDTO: AlertDTO){
        viewModelScope.launch(Dispatchers.IO){
            try{
                Log.d(TAG, "insertAlert: ${localRepository.insertAlert(alertDTO)}")
            }catch(e: Exception){
                Log.e(TAG, "getAlerts: ", e)
                _alerts.value = CurrentState.Failure(application.getString(R.string.couldnt_save_to_db))
            }
        }
    }

    fun deleteAlert(alertId: Long){
        viewModelScope.launch(Dispatchers.IO){
            try{
                Log.d(TAG, "deleteAlert: ${localRepository.deleteAlert(alertId)}")
            }catch(e:Exception){
                Log.e(TAG, "getAlerts: ", e)
                _alerts.value = CurrentState.Failure(application.getString(R.string.couldnt_delete_from_db))
            }
        }
    }

    companion object{
        private const val TAG = "AlertViewModel"
    }

}