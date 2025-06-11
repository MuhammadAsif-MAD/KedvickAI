package com.kedvik.ai.api

import android.app.Application
import android.system.ErrnoException
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.kedvik.ai.repositories.ApiRepository
import dagger.hilt.android.HiltAndroidApp
import java.io.EOFException
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLProtocolException


@HiltAndroidApp
class ApplicationClass : Application(), LifecycleObserver {

    companion object {
        lateinit var app: ApplicationClass

        fun get(): ApplicationClass {
            return app
        }
    }

    // Repository declare
    lateinit var apiRepository: ApiRepository

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
       FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this)

        // Initialize Repository
        initialize()

        app = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        //for Glide image caching
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)
    }

    private fun initialize() {
        try {
            val apiService = RetrofitBuilder.getRetrofit(applicationContext).create(ApiService::class.java)
            apiRepository = ApiRepository(apiService)
        } catch (e: SocketTimeoutException) {
            Log.e("RetrofitBuilder", "Socket Timeout Exception", e)
        } catch (e: UnknownHostException) {
            Log.e("RetrofitBuilder", "Unknown Host Exception", e)
        } catch (e: ProtocolException) {
            Log.e("RetrofitBuilder", "Protocol Exception", e)
        } catch (e: IOException) {
            Log.e("RetrofitBuilder", "IO Exception", e)
        } catch (e: Exception) {
            Log.e("RetrofitBuilder", "Exception", e)
        }catch (e: ErrnoException) {
            Log.e("RetrofitBuilder", "ErrnoException", e)
        } catch (e: SocketException) {
            Log.e("RetrofitBuilder", "Socket Exception", e)
        } catch (e: EOFException) {
            Log.e("RetrofitBuilder", "EOF Exception", e)
        } catch (e: ProtocolException) {
            Log.e("RetrofitBuilder", "Protocol Exception", e)
        } catch (e: ErrnoException) {
            Log.e("RetrofitBuilder", "ErrnoException", e)
        }catch (e: NoRouteToHostException) {
            Log.e("RetrofitBuilder", "NoRouteToHostException", e)
        } catch (e: SSLProtocolException) {
            Log.e("RetrofitBuilder", "SSLProtocolException", e)
        } catch (e: EOFException) {
            Log.e("RetrofitBuilder", "EOFException", e)
        }
    }
}