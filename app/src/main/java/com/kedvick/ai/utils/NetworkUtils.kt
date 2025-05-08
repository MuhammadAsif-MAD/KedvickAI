package com.kedvick.ai.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

/**
 * Drudots Technologies
 * Created by Muhammad Sulaiman on 4/13/2023.
 */
class NetworkUtils {

    companion object{

        @SuppressLint("ObsoleteSdkInt")
        fun isInternetAvailable(context: Activity): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
            } else {
                CustomCookieToast(context).showNoInternetToast()
                false
            }

        }
        @SuppressLint("ObsoleteSdkInt")
        fun isInternetConnectionAvailable(context: Activity): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
            } else {
                // CustomCookieToast(context).showNoInternetToast()
                false
            }

        }

        @SuppressLint("ObsoleteSdkInt")
        fun isSplashInternetAvailable(context: Activity): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
                val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
            } else false
        }
    }
}