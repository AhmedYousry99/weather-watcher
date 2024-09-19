package com.senseicoder.emara.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkUtils{

    private var connectionState: MutableLiveData<Boolean>? = null

    fun observeNetworkConnectivity(context: Context) : LiveData<Boolean>{
        if(connectionState == null){
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val request =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()

            connectionState = MutableLiveData<Boolean>()

            connectivityManager.registerNetworkCallback(request, object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    this@NetworkUtils.connectionState.let{
                        it?.postValue(true)
                    }
                }
                override fun onLost(network: Network) {
                    this@NetworkUtils.connectionState.let{
                        it?.postValue(false)
                    }
                }
            })
            if(!isConnected(context)){
                this@NetworkUtils.connectionState.let{
                    it?.postValue(false)
                }
            }
        }
        return connectionState as LiveData<Boolean>
    }

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

