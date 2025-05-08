package com.kedvick.ai.api

import android.content.Context
import android.system.ErrnoException
import android.util.Log
import android.widget.Toast
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.EOFException
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLProtocolException


object RetrofitBuilder {
    private var retrofit: Retrofit? = null

    private const val BASE_URL = "https://kedvik.com/boboshantimith@1/"
    const val MEDIA_URL = "https://kedvik.com/"
    const val MEDIA_AUDIO_URL = "https://kedvik.com/uploads/"

    fun getRetrofit(context: Context): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        try {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
            }
        } catch (e: SocketTimeoutException) {
            showToast(context, "Request Timed Out. Please check your internet connection.")
            Log.e("RetrofitBuilder", "Socket Timeout Exception", e)
        } catch (e: UnknownHostException) {
            showToast(context, "Unknown Host. Please check your internet connection.")
            Log.e("RetrofitBuilder", "Unknown Host Exception", e)
        } catch (e: ProtocolException) {
            showToast(context, "Protocol Exception. Please try again.")
            Log.e("RetrofitBuilder", "Protocol Exception", e)
        } catch (e: IOException) {
            showToast(context, "Network Error. Please try again later.")
            Log.e("RetrofitBuilder", "IO Exception", e)
        } catch (e: Exception) {
            showToast(context, "An unexpected error occurred. Please try again.")
            Log.e("RetrofitBuilder", "Exception", e)
        }catch (e: ErrnoException) {
            showToast(context, "Network is unreachable. Please check your internet connection.")
            Log.e("RetrofitBuilder", "ErrnoException", e)
        } catch (e: SocketException) {
            showToast(context, "Connection aborted. Please check your internet connection.")
            Log.e("RetrofitBuilder", "Socket Exception", e)
        } catch (e: EOFException) {
            showToast(context, "Connection closed unexpectedly. Please try again.")
            Log.e("RetrofitBuilder", "EOF Exception", e)
        } catch (e: ProtocolException) {
            showToast(context, "Protocol Exception. Please try again.")
            Log.e("RetrofitBuilder", "Protocol Exception", e)
        } catch (e: ErrnoException) {
            showToast(context, "Network is unreachable. Please check your internet connection.")
            Log.e("RetrofitBuilder", "ErrnoException", e)
        } catch (e: NoRouteToHostException) {
            Log.e("RetrofitBuilder", "NoRouteToHostException", e)
            showToast(context, "Network error: No route to host. Please try again.")
        } catch (e: SSLProtocolException) {
            Log.e("RetrofitBuilder", "SSLProtocolException", e)
            showToast(context, "SSL handshake failed. Please try again.")
        } catch (e: EOFException) {
            Log.e("RetrofitBuilder", "EOFException", e)
            showToast(context, "Unexpected end of input stream. Please try again.")
        }

        return retrofit!!
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}