package com.app.panditji.ui.login

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.MainActivity
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.model.get_services.GetAllServicesResponse
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import com.app.panditji.databinding.FragmentRegisterBinding
import com.app.panditji.ui.dashboard.LanguageAdapter
import com.app.panditji.ui.dashboard.ServicesAdapter
import com.app.panditji.utils.extensions.getString
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val apiVm by viewModel<apiVm>()
    private var progresbar: Dialog? = null
    private val prefs by inject<PrefsHelper>()

    // Aadhaar image variables
    private var profileImageFile: File? = null

    var selectedDate = ""
    private lateinit var imageUri: Uri

    private var selectedServices = mutableListOf<GetAllServicesResponse.Payload.Service>()

    private var servicesList: List<GetAllServicesResponse.Payload.Service> = listOf()
    private var progressBar: Dialog? = null

    private var selectedLanguages = mutableListOf<String>()
    private var profileImageUrl = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        initObservers()
        notificationPermission()
        getAllServices()
        binding.profileImage.setOnClickListener {
            showImagePickerBottomSheet()
        }

        binding.etServices.setOnClickListener {
            selectServices()
        }

        binding.clSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.etLanguage.setOnClickListener {
            selectLanguage()
        }
        return binding.root
    }

    private fun getAllServices() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getAllServices()
            .observe(
                requireActivity()
            ) { it ->
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload?.services
                        servicesList = data ?: listOf()
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                requireActivity().toast("No Internet")
                            }

                            else -> {
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorCode}")
                                Log.e(
                                    "TAG",
                                    "loginUser: ${it.errorBody?.getError()?.errorMessage}",
                                )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.statusCode}")
                                it.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                    requireActivity().toast(errorMessage)
                                }
                            }
                        }
                    }

                    else -> {

                    }
                }
            }
    }

    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun initObservers() {
        alreadyHaveAccount()
        AppUtils.setupHideKeyboardOnTouch(binding.root, requireActivity())

        binding.btnSignUp.setOnClickListener {
            validation()
        }
    }

    private fun validation() {

        val etName = binding.etName.getString()
        val etLastName = binding.etLastName.getString()
        val etPhone = binding.etPhoneNumber.getString()
        val etEmail = binding.etEmail.getString()
        val etPassword = binding.etPassword.getString()
        val dob = selectedDate
        val etGotra = binding.etGotra.getString()
        val prawar = binding.etPrawar.getString()
        val veda = binding.etVeda.getString()
        val pankti = binding.etPankti.getString()
        val shakha = binding.etShakha.getString()
        val sutra = binding.etSutra.getString()
        val aadharNumber = binding.etAadharNumber.getString()
        val experience = binding.etExperience.getString()
        val etAbout = binding.etAbout.getString()
        val address = binding.address.getString()
        val state = binding.state.getString()
        val city = binding.city.getString()
        val pincode = binding.pincode.getString()
        val country = binding.country.getString()
        val referralCode = binding.referralCode.getString()

        when {
            profileImageFile == null -> toast(getString(R.string.please_upload_profile_image))
            etName.isEmpty() -> toast(getString(R.string.please_enter_first_name))
            etLastName.isEmpty() -> toast(getString(R.string.please_enter_last_name))
            etPhone.isEmpty() -> toast(getString(R.string.please_enter_phone_number))
            etEmail.isEmpty() -> toast(getString(R.string.please_enter_email))
            !AppUtils.isValidEmailId(etEmail) -> toast(getString(R.string.please_enter_valid_email))
            etPassword.isEmpty() -> toast(getString(R.string.please_enter_password))
            dob.isEmpty() -> toast(getString(R.string.error_select_dob))
            prawar.isEmpty() -> toast(getString(R.string.error_enter_prawar))
            veda.isEmpty() -> toast(getString(R.string.error_enter_veda))
            shakha.isEmpty() -> toast(getString(R.string.error_enter_shakha))
            pankti.isEmpty() -> toast(getString(R.string.error_enter_pankti))
            sutra.isEmpty() -> toast(getString(R.string.error_enter_sutra))
            aadharNumber.isEmpty() -> toast(getString(R.string.error_enter_your_aadhar_number))
            selectedLanguages.isEmpty() -> toast(getString(R.string.error_select_at_least_one_language))
            experience.isEmpty() -> toast(getString(R.string.error_enter_your_experience))
            etAbout.isEmpty() -> toast(getString(R.string.error_enter_about))
            address.isEmpty() -> toast(getString(R.string.error_enter_address))
            state.isEmpty() -> toast(getString(R.string.error_enter_state))
            city.isEmpty() -> toast(getString(R.string.error_enter_city))
            pincode.isEmpty() -> toast(getString(R.string.error_enter_pincode))
            country.isEmpty() -> toast(getString(R.string.error_enter_country))

            else -> {
                uploadProfileImageAndRegister(
                    etName, etLastName, etPhone, etEmail, etPassword,
                    dob, etAbout, etGotra, prawar, veda, pankti,
                    shakha, sutra, aadharNumber, experience,
                    address, city, state, pincode, country, referralCode
                )
            }
        }
    }

    private fun uploadProfileImageAndRegister(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        dob: String,
        about: String,
        gotra: String,
        pravar: String,
        veda: String,
        pankti: String,
        shakha: String,
        sutra: String,
        aadharNumber: String,
        experience: String,
        address: String,
        city: String,
        state: String,
        pincode: String,
        country: String,
        referralCode: String?,
    ) {

        progresbar = AppUtils.progressDialog(requireActivity())

        val requestFile = profileImageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart =
            MultipartBody.Part.createFormData("file", profileImageFile!!.name, requestFile)
        val customerIdRB = "0".toRequestBody("text/plain".toMediaTypeOrNull())

        apiVm.uploadImage(filePart, customerIdRB)
            .observe(viewLifecycleOwner) { result ->

                when (result) {
                    is Resource.Success -> {
                        val profileImageUrl = result.data?.payload?.url

                        if (profileImageUrl.isNullOrEmpty()) {
                            progresbar?.dismiss()
                            toast("Image upload failed")
                            return@observe
                        }

                        registerUserMultipart(
                            firstName,
                            lastName,
                            phone,
                            email,
                            password,
                            "male",
                            dob,
                            about,
                            gotra,
                            pravar,
                            veda,
                            pankti,
                            shakha,
                            sutra,
                            aadharNumber,
                            experience,
                            address,
                            city,
                            state,
                            pincode,
                            country,
                            profileImageUrl,
                            referralCode
                        )
                    }

                    is Resource.Error -> {
                        progresbar?.dismiss()
                        toast("Failed to upload profile image")
                    }
                }
            }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        // Open with previously selected date if available
        if (selectedDate.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            calendar.time = sdf.parse(selectedDate) ?: calendar.time
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = formatter.format(selectedCalendar.time)

                binding.etDob.text = formattedDate
                selectedDate = formattedDate
            },
            year,
            month,
            day
        )

        // 🚫 Disable future dates
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }


    private fun toast(msg: String) =
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()

    // -----------------------------------------------------
    // Image Picker BottomSheet
    // -----------------------------------------------------
    private fun showImagePickerBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottomsheet_open_camera_gallery_box, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)

        val camera = view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.openCamera)
        val gallery =
            view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.openGallery)

        camera.setOnClickListener {
            checkPermissionForCamera()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
//            checkPermissionForGallery()
            openGallery()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkPermissionForCamera() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }


    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile = File(
            requireContext().cacheDir,
            "temp_image_${System.currentTimeMillis()}.jpg"
        )

        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        imageUri = photoUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                Glide.with(this).load(it).into(binding.profileImage)
                handleSelectedImage(it)
            }
        }

    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, IMAGE_PICK_CODE)
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {

            CAMERA_REQUEST_CODE -> {
                Glide.with(this).load(imageUri).into(binding.profileImage)
                handleSelectedImage(imageUri)
            }

            IMAGE_PICK_CODE -> {
                data?.data?.let { uri ->
                    Glide.with(this).load(uri).into(binding.profileImage)
                    handleSelectedImage(uri)
                }
            }
        }
    }

    private fun handleSelectedImage(uri: Uri) {
        val file = uriToFile(uri)
        profileImageFile = file
//        aadhaarMimeType = getMimeTypeFromUri(uri)
//        isAadhaarImageSelected = true
    }

    private fun uriToFile(uri: Uri): File {
        val ext = getMimeTypeFromUri(uri).substringAfter("/")
        val file = File(requireContext().cacheDir, "temp_$ext.jpg")

        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { out -> input.copyTo(out) }
        }
        return file
    }

    private fun getMimeTypeFromUri(uri: Uri): String {
        return requireContext().contentResolver.getType(uri) ?: "image/jpeg"
    }

    // -----------------------------------------------------
    // Multipart Register User
    // -----------------------------------------------------
    private fun registerUserMultipart(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        gender: String,
        dob: String,
        about: String,
        gotra: String,
        pravar: String,
        veda: String,
        pankti: String,
        shakha: String,
        sutra: String,
        aadharNumber: String,
        experience: String,
        address: String,
        city: String,
        state: String,
        pincode: String,
        country: String,
        profileImageUrl: String,
        referralCode: String?,
    ) {
        fun String.rb() = this.toRequestBody("text/plain".toMediaTypeOrNull())

        // ─── Text RequestBodies ─────────────────────────────
        val firstNameRB = firstName.rb()
        val lastNameRB = lastName.rb()
        val phoneRB = phone.rb()
        val emailRB = email.rb()
        val passwordRB = password.rb()
        val genderRB = gender.rb()
        val dobRB = dob.rb()
        val aboutRB = about.rb()

        val gotraRB = gotra.rb()
        val pravarRB = pravar.rb()
        val vedaRB = veda.rb()
        val panktiRB = pankti.rb()
        val shakhaRB = shakha.rb()
        val sutraRB = sutra.rb()

        val aadharRB = aadharNumber.rb()
        val experienceRB = experience.rb()

        val addressRB = address.rb()
        val cityRB = city.rb()
        val stateRB = state.rb()
        val zipRB = pincode.rb()
        val countryRB = country.rb()
        val referralCodeRB = referralCode?.rb()

        val deviceTypeRB = "android".rb()
        val deviceTokenRB = prefs.fcmToken.rb()

        // ─── Services Multipart ─────────────────────────────
        val serviceParts = selectedServices.mapIndexed { index, service ->
            MultipartBody.Part.createFormData("services", service.id)

        }

        // ─── Languages Multipart (same as services) ─────────
        val languageParts = selectedLanguages.mapIndexed { index, lang ->
            MultipartBody.Part.createFormData("language", lang)

        }
        val image = profileImageUrl.rb()

        // ─── Profile Image (optional) ───────────────────────
//        val imagePart = profileImageFile?.let {
//            val req = it.asRequestBody("image/*".toMediaTypeOrNull())
//            MultipartBody.Part.createFormData("image", it.name, req)
//        }
        /*logRegisterRequest(
            firstNameRB,
            lastNameRB,
            emailRB,
            passwordRB,
            genderRB,
            phoneRB,
            aboutRB,
            addressRB,
            cityRB,
            stateRB,
            countryRB,
            zipRB,
            gotraRB,
            panktiRB,
            shakhaRB,
            vedaRB,
            sutraRB,
            pravarRB,
            aadharRB,
            dobRB,
            experienceRB,
            deviceTypeRB,
            deviceTokenRB,
            image,
            serviceParts,
            languageParts
        )*/


        apiVm.registerUserMultipart(
            firstNameRB,
            lastNameRB,
            emailRB,
            passwordRB,
            genderRB,
            phoneRB,
            aboutRB,
            addressRB,
            cityRB,
            stateRB,
            countryRB,
            zipRB,
            gotraRB,
            panktiRB,
            shakhaRB,
            vedaRB,
            sutraRB,
            pravarRB,
            aadharRB,
            dobRB,
            experienceRB,
            deviceTypeRB,
            deviceTokenRB,
            image,
            "vendor".rb(),
            serviceParts,
            languageParts,
            referralCodeRB
        ).observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    progresbar?.dismiss()

                    val data = it.data?.payload
                    toast(it.data?.message ?: "")

                    if (it.data?.code == 201) {
                        prefs.firstName = data?.customer?.firstName.orEmpty()
                        prefs.lastName = data?.customer?.lastName.orEmpty()
                        prefs.authToken = data?.accessToken.orEmpty()
                        prefs.userId = data?.customer?.id.orEmpty()
                        prefs.email = data?.customer?.email.orEmpty()
                        prefs.phone = data?.customer?.phone.orEmpty()
                        prefs.profileImage = data?.customer?.cover?.url.orEmpty()
                        prefs.isLoggedIn = true

                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }

                is Resource.Error -> {
                    progresbar?.dismiss()
                    toast(it.errorBody?.getError()?.errorMessage ?: "Something went wrong")
                }
            }
        }
    }


    // -----------------------------------------------------
    // Already Have Account
    // -----------------------------------------------------
    private fun alreadyHaveAccount() {
        val fullText = getString(R.string.already_have_an_account_sign_in)
        val spannable = SpannableString(fullText)

        val click = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                ds.isUnderlineText = true
            }
        }

        val start = fullText.indexOf(getString(R.string.sign_in))
        val end = start + getString(R.string.sign_in).length

        spannable.setSpan(click, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvAlreadyHaveAccount.text = spannable
        binding.tvAlreadyHaveAccount.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun selectLanguage() {
        val dialogView = layoutInflater.inflate(R.layout.language_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvLanguages)
        val btnAdd =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)

        // Full list of languages (you can come from API or static)
        val languageList = listOf(
            "Hindi",
            "English",
            "Marathi",
            "Sanskrit",
            "Bangali",
            "Gujarati",
            "Odia",
            "Tamil",
            "Telugu",
            "Kannada",
            "Malayalam",
            "Others"
        )

        // Already selected ones (capitalize matching)
        val selectedLanguages = mutableListOf<String>()
        selectedLanguages.addAll(
            binding.etLanguage.text.split(", ").filter { it.isNotEmpty() }
        )

        val adapter = LanguageAdapter(languageList, selectedLanguages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedLanguages.joinToString(", ")
            binding.etLanguage.text = selectedDisplay

            // Store lowercase version for API
            this.selectedLanguages = selectedLanguages.map { it.lowercase() }.toMutableList()

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun selectServices() {
        val dialogView = layoutInflater.inflate(R.layout.language_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvLanguages)
        val btnAdd =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedServices = selectedServices
        binding.etServices.setText(
            selectedServices.joinToString(", ") { it.poojaType }
        )

        title.text = "Services"
        description.text = "Select your services"
        Log.i("TAG", "selectServices: ${selectedServices}")
        val adapter = ServicesAdapter(servicesList, selectedServices)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedServices.joinToString(", ") { it.poojaType }
            binding.etServices.text = selectedDisplay

            // Store lowercase version for API
            this.selectedServices = selectedServices.map { it }.toMutableList()

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }


    companion object {
        private const val CAMERA_REQUEST_CODE = 1010
        private const val IMAGE_PICK_CODE = 2020
        private const val CAMERA_PERMISSION_CODE = 1515
        private const val GALLERY_PERMISSION_CODE = 2525
    }
    fun String.rb() = this.toRequestBody("text/plain".toMediaTypeOrNull())
    private fun RequestBody.asString(): String {
        return try {
            val buffer = okio.Buffer()
            this.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            "unable-to-read"
        }
    }
    private fun logRegisterRequest(
        firstNameRB: RequestBody,
        lastNameRB: RequestBody,
        emailRB: RequestBody,
        passwordRB: RequestBody,
        genderRB: RequestBody,
        phoneRB: RequestBody,
        aboutRB: RequestBody,
        addressRB: RequestBody,
        cityRB: RequestBody,
        stateRB: RequestBody,
        countryRB: RequestBody,
        zipRB: RequestBody,
        gotraRB: RequestBody,
        panktiRB: RequestBody,
        shakhaRB: RequestBody,
        vedaRB: RequestBody,
        sutraRB: RequestBody,
        pravarRB: RequestBody,
        aadharRB: RequestBody,
        dobRB: RequestBody,
        experienceRB: RequestBody,
        deviceTypeRB: RequestBody,
        deviceTokenRB: RequestBody,
        imagePart: RequestBody,
        serviceParts: List<MultipartBody.Part>,
        languageParts: List<MultipartBody.Part>
    ) {

        Log.d("REGISTER_API", "firstName = ${firstNameRB.asString()}")
        Log.d("REGISTER_API", "lastName = ${lastNameRB.asString()}")
        Log.d("REGISTER_API", "email = ${emailRB.asString()}")
        Log.d("REGISTER_API", "password = ${passwordRB.asString()}")
        Log.d("REGISTER_API", "gender = ${genderRB.asString()}")
        Log.d("REGISTER_API", "phone = ${phoneRB.asString()}")

        Log.d("REGISTER_API", "about = ${aboutRB.asString()}")
        Log.d("REGISTER_API", "address = ${addressRB.asString()}")
        Log.d("REGISTER_API", "city = ${cityRB.asString()}")
        Log.d("REGISTER_API", "state = ${stateRB.asString()}")
        Log.d("REGISTER_API", "country = ${countryRB.asString()}")
        Log.d("REGISTER_API", "zip = ${zipRB.asString()}")

        Log.d("REGISTER_API", "gotra = ${gotraRB.asString()}")
        Log.d("REGISTER_API", "pankti = ${panktiRB.asString()}")
        Log.d("REGISTER_API", "shakha = ${shakhaRB.asString()}")
        Log.d("REGISTER_API", "veda = ${vedaRB.asString()}")
        Log.d("REGISTER_API", "sutra = ${sutraRB.asString()}")
        Log.d("REGISTER_API", "pravar = ${pravarRB.asString()}")

        Log.d("REGISTER_API", "aadhar = ${aadharRB.asString()}")
        Log.d("REGISTER_API", "dob = ${dobRB.asString()}")
        Log.d("REGISTER_API", "experience = ${experienceRB.asString()}")

        Log.d("REGISTER_API", "deviceType = ${deviceTypeRB.asString()}")
        Log.d("REGISTER_API", "deviceToken = ${deviceTokenRB.asString()}")
        Log.d("REGISTER_API", "image = ${imagePart.asString()}")

        serviceParts.forEachIndexed { index, part ->
            Log.d(
                "REGISTER_API",
                "services[$index] = ${part.headers?.get("Content-Disposition")}"
            )
        }

        languageParts.forEachIndexed { index, part ->
            Log.d(
                "REGISTER_API",
                "language[$index] = ${part.headers?.get("Content-Disposition")}"
            )
        }
    }


}