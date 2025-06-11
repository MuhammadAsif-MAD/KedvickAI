package com.kedvik.ai.api

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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Auth
    @GET("app-about")
    suspend fun splashApi(
        @Query("user_id") myId: String
    ): Response<SplashResponseModel>

    @FormUrlEncoded
    @POST("user/register")
    suspend fun authRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_name") deviceName: String,
        @Field("device_id") deviceId: String,
        @Field("timezone") timezone: String,
        @Field("fcm_token") fcmToken: String,
        @Field("referral_code") referralCode: String
    ): Response<UserResponseModel>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun authLogin(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_name") deviceName: String,
        @Field("device_id") deviceId: String,
        @Field("timezone") timezone: String,
        @Field("fcm_token") fcmToken: String
    ): Response<UserResponseModel>

    @FormUrlEncoded
    @POST("user/social")
    suspend fun authSocialLogin(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("platform") platform: String,
        @Field("platform_id") platformId: String,
        @Field("device_name") deviceName: String,
        @Field("device_id") deviceId: String,
        @Field("timezone") timezone: String,
        @Field("fcm_token") fcmToken: String
    ): Response<UserResponseModel>

    @FormUrlEncoded
    @POST("user/verify")
    suspend fun userVerify(
        @Field("email") email: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("user/recover/verify")
    suspend fun authForgotPassword(
        @Field("email") email: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("user/new/password")
    suspend fun authResetPassword(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("otp/verify")
    suspend fun authOtpVerify(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): Response<BaseResponseModel>

    // Settings
    @FormUrlEncoded
    @POST("profile-update")
    suspend fun profileUpdate(
        @Field("user_id") userId: String,
        @Field("name") name: String,
        @Field("email") email: String
    ): Response<UserResponseModel>

    @Multipart
    @POST("profile-update")
    suspend fun profileImageUpdate(
        @Part("user_id") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part file: MultipartBody.Part,
    ): Response<UserResponseModel>

    @GET("user/profile/delete/{myId}")
    suspend fun profilePictureRemove(
        @Path("myId") myId: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("user/delete/account")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Field("password") password: String
    ): Response<BaseResponseModel>

    @POST("user/delete/account")
    suspend fun deleteSocialAccount(
        @Header("Authorization") token: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("user/change/password")
    suspend fun changePassword(
        @Field("user_id") userId: String,
        @Field("old_password") oldPassword: String,
        @Field("password") password: String
    ): Response<BaseResponseModel>

    // Dashboard
    @GET("user")
    suspend fun userProfile(
        @Query("id") userId: String
    ): Response<UserDataModel>

    @FormUrlEncoded
    @POST("favorite-template")
    suspend fun favouriteUnFavoriteTemplate(
        @Field("user_id") userId: String,
        @Field("template_id") templateId: String
    ): Response<FavouriteUnFavouriteResponseModel>

    @FormUrlEncoded
    @POST("update-document")
    suspend fun updateDocument(
        @Field("id") documentId: String,
        @Field("title") title: String,
        @Field("text") text: String
    ): Response<BaseResponseModel>

    // Research
    @FormUrlEncoded
    @POST("user/chat")
    suspend fun userChatTextMessage(
        @Header("Authorization") token: String,
        @Field("chat_id") chatId: String,
        @Field("text") message: String
    ): Response<MutableList<ChatDataItemModel>>

    @Multipart
    @POST("user/chat")
    suspend fun userChatMediaMessage(
        @Header("Authorization") token: String,
        @Part("chat_id") chatId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<MutableList<ChatDataItemModel>>

    @Multipart
    @POST("user/chat")
    suspend fun userChatPdfMessage(
        @Header("Authorization") token: String,
        @Part("chat_id") chatId: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("pdf_text") pdfText: RequestBody
    ): Response<MutableList<ChatDataItemModel>>

    @Multipart
    @POST("user/chat")
    suspend fun userChatTextAndPdfMessage(
        @Header("Authorization") token: String,
        @Part("chat_id") chatId: RequestBody,
        @Part("text") message: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("pdf_text") pdfText: RequestBody
    ): Response<MutableList<ChatDataItemModel>>

    @Multipart
    @POST("user/chat")
    suspend fun userChatTextAndMediaMessage(
        @Header("Authorization") token: String,
        @Part("chat_id") chatId: RequestBody,
        @Part("text") message: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<MutableList<ChatDataItemModel>>

    @FormUrlEncoded
    @POST("user/chat/history")
    suspend fun chatHistory(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String
    ): Response<ChatHistoryResponseModel>

    @GET("user/chat/history/delete/{chat_id}")
    suspend fun deleteChatHistory(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: String
    ): Response<BaseResponseModel>


    @FormUrlEncoded
    @POST("delete-document")
    suspend fun deleteDocument(
        @Field("id") documentId: String
    ): Response<BaseResponseModel>

    // Help And Support
    @FormUrlEncoded
    @POST("user/ticket/create")
    suspend fun ticketCreate(
        @Header("Authorization") token: String,
        @Field("category_id") categoryId: String,
        @Field("message") message: String
    ): Response<TicketCreateResponseModel>

    @GET("ticket/categories")
    suspend fun ticketCategories(
        @Header("Authorization") token: String
    ): Response<TicketCategoriesResponseModel>

    @GET("user/counter")
    suspend fun notificationCounter(
        @Header("Authorization") token: String
    ): Response<NotificationCounterResponseModel>

    @GET("user/ticket/list/0")
    suspend fun ticketsOpenedList(
        @Header("Authorization") token: String
    ): Response<TicketsListResponseModel>

    @GET("user/ticket/list/1")
    suspend fun ticketsClosedList(
        @Header("Authorization") token: String
    ): Response<TicketsListResponseModel>

    @GET("user/ticket/conversation/{ticketId}")
    suspend fun ticketDetail(
        @Header("Authorization") token: String,
        @Path("ticketId") ticketId: String
    ): Response<TicketDetailResponseModel>

    @GET("user/ticket/close/{ticketId}")
    suspend fun ticketsClose(
        @Header("Authorization") token: String,
        @Path("ticketId") ticketId: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("user/ticket/send/message")
    suspend fun sendTicketMessage(
        @Header("Authorization") token: String,
        @Field("ticket_id") ticketId: String,
        @Field("type") type: String,
        @Field("message") message: String
    ): Response<SendTicketMessageResponseModel>

    // Support Request Old
    @Multipart
    @POST("create-support-request")
    suspend fun createSupport(
        @Part("image") imageName: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("category") category: RequestBody,
        @Part("priority") priority: RequestBody,
        @Part("message") massage: RequestBody
    ): Response<ItemSupportReq>

    @FormUrlEncoded
    @POST("create-support-request")
    suspend fun createSupport(
        @Field("user_id") userId: String,
        @Field("subject") subject: String,
        @Field("category") category: String,
        @Field("priority") priority: String,
        @Field("message") message: String
    ): Response<ItemSupportReq>
    
    @GET("support-request")
    suspend fun getSupportRequestList(
        @Query("user_id") userId: String
    ): Response<MutableList<ItemSupportReq>>

    @GET("support-request-detail")
    suspend fun getSupportDetails(
        @Query("ticket_id") ticketId: String
    ): Response<SupportDetails>

    @Multipart
    @POST("support-request-reply")
    suspend fun sendToSupportImageMessage(
        @Part("attachment") imageName: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("message") message: RequestBody,
        @Part("ticket_id") ticketId: RequestBody
    ): Response<SupportDetails>

    @FormUrlEncoded
    @POST("support-request-reply")
    suspend fun sendToSupportTextMessage(
        @Field("user_id") userId: String,
        @Field("message") message: String,
        @Field("ticket_id") ticketId: String
    ): Response<SupportDetails>

    // FAQs
    @GET("faqs")
    suspend fun listOfFaqs(): Response<FAQsResponseModel>

    // Create Image
    @GET("magik-art")
    suspend fun getMagicArt(
        @Query("user_id") userId: String
    ): Response<MutableList<ArtResponseModel>>

    @FormUrlEncoded
    @POST("create-magik-art")
    suspend fun createMagicArt(
        @Field("user_id") userId: String,
        @Field("description") description: String,
        @Field("style") style: String,
        @Field("medium") medium: String,
        @Field("number_of_images") noOfImage: String,
        @Field("resolution") resolution: String
    ): Response<MutableList<ArtResponseModel>>

    @FormUrlEncoded
    @POST("delete-magik-art")
    suspend fun deleteArt(
        @Field("id") artId: String
    ): Response<BaseResponseModel>

    // Text To Speech
    @FormUrlEncoded
    @POST("text-to-speech")
    suspend fun createTextToSpeech(
        @Field("user_id") userId: String,
        @Field("document_name") documentName: String,
        @Field("text") text: String,
        @Field("gender") gender: String,
        @Field("language") language: String
    ): Response<TextToSpeechResponseModel>


    @GET("text-to-speech-list/delete/{id}")
    suspend fun deleteTextToSpeechList(
        @Header("Authorization") token: String?,
        @Path("id") textToSpeechId: String?
    ): Response<BaseResponseModel>

    @GET("text-to-speech-list")
    suspend fun textToSpeechList(
        @Query("user_id") userId: String
    ): Response<MutableList<TextToSpeechResponseModel>>


    @FormUrlEncoded
    @POST("create-payment")
    suspend fun createPayment(
        @Field("user_id") userId: String,
        @Field("plan_id") planId: String,
        @Field("payment_id") paymentId: String,
        @Field("payment_type") paymentType: String
    ): Response<BaseResponseModel>

    @FormUrlEncoded
    @POST("contact-massage")
    suspend fun contactUs(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("message") message: String,
        @Field("subject") subject: String
    ): Response<BaseResponseModel>
}
