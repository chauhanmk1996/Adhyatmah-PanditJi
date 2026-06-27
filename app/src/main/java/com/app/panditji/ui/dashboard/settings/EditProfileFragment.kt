package com.app.panditji.ui.dashboard.settings

import UpdateProfile
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.model.get_profile.GetProfileResponse
import com.app.panditji.data.model.get_services.GetAllServicesResponse
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentEditProfileBinding
import com.app.panditji.ui.dashboard.LanguageAdapter
import com.app.panditji.ui.dashboard.ServicesAdapter
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.AppUtils.convertToYyyyMmDd
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.getString
import com.app.panditji.utils.extensions.toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.getValue

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()
    var selectedGender = ""
    var selectedDate = ""

    private lateinit var imageView: ImageView
    private lateinit var imageUri: Uri
    private var selectedLanguages = mutableListOf<String>()
    private var selectedServices = mutableListOf<GetAllServicesResponse.Payload.Service>()

    private var file: File? = null
    private var mimeType: String = ""
    private var isImageUpdated = false


    //    private lateinit var aadhaarImageView: ImageView
    private var aadhaarFile: File? = null
    private var aadhaarMimeType: String = ""
    private var isAadhaarImageUpdated = false
    private var currentImageForAadhaar = false

    private var servicesList: List<GetAllServicesResponse.Payload.Service> = listOf()


    companion object {
        const val CAMERA_REQUEST_CODE = 1001
        const val IMAGE_PICK_CODE = 1002
        const val CAMERA_PERMISSION_CODE = 1003
//        const val GALLERY_PERMISSION_CODE = 1004
    }

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                val fileObj = uriToFileSafe(it)
                val imageTargetView = binding.profileImage

                Glide.with(this).load(fileObj).into(imageTargetView)

                if (currentImageForAadhaar) {
                    aadhaarFile = fileObj
                    aadhaarMimeType = getMimeTypeFromUri(it)
                    isAadhaarImageUpdated = true
                } else {
                    file = fileObj
                    mimeType = getMimeTypeFromUri(it)
                    isImageUpdated = true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()


        imageView = binding.profileImage

//        aadhaarImageView = binding.uploadAadhaarImage

        /*aadhaarImageView.setOnClickListener {
            // Similar to profile image — let’s reuse the picker
            showImagePickerBottomSheet(isForAadhaar = true)
        }*/


        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.frImage.setOnClickListener {
            showImagePickerBottomSheet()
        }

        binding.clSelectDate.setOnClickListener {
            showDatePicker()
        }
        binding.etLanguage.setOnClickListener {
            selectLanguage()
        }

        binding.etServices.setOnClickListener {
            selectServices()
        }

        val genderOptions = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            genderOptions
        )
        binding.mySpinner.adapter = adapter
        binding.mySpinner.setSelection(0) // set default

        binding.mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                selectedGender = genderOptions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

    }

    private fun initViews() {
        getUserDetail()
        lifecycleScope.launch {
            delay(500)
            getAllServices()
        }

        AppUtils.setupHideKeyboardOnTouch(binding.root, requireActivity())

        binding.btnSave.setOnClickListener {
            if (checkValidations()) {
                val model = UpdateProfile(
                    firstName = binding.etName.getString(),
                    lastName = binding.etLastName.getString(),
                    email = binding.etEmail.getString(),
                    phone = binding.etPhoneNumber.getString(),
                    gender = selectedGender.toLowerCase(Locale.ROOT),
                    about = binding.etAbout.getString(),
                    gotra = binding.etGotra.getString(),
                    pravar = binding.etPrawar.getString(),
                    dateOfBirth = if (selectedDate.isNotEmpty()) selectedDate else null,
                    veda = binding.etVeda.getString(),
                    pankti = binding.etPankti.getString(),
                    shakha = binding.etShakha.getString(),
                    sutra = binding.etSutra.getString(),
                    address = binding.address.getString(),
                    state = binding.state.getString(),
                    city = binding.city.getString(),
                    zip = binding.pincode.getString(),
                    country = binding.country.getString(),
                    language = selectedLanguages.map { it.lowercase() }.toMutableList(),
                    services = selectedServices.map { it.id },
                    aadhar = binding.etAadhar.getString()
                )
                when {
//                    isImageUpdated && isAadhaarImageUpdated -> uploadBothImages(model)
                    isImageUpdated -> uploadMedia(model)
//                    isAadhaarImageUpdated -> uploadAadhaarImage(model)
                    else -> updateProfile(model)
                }

            }
        }

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

    private fun uploadBothImages(request: UpdateProfile) {
        if (file == null || aadhaarFile == null) {
            Toast.makeText(requireContext(), "Please select both images", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar = AppUtils.progressDialog(requireActivity())

        // Upload profile image first
        val requestFileProfile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
        val filePartProfile =
            MultipartBody.Part.createFormData("file", file!!.name, requestFileProfile)
        val customerIdBody = prefs.userId.toRequestBody("text/plain".toMediaTypeOrNull())

        apiVm.uploadImage(filePartProfile, customerIdBody)
            .observe(requireActivity()) { resultProfile ->
                when (resultProfile) {
                    is Resource.Success -> {
                        val profileUrl = resultProfile.data?.payload?.url
                        if (profileUrl != null) {
                            request.image = profileUrl

                            // Now upload Aadhaar image
                            val requestFileAadhaar =
                                aadhaarFile!!.asRequestBody("image/*".toMediaTypeOrNull())
                            val filePartAadhaar = MultipartBody.Part.createFormData(
                                "file",
                                aadhaarFile!!.name,
                                requestFileAadhaar
                            )

                            apiVm.uploadImage(filePartAadhaar, customerIdBody)
                                .observe(requireActivity()) { resultAadhaar ->
                                    progressBar?.dismiss()
                                    when (resultAadhaar) {
                                        is Resource.Success -> {
                                            val aadhaarUrl = resultAadhaar.data?.payload?.url
                                            if (aadhaarUrl != null) {
                                                request.aadhar = aadhaarUrl
                                                updateProfile(request)
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Failed to get Aadhaar URL",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        is Resource.Error -> {
                                            Toast.makeText(
                                                requireContext(),
                                                "Failed to upload Aadhaar image",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                        } else {
                            progressBar?.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload profile image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Failed to upload profile image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }


    private fun checkValidations(): Boolean {

        val name = binding.etName.getString()
        val lastName = binding.etLastName.getString()
        val email = binding.etEmail.getString()
        val phone = binding.etPhoneNumber.getString()
        val about = binding.etAbout.getString()
        val gotra = binding.etGotra.getString()
        val prawar = binding.etPrawar.getString()
        val dob = selectedDate
        val gender = selectedGender
        val language = selectedLanguages
        val veda = binding.etVeda.getString()
        val pankti = binding.etPankti.getString()
        val shakha = binding.etShakha.getString()
        val sutra = binding.etSutra.getString()
        val address = binding.address.getString()
        val state = binding.state.getString()
        val city = binding.city.getString()
        val pincode = binding.pincode.getString()
        val country = binding.country.getString()
        val aadhar = binding.etAadhar.getString()

        if (name.isEmpty()) {
            showToast(getString(R.string.error_enter_first_name))
            binding.etName.requestFocus()
            return false
        }

        if (lastName.isEmpty()) {
            showToast(getString(R.string.error_enter_last_name))
            binding.etLastName.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            showToast(getString(R.string.error_enter_email))
            binding.etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(getString(R.string.error_invalid_email))
            binding.etEmail.requestFocus()
            return false
        }

        if (phone.isEmpty()) {
            showToast(getString(R.string.error_enter_phone))
            binding.etPhoneNumber.requestFocus()
            return false
        }

        if (gender.isNullOrEmpty()) {
            showToast(getString(R.string.error_select_gender))
            return false
        }

        if (about.isEmpty()) {
            showToast(getString(R.string.error_enter_about))
            binding.etAbout.requestFocus()
            return false
        }

        if (gotra.isEmpty()) {
            showToast(getString(R.string.error_enter_gotra))
            binding.etGotra.requestFocus()
            return false
        }

        if (prawar.isEmpty()) {
            showToast(getString(R.string.error_enter_prawar))
            binding.etPrawar.requestFocus()
            return false
        }

        if (dob.isEmpty()) {
            showToast(getString(R.string.error_select_dob))
            return false
        }

        if (language.isEmpty()) {
            showToast(getString(R.string.error_select_language))
            return false
        }

        if (veda.isEmpty()) {
            showToast(getString(R.string.error_enter_veda))
            binding.etVeda.requestFocus()
            return false
        }

        if (pankti.isEmpty()) {
            showToast(getString(R.string.error_enter_pankti))
            binding.etPankti.requestFocus()
            return false
        }

        if (shakha.isEmpty()) {
            showToast(getString(R.string.error_enter_shakha))
            binding.etShakha.requestFocus()
            return false
        }

        if (sutra.isEmpty()) {
            showToast(getString(R.string.error_enter_sutra))
            binding.etSutra.requestFocus()
            return false
        }

        if (address.isEmpty()) {
            showToast(getString(R.string.error_enter_address))
            binding.address.requestFocus()
            return false
        }

        if (state.isEmpty()) {
            showToast(getString(R.string.error_enter_state))
            binding.state.requestFocus()
            return false
        }

        if (city.isEmpty()) {
            showToast(getString(R.string.error_enter_city))
            binding.city.requestFocus()
            return false
        }

        if (pincode.isEmpty()) {
            showToast(getString(R.string.error_enter_pincode))
            binding.pincode.requestFocus()
            return false
        }

        if (country.isEmpty()) {
            showToast(getString(R.string.error_enter_country))
            binding.country.requestFocus()
            return false
        }

        if (selectedLanguages.isEmpty()) {
            showToast(getString(R.string.error_select_at_least_one_language))
            return false
        }
        if (aadhar.isEmpty()) {
            showToast(getString(R.string.error_enter_your_aadhar_number))
            return false
        }

        return true
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }


    private fun showImagePickerBottomSheet(isForAadhaar: Boolean = false) {
        val view = layoutInflater.inflate(R.layout.bottomsheet_open_camera_gallery_box, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)

        val camera = view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.openCamera)
        val gallery =
            view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.openGallery)

        camera.setOnClickListener {
            currentImageForAadhaar = isForAadhaar
            checkPermissionForCamera()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            currentImageForAadhaar = isForAadhaar
            openPhotoPicker()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openPhotoPicker() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun getUserDetail() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.userProfile(prefs.userId)
            .observe(
                requireActivity()
            ) { it ->
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload
                        setUserData(data)
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                requireActivity().toast("No Internet")
                            }

                            else -> {
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

    private fun updateProfile(model: UpdateProfile?) {
        model?.accessToken = prefs.authToken
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.userUpdateProfile(prefs.authToken, model)
            .observe(
                requireActivity()
            ) { it ->
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload
                        prefs.firstName = model?.firstName ?: ""
                        prefs.lastName = model?.lastName ?: ""
                        prefs.email = model?.email ?: ""
                        prefs.phone = model?.phone ?: ""
                        if (model?.image?.isNotEmpty() == true && model.image != null) prefs.profileImage =
                            model.image ?: ""
                        findNavController().popBackStack()
                        Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                requireActivity().toast("No Internet")
                            }

                            else -> {
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

    private fun uploadMedia(request: UpdateProfile) {
        uploadGenericImage(file, "profile", request) { imageUrl ->
            request.image = imageUrl
            updateProfile(request)
        }
    }

    private fun uploadAadhaarImage(request: UpdateProfile) {
        uploadGenericImage(aadhaarFile, "aadhaar", request) { imageUrl ->
            request.aadhar = imageUrl  // add this property in your UpdateProfile model
            updateProfile(request)
        }
    }

    private fun uploadGenericImage(
        imageFile: File?,
        type: String,
        request: UpdateProfile,
        onSuccess: (String) -> Unit,
    ) {
        if (imageFile == null) {
            Toast.makeText(requireContext(), "No $type image selected", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar = AppUtils.progressDialog(requireActivity())
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
        val customerIdBody = prefs.userId.toRequestBody("text/plain".toMediaTypeOrNull())

        apiVm.uploadImage(filePart, customerIdBody).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val imageUrl = it.data?.payload?.url
                    if (imageUrl != null) onSuccess(imageUrl)
                }

                is Resource.Error -> {
                    progressBar?.dismiss()
                    requireActivity().toast("Failed to upload $type image")
                }

                else -> {}
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


    private fun setUserData(data: GetProfileResponse.Payload?) {
        with(binding) {
            etName.text = Editable.Factory.getInstance().newEditable(data?.vendor?.firstName)
            etLastName.text = Editable.Factory.getInstance().newEditable(data?.vendor?.lastName)
            etEmail.text = Editable.Factory.getInstance().newEditable(data?.vendor?.email)
            etPhoneNumber.text = Editable.Factory.getInstance().newEditable(data?.vendor?.phone)
            etAbout.text = Editable.Factory.getInstance().newEditable(data?.vendor?.about)
            etGotra.text = Editable.Factory.getInstance().newEditable(data?.vendor?.gotra ?: "")
            etPrawar.text = Editable.Factory.getInstance().newEditable(data?.vendor?.pravar ?: "")
            data?.vendor?.dateOfBirth?.let {
                etDob.text = convertToYyyyMmDd(it)
                selectedDate = it
            }
            etVeda.text = Editable.Factory.getInstance().newEditable(data?.vendor?.veda ?: "")
            etPankti.text = Editable.Factory.getInstance().newEditable(data?.vendor?.pankti ?: "")
            etShakha.text = Editable.Factory.getInstance().newEditable(data?.vendor?.shakha ?: "")
            etSutra.text = Editable.Factory.getInstance().newEditable(data?.vendor?.sutra ?: "")
            address.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.street ?: "")
            state.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.state ?: "")
            city.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.city ?: "")
            pincode.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.zip ?: "")
            etAadhar.text = Editable.Factory.getInstance().newEditable(data?.vendor?.aadhar ?: "")
            country.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.country ?: "")
            referralCode.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.referral_code ?: "")
            Glide.with(requireActivity())
                .load(data?.vendor?.image?.url)
                .placeholder(R.drawable.pandit_ji_img)
                .error(R.drawable.pandit_ji_img)
                .into(binding.profileImage)
//            Glide.with(requireActivity())
//                .load(data?.vendor?.aadhar)
//                .placeholder(R.drawable.pandit_ji_img)
//                .error(R.drawable.pandit_ji_img)
//                .into(binding.uploadAadhaarImage)
            data?.vendor?.gender?.let { gender ->
                val genderOptions = listOf("Male", "Female", "Other")
                val index = genderOptions.indexOfFirst { it.equals(gender, ignoreCase = true) }
                if (index >= 0) {
                    mySpinner.setSelection(index)
                }
            }
            data?.vendor?.language?.let { languages ->
                selectedLanguages = languages.map { lang ->
                    lang.replaceFirstChar { it.uppercaseChar() }
                }.toMutableList()

                val selected = selectedLanguages.joinToString(", ")
                etLanguage.text = selected
            }
            data?.vendor?.services?.let { services ->
                selectedServices = services.toMutableList()
                val serviceName = services.map { lang ->
                    lang.poojaType
                }.toMutableList()

                val selected = serviceName.joinToString(", ")
                etServices.text = selected
            }
        }
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
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedServices = selectedServices
        binding.etServices.setText(
            selectedServices.joinToString(", ") { it.poojaType }
        )

        title.text = getString(R.string.services)
        description.text = getString(R.string.select_your_services)
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


    private fun showGoToSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("To upload a profile picture, we need access to your Camera and Photos. Please go to App Settings and allow the permissions.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Permission is required to upload profile image.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkPermissionForCamera() {
        Log.i(
            "TAG",
            "checkPermissionForCamera: " + "${
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            }"
        )
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }


    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile =
                File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )

            imageUri = photoUri // store globally if needed
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        val imageTargetView = binding.profileImage

        try {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if (::imageUri.isInitialized) {
                        val fileObj = uriToFileSafe(imageUri)
                        Glide.with(this).load(fileObj).into(imageTargetView)

                        if (currentImageForAadhaar) {
                            aadhaarFile = fileObj
                            aadhaarMimeType = getMimeTypeFromUri(imageUri)
                            isAadhaarImageUpdated = true
                        } else {
                            file = fileObj
                            mimeType = getMimeTypeFromUri(imageUri)
                            isImageUpdated = true
                        }
                    }
                }
                /*IMAGE_PICK_CODE -> {
                    data?.data?.let { uri ->
                        val fileObj = uriToFileSafe(uri)
                        Glide.with(this).load(fileObj).into(imageTargetView)

                        if (currentImageForAadhaar) {
                            aadhaarFile = fileObj
                            aadhaarMimeType = getMimeTypeFromUri(uri)
                            isAadhaarImageUpdated = true
                        } else {
                            file = fileObj
                            mimeType = getMimeTypeFromUri(uri)
                            isImageUpdated = true
                        }
                    }
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToFileSafe(uri: Uri): File {
        val mime = getMimeTypeFromUri(uri)
        val extension = mime.substringAfterLast("/").let { ".$it" }
        val tempFile =
            File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}$extension")

        try {
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException("Failed to convert URI to File")
        }
        return tempFile
    }


    private fun getMimeTypeFromUri(uri: Uri): String {
        return requireContext().contentResolver.getType(uri) ?: "image/jpeg"
    }

    private fun uriToFile(uri: Uri): File {
        val extension = getMimeTypeFromUri(uri).substringAfterLast("/").let { ".$it" }
        val file = File(requireContext().cacheDir, "temp_image$extension")
        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output -> input.copyTo(output) }
        }
        return file
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (ifUserPermanentDe(permissions[0])) {
                showGoToSettingsDialog()
            } else {
                Toast.makeText(requireContext(), "Permission is required", Toast.LENGTH_SHORT)
                    .show()
            }
            return
        }

        when (requestCode) {
            CAMERA_PERMISSION_CODE -> launchCamera()
//            GALLERY_PERMISSION_CODE -> openGallery()
        }

    }

    private fun ifUserPermanentDe(permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), permission
        )
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        return file
    }

}
