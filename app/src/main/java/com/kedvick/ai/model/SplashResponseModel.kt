package com.kedvick.ai.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SplashResponseModel(

	@field:SerializedName("youtube")
	val youtube: String? = null,

	@field:SerializedName("rewardedDisplayType")
	val rewardedDisplayType: String? = null,

	@field:SerializedName("razorpayEnable")
	val razorpayEnable: String? = null,

	@field:SerializedName("userStatus")
	val userStatus: String? = null,

	@field:SerializedName("bannerId")
	val bannerId: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("instagram")
	val instagram: String? = null,

	@field:SerializedName("linkedin")
	val linkedin: String? = null,

	@field:SerializedName("phoneNo")
	val phoneNo: String? = null,

	@field:SerializedName("appOpenAdsId")
	val appOpenAdsId: String? = null,

	@field:SerializedName("currencyExchange")
	val currencyExchange: String? = null,

	@field:SerializedName("twitter")
	val twitter: String? = null,

	@field:SerializedName("termsCondition")
	val termsCondition: String? = null,

	@field:SerializedName("bannerDisplayType")
	val bannerDisplayType: String? = null,

	@field:SerializedName("rewardedWord")
	val rewardedWord: String? = null,

	@field:SerializedName("admobAppId")
	val admobAppId: String? = null,

	@field:SerializedName("clickInterstitialAd")
	val clickInterstitialAd: String? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("appUpdate")
	val appUpdate: AppUpdate? = null,

	@field:SerializedName("rewardedId")
	val rewardedId: String? = null,

	@field:SerializedName("razorpayKeySecret")
	val razorpayKeySecret: String? = null,

	@field:SerializedName("paystackEnable")
	val paystackEnable: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("rewardedImage")
	val rewardedImage: String? = null,

	@field:SerializedName("razorpayKeyId")
	val razorpayKeyId: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("appName")
	val appName: String? = null,

	@field:SerializedName("privacyPolicy")
	val privacyPolicy: String? = null,

	@field:SerializedName("facebook")
	val facebook: String? = null,

	@field:SerializedName("paystackKeyId")
	val paystackKeyId: String? = null,

	@field:SerializedName("stripeSecretKey")
	val stripeSecretKey: String? = null,

	@field:SerializedName("refundPolicy")
	val refundPolicy: String? = null,

	@field:SerializedName("interstitialId")
	val interstitialId: String? = null,

	@field:SerializedName("stripePublishableKey")
	val stripePublishableKey: String? = null,

	@field:SerializedName("appOpensAdsEnable")
	val appOpensAdsEnable: String? = null,

	@field:SerializedName("interstitialDisplayType")
	val interstitialDisplayType: String? = null,

	@field:SerializedName("admobPublisherId")
	val admobPublisherId: String? = null,

	@field:SerializedName("stripeEnable")
	val stripeEnable: String? = null,

	@field:SerializedName("dailyLimitRewarded")
	val dailyLimitRewarded: String? = null,

	@field:SerializedName("paystackKeySecret")
	val paystackKeySecret: String? = null,

	@field:SerializedName("nativeId")
	val nativeId: String? = null,

	@field:SerializedName("nativeDisplayType")
	val nativeDisplayType: String? = null,

	@field:SerializedName("app_versions")
	val appVersionsModel: SplashAppVersionsModel? = null

): Serializable

data class AppUpdate(

	@field:SerializedName("updatePopupShow")
	val updatePopupShow: String? = null,

	@field:SerializedName("appLink")
	val appLink: String? = null,

	@field:SerializedName("cancelOption")
	val cancelOption: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("newAppVersionCode")
	val newAppVersionCode: String? = null
)

data class SplashAppVersionsModel(

	@field:SerializedName("ios")
	val ios: SplashIosModel,

	@field:SerializedName("android")
	val android: SplashAndroidModel,
)

data class SplashIosModel(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("new")
	val new: String,

	@field:SerializedName("old")
	val old: String
)

data class SplashAndroidModel(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("new")
	val new: String,

	@field:SerializedName("old")
	val old: String
)