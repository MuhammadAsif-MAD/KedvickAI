package com.kedvik.ai.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.kedvik.ai.R
import com.kedvik.ai.model.UserDataModel

object SessionAndCookies {
    private const val SESSION_AND_COOKIES = "kedvikAISessionAndCookies"
    private const val PRIVACY_SESSION_AND_COOKIES = "kedvikAIPrivacySessionAndCookies"
    private var session: SharedPreferences? = null

    private fun getSessionAndCookiesStorage(context: Context): SharedPreferences? {
        if (session == null) session = context.getSharedPreferences(SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        return session
    }

    fun clearSessionAndCookies(context: Context) {
        getSessionAndCookiesStorage(context)!!.edit().clear().apply()
    }

    // SESSION SAVE METHODS
    fun saveLoginSession(context: Context, isLoggedIn: Boolean) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putBoolean(getStringResource(context, R.string.storage_key_user_logged_in), isLoggedIn)

            .apply()
    }

    private fun saveUserId(context: Context, userId: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit().putString(getStringResource(context, R.string.storage_key_user_id), userId)
            .apply()
    }

    fun saveUserName(context: Context, userName: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit().putString(getStringResource(context, R.string.storage_key_user_name), userName)
            .apply()
    }

    fun saveUserEmail(context: Context, userEmail: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_user_email), userEmail)
            .apply()
    }

    private fun saveUserToken(context: Context, token: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_user_token), "Bearer $token")
            .apply()
    }

    fun saveMyPassword(context: Context, userPassword: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_user_password), userPassword)
            .apply()
    }
    fun saveProfileImage(context: Context, profilePicture: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_profile_picture), profilePicture)
            .apply()
    }
    fun saveAvailableImages(context: Context, availableImages: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_available_images), availableImages)
            .apply()
    }
    fun saveReferralCode(context: Context, referralCode: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_referral_code), referralCode)
            .apply()
    }
    fun saveAvailableWords(context: Context, availableWords: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit()
            .putString(getStringResource(context, R.string.storage_key_available_words), availableWords)
            .apply()
    }

    fun getDeviceId(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!.getString(getStringResource(context, R.string.storage_key_device_id), "")
    }

    fun saveMyDeviceId(context: Context, deviceId: String?) {
        getSessionAndCookiesStorage(context)!!
            .edit().putString(getStringResource(context, R.string.storage_key_device_id), deviceId)
            .apply()
    }

    // SESSION GET METHODS
    fun isUserLoggedIn(context: Context): Boolean {
        return getSessionAndCookiesStorage(context)!!
            .getBoolean(getStringResource(context, R.string.storage_key_user_logged_in), false)
    }


    fun getMyId(context: Context): String? {
        var myId = getSessionAndCookiesStorage(context)!!.getString(getStringResource(context, R.string.storage_key_user_id), "")
        if (TextUtils.isEmpty(myId)) {
            myId = "0"
        }
        return myId
    }

    fun getUserName(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!
            .getString(getStringResource(context, R.string.storage_key_user_name), "")
    }

    fun getUserEmail(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!
            .getString(getStringResource(context, R.string.storage_key_user_email), "")
    }
    fun getUserToken(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!.getString(getStringResource(context, R.string.storage_key_user_token), "")
    }

    fun getProfileImage(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!
            .getString(getStringResource(context, R.string.storage_key_profile_picture), "")
    }
    fun getAvailableWord(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!
            .getString(getStringResource(context, R.string.storage_key_available_words), "")
    }
    fun getAvailableImages(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!
            .getString(getStringResource(context, R.string.storage_key_available_images), "")
    }

    fun getReferralCode(context: Context): String? {
        return getSessionAndCookiesStorage(context)!!
            .getString(getStringResource(context, R.string.storage_key_referral_code), "")
    }

    fun saveUserProfileData(context: Context, userModel: UserDataModel) {
        saveUserId(context, userModel.userId)
        saveUserName(context, userModel.userName)
        saveUserEmail(context, userModel.emailId)
        if (userModel.token != null){
            saveUserToken(context, userModel.token)
        }
        saveProfileImage(context, userModel.image)
        saveAvailableWords(context, userModel.availableWord)
        saveAvailableImages(context, userModel.availableImage)
        saveReferralCode(context, userModel.referralCode)
    }

    fun getUserProfileData(context: Context): UserDataModel {
        val user = UserDataModel()
        user.userId = getMyId(context)
        user.userName = getUserName(context)
        user.emailId = getUserEmail(context)
        user.token = getUserToken(context)
        user.image = getProfileImage(context)
        user.availableWord = getAvailableWord(context)
        user.availableImage = getAvailableImages(context)
        user.referralCode = getReferralCode(context)

        return user
    }

    fun getStringResource(context: Context, p: Int): String {
        return context.resources.getString(p)
    }

    // Save TEMP SESSION
    fun saveString(context: Context, key: String?, value: String?) {
        val sharedPref = context.getSharedPreferences(SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String?): String? {
        val sharedPref = context.getSharedPreferences(SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        return sharedPref.getString(key, "")
    }

    // Save TEMP SESSION
    fun saveInt(context: Context, key: String, value: Int) {
        val sharedPref = context.getSharedPreferences("SESSION_AND_COOKIES", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String): Int {
        val sharedPref = context.getSharedPreferences("SESSION_AND_COOKIES", Context.MODE_PRIVATE)
        return sharedPref.getInt(key, 0)
    }

    fun saveBoolean(context: Context, key: String?, value: Boolean) {
        val sharedPref = context.getSharedPreferences(SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String?): Boolean {
        val sharedPref = context.getSharedPreferences(SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }

    fun savePopupBoolean(context: Context, key: String?, value: Boolean) {
        val sharedPref = context.getSharedPreferences("kedvikPopopSessionAndCookies", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPopupBoolean(context: Context, key: String?): Boolean {
        val sharedPref = context.getSharedPreferences("kedvikPopopSessionAndCookies", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }

    fun savePrivacyPolicyPopupValue(context: Context, key: String?, value: Boolean) {
        val sharedPref =
            context.getSharedPreferences(PRIVACY_SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPrivacyPolicyPopupValue(context: Context, key: String?): Boolean {
        val sharedPref =
            context.getSharedPreferences(PRIVACY_SESSION_AND_COOKIES, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }
}