package com.kedvik.ai.repositories

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.shimmer.ShimmerFrameLayout
import com.kedvik.ai.api.ApiService
import com.kedvik.ai.api.Event
import com.kedvik.ai.model.BaseResponseModel
import com.kedvik.ai.model.FavouriteUnFavouriteResponseModel
import com.kedvik.ai.model.SplashResponseModel
import com.kedvik.ai.model.UserDataModel
import com.kedvik.ai.model.UserResponseModel
import com.kedvik.ai.ui.createImage.model.ArtResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.FAQsResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.ItemSupportReq
import com.kedvik.ai.ui.helpAndSupport.models.NotificationCounterResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.SendTicketMessageResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.SupportDetails
import com.kedvik.ai.ui.helpAndSupport.models.TicketCategoriesResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.TicketCreateResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.TicketDetailResponseModel
import com.kedvik.ai.ui.helpAndSupport.models.TicketsListResponseModel
import com.kedvik.ai.ui.researchCenter.model.ChatDataItemModel
import com.kedvik.ai.ui.researchCenter.model.ChatHistoryResponseModel
import com.kedvik.ai.ui.textToSpeech.model.TextToSpeechResponseModel
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.NetworkUtils
import com.kedvik.ai.utils.Utilities
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject


class ApiRepository(private val apiService: ApiService) {

    // Auth
    private val splashApiLiveData = MutableLiveData<Event<SplashResponseModel>>()
    val splashApi: LiveData<Event<SplashResponseModel>> get() = splashApiLiveData

    private val homeSplashApiLiveData = MutableLiveData<Event<SplashResponseModel>>()
    val homeSplashApi: LiveData<Event<SplashResponseModel>> get() = homeSplashApiLiveData

    private val authOtpVerifyLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val authOtpVerify: LiveData<Event<BaseResponseModel>> get() = authOtpVerifyLiveData

    private val authRegisterLiveData = MutableLiveData<Event<UserResponseModel>>()
    val authRegister: LiveData<Event<UserResponseModel>> get() = authRegisterLiveData

    private val authLoginLiveData = MutableLiveData<Event<UserResponseModel>>()
    val authLogin: LiveData<Event<UserResponseModel>> get() = authLoginLiveData

    private val authSocialLoginLiveData = MutableLiveData<Event<UserResponseModel>>()
    val authSocialLogin: LiveData<Event<UserResponseModel>> get() = authSocialLoginLiveData

    private val authUserVerifyLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val userVerify: LiveData<Event<BaseResponseModel>> get() = authUserVerifyLiveData

    private val authForgotPasswordLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val authForgotPassword: LiveData<Event<BaseResponseModel>> get() = authForgotPasswordLiveData

    private val authResetPasswordLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val authResetPassword: LiveData<Event<BaseResponseModel>> get() = authResetPasswordLiveData

    // Settings
    private val profileUpdateLiveData = MutableLiveData<Event<UserResponseModel>>()
    val profileUpdate: LiveData<Event<UserResponseModel>> get() = profileUpdateLiveData

    private val profileImageUpdateLiveData = MutableLiveData<Event<UserResponseModel>>()
    val profileImageUpdate: LiveData<Event<UserResponseModel>> get() = profileImageUpdateLiveData


    private val profilePictureRemoveApiLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val profilePictureRemove: LiveData<Event<BaseResponseModel>> get() = profilePictureRemoveApiLiveData

    private val deleteUserLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val deleteUser: LiveData<Event<BaseResponseModel>> get() = deleteUserLiveData

    private val deleteSocialAccountLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val deleteSocialAccount: LiveData<Event<BaseResponseModel>> get() = deleteSocialAccountLiveData

    private val changePasswordLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val changePassword: LiveData<Event<BaseResponseModel>> get() = changePasswordLiveData

    // Dashboard
    private val userProfileLiveData = MutableLiveData<Event<UserDataModel>>()
    val userProfile: LiveData<Event<UserDataModel>> get() = userProfileLiveData

    private val favouriteUnFavoriteTemplateLiveData =
        MutableLiveData<Event<FavouriteUnFavouriteResponseModel>>()
    val favouriteUnFavoriteTemplate: LiveData<Event<FavouriteUnFavouriteResponseModel>> get() = favouriteUnFavoriteTemplateLiveData

    private val updateDocumentLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val updateDocument: LiveData<Event<BaseResponseModel>> get() = updateDocumentLiveData


    // Research
    private val userChatLiveData = MutableLiveData<Event<MutableList<ChatDataItemModel>>>()
    val userChat: LiveData<Event<MutableList<ChatDataItemModel>>> get() = userChatLiveData

    private val chatHistoryLiveData = MutableLiveData<Event<ChatHistoryResponseModel>>()
    val chatHistory: LiveData<Event<ChatHistoryResponseModel>> get() = chatHistoryLiveData

    private val deleteChatHistoryLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val deleteChatHistory: LiveData<Event<BaseResponseModel>> get() = deleteChatHistoryLiveData

    private val createPaymentApiLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val createPayment: LiveData<Event<BaseResponseModel>> get() = createPaymentApiLiveData

    private val contactUsLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val contactUs: LiveData<Event<BaseResponseModel>> get() = contactUsLiveData

    private val deleteDocumentLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val deleteDocument: LiveData<Event<BaseResponseModel>> get() = deleteDocumentLiveData

    // Help And Support
    private val ticketCreateApiLiveData = MutableLiveData<Event<TicketCreateResponseModel>>()
    val ticketCreate: LiveData<Event<TicketCreateResponseModel>> get() = ticketCreateApiLiveData

    private val ticketCategoriesApiLiveData =
        MutableLiveData<Event<TicketCategoriesResponseModel>>()
    val ticketCategories: LiveData<Event<TicketCategoriesResponseModel>> get() = ticketCategoriesApiLiveData

    private val notificationCounterApiLiveData =
        MutableLiveData<Event<NotificationCounterResponseModel>>()
    val notificationCounter: LiveData<Event<NotificationCounterResponseModel>> get() = notificationCounterApiLiveData

    private val ticketsOpenedApiLiveData = MutableLiveData<Event<TicketsListResponseModel>>()
    val ticketsOpenedList: LiveData<Event<TicketsListResponseModel>> get() = ticketsOpenedApiLiveData

    private val ticketsClosedApiLiveData = MutableLiveData<Event<TicketsListResponseModel>>()
    val ticketsClosedList: LiveData<Event<TicketsListResponseModel>> get() = ticketsClosedApiLiveData

    private val ticketDetailApiLiveData = MutableLiveData<Event<TicketDetailResponseModel>>()
    val ticketDetail: LiveData<Event<TicketDetailResponseModel>> get() = ticketDetailApiLiveData

    private val ticketsCloseApiLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val ticketClose: LiveData<Event<BaseResponseModel>> get() = ticketsCloseApiLiveData

    private val sendTicketMessageApiLiveData =
        MutableLiveData<Event<SendTicketMessageResponseModel>>()
    val sendTicketMessage: LiveData<Event<SendTicketMessageResponseModel>> get() = sendTicketMessageApiLiveData

    // Support Request
    private val createSupportApiLiveData = MutableLiveData<Event<ItemSupportReq>>()
    val createSupportRequest: LiveData<Event<ItemSupportReq>> get() = createSupportApiLiveData

    private val supportRequestListApiLiveData =
        MutableLiveData<Event<MutableList<ItemSupportReq>>>()
    val supportRequestList: LiveData<Event<MutableList<ItemSupportReq>>> get() = supportRequestListApiLiveData

    private val supportDetailsApiLiveData = MutableLiveData<Event<SupportDetails>>()
    val supportDetailsApi: LiveData<Event<SupportDetails>> get() = supportDetailsApiLiveData

    private val sendToSupportImageMessageApiLiveData = MutableLiveData<Event<SupportDetails>>()
    val sendToSupportImageMessage: LiveData<Event<SupportDetails>> get() = sendToSupportImageMessageApiLiveData

    private val sendToSupportTextMessageApiLiveData = MutableLiveData<Event<SupportDetails>>()
    val sendToSupportTextMessage: LiveData<Event<SupportDetails>> get() = sendToSupportTextMessageApiLiveData

    private val listOfFaqsApiLiveData = MutableLiveData<Event<FAQsResponseModel?>>()
    val listOfFaqs: LiveData<Event<FAQsResponseModel?>> get() = listOfFaqsApiLiveData

    // Create Image
    private val createMagicArtApiLiveData = MutableLiveData<Event<MutableList<ArtResponseModel>>>()
    val createMagicArt: LiveData<Event<MutableList<ArtResponseModel>>> get() = createMagicArtApiLiveData

    private val getMagicArtApiLiveData = MutableLiveData<Event<MutableList<ArtResponseModel>>>()
    val getMagicArt: LiveData<Event<MutableList<ArtResponseModel>>> get() = getMagicArtApiLiveData

    private val deleteMagicArtApiLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val deleteMagicArt: LiveData<Event<BaseResponseModel>> get() = deleteMagicArtApiLiveData

    // Text To Speech
    private val textToSpeechListApiLiveData =
        MutableLiveData<Event<MutableList<TextToSpeechResponseModel>>>()
    val textToSpeechList: LiveData<Event<MutableList<TextToSpeechResponseModel>>> get() = textToSpeechListApiLiveData

    private val createTextToSpeechApiLiveData = MutableLiveData<Event<TextToSpeechResponseModel>>()
    val createTextToSpeech: LiveData<Event<TextToSpeechResponseModel>> get() = createTextToSpeechApiLiveData

    private val deleteTextToSpeechApiLiveData = MutableLiveData<Event<BaseResponseModel>>()
    val deleteTextToSpeech: LiveData<Event<BaseResponseModel>> get() = deleteTextToSpeechApiLiveData

    // Auth
    suspend fun splashApi(context: Activity, myId: String) {
        val result = apiService.splashApi(myId)
        if (result.body() != null) {
            splashApiLiveData.postValue(Event(result.body()!!))
        } else {
            // Utilities.somethingWentWrong(context)
        }
    }

    suspend fun homeSplashApi(context: Activity, myId: String) {
        val result = apiService.splashApi(myId)
        if (result.body() != null) {
            homeSplashApiLiveData.postValue(Event(result.body()!!))
        } else {
            // Utilities.somethingWentWrong(context)
        }
    }

    suspend fun authOtpVerify(
        context: Activity,
        email: String,
        code: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.authOtpVerify(email, code)
            if (result.body() != null) {
                authOtpVerifyLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun authRegister(
        context: Activity,
        name: String,
        email: String,
        password: String,
        deviceName: String,
        deviceId: String,
        timezone: String,
        fcmToken: String,
        referralCode: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.authRegister(
                name,
                email,
                password,
                deviceName,
                deviceId,
                timezone,
                fcmToken,
                referralCode
            )
            if (result.body() != null) {
                // val model = UserResponseModel(name, email, password, referralCode)
                authRegisterLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun authLogin(
        context: Activity,
        email: String,
        password: String,
        deviceName: String,
        deviceId: String,
        timezone: String,
        fcmToken: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result =
                apiService.authLogin(email, password, deviceName, deviceId, timezone, fcmToken)
            if (result.body() != null) {
                authLoginLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                if (result.code() == 404) {
                    CustomCookieToast(context).showFailureToast("Invalid Credentials")
                } else {
                    Utilities.somethingWentWrong(context)
                }
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun authSocialLogin(
        context: Activity,
        name: String,
        email: String,
        platform: String,
        platformId: String,
        deviceName: String,
        deviceId: String,
        timezone: String,
        fcmToken: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.authSocialLogin(
                name,
                email,
                platform,
                platformId,
                deviceName,
                deviceId,
                timezone,
                fcmToken
            )
            if (result.body() != null) {
                authSocialLoginLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                val errorResponse = result.errorBody()?.string()
                if (!errorResponse.isNullOrEmpty()) {
                    try {
                        // Try to parse the response as JSON
                        val errorMessage =
                            JSONObject(errorResponse).optString("message", "Something went wrong")
                        CustomCookieToast(context).showFailureToast(errorMessage)
                    } catch (e: JSONException) {
                        // If JSON parsing fails, show a generic error message
                        CustomCookieToast(context).showFailureToast("Error: $errorResponse")
                        Log.d("ERROR_RESPONSE_MESSAGE", "$errorResponse")
                    }
                } else {
                    Utilities.somethingWentWrong(context)
                }
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun userVerify(context: Activity, email: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.userVerify(email)
            if (result.body() != null) {
                authUserVerifyLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE

                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun authForgotPassword(context: Activity, email: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.authForgotPassword(email)
            if (result.body() != null) {
                authForgotPasswordLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun authResetPassword(
        context: Activity,
        email: String,
        password: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.authResetPassword(email, password)
            if (result.body() != null) {
                authResetPasswordLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    // Settings
    suspend fun profileUpdate(
        context: Activity,
        userId: String,
        name: String,
        email: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.profileUpdate(userId, name, email)
            if (result.body() != null) {
                profileUpdateLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun profileImageUpdate(
        context: Activity,
        userId: RequestBody,
        name: RequestBody,
        email: RequestBody,
        imageFile: MultipartBody.Part,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.profileImageUpdate(userId, name, email, imageFile)
            if (result.body() != null) {
                profileImageUpdateLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun profilePictureRemove(
        context: Activity,
        myId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.profilePictureRemove(myId)
            if (result.body() != null) {
                profilePictureRemoveApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun deleteUser(
        context: Activity,
        token: String,
        password: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.deleteUser(token, password)
            if (result.body() != null) {
                deleteUserLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun deleteSocialAccount(
        context: Activity,
        token: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.deleteSocialAccount(token)
            if (result.body() != null) {
                deleteSocialAccountLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun changePassword(
        context: Activity,
        userId: String,
        oldPassword: String,
        password: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.changePassword(userId, oldPassword, password)
            if (result.body() != null) {
                changePasswordLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    // Dashboard
    suspend fun userProfile(context: Activity, userId: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.userProfile(userId)
            if (result.body() != null) {
                userProfileLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun favouriteUnFavoriteTemplate(
        context: Activity,
        userId: String,
        templateId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.favouriteUnFavoriteTemplate(userId, templateId)
            if (result.body() != null) {
                favouriteUnFavoriteTemplateLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun updateDocument(
        context: Activity,
        documentId: String,
        title: String,
        text: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.updateDocument(documentId, title, text)
            if (result.body() != null) {
                updateDocumentLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }


    /**
     * ARTICLE & BLOG CATEGORY END
     */

    /**
     * GENERAL WRITING CATEGORY START
     */


    /**
     * GENERAL WRITING CATEGORY END
     */

    /**
     * ASO CATEGORY START
     */
    /**
     * ASO CATEGORY END
     */

    /**
     * EMAIL WRITING CATEGORY START
     */

    // Research
    suspend fun userChatTextMessage(
        context: Activity,
        token: String,
        chatId: String,
        message: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.userChatTextMessage(token, chatId, message)
            val chatList = result.body() ?: mutableListOf()
            if (result.body() != null) {
                userChatLiveData.postValue(Event(chatList))
            } else {
                layoutLoader.visibility = View.GONE
                if (result.code().toString() == "404") {
                    userChatLiveData.postValue(Event(chatList))
                    // Utilities.somethingWentWrong(context)
                } else {
                    Utilities.somethingWentWrong(context)
                }
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun userChatMediaMessage(
        context: Activity,
        token: String,
        chatId: RequestBody,
        filePath: MultipartBody.Part,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.userChatMediaMessage(token, chatId, filePath)
            if (result.body() != null) {
                userChatLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun userChatPdfMessage(
        context: Activity,
        token: String,
        chatId: RequestBody,
        filePath: MultipartBody.Part,
        pdfText: RequestBody,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.userChatPdfMessage(token, chatId, filePath, pdfText)
            if (result.body() != null) {
                userChatLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun userChatTextAndMediaMessage(
        context: Activity, token: String, chatId: RequestBody, message: RequestBody,
        filePath: MultipartBody.Part, layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.userChatTextAndMediaMessage(token, chatId, message, filePath)
            if (result.body() != null) {
                userChatLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun userChatTextAndPdfMessage(
        context: Activity, token: String, chatId: RequestBody, message: RequestBody,
        filePath: MultipartBody.Part, pdfText: RequestBody, layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result =
                apiService.userChatTextAndPdfMessage(token, chatId, message, filePath, pdfText)
            if (result.body() != null) {
                userChatLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun chatHistory(
        context: Activity,
        token: String,
        userId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.chatHistory(token, userId)
            if (result.body() != null) {
                chatHistoryLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun deleteChatHistory(
        context: Activity,
        token: String,
        chatId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.deleteChatHistory(token, chatId)
            if (result.body() != null) {
                deleteChatHistoryLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    // Generated Document

    suspend fun ticketCreate(
        context: Activity,
        token: String,
        categoryId: String,
        message: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.ticketCreate(token, categoryId, message)
            if (result.body() != null) {
                ticketCreateApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun ticketCategories(context: Activity, token: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.ticketCategories(token)
            if (result.body() != null) {
                ticketCategoriesApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun notificationCounter(
        context: Activity,
        token: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.notificationCounter(token)
            if (result.body() != null) {
                notificationCounterApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun ticketsOpenedList(context: Activity, token: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.ticketsOpenedList(token)
            if (result.body() != null) {
                ticketsOpenedApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun ticketsClosedList(context: Activity, token: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.ticketsClosedList(token)
            if (result.body() != null) {
                ticketsClosedApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun ticketDetail(
        context: Activity,
        token: String,
        ticketId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.ticketDetail(token, ticketId)
            if (result.body() != null) {
                ticketDetailApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun ticketClose(
        context: Activity,
        token: String,
        ticketId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.ticketsClose(token, ticketId)
            if (result.body() != null) {
                ticketsCloseApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun sendTicketMessage(
        context: Activity,
        token: String,
        ticketId: String,
        type: String,
        message: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.sendTicketMessage(token, ticketId, type, message)
            if (result.body() != null) {
                sendTicketMessageApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    // Support Request
    suspend fun createSupport(
        context: Activity,
        imageName: RequestBody,
        imageFile: MultipartBody.Part,
        userId: RequestBody,
        subject: RequestBody,
        category: RequestBody,
        priority: RequestBody,
        message: RequestBody,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.createSupport(
                imageName,
                imageFile,
                userId,
                subject,
                category,
                priority,
                message
            )
            if (result.body() != null) {
                createSupportApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun createSupport(
        context: Activity,
        userId: String,
        subject: String,
        category: String,
        priority: String,
        message: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.createSupport(userId, subject, category, priority, message)
            if (result.body() != null) {
                createSupportApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun supportRequestList(
        context: Activity,
        userId: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.getSupportRequestList(userId)
            if (result.body() != null) {
                supportRequestListApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun supportDetails(context: Activity, ticketId: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.getSupportDetails(ticketId)
            if (result.body() != null) {
                supportDetailsApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun sendToSupportImageMessage(
        context: Activity,
        imageName: RequestBody,
        imageFile: MultipartBody.Part,
        userId: RequestBody,
        message: RequestBody,
        ticketId: RequestBody,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.sendToSupportImageMessage(
                imageName,
                imageFile,
                userId,
                message,
                ticketId
            )
            if (result.body() != null) {
                sendToSupportImageMessageApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun sendToSupportTextMessage(
        context: Activity,
        userId: String,
        message: String,
        ticketId: String,
        pbSend: ProgressBar,
        btnSend: ImageView,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.sendToSupportTextMessage(userId, message, ticketId)
            if (result.body() != null) {
                sendToSupportTextMessageApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
                pbSend.visibility = View.GONE
                btnSend.visibility = View.VISIBLE
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun listOfFaqs(context: Activity, layoutLoader: RelativeLayout) {
        layoutLoader.visibility = View.VISIBLE
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.listOfFaqs()
            if (result.body() != null) {
                listOfFaqsApiLiveData.postValue(Event(result.body()))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }


    // Create Image
    suspend fun createMagicArt(
        context: Activity,
        userId: String,
        description: String,
        style: String,
        medium: String,
        noOfImages: String,
        resolution: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.createMagicArt(
                userId,
                description,
                style,
                medium,
                noOfImages,
                resolution
            )
            if (result.body() != null && result.isSuccessful) {
                createMagicArtApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                CustomCookieToast(context).showFailureToast(result.message())
                // Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun getMagicArt(context: Activity, userId: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.getMagicArt(userId)
            if (result.body() != null && result.isSuccessful) {
                getMagicArtApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun deleteMagicArt(context: Activity, artId: String, layoutLoader: RelativeLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.deleteArt(artId)
            if (result.body() != null && result.isSuccessful) {
                deleteMagicArtApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    // Text To Speech
    suspend fun createTextToSpeech(
        context: Activity,
        userId: String,
        documentName: String,
        text: String,
        gender: String,
        language: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.createTextToSpeech(userId, documentName, text, gender, language)
            if (result.body() != null && result.isSuccessful) {
                createTextToSpeechApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun textToSpeechList(context: Activity, userId: String, layoutLoader: ShimmerFrameLayout) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.textToSpeechList(userId)
            if (result.body() != null && result.isSuccessful) {
                textToSpeechListApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun deleteTextToSpeechList(
        context: Activity,
        token: String?,
        textToSpeechId: String?,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.deleteTextToSpeechList(
                token,
                textToSpeechId
            )
            if (result.body() != null) {
                deleteTextToSpeechApiLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

    suspend fun contactUs(
        context: Activity,
        name: String,
        email: String,
        message: String,
        subject: String,
        layoutLoader: RelativeLayout
    ) {
        if (NetworkUtils.isInternetAvailable(context)) {
            val result = apiService.contactUs(name, email, message, subject)
            if (result.body() != null && result.isSuccessful) {
                // val model = UserResponseModel(name, email, password, referralCode)
                contactUsLiveData.postValue(Event(result.body()!!))
            } else {
                layoutLoader.visibility = View.GONE
                Utilities.somethingWentWrong(context)
            }
        } else {
            layoutLoader.visibility = View.GONE
        }
    }

}