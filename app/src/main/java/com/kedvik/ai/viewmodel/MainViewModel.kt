package com.kedvik.ai.viewmodel

import android.app.Activity
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.shimmer.ShimmerFrameLayout
import com.kedvik.ai.api.Event
import com.kedvik.ai.model.BaseResponseModel
import com.kedvik.ai.model.SplashResponseModel
import com.kedvik.ai.model.UserDataModel
import com.kedvik.ai.model.UserResponseModel
import com.kedvik.ai.repositories.ApiRepository
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
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody


class MainViewModel(private val repository: ApiRepository) : ViewModel() {

    // Auth
    val splashApi: LiveData<Event<SplashResponseModel>> get() = repository.splashApi
    val homeSplashApi: LiveData<Event<SplashResponseModel>> get() = repository.homeSplashApi
    val authOtpVerify: LiveData<Event<BaseResponseModel>> get() = repository.authOtpVerify
    val authRegister: LiveData<Event<UserResponseModel>> get() = repository.authRegister
    val authLogin: LiveData<Event<UserResponseModel>> get() = repository.authLogin
    val authSocialLogin: LiveData<Event<UserResponseModel>> get() = repository.authSocialLogin
    val authUserVerify: LiveData<Event<BaseResponseModel>> get() = repository.userVerify
    val authForgotPassword: LiveData<Event<BaseResponseModel>> get() = repository.authForgotPassword
    val authResetPassword: LiveData<Event<BaseResponseModel>> get() = repository.authResetPassword

    // Settings
    val profileUpdate: LiveData<Event<UserResponseModel>> get() = repository.profileUpdate
    val profileImageUpdate: LiveData<Event<UserResponseModel>> get() = repository.profileImageUpdate
    val profilePictureRemove: LiveData<Event<BaseResponseModel>> get() = repository.profilePictureRemove
    val deleteUser: LiveData<Event<BaseResponseModel>> get() = repository.deleteUser
    val deleteSocialAccount: LiveData<Event<BaseResponseModel>> get() = repository.deleteSocialAccount
    val changePassword: LiveData<Event<BaseResponseModel>> get() = repository.changePassword

    // Dashboard
    val userProfile: LiveData<Event<UserDataModel>> get() = repository.userProfile

    // contact us
    val contactUs: LiveData<Event<BaseResponseModel>> get() = repository.contactUs

    // Research
    val chatData: LiveData<Event<MutableList<ChatDataItemModel>>> get() = repository.userChat
    val chatHistory: LiveData<Event<ChatHistoryResponseModel>> get() = repository.chatHistory
    val deleteChatHistory: LiveData<Event<BaseResponseModel>> get() = repository.deleteChatHistory

    // Help And Support
    val ticketCreate: LiveData<Event<TicketCreateResponseModel>> get() = repository.ticketCreate
    val ticketCategories: LiveData<Event<TicketCategoriesResponseModel>> get() = repository.ticketCategories
    val notificationCounts: LiveData<Event<NotificationCounterResponseModel>> get() = repository.notificationCounter
    val ticketsOpenedList: LiveData<Event<TicketsListResponseModel>> get() = repository.ticketsOpenedList
    val ticketsClosedList: LiveData<Event<TicketsListResponseModel>> get() = repository.ticketsClosedList
    val ticketDetail: LiveData<Event<TicketDetailResponseModel>> get() = repository.ticketDetail
    val ticketClose: LiveData<Event<BaseResponseModel>> get() = repository.ticketClose
    val sendTicketMessage: LiveData<Event<SendTicketMessageResponseModel>> get() = repository.sendTicketMessage

    // Support Request
    val createSupportRequest: LiveData<Event<ItemSupportReq>> get() = repository.createSupportRequest
    val supportRequestList: LiveData<Event<MutableList<ItemSupportReq>>> get() = repository.supportRequestList
    val supportDetailsApi: LiveData<Event<SupportDetails>> get() = repository.supportDetailsApi
    val sendToSupportImageMessage: LiveData<Event<SupportDetails>> get() = repository.sendToSupportImageMessage
    val sendToSupportTextMessage: LiveData<Event<SupportDetails>> get() = repository.sendToSupportTextMessage
    // Faq
    val listOfFaqs: LiveData<Event<FAQsResponseModel?>> get() = repository.listOfFaqs

    // Create Image
    val createMagicArt: LiveData<Event<MutableList<ArtResponseModel>>> get() = repository.createMagicArt
    val getMagicArt: LiveData<Event<MutableList<ArtResponseModel>>> get() = repository.getMagicArt
    val deleteMagicArt: LiveData<Event<BaseResponseModel>> get() = repository.deleteMagicArt

    // Text To Speech
    val createTextToSpeech: LiveData<Event<TextToSpeechResponseModel>> get() = repository.createTextToSpeech
    val textToSpeechList: LiveData<Event<MutableList<TextToSpeechResponseModel>>> get() = repository.textToSpeechList
    val deleteTextToSpeechList: LiveData<Event<BaseResponseModel>> get() = repository.deleteTextToSpeech

    // Auth
    fun callSplashApi(context: Activity, myId: String) {
        viewModelScope.launch {
            repository.splashApi(context, myId)
        }
    }
    fun callHomeSplashApi(context: Activity, myId: String) {
        viewModelScope.launch {
            repository.homeSplashApi(context, myId)
        }
    }
    fun callAuthOtpVerifyApi(context: Activity, email: String, code: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.authOtpVerify(context, email, code, layoutLoader)
        }
    }

    fun callAuthRegisterApi(context: Activity, name: String, email: String, password: String, deviceName: String, deviceId: String, timezone: String, fcmToken: String, referralCode: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.authRegister(context, name, email, password, deviceName, deviceId, timezone, fcmToken, referralCode, layoutLoader)
        }
    }

    fun callLoginApi(context: Activity, email: String, password: String, deviceName: String, deviceId: String, timezone: String, fcmToken: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.authLogin(context, email, password, deviceName, deviceId, timezone, fcmToken, layoutLoader)
        }
    }

    fun callSocialLoginApi(context: Activity, name: String, email: String, platform: String, platformId: String, deviceName: String, deviceId: String, timezone: String, fcmToken: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.authSocialLogin(context, name, email, platform, platformId, deviceName, deviceId, timezone, fcmToken, layoutLoader)
        }
    }
    fun callForgotPasswordApi(context: Activity, email: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.authForgotPassword(context, email, layoutLoader)
        }
    }
    fun callUserVerifyApi(context: Activity, email: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userVerify(context, email, layoutLoader)
        }
    }
    fun callResetPasswordApi(context: Activity, email: String, password: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.authResetPassword(context, email, password, layoutLoader)
        }
    }

    // Settings
    fun callProfileUpdateApi(context: Activity, userId: String, name: String, email: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.profileUpdate(context, userId, name, email, layoutLoader)
        }
    }
    fun callProfileImageUpdateApi(context: Activity, userId: RequestBody, name: RequestBody, email: RequestBody, imageFile: MultipartBody.Part, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.profileImageUpdate(context, userId, name, email, imageFile, layoutLoader)
        }
    }
    fun callProfilePictureRemoveApi(context: Activity, myId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.profilePictureRemove(context, myId, layoutLoader)
        }
    }
    fun callDeleteUserApi(context: Activity, token: String, password: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.deleteUser(context, token, password, layoutLoader)
        }
    }

    fun callDeleteSocialAccountApi(context: Activity, token: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.deleteSocialAccount(context, token, layoutLoader)
        }
    }
    fun callChangePasswordApi(context: Activity, userId: String, oldPassword: String, password: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.changePassword(context, userId, oldPassword, password, layoutLoader)
        }
    }

    // Dashboard
    fun callUserProfileApi(context: Activity, userId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userProfile(context, userId, layoutLoader)
        }
    }

    fun callFavouriteUnFavoriteTemplateApi(context: Activity, userId: String, templateId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.favouriteUnFavoriteTemplate(context, userId, templateId, layoutLoader)
        }
    }
    fun callUpdateDocumentApi(context: Activity, documentId: String, title: String, text: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.updateDocument(context, documentId, title, text, layoutLoader)
        }
    }


    // Research
    fun callUserChatTextMessageApi(context: Activity, token: String, chatId: String, message: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userChatTextMessage(context, token, chatId, message, layoutLoader)
        }
    }
    fun callUserChatMediaMessageApi(context: Activity, token: String, chatId: RequestBody, filePath: MultipartBody.Part, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userChatMediaMessage(context, token, chatId, filePath, layoutLoader)
        }
    }
    fun callUserChatPdfMessageApi(context: Activity, token: String, chatId: RequestBody, filePath: MultipartBody.Part, pdfText: RequestBody,layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userChatPdfMessage(context, token, chatId, filePath, pdfText, layoutLoader)
        }
    }
    fun callUserChatTextAndMediaMessageApi(context: Activity, token: String, chatId: RequestBody, message: RequestBody, filePath: MultipartBody.Part, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userChatTextAndMediaMessage(context, token, chatId, message, filePath, layoutLoader)
        }
    }
    fun callUserChatTextAndPdfMessageApi(context: Activity, token: String, chatId: RequestBody, message: RequestBody, filePath: MultipartBody.Part,
        pdfText: RequestBody, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.userChatTextAndPdfMessage(context, token, chatId, message, filePath, pdfText, layoutLoader)
        }
    }

    fun callChatHistoryApi(context: Activity, token: String, userId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.chatHistory(context, token, userId, layoutLoader)
        }
    }

    fun callDeleteChatHistoryApi(context: Activity, token: String, chatId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.deleteChatHistory(context, token, chatId, layoutLoader)
        }

    }
    fun callContactUsApi(context: Activity, name: String, email: String, message: String, subject: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.contactUs(context, name, email, message, subject, layoutLoader)
        }
    }


    // Help And Support
    fun callTicketCreateApi(
        context: Activity,
        token: String,
        categoryId: String,
        message: String,
        layoutLoader: RelativeLayout
    ) {
        viewModelScope.launch {
            repository.ticketCreate(context, token, categoryId, message, layoutLoader)
        }
    }
    fun callTicketCategoriesApi(context: Activity, token: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.ticketCategories(context, token, layoutLoader)
        }
    }
    fun callNotificationCountsApi(context: Activity, token: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.notificationCounter(context, token, layoutLoader)
        }
    }
    fun callTicketsOpenedListApi(context: Activity, token: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.ticketsOpenedList(context, token, layoutLoader)
        }
    }
    fun callTicketsClosedListApi(context: Activity, token: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.ticketsClosedList(context, token, layoutLoader)
        }
    }
    fun callTicketDetailApi(context: Activity, token: String, ticketId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.ticketDetail(context, token, ticketId, layoutLoader)
        }
    }
    fun callTicketCloseApi(context: Activity, token: String, ticketId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.ticketClose(context, token, ticketId, layoutLoader)
        }
    }
    fun callSendTicketMessageApi(context: Activity, token: String, ticketId: String, type: String, message: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.sendTicketMessage(context, token, ticketId, type, message, layoutLoader)
        }
    }

    // Support Request
    fun callCreateSupportApi(context: Activity, imageName: RequestBody, imageFile: MultipartBody.Part, userId: RequestBody, subject: RequestBody, category: RequestBody, priority: RequestBody, message: RequestBody, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.createSupport(context, imageName, imageFile, userId, subject, category, priority, message, layoutLoader)
        }
    }
    fun callCreateSupportApi(context: Activity, userId: String, subject: String, category: String, priority: String, message: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.createSupport(context, userId, subject, category, priority, message, layoutLoader)
        }
    }

    fun callSupportRequestListApi(context: Activity, userId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.supportRequestList(context, userId, layoutLoader)
        }
    }

    fun callSupportDetailApi(context: Activity, ticketId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.supportDetails(context, ticketId, layoutLoader)
        }
    }

    fun callSendToSupportImageMessageApi(context: Activity, imageName: RequestBody, imageFile: MultipartBody.Part, userId: RequestBody, message: RequestBody, ticketId: RequestBody, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.sendToSupportImageMessage(context, imageName, imageFile, userId, message, ticketId, layoutLoader)
        }
    }

    fun callSendToSupportTextMessageApi(context: Activity, userId: String, message: String, ticketId: String, pbSend: ProgressBar, btnSend: ImageView, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.sendToSupportTextMessage(context, userId, message, ticketId, pbSend, btnSend, layoutLoader)
        }
    }

    fun callListOfFaqsApi(context: Activity, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.listOfFaqs(context, layoutLoader)
        }
    }

    // Create Image
    fun callCreateMagicArtApi(context: Activity, userId: String, description: String, style: String, medium: String, noOfImages: String, resolution: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.createMagicArt(context, userId, description, style, medium, noOfImages, resolution, layoutLoader)
        }
    }
    fun callGetMagicArtApi(context: Activity, userId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.getMagicArt(context, userId, layoutLoader)
        }
    }
    fun callDeleteMagicArtApi(context: Activity, artId: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.deleteMagicArt(context, artId, layoutLoader)
        }
    }

    // Text To Speech
    fun callCreateTextToSpeechApi(context: Activity, userId: String, documentName: String, text: String, gender: String, language: String, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.createTextToSpeech(context, userId, documentName, text, gender, language, layoutLoader)
        }
    }
    fun callTextToSpeechListApi(context: Activity, userId: String, layoutLoader: ShimmerFrameLayout) {
        viewModelScope.launch {
            repository.textToSpeechList(context, userId, layoutLoader)
        }
    }

    fun callDeleteTextToSpeechListApi(context: Activity,token:String?, textToSpeechId: String?, layoutLoader: RelativeLayout) {
        viewModelScope.launch {
            repository.deleteTextToSpeechList(context, token,textToSpeechId, layoutLoader)
        }
    }
}