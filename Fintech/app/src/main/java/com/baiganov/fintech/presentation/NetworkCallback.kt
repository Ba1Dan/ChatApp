package com.baiganov.fintech.presentation

import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.lifecycle.MutableLiveData

class NetworkCallback : ConnectivityManager.NetworkCallback() {

    val result = MutableLiveData<Boolean>()

    override fun onAvailable(network: Network) {
        Log.d("callback", "available")
        result.postValue(true)
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        Log.d("callback", "Losing")
    }

    override fun onLost(network: Network) {
        Log.d("callback", "onLost")
        result.postValue(false)
    }
}