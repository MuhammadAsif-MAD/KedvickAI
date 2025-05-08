package com.kedvick.ai.ui.settings.inviteFriends.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.settings.inviteFriends.adapters.ContactsAdapter
import com.kedvick.ai.ui.settings.inviteFriends.model.ContactsModel
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityContactsListBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.utils.SessionAndCookies
import java.util.Locale

open class ContactsListActivity : BaseActivity(), LoaderManager.LoaderCallbacks<Cursor>, ContactsAdapter.InviteFriendContactAdapterCallBacks {

    private val CONTACTS_PERMISSION = arrayOf(Manifest.permission.READ_CONTACTS)
    private val PROJECTION_NUMBERS = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER)
    private val PROJECTION_DETAILS = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

    private lateinit var contactsAdapter: ContactsAdapter
    private val contactsList = ArrayList<ContactsModel>()
    private var phones = HashMap<Long, MutableList<String>>()

    private lateinit var gallerySettingsIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestContactPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestContactPermissionLauncherEnd: ActivityResultLauncher<Array<String>>

    private lateinit var referralCode: String
    private lateinit var binding: ActivityContactsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ContactsListActivity

        init()
    }

    private fun init() {
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        binding.layoutPermissionDenied.visibility = View.GONE
        referralCode = SessionAndCookies.getReferralCode(context)?:""

        initLaunchers()
        initClickListeners()
        checkContactsPermissions()
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.rlLater.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.edSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun checkContactsPermissions() {
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            initLoaderManager()
        } else {
            requestContactPermissionLauncher.launch(CONTACTS_PERMISSION)
        }
    }

    private fun initLaunchers() {
        requestContactPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val permissionGranted = result.values.all { it }
            if (permissionGranted) {
                initLoaderManager()
            } else {
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.layoutPermissionDenied.visibility = View.VISIBLE

                binding.btnGoToSettings.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    gallerySettingsIntentLauncher.launch(intent)
                }
            }
        }

        requestContactPermissionLauncherEnd = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val permissionGranted = result.values.all { it }
            if (permissionGranted) {
                showContactsRecyclerView(binding.recyclerView, contactsList)
            } else {
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.layoutPermissionDenied.visibility = View.VISIBLE

                binding.btnGoToSettings.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    gallerySettingsIntentLauncher.launch(intent)
                }
            }
        }

        gallerySettingsIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkContactsPermissions()
        }
    }

    private fun initLoaderManager() {
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        binding.layoutPermissionDenied.visibility = View.GONE
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    private fun filter(text: String) {
        val modelList: ArrayList<ContactsModel> = java.util.ArrayList<ContactsModel>()
        for (item in contactsList) {
            if (item.name.lowercase().trim().contains(
                    text.lowercase(Locale.getDefault()).trim { it <= ' ' }) || item.phoneNumber
                    .lowercase().trim().contains(text.lowercase(
                        Locale.getDefault()
                    ).trim { it <= ' ' })) {
                modelList.add(item)
            }
        }
        contactsAdapter.filerList(modelList)
    }
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            0 -> CursorLoader(context, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION_NUMBERS, null, null, null)
            else -> CursorLoader(context, ContactsContract.Contacts.CONTENT_URI, PROJECTION_DETAILS, null, null, null)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            0 -> {
                phones = HashMap()
                data?.use {
                    if (!it.isClosed){
                        while (it.moveToNext()) {
                            val contactId = it.getLong(0)
                            val phone = it.getString(1)
                            val list = phones[contactId] ?: ArrayList()
                            list.add(phone)
                            phones[contactId] = list
                        }
                    }
                }
                LoaderManager.getInstance(this).initLoader(1, null, this)
            }
            1 -> {
                data?.use {
                    if (!it.isClosed){
                        while (it.moveToNext()) {
                            val contactId = it.getLong(0)
                            val name = it.getString(1)
                            phones[contactId]?.forEach { phone ->
                                addContact(ContactsModel(name, phone))
                            }
                        }
                    }

                    if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        showContactsRecyclerView(binding.recyclerView, contactsList)
                    } else {
                        requestContactPermissionLauncherEnd.launch(CONTACTS_PERMISSION)
                    }
                }
            }
        }
    }

    override fun onLoaderReset(@NonNull loader: Loader<Cursor>) {}

    private fun addContact(contact: ContactsModel) {
        contactsList.add(contact)
    }

    private fun showContactsRecyclerView(recyclerView: RecyclerView, contactsList: ArrayList<ContactsModel>) {
        removeDuplicatesAndMakeThemAlphabetically()

        binding.topLayoutLoader.layoutLoader.visibility = View.GONE
        binding.layoutPermissionDenied.visibility = View.GONE
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        contactsAdapter = ContactsAdapter(context, contactsList, this)
        recyclerView.adapter = contactsAdapter
    }

    override fun onInviteFriendContact(contactsModel: ContactsModel) {
        val smsBody = "Introducing KEDVICK: the ultimate mobile app for research, content creation, text-to-speech transcription, and image generation. Perfect for students, bloggers, and professionals. Download the app now and use this referral code: $referralCode\n" +
                "\n" +
                "App link: https://apps.kedvick.com\n"
        val phoneNumber = "smsto:" + contactsModel.phoneNumber
        val uri = Uri.parse(phoneNumber)
        val sendIntent = Intent(Intent.ACTION_SENDTO, uri)
        sendIntent.putExtra("sms_body", smsBody)
        startActivity(sendIntent)
        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
    }

    private fun removeDuplicatesAndMakeThemAlphabetically() {
        val oldContacts = contactsList.toMutableList()
        contactsList.clear()
        contactsList.addAll(oldContacts.distinctBy { it.name })
        contactsList.sortBy { it.name }
    }
}