@file:Suppress("UNUSED_EXPRESSION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.kedvik.ai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kedvik.ai"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // sdp and ssp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    // Viewpager indicator
    implementation(libs.viewpagerindicator)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.gson)

    // Cookie bar
    implementation(libs.cookiebar2)
    // Lifecycle
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //Glide
    implementation(libs.glide.v4142)
    implementation(libs.annotations)
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0")
    { "exclude group: 'glide-parent'" }
    annotationProcessor(libs.compiler)
    implementation(libs.glide.transformations)
    implementation(libs.gpuimage.library)

    // Play review
    implementation(libs.play.review)
    implementation(libs.review.ktx)

    //Pretty Time
    implementation(libs.prettytime)

    // OTP view
    implementation(libs.aabhasr.otpview)

    //ImageBadgeView
    implementation(libs.image.support)
    // Circle image view
    implementation(libs.circleimageview)

    // Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics.ktx)
    // Firebase Crashlytics SDK
    implementation(libs.firebase.crashlytics.ktx)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    // rounded image view
    implementation(libs.roundedimageview)
    // Library to get real file path from the Uri
    implementation(libs.pickit)
    //Animation
    implementation(libs.lottie)
    // Html To pdf convertor
    implementation(libs.html.to.pdf.convertor)

    // For rendering/opening WORD files
    implementation(libs.itext7.core)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(libs.poi.scratchpad)

    // PDF text extractor
    implementation(libs.itextg)

    // For rendering/opening PDF files
    implementation(libs.android.pdf.viewer)

    //zoomage Image View
    implementation(libs.zoomage)

    //Material Spinner
    implementation(libs.material.spinner)

    //firebase messaging
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.messaging)

    // Google Auth
    implementation(libs.play.services.auth)

    //waveform seekbar for audio
    implementation  (libs.massoudss.waveformseekbar)
    implementation (libs.amplituda)

    implementation (libs.amplituda.v220)

    //shimmers
    implementation(libs.shimmer)

    implementation(libs.play.services.auth.v2100)


}