package com.kedvik.ai.utils

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent

/**
 * Drudots Technologies
 * Created by Muhammad Sulaiman on 07/31/2024.
 */

object AnalyticsUtil {
    private lateinit var analytics: FirebaseAnalytics

    fun logEvent(context: Context, eventName: String, screenName: String) {
        // Obtain the FirebaseAnalytics instance.
        analytics = com.google.firebase.ktx.Firebase.analytics

        analytics.logEvent(eventName) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, context.javaClass.simpleName)
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }
}

