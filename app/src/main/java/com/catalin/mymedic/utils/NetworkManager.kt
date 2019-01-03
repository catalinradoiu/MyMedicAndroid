package com.catalin.mymedic.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author catalinradoiu
 * @since 6/1/2018
 */

class NetworkManager {

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}