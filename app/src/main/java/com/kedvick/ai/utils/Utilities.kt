package com.kedvick.ai.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputLayout
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.kedvick.ai.R
import com.kedvick.ai.api.RetrofitBuilder
import io.github.mddanishansari.html_to_pdf.HtmlToPdfConvertor
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random



class Utilities {
    companion object {

        fun setActivityFullScreen(activity: Activity) {
            val window = activity.window
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.statusBarColor = activity.getColor(R.color.background_color)
            window.navigationBarColor = activity.getColor(R.color.background_color)
        }

        fun hideKeyboard(activity: Activity) {
            val inm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            inm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getTimeZone(): String {
            val timeZone = TimeZone.getDefault()
            return timeZone.id
        }

        @SuppressLint("HardwareIds")
        fun getMyDeviceId(context: Activity): String {
            val deviceId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            SessionAndCookies.saveMyDeviceId(context, deviceId)
            return deviceId
        }

        fun somethingWentWrong(context: Activity?) {
            CustomCookieToast(context!!).showFailureToast(Constant.SOMETHING_WENT_WRONG)
        }

        fun setNavigationBarColor(activity: Activity, black: Int) {
            val window: Window = activity.window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.navigationBarColor = activity.getColor(black)
        }

        fun hideNavAndStatusBar(activity: Activity) {
            val window = activity.window
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = activity.getColor(R.color.transparent)
            window.navigationBarColor = activity.getColor(R.color.black)
        }

        fun loadImage(context: Context, image: String?, imageView: ImageView) {
            if (image != null) {
                if (image.contains("/uploads")) {
                    Glide.with(context.applicationContext).load(image)
                        .placeholder(R.drawable.place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView)
                } else {
                    Glide.with(context.applicationContext).load(image)
                        .placeholder(R.drawable.place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView)
                }
            }
        }

        fun loadChatImage(context: Context, image: String?, imageView: ImageView) {
            if (image != null) {
                if (image.contains("/storage")) {
                    Glide.with(context.applicationContext).load(image)
                        .placeholder(R.drawable.place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView)
                } else {
                    Glide.with(context.applicationContext).load(RetrofitBuilder.MEDIA_URL + image)
                        .placeholder(R.drawable.place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView)
                }
            }
        }

        fun textCopyToClipboard(context: Activity, textCopied: String) {
            val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            // When setting the clipboard text.
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", textCopied))
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()

            /*val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied", Html.fromHtml(referralCode).toString())
            clipboard.setPrimaryClip(clip)*/
        }

        fun loadImageWithoutAvatar(context: Context, image: String?, imageView: ImageView) {
            if (image != null) {
                if (image.contains("/uploads")) {
                    Glide.with(context.applicationContext).load(image)
                        .diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView)
                } else {
                    Glide.with(context.applicationContext).load(image)
                        .diskCacheStrategy(DiskCacheStrategy.DATA).into(imageView)
                }
            }
        }

        fun generateChatID(): String {
            val currentTimeMillis = Date().time
            return "chat_$currentTimeMillis"
        }

        fun getCurrentTimeStamp(): String? {
            val tsLong = System.currentTimeMillis() / 1000
            return java.lang.Long.toString(tsLong)
        }

        fun getFormattedTime(time: String): String {
            val curFormater = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var dateObj: Date? = null
            dateObj = try {
                curFormater.parse(time)
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
            val postFormater = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)

            return postFormater.format(dateObj)
        }

        fun getFormatedTime(time: String): String {
            val curFormatter = SimpleDateFormat("dd MMM, yy", Locale.ENGLISH)
            var dateObj: Date? = null
            dateObj = try {
                curFormatter.parse(time)
            } catch (e: ParseException) {
                throw java.lang.RuntimeException(e)
            }
            val postFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)

            return postFormatter.format(dateObj)
        }


        private fun getPdfFilePath(context: Context): File {
            return File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir, "PDFs").apply {
                if (!exists()) {
                    mkdirs()
                }
            }
        }


        /*fun downloadPdf(context: Activity, document: DocumentResponseModel, content: String) {
            val htmlToPdfConvertor = HtmlToPdfConvertor(context)
            htmlToPdfConvertor.setBaseUrl("file:///android_asset/images/")

            val html =
                (("<h2 style=\"text-align: center;\"><strong>" + document.documentName) + "</strong></h2>" + content)

            htmlToPdfConvertor.convert(
                File(
                    getPdfFilePath(context),
                    document.documentName + "_" + System.currentTimeMillis() + ".pdf"
                ), html, { e: Exception ->
                    Log.d("PDF_ERROR", e.message.toString())
                    null
                }) { file: File? ->
                if (file != null) {
                    displayPdfDownloadedPopup(context, file)
                }
            }
        }

        private fun getPdfFilePath(context: Activity): File {
            val file = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
                ), "/" + context.resources.getString(R.string.app_name)
            )
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    CustomCookieToast(context).showFailureToast("Can't create directory to save pdf.")
                    Toast.makeText(
                        context,
                        "Directory Error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            return file
        }*/

        @SuppressLint("SetTextI18n")
        private fun displayPdfDownloadedPopup(context: Activity, file: File) {
            val builder = AlertDialog.Builder(context, R.style.CustomDialog)
            val customLayout: View =
                LayoutInflater.from(context).inflate(R.layout.pdf_downloaded_popup, null)
            builder.setView(customLayout)

            val btnRight = customLayout.findViewById<TextView>(R.id.txtRight)
            val btnCancel = customLayout.findViewById<TextView>(R.id.txtLeft)

            val permissionDialog: AlertDialog = builder.create()
            permissionDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            permissionDialog.setCancelable(false)
            permissionDialog.show()
            btnRight.setOnClickListener {
                permissionDialog.dismiss()
                openPdf(context, file)
            }
            btnCancel.setOnClickListener { permissionDialog.dismiss() }
        }

        private fun openPdf(context: Activity, file: File) {
            try {
                val stringBuilder = StringBuilder()
                stringBuilder.append(context.packageName)
                stringBuilder.append(".provider")
                val uri = FileProvider.getUriForFile(context, stringBuilder.toString(), file)
                val intent = Intent("android.intent.action.VIEW")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setDataAndType(uri, "application/pdf")
                context.startActivity(intent)
                return
            } catch (activityNotFoundException: ActivityNotFoundException) {
                activityNotFoundException.printStackTrace()
                CustomCookieToast(context).showFailureToast(
                    "No application found to open pdf file",
                    "Ok"
                )
                return
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
                val stringBuilder = StringBuilder()
                stringBuilder.append("Unable to open ")
                stringBuilder.append(", please try again later.")
                stringBuilder.append(exception.message)

                CustomCookieToast(context).showFailureToast(stringBuilder.toString(), "Ok")
                return
            }
        }

        fun getPrettyTime(setTime: String): String {
            if (!TextUtils.isEmpty(setTime)) {
                val timeMilliSec = setTime.toLong()
                val date = Date(timeMilliSec * 1000)
                var time: String = PrettyTime().format(date)
                time = time.replace("moments ago", "just now")
                time = time.replace("moments from now", "just now")
                time = time.replace("1 minute ago", "just now")
                time = time.replace("2 minutes ago", "1 min ago")
                time = time.replace("3 minutes ago", "2 min ago")
                time = time.replace("4 minutes ago", "3 min ago")
                time = time.replace("5 minutes ago", "4 min ago")
                time = time.replace("6 minutes ago", "5 min ago")
                time = time.replace("minutes", "min")
                time = time.replace("moments", "sec")
                time = time.replace("minutes", "min")
                time = time.replace("seconds", "sec")
                time = time.replace("hours", "hrs")
                time = time.replace("days", "d")
                return time
            } else {
                return ""
            }
        }

        fun formatTimestamp(timestamp: String): String {
            // Convert the string timestamp to Long and then to milliseconds
            val timestampLong = timestamp.toLong() * 1000

            // Create a Date object from the timestamp
            val date = Date(timestampLong)

            // Define the desired format
            val format = SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.ENGLISH)
            format.timeZone = TimeZone.getTimeZone("UTC") // Set the time zone if needed

            // Format the date
            return format.format(date)
        }

        fun ShowPopUpMenu(context: Context?, parentView: TextView?, mainView: View, iconView: View?, menu: Int) {
            val popup = PopupMenu(context!!, parentView!!, Gravity.NO_GRAVITY, 0, R.style.popupMenu)
            popup.setOnMenuItemClickListener { item ->
                (mainView as TextView).text = item.title
                if (iconView != null) {
                    (iconView as TextInputLayout).startIconDrawable = item.icon
                    // Set drawable on the start (left) of the TextView
                    // val drawableStart: Drawable? = ContextCompat.getDrawable(context, item.icon)
                    parentView.setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null, null)
                }
                true
            }
            popup.inflate(menu)
            if (menu == R.menu.outputs || menu == R.menu.outputs) {
                popup.setForceShowIcon(false)
            } else {
                popup.setForceShowIcon(true)
            }
            popup.show()
        }

        fun sendSmsIntent(context: Activity, referralCode: String, packageName: String) {
            val smsBody =
                "Introducing KEDVICK: the ultimate mobile app for research, content creation, text-to-speech transcription, and image generation. Perfect for students, bloggers, and professionals. Download the app now and use this referral code: $referralCode\n" +
                        "\n" +
                        "App link: https://apps.kedvick.com\n"

            val uri = Uri.parse("smsto:")
            val sendIntent = Intent(Intent.ACTION_SENDTO, uri)
            sendIntent.putExtra("sms_body", smsBody)
            context.startActivity(sendIntent)
            context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        }

        fun sendEmailIntent(context: Activity, referralCode: String, packageName: String) {
            val subject = "KEDVICK AI Invitation"
            val body =
                "Introducing KEDVICK: the ultimate mobile app for research, content creation, text-to-speech transcription, and image generation. Perfect for students, bloggers, and professionals. Download the app now and use this referral code: $referralCode\n" +
                        "\n" +
                        "App link: https://apps.kedvick.com\n"


            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
            }
        }

        fun shareAppIntent(context: Activity, referralCode: String, packageName: String) {
            val message =
                "Introducing kedvick: the ultimate mobile app for research, content creation, text-to-speech transcription, and image generation. Perfect for students, bloggers, and professionals. Download the app now and use this referral code: $referralCode\n" +
                        "\n" +
                        "App link: https://apps.kedvick.com\n"
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("text/plain")
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                context.startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: java.lang.Exception) {
                //e.toString();
            }
        }

        fun openWebLink(context: Activity, url: String) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        fun openFacebookGroup(context: Activity, url: String) {
            val facebookAppUri = Uri.parse("fb://facewebmodal/f?href=$url")
            val webUri = Uri.parse(url)

            val facebookAppIntent = Intent(Intent.ACTION_VIEW, facebookAppUri)
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)

            try {
                context.startActivity(facebookAppIntent)
            } catch (e: ActivityNotFoundException) {
                // If the Facebook app is not installed, open the URL in a web browser
                context.startActivity(webIntent)
            }
        }

        fun openWhatsappGroupJoinLink(context: Activity, url: String) {
            val intentWhatsAppGroup = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(url)
            intentWhatsAppGroup.setData(uri)
            intentWhatsAppGroup.setPackage("com.whatsapp")
            context.startActivity(intentWhatsAppGroup)

            // context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        fun openGmail(context: Activity) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf("support@kedvick.com", "zrzunair10@gmail.com")
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Immediate Help Required")

            if (emailIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(emailIntent)
            }
        }

        fun setAppBrightness(activity: Activity, brightness: Float) {
            val window = activity.window
            val layoutParams = window.attributes
            layoutParams.screenBrightness = brightness
            window.attributes = layoutParams
        }

        fun updateAppVersion(context: Activity) {
            val appPackageName: String = context.packageName
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }

        fun launchInAppReview(activity: Activity?) {
            val reviewManager = ReviewManagerFactory.create(activity!!)
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo: ReviewInfo = task.result
                    val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        Log.d("ReviewStatus", "Review flow completed")
                    }
                } else {
                    Log.d("ReviewStatus", "Review flow failed")
                }
            }
        }

        fun shareImageAndCaption(context: Activity, userImage: String, text: String) {
            try {
                val url = URL(userImage)
                val policy = ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                val bitmap = BitmapFactory.decodeStream(url.openStream())

                val share = Intent(Intent.ACTION_SEND)
                share.setType("image/*")

                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "title")
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                val uri: Uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )!!

                val outStream: OutputStream
                try {
                    outStream = context.contentResolver.openOutputStream(uri)!!
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                    outStream.close()
                } catch (e: java.lang.Exception) {
                    System.err.println(e.toString())
                }

                share.putExtra(Intent.EXTRA_STREAM, uri)
                if (!TextUtils.isEmpty(text)) {
                    share.putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(share, "Share Image"))
            } catch (e: IOException) {
                println(e.message)
            }
        }



        private fun sharePdfAndCaption(context: Activity, pdfFile: File, caption: String) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    pdfFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "application/pdf"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                if (caption.isNotEmpty()) {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, caption)
                }
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SHARE_ERROR", "Failed to share PDF: ${e.message}")
            }
        }

        /*private fun sharePdfAndCaption(context: Activity, pdfFile: File, caption: String) {
            try {
                // Create a content URI for the PDF file
                val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", pdfFile)

                val share = Intent(Intent.ACTION_SEND)
                share.type = "application/pdf"
                share.putExtra(Intent.EXTRA_STREAM, uri)
                if (!TextUtils.isEmpty(caption)){
                    share.putExtra(Intent.EXTRA_TEXT, caption)
                }
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                context.startActivity(Intent.createChooser(share, "Share PDF"))
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }*/

        fun getPDFFilePath(context: Activity): File {
            val file = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
                ), "/" + context.resources.getString(R.string.app_name)
            )
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    CustomCookieToast(context).showFailureToast("Can't create directory to save pdf.")
                    Toast.makeText(
                        context,
                        "Directory Error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            return file
        }

        private fun generateRandomNumbers(count: Int): List<Int> {
            val randomNumbers = mutableListOf<Int>()
            repeat(count) {
                randomNumbers.add(Random.nextInt())
            }
            return randomNumbers
        }

        fun setPasswordAddedValue(context: Activity, passwordAdded: Int) {
            if (passwordAdded == 0) {
                SessionAndCookies.saveBoolean(context, Constant.IS_PASSWORD_ADDED, false)
            } else {
                SessionAndCookies.saveBoolean(context, Constant.IS_PASSWORD_ADDED, true)
            }
        }

        fun generateRandomNumbers(): String {
            return List(5) { Random.nextInt(10) }.joinToString("")
        }

        fun downloadAndSharePdf(context: Activity, content: String, caption: String) {
            val pdfName = "kedvick ${generateRandomNumbers(1)}"
            val htmlToPdfConvertor = HtmlToPdfConvertor(context)
            htmlToPdfConvertor.setBaseUrl("file:///android_asset/images/")

            val html = "<h2 style=\"text-align: center;\"><strong>$pdfName</strong></h2>$content"

            htmlToPdfConvertor.convert(
                File(
                    getPDFFilePath(context),
                    pdfName + "_" + System.currentTimeMillis() + ".pdf"
                ), html, { e: Exception ->
                    Log.d("PDF_ERROR", e.message.toString())
                    null
                }) { file: File? ->
                if (file != null) {
                    sharePdfAndCaption(context, file, caption)
                }
            }
        }

    }
}