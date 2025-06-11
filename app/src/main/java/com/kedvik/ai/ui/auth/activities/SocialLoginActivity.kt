package com.kedvik.ai.ui.auth.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.kedvik.ai.databinding.ActivitySocialLoginBinding
import com.kedvik.ai.model.UserDataModel
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities

class SocialLoginActivity : BaseActivity() {

    private lateinit var userSocialName: String
    private var userGoogleEmail =  ""
    private var platformId: String = ""
    private val GOOGLE_SIGN_IN = 1
    lateinit var email: String

    private var platform = "normal"
    var password: String = ""
    var device = "android"
    var fcmToken = ""
    var deviceId: String = ""
    private lateinit var timezone: String

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var appleAuthProvider: OAuthProvider.Builder = OAuthProvider.newBuilder("apple.com")
    private lateinit var binding: ActivitySocialLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@SocialLoginActivity

        initData()
        setClickListener()
        setClickListener()
//        initApple()
        setObserver()
        onBackPressCallback()
    }

    private fun initData() {
        timezone = Utilities.getTimeZone()
        deviceId = Utilities.getMyDeviceId(context)
        Constant.getFCMToken { token ->
            this.fcmToken = token
        }
    }

    private fun setClickListener() {
        binding.llGoogleSignIn.setOnClickListener {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            // CustomCookieToast(context).showSuccessToast(context, "App Launching Soon!", "Currently in beta testing, our app will soon be available for public use. Stay tuned!")
            signInWithGoogle()
        }
//        binding.llAppleSignIn.setOnClickListener {
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//            signInWithApple()
//        }
        binding.btnCreateAccount.setOnClickListener {
            Constant.startSignUpActivity(context)
        }
        binding.tvSignIn.setOnClickListener {
            Constant.startSignInScreen(context)
        }
    }

    private fun setObserver() {
        mainViewModel.authSocialLogin.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE


                if (data.status == true){
                    val userData = data.data
                    if (userData != null){
                        saveLoginData(userData)
                    }
                    Utilities.setPasswordAddedValue(context, data.data?.passwordAdded?:0)
                }else{
                    CustomCookieToast(context).showFailureToast(data.title?:"", data.errorsList?.get(0)?.message ?:"")
                }
            }
        }
    }

    private fun saveLoginData(dataModel: UserDataModel) {
        SessionAndCookies.saveUserProfileData(context, dataModel)
        SessionAndCookies.saveLoginSession(context, true)

        if (SessionAndCookies.isUserLoggedIn(context)) {
            Constant.startDashboardScreen(context)
        }
    }

    // SIGN_IN WITH GOOGLE
    private fun signInWithGoogle() {
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        val googleSignInClient: GoogleSignInClient
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<*>) {
        try {
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            val account = task.getResult(ApiException::class.java) as GoogleSignInAccount
            platformId = account.id!!
            userSocialName = account.displayName?:""
            userGoogleEmail = account.email?:""
            platform = "google"

            if (userGoogleEmail.isEmpty()) {
                email = "$platformId@gmail.com"

                callSocialLoginApi(email)
            } else if (userGoogleEmail.isNotEmpty()) {
                email = userGoogleEmail
                callSocialLoginApi(userGoogleEmail)
            }
        } catch (e: ApiException) {
            binding.layoutLoader.layoutLoader.visibility = View.GONE
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            binding.layoutLoader.layoutLoader.visibility = View.GONE
        }
    }



//    // SIGN_IN USING APPLE
//    private fun initApple() {
//        appleAuthProvider.scopes = object : ArrayList<String?>() {
//            init {
//                add("email")
//                add("name")
//            }
//        }
//        appleAuthProvider.addCustomParameter("locale", "eng")
//    }

//    private fun signInWithApple() {
//        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
//        val pending = firebaseAuth.pendingAuthResult
//        if (pending != null) {
//            pending.addOnSuccessListener { authResult ->
//                val user = authResult.user
//                if (user != null) {
//                    platform = "apple"
//                    platformId = user.uid
//                    email = authResult.user?.email?:""
//                    val userDefaultName = "User${Utilities.generateRandomNumbers()}"
//                    userSocialName = authResult.user!!.displayName?:userDefaultName
//
//                    callSocialLoginApi(email)
//                }
//            }.addOnFailureListener {e->
//                CustomCookieToast(context).showFailureToast(e.toString())
//                binding.layoutLoader.layoutLoader.visibility = View.GONE
//                CustomCookieToast(context).showFailureToast("Failed to sign in with Apple.")}
//        } else {
//            launchAppleSignInFlow()
//        }
//    }

//    private fun launchAppleSignInFlow() {
//        firebaseAuth.startActivityForSignInWithProvider(this, appleAuthProvider.build())
//            .addOnSuccessListener { authResult ->
//                val user: FirebaseUser = authResult.user!!
//                platform = "apple"
//                platformId = user.uid
//                email = authResult.user!!.email?:""
//                val userDefaultName = "User${Utilities.generateRandomNumbers()}"
//                userSocialName = authResult.user?.displayName ?: userDefaultName
//
//                callSocialLoginApi(email)
//            }.addOnFailureListener { e ->
//                binding.layoutLoader.layoutLoader.visibility = View.GONE
//                CustomCookieToast(context).showFailureToast(e.toString()) }
//        // CustomCookieToast(context).showFailureToast("Failed to sign in with Apple.") }
//    }

    private fun callSocialLoginApi(email: String) {
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callSocialLoginApi(context, userSocialName, email, platform, platformId, device, deviceId, timezone, fcmToken, binding.layoutLoader.layoutLoader)
    }

    private fun onBackPressCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}