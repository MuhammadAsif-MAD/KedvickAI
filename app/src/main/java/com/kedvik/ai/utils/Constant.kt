package com.kedvik.ai.utils

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.kedvik.ai.R
import com.kedvik.ai.ui.auth.activities.AccountVerificationActivity
import com.kedvik.ai.ui.auth.activities.ForgotPasswordActivity
import com.kedvik.ai.ui.auth.activities.ResetPasswordActivity
import com.kedvik.ai.ui.auth.activities.SignInActivity
import com.kedvik.ai.ui.auth.activities.SignUpActivity
import com.kedvik.ai.ui.auth.activities.SocialLoginActivity
import com.kedvik.ai.ui.contactus.ContactUsActivity
import com.kedvik.ai.ui.createImage.activities.ArtImagePreviewActivity
import com.kedvik.ai.ui.createImage.activities.CreateArtActivity
import com.kedvik.ai.ui.dashboard.activities.DashboardActivity
import com.kedvik.ai.ui.helpAndSupport.activities.CreateReportActivity
import com.kedvik.ai.ui.helpAndSupport.activities.CreateSupportActivity
import com.kedvik.ai.ui.helpAndSupport.activities.FaqsActivity
import com.kedvik.ai.ui.helpAndSupport.activities.HelpAndSupportActivity
import com.kedvik.ai.ui.helpAndSupport.activities.MyReportsActivity
import com.kedvik.ai.ui.researchCenter.activities.ChatActivity
import com.kedvik.ai.ui.researchCenter.activities.PdfViewerActivity
import com.kedvik.ai.ui.researchCenter.model.ChatDataItemModel
import com.kedvik.ai.ui.settings.AccountSettingsActivity
import com.kedvik.ai.ui.settings.ChangePasswordActivity
import com.kedvik.ai.ui.settings.DeleteAccountActivity
import com.kedvik.ai.ui.settings.EditProfileActivity
import com.kedvik.ai.ui.settings.SetPasswordActivity
import com.kedvik.ai.ui.settings.SettingsActivity
import com.kedvik.ai.ui.settings.TermsAndPrivacyActivity
import com.kedvik.ai.ui.settings.WebViewActivity
import com.kedvik.ai.ui.settings.inviteFriends.activities.ContactsListActivity
import com.kedvik.ai.ui.settings.inviteFriends.activities.InviteFriendsActivity
import com.kedvik.ai.ui.textToSpeech.activities.ImagePreviewActivity
import com.kedvik.ai.ui.textToSpeech.activities.ListOfAvailableSpeechActivity
import com.kedvik.ai.ui.textToSpeech.activities.TextToSpeechActivity
import com.kedvik.ai.ui.textToSpeech.activities.TextToSpeechDetailsActivity

class Constant {
    companion object {
        const val PRIVACY_POLICY = "Privacy Policy"
        const val TERMS_AND_CONDITIONS = "Terms & Conditions"

        const val PRIVACY_POLICY_URL = "https://site.kedvik.com/privacy-policy"
        const val TERMS_AND_CONDITIONS_URL = "https://site.kedvik.com/terms-condition"
        const val FACEBOOK_LINK = "https://www.facebook.com/share/Za24UxvX1bFGXLL3/?mibextid=LQQJ4d"
        const val WHATSAPP_LINK = "https://whatsapp.com/channel/0029VaC9oG059PwKUDbdNu1Q"
        const val LINKEDIN_LINK = "https://www.linkedin.com/company/kedvik/"
        const val THREADS_LINK = "https://www.threads.net/@kedvikapp"
        const val INSTAGRAM_LINK = "https://www.instagram.com/kedvikapp?igsh=a3hmcW5kczFuN2Vv&utm_source=qr"

        const val IS_PRIVACY_POLICY_AGREE = "IS_PRIVACY_POLICY_AGREE"
        const val LOGCAT_TUDO = "LOGCAT_TUDO"
        const val CALL_FROM = "CALL_FROM"
        const val IS_REVIEW_ADDED = "IS_REVIEW_ADDED"
        const val IS_SERVICE_USED = "IS_SERVICE_USED"
        const val LOGIN_PLATFORM = "LOGIN_PLATFORM"
        const val PLATFORM_ID = "PLATFORM_ID"
        const val NORMAL_LOGIN = "NORMAL_LOGIN"
        const val IS_PASSWORD_ADDED = "IS_PASSWORD_ADDED"
        const val USER_ID = "USER_ID"
        const val CHAT_ID = "CHAT_ID"
        const val DATA_ITEM = "DATA_LIST"
        const val DATA_LIST = "DATA_LIST"
        const val CATEGORY_NAME = "CATEGORY_NAME"
        const val CALL_FOR = "CALL_FOR"
        const val WEB_URL = "WEB_URL"
        const val NAME = "NAME"
        const val EMAIL = "EMAIL"
        const val PASSWORD = "PASSWORD"
        const val DEVICE = "DEVICE"
        const val DEVICE_ID = "DEVICE_ID"
        const val TIME_ZONE = "TIME_ZONE"
        const val FCM_TOKEN = "FCM_TOKEN"
        const val REFERRAL_CODE = "REFERRAL_CODE"
        const val NEW_CHAT = "NEW_CHAT"
        const val CHAT_HISTORY = "CHAT_HISTORY"
        const val TEXT = "text"
        const val IMAGE = "image"
        const val PDF = "pdf"
        const val SIGNUP_SCREEN = "SIGNUP_SCREEN"
        const val FORGOT_PASSWORD_SCREEN = "FORGOT_PASSWORD_SCREEN"
        const val SOMETHING_WENT_WRONG = "Something went wrong please try again"
        const val GALLERY_PERMISSION = 1122
        const val PERMISSION_DENIED = "Permission denied"
        const val BACKGROUND = "BACKGROUND"
        const val TAG = "kedvik_TESTING"
        const val PAYSTACK_SECRET = "PAYSTACK_SECRET"
        const val CURRENCY_EXCHANGE_VALUE = "CURRENCY_EXCHANGE_VALUE"
        const val PLEASE_SELECT_COMPLAIN_CATEGORY = "Please select complain category."
        const val ENTER_SOME_COMPLAINT_DESCRIPTION_TO_PROCEED =
            "Enter some complaint's description to proceed."

        // HElP AND SUPPORT
        var TICKET_ID = "TICKET_ID"
        var TICKET_STATUS = "TICKET_STATUS"
        var NEW_COMPLAINT = "NEW_COMPLAINT"
        var COMPLAINT = "COMPLAINT"
        const val IS_ON_CHAT_SCREEN = "IS_ON_CHAT_SCREEN"

        const val PDF_URL = "PDF_URL"
        const val IS_SERVER_URL = "IS_SERVER_URL"

        const val IS_MAGIC_ART_UPDATED = "IS_MAGIC_ART_UPDATED"
        const val IS_TEXT_TO_SPEECH_UPDATED = "IS_TEXT_TO_SPEECH_UPDATED"
        const val IS_DOCUMENT_UPDATED = "IS_DOCUMENT_UPDATED"
        const val IS_TEMPLATE_UPDATED = "IS_TEMPLATE_UPDATED"

        fun getFCMToken(callback: (String) -> Unit) {
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
                val token = if (task.isSuccessful) {
                    task.result
                } else {
                    null
                }
                callback(token ?: "")
            }
        }

        // Auth Section
        fun startSignUpActivity(context: Activity) {
            val intent = Intent(context, SignUpActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//
        fun startSignInScreen(context: Activity) {
            val intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//
//        fun startAccountCreatedScreen(context: Activity) {
//            val intent = Intent(context, AccountCreatedActivity::class.java)
//            context.startActivity(intent)
//            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
//        }
//
        fun startSocialLoginScreen(context: Activity) {
            val intent = Intent(context, SocialLoginActivity::class.java)
            context.startActivity(intent)
            context.finishAffinity()
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startAccountVerificationScreen(context: Activity, email: String, callFrom: String) {
            val intent = Intent(context, AccountVerificationActivity::class.java)
            intent.putExtra(EMAIL, email)
            intent.putExtra(CALL_FROM, callFrom)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startAccountVerificationScreen(context: Activity, email: String, callFrom: String, name: String, password: String, device: String, deviceId: String, timeZone: String, fcmToken: String, referralCode: String) {
            val intent = Intent(context, AccountVerificationActivity::class.java)
            intent.putExtra(EMAIL, email)
            intent.putExtra(CALL_FROM, callFrom)
            intent.putExtra(NAME, name)
            intent.putExtra(PASSWORD, password)
            intent.putExtra(DEVICE, device)
            intent.putExtra(DEVICE_ID, deviceId)
            intent.putExtra(TIME_ZONE, timeZone)
            intent.putExtra(FCM_TOKEN, fcmToken)
            intent.putExtra(REFERRAL_CODE, referralCode)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startSignInFinishScreen(context: Activity, email: String, name: String) {
            val intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
            context.finishAffinity()
            context.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        }
//
        fun startForgotPasswordScreen(context: Activity, email: String) {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            intent.putExtra(EMAIL, email)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startResetPasswordScreen(context: Activity, email: String) {
            val intent = Intent(context, ResetPasswordActivity::class.java)
            intent.putExtra(EMAIL, email)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startWebViewActivity(context: Activity, callFrom: String, webUrl: String) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(CALL_FROM, callFrom)
            intent.putExtra(WEB_URL, webUrl)
            intent.putExtra(WEB_URL, webUrl)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//
        // Dashboard
        fun startDashboardScreen(context: Activity) {
            val intent = Intent(context, DashboardActivity::class.java)
            context.startActivity(intent)
            context.finishAffinity()
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//        fun startDashboardFinishScreen(context: Activity) {
//            val intent = Intent(context, DashboardActivity::class.java)
//            context.startActivity(intent)
//            context.finishAffinity()
//            context.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
//        }
//

        fun startSettingActivity(context: Activity) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//
        fun startCreateArtActivity(context: Activity) {
            val intent = Intent(context, CreateArtActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startTextToSpeechActivity(context: Activity) {
            val intent = Intent(context, TextToSpeechActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startListOfAvailableSpeechActivity(context: Activity) {
            val intent = Intent(context, ListOfAvailableSpeechActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startContactUsActivity(context: Activity) {
            val intent = Intent(context, ContactUsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        // Setting
        fun startAccountSettingActivity(context: Activity) {
            val intent = Intent(context, AccountSettingsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startInviteFriendsActivity(context: Activity) {
            val intent = Intent(context, InviteFriendsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startContactsListActivity(context: Activity) {
            val intent = Intent(context, ContactsListActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun startTermsAndPrivacyActivity(context: Activity) {
            val intent = Intent(context, TermsAndPrivacyActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startEditProfileActivity(context: Activity) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startChangePasswordActivity(context: Activity) {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startSetPasswordActivity(context: Activity) {
            val intent = Intent(context, SetPasswordActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startDeleteAccountActivity(context: Activity) {
            val intent = Intent(context, DeleteAccountActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startPdfViewerActivity(context: Activity, pdfUrl: String, pdfText: String) {
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra(PDF_URL, pdfUrl)
            intent.putExtra("PDF_TEXT", pdfText)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startChatActivity(context: Activity, callFor: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CALL_FOR, callFor)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//
        fun startChatActivity(context: Activity, chatId: String, callFor: String, modelList: MutableList<ChatDataItemModel>) {
            // Convert the selectedChatHistoryList to a JSON string
            val gson = Gson()
            val chatList = gson.toJson(modelList)

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CALL_FOR, callFor)
            intent.putExtra(CHAT_ID, chatId)
            intent.putExtra(DATA_LIST, chatList)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }


        fun openImagePreviewActivity(context: Activity, imageView: ImageView?, image: String) {
            if (!TextUtils.isEmpty(image)) {
                val intent = Intent(context, ImagePreviewActivity::class.java)
                if (image.contains("/storage")) {
                    ViewCompat.setTransitionName(imageView!!, image)
                    intent.putExtra("image", image)
                } else {
                    ViewCompat.setTransitionName(imageView!!, image)
                    intent.putExtra("image", image)
                }
                val animationName = ViewCompat.getTransitionName(imageView)
                intent.putExtra("animationName", animationName)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context,
                    imageView, animationName!!
                )
                context.startActivity(intent, options.toBundle())
            }
        }
//
        fun openArtPreviewActivity(context: Activity, image: String,  artId: String) {
            if (!TextUtils.isEmpty(image)) {
                val intent = Intent(context, ArtImagePreviewActivity::class.java)
                intent.putExtra("image", image)
                intent.putExtra("art_id", artId)
                context.startActivity(intent)
                context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
            }
        }

        // Help And Support
        fun startHelpAndSupportActivity(context: Activity) {
            val intent = Intent(context, HelpAndSupportActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//        fun startHelpAndSupportActivity(context: Activity) {
//            val intent = Intent(context, SupportRequestsActivity::class.java)
//            context.startActivity(intent)
//            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
//        }
        fun startCreateReportActivity(context: Activity) {
            val intent = Intent(context, CreateReportActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startFaqsActivity(context: Activity) {
            val intent = Intent(context, FaqsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startCreateSupportActivity(context: Activity) {
            val intent = Intent(context, CreateSupportActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startMyReportsActivity(context: Activity) {
            val intent = Intent(context, MyReportsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
        fun startTextToSpeechDetailsActivity(context: Activity) {
            val intent = Intent(context, TextToSpeechDetailsActivity::class.java)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }
//
//        // Generated Documents
//        fun startEditorActivity(context: Activity, dataItem: DocumentResponseModel) {
//            val gson = Gson()
//            val dataModel = gson.toJson(dataItem)
//
//            val intent = Intent(context, EditorActivity::class.java)
//            intent.putExtra(DATA_ITEM, dataModel)
//            context.startActivity(intent)
//            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
//        }
//
        fun setWindowToAdjustResize(context: Activity) {
            context.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        fun setPasswordTransformationMethod(edPassword: EditText, icPasswordEye: ImageView) {
            if (edPassword.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
                icPasswordEye.setImageResource(R.drawable.eye_open)
                edPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                icPasswordEye.setImageResource(R.drawable.eye_close)
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}