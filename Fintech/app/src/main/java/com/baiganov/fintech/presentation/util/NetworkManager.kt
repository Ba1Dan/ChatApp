package com.baiganov.fintech.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class NetworkManager @Inject constructor(context: Context) : ConnectivityManager.NetworkCallback() {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val isNetworkAvailable = MutableStateFlow(false)

    private val _isConnectedNetwork = MutableLiveData<Boolean>()
    val isConnectedNetwork: LiveData<Boolean>
        get() = _isConnectedNetwork

    fun isConnected(): MutableStateFlow<Boolean> {
        var isConnected = false
        connectivityManager.allNetworks.forEach { network ->
            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            networkCapability?.let {
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    isConnected = true
                    return@forEach
                }
            }
        }

        isNetworkAvailable.value = isConnected
        return isNetworkAvailable
    }

    fun registerCallback(){
        val request = connectionRequest()
        connectivityManager.registerNetworkCallback(request, this)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    private fun connectionRequest(): NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isNetworkAvailable.value = true
        _isConnectedNetwork.postValue(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isNetworkAvailable.value = false
        _isConnectedNetwork.postValue(false)
    }
}