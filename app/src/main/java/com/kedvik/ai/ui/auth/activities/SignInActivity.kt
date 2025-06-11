package com.kedvik.ai.ui.auth.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.kedvik.ai.R
import com.kedvik.ai.api.ApplicationClass
import com.kedvik.ai.databinding.ActivitySignInBinding
import com.kedvik.ai.model.UserDataModel
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities
import com.kedvik.ai.viewmodel.MainViewModel
import com.kedvik.ai.viewmodel.MainViewModelFactory

class SignInActivity : AppCompatActivity() {

    private lateinit var userSocialName: String
    private var userGoogleEmail = ""
    private var platformId: String = ""

    private var platform = "normal"
    var device = "android"
    private var fcmToken = ""
    var deviceId: String = ""
    private lateinit var timezone: String
    lateinit var email: String
    var password: String = ""
    private val GOOGLE_SIGN_IN = 1

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var appleAuthProvider: OAuthProvider.Builder = OAuthProvider.newBuilder("apple.com")
    private lateinit var mainViewModel: MainViewModel
    lateinit var context: Activity
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@SignInActivity

        timezone = Utilities.getTimeZone()
        deviceId = Utilities.getMyDeviceId(context)
        Constant.getFCMToken { token ->
            this.fcmToken = token
        }

        // viewModel & repository initialize
        val repository = (application as ApplicationClass).apiRepository
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        onBackPressCallback()
        editTextWatcher()
        setClickListener()
//        initApple()
        setObserver()

    }

    private fun editTextWatcher() {
        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edEmail.text.toString()
                if (text.startsWith(" ")) {
                    binding.edEmail.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())) {
                        binding.edEmail.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.edPassword.removeTextChangedListener(this)

                val text: String = binding.edPassword.text.toString()
                if (text.contains(" ")) {
                    binding.edPassword.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())) {
                        binding.edPassword.setSelection(text.trim().length)
                    }
                }
                binding.edPassword.addTextChangedListener(this)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun setClickListener() {
        binding.edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.icPasswordEye.setOnClickListener {
            Constant.setPasswordTransformationMethod(binding.edPassword, binding.icPasswordEye)
        }
        binding.tvResetItEffortlessly.setOnClickListener {
            Constant.startForgotPasswordScreen(context, "")
        }
        binding.btnSignIn.setOnClickListener {
            checkValidations()
        }
        binding.llGoogleSignIn.setOnClickListener {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            // CustomCookieToast(context).showSuccessToast(context, "App Launching Soon!", "Currently in beta testing, our app will soon be available for public use. Stay tuned!")
            signInWithGoogle()
        }
//        binding.llAppleSignIn.setOnClickListener {
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//            signInWithApple()
//        }
        binding.tvSignUp.setOnClickListener {
            Constant.startSignUpActivity(context)
        }
    }

    private fun checkValidations() {
        email = binding.edEmail.text.toString().trim()
        password = binding.edPassword.text.toString().trim()
        binding.edEmail.requestFocus()

        if (TextUtils.isEmpty(email)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourEmail))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterValidEmail))
        } else if (TextUtils.isEmpty(password)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterPassword))
        } else if (password.length < 6) {
            CustomCookieToast(context).showRequiredToast("Password must be a minimum of 6 characters in length.")
        } else {
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callLoginApi(
                context,
                email,
                password,
                device,
                deviceId,
                timezone,
                fcmToken,
                binding.layoutLoader.layoutLoader
            )
        }
    }

    private fun setObserver() {
        mainViewModel.authLogin.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (data.status == true){
                    CustomCookieToast(context).showSuccessToast("Login successfully")
                    Utilities.setPasswordAddedValue(context, data.data?.passwordAdded?:0)

                    Handler(Looper.myLooper()!!).postDelayed({
                        if (data.data != null){
                            saveLoginData(data.data)
                        }
                    }, 2000)
                }else{
                    val title = data.title?:""
                    val field = data.errorsList?.get(0)?.field ?:""
                    val loginType = when (field) {
                        "apple" -> {
                            "Apple"
                        }
                        "google" -> {
                            "Google"
                        }
                        else -> {
                            ""
                        }
                    }
                    if (title == "Social Media Login Required!"){
                        Utilities.hideKeyboard(context)
                        popupSetPassword(loginType)
                    }else{
                        CustomCookieToast(context).showFailureToast(data.title?:"", data.errorsList?.get(0)?.message ?:"")
                    }
                }
            }
        }
        mainViewModel.authSocialLogin.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (data.status == true){
                    Utilities.setPasswordAddedValue(context, data.data?.passwordAdded?:0)
                    val userData = data.data
                    if (userData != null){
                        saveLoginData(data.data)
                    }
                }else{
                    CustomCookieToast(context).showFailureToast( data.title?:"", data.errorsList?.get(0)?.message ?:"")
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

    private fun onBackPressCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            }
        })
    }

    // SIGN_IN WITH GOOGLE
    private fun signInWithGoogle() {
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        val googleSignInClient: GoogleSignInClient
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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
            userSocialName = account.displayName!!
            userGoogleEmail = account.email!!
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
//
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
//
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
//            }.addOnFailureListener { e ->
//                CustomCookieToast(context).showFailureToast(e.toString())
//                binding.layoutLoader.layoutLoader.visibility = View.GONE
//                CustomCookieToast(context).showFailureToast("Failed to sign in with Apple.")
//            }
//        } else {
//            launchAppleSignInFlow()
//        }
//    }
//
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
//                CustomCookieToast(context).showFailureToast(e.toString())
//            }
//        // CustomCookieToast(context).showFailureToast("Failed to sign in with Apple.") }
//    }

    private fun callSocialLoginApi(email: String) {
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callSocialLoginApi(
            context,
            userSocialName,
            email,
            platform,
            platformId,
            device,
            deviceId,
            timezone,
            fcmToken,
            binding.layoutLoader.layoutLoader
        )
    }

    @SuppressLint("SetTextI18n")
    private fun popupSetPassword(loginType: String) {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_set_password_popup, null)
        builder.setView(customLayout)

        val tvEmail = customLayout.findViewById<TextView>(R.id.tvEmail)
        val tvDetail = customLayout.findViewById<TextView>(R.id.tvDetail)
        val btnClose = customLayout.findViewById<Button>(R.id.btnClose)
        val btnSetPassword = customLayout.findViewById<Button>(R.id.btnSetPassword)

        tvEmail.text = email
        tvDetail.text = "You are currently logged in through your\n $loginType account"

        alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        btnSetPassword.setOnClickListener {
            alertDialog.dismiss()
            // Api Call
            Constant.startForgotPasswordScreen(context, email)
        }
        btnClose.setOnClickListener { alertDialog.dismiss() }
    }
}