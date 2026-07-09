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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupMenu
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
import com.app.panditji.data.model.SignUpData
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
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private var profileImageFile: File? = null
    var selectedDate = ""
    private lateinit var imageUri: Uri

    private var selectedServices = mutableListOf<GetAllServicesResponse.Payload.Service>()
    private var servicesList: List<GetAllServicesResponse.Payload.Service> = listOf()

    private var selectedLanguages = mutableListOf<String>()

    private var designationList: List<SignUpData> = listOf()
    private var selectedDesignation = mutableListOf<SignUpData>()

    private var experienceList: List<SignUpData> = listOf()
    private var selectedExperience = mutableListOf<SignUpData>()

    private var countryList: List<SignUpData> = listOf()
    private var selectedCountry = mutableListOf<SignUpData>()

    private var stateList: List<SignUpData> = listOf()
    private var selectedState = mutableListOf<SignUpData>()

    private var cityList: List<SignUpData> = listOf()
    private var selectedCity = mutableListOf<SignUpData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)

        notificationPermission()
        getSignUpData()
        alreadyHaveAccount()
        AppUtils.setupHideKeyboardOnTouch(binding.root, requireActivity())

        binding.apply {
            profileImage.setOnClickListener {
                showImagePickerBottomSheet()
            }

            etDesignation.setOnClickListener {
                selectDesignation()
            }

            etDob.setOnClickListener {
                showDatePicker()
            }

            etGender.setOnClickListener {
                genderPopUpMenu()
            }

            etServices.setOnClickListener {
                selectServices()
            }

            etLanguage.setOnClickListener {
                selectLanguage()
            }

            etExperience.setOnClickListener {
                selectExperience()
            }

            etCountry.setOnClickListener {
                selectCountry()
            }

            etState.setOnClickListener {
                selectState()
            }

            etCity.setOnClickListener {
                selectCity()
            }

            binding.btnSignUp.setOnClickListener {
                validation()
            }
        }
        return binding.root
    }

    private fun selectDesignation() {
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedDesignation = selectedDesignation

        binding.etDesignation.text = selectedDesignation.joinToString(", ") { it.name }

        title.text = getString(R.string.designation)
        description.text = getString(R.string.select_your_designation)

        val adapter = DesignationAdapter(designationList, selectedDesignation)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedDesignation.joinToString(", ") { it.name }
            binding.etDesignation.text = selectedDisplay
            this.selectedDesignation = selectedDesignation.map { it }.toMutableList()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
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
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun genderPopUpMenu(){
        val popupMenu = PopupMenu(requireContext(), binding.etGender)
        popupMenu.menu.add("Male")
        popupMenu.menu.add("Female")
        popupMenu.menu.add("Other")

        popupMenu.setOnMenuItemClickListener { selectedItem ->
            binding.etGender.text = selectedItem.title
            true
        }
        popupMenu.show()
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedServices = selectedServices

        binding.etServices.text = selectedServices.joinToString(", ") { it.poojaType }

        title.text = getString(R.string.services)
        description.text = getString(R.string.select_your_services)

        val adapter = ServicesAdapter(servicesList, selectedServices)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedServices.joinToString(", ") { it.poojaType }
            binding.etServices.text = selectedDisplay
            this.selectedServices = selectedServices.map { it }.toMutableList()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)

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

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedLanguages.joinToString(", ")
            binding.etLanguage.text = selectedDisplay

            // Store lowercase version for API
            this.selectedLanguages = selectedLanguages.map { it.lowercase() }.toMutableList()

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun selectExperience() {
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedExperience = selectedExperience

        binding.etExperience.text = selectedExperience.joinToString(", ") { it.name }

        title.text = getString(R.string.total_experience)
        description.text = getString(R.string.select_total_experience)

        val adapter = ExperienceAdapter(experienceList, selectedExperience)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedExperience.joinToString(", ") { it.name }
            binding.etExperience.text = selectedDisplay
            this.selectedExperience = selectedExperience.map { it }.toMutableList()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun selectCountry() {
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedCountry = selectedCountry

        binding.etCountry.text = selectedCountry.joinToString(", ") { it.name }

        title.text = getString(R.string.country)
        description.text = getString(R.string.select_your_country)

        val adapter = ExperienceAdapter(countryList, selectedCountry)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedCountry.joinToString(", ") { it.name }
            binding.etCountry.text = selectedDisplay
            if (selectedDisplay != "") {
                stateList = emptyList()
                selectedState.clear()
                binding.etState.text = ""
                cityList = emptyList()
                selectedCity.clear()
                binding.etCity.text = ""
                getStateList(selectedDisplay)
            }
            this.selectedCountry = selectedCountry.map { it }.toMutableList()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun selectState() {
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedState = selectedState

        binding.etState.text = selectedState.joinToString(", ") { it.name }

        title.text = getString(R.string.state)
        description.text = getString(R.string.select_your_state)

        val adapter = ExperienceAdapter(stateList, selectedState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedState.joinToString(", ") { it.name }
            if (selectedDisplay != "") {
                binding.etState.text = selectedDisplay
                cityList = emptyList()
                selectedCity.clear()
                binding.etCity.text = ""
                getCityList(selectedDisplay)
            }
            this.selectedState = selectedState.map { it }.toMutableList()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun selectCity() {
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
        val btnAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAdd)
        val title =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvTitle)
        val description =
            dialogView.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDescription)
        val selectedCity = selectedCity

        binding.etCity.text = selectedCity.joinToString(", ") { it.name }

        title.text = getString(R.string.city)
        description.text = getString(R.string.select_your_city)

        val adapter = ExperienceAdapter(cityList, selectedCity)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.text = getString(R.string.select)

        btnAdd.setOnClickListener {
            val selectedDisplay = selectedCity.joinToString(", ") { it.name }
            binding.etCity.text = selectedDisplay
            this.selectedCity = selectedCity.map { it }.toMutableList()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getSignUpData() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getAllServices().observe(requireActivity()) {
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

        apiVm.getDesignationList().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = it.data?.payload
                    designationList = data ?: listOf()
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

        apiVm.getExperienceList().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = it.data?.payload
                    experienceList = data ?: listOf()
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

        apiVm.getCountryList().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = it.data?.payload
                    countryList = data ?: listOf()
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

    private fun getStateList(country: String) {
        apiVm.getStateList(country).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = it.data?.payload
                    stateList = data ?: listOf()
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

    private fun getCityList(state: String) {
        apiVm.getCityList(state).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = it.data?.payload
                    cityList = data ?: listOf()
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

    private fun validation() {
        val etDesignation = binding.etDesignation.getString()
        val etFirstName = binding.etFirstName.getString()
        val etLastName = binding.etLastName.getString()
        val etPhone = binding.etPhoneNumber.getString()
        val etEmail = binding.etEmail.getString()
        val etPassword = binding.etPassword.getString()
        val dob = selectedDate
        val gender = binding.etGender.getString()
        val etGotra = binding.etGotra.getString()
        val prawar = binding.etPrawar.getString()
        val veda = binding.etVeda.getString()
        val shakha = binding.etShakha.getString()
        val pankti = binding.etPankti.getString()
        val sutra = binding.etSutra.getString()
        val aadharNumber = binding.etAadharNumber.getString()
        val experience = binding.etExperience.getString()
        val address = binding.etAddress.getString()
        val state = binding.etState.getString()
        val city = binding.etCity.getString()
        val pincode = binding.etPincode.getString()
        val country = binding.etCountry.getString()
        val referralCode = binding.etReferralCode.getString()

        when {
            profileImageFile == null -> toast(getString(R.string.please_upload_profile_image))
            etDesignation.isEmpty() -> toast(getString(R.string.error_select_designation))
            etFirstName.isEmpty() -> toast(getString(R.string.please_enter_first_name))
            etLastName.isEmpty() -> toast(getString(R.string.please_enter_last_name))
            etPhone.isEmpty() -> toast(getString(R.string.please_enter_phone_number))
            etEmail.isEmpty() -> toast(getString(R.string.please_enter_email))
            !AppUtils.isValidEmailId(etEmail) -> toast(getString(R.string.please_enter_valid_email))
            etPassword.isEmpty() -> toast(getString(R.string.please_enter_password))
            dob.isEmpty() -> toast(getString(R.string.error_select_dob))
            gender.isEmpty() -> toast(getString(R.string.error_select_gender))
            etGotra.isEmpty() -> toast(getString(R.string.error_enter_gotra))
            prawar.isEmpty() -> toast(getString(R.string.error_enter_prawar))
            veda.isEmpty() -> toast(getString(R.string.error_enter_veda))
            shakha.isEmpty() -> toast(getString(R.string.error_enter_shakha))
            pankti.isEmpty() -> toast(getString(R.string.error_enter_pankti))
            sutra.isEmpty() -> toast(getString(R.string.error_enter_sutra))
            aadharNumber.isEmpty() -> toast(getString(R.string.error_enter_your_aadhar_number))
            selectedServices.isEmpty() -> toast(getString(R.string.error_select_at_least_one_service))
            selectedLanguages.isEmpty() -> toast(getString(R.string.error_select_at_least_one_language))
            experience.isEmpty() -> toast(getString(R.string.error_enter_your_experience))
            address.isEmpty() -> toast(getString(R.string.error_enter_address))
            country.isEmpty() -> toast(getString(R.string.error_enter_country))
            state.isEmpty() -> toast(getString(R.string.error_enter_state))
            city.isEmpty() -> toast(getString(R.string.error_enter_city))
            pincode.isEmpty() -> toast(getString(R.string.error_enter_pincode))

            else -> {
                uploadProfileImageAndRegister(
                    designation = etDesignation,
                    firstName = etFirstName,
                    lastName = etLastName,
                    phone = etPhone,
                    email = etEmail,
                    password = etPassword,
                    dob = dob,
                    gender = gender,
                    gotra = etGotra,
                    pravar = prawar,
                    veda = veda,
                    shakha = shakha,
                    pankti = pankti,
                    sutra = sutra,
                    aadharNumber = aadharNumber,
                    experience = experience,
                    address = address,
                    country = country,
                    state = state,
                    city = city,
                    pincode = pincode,
                    referralCode = referralCode
                )
            }
        }
    }

    private fun uploadProfileImageAndRegister(
        designation: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        dob: String,
        gender: String,
        gotra: String,
        pravar: String,
        veda: String,
        shakha: String,
        pankti: String,
        sutra: String,
        aadharNumber: String,
        experience: String,
        address: String,
        country: String,
        state: String,
        city: String,
        pincode: String,
        referralCode: String?,
    ) {
        progressBar = AppUtils.progressDialog(requireActivity())

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
                            progressBar?.dismiss()
                            toast("Image upload failed")
                            return@observe
                        }

                        registerUserMultipart(
                            profileImageUrl = profileImageUrl,
                            designation = designation,
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone,
                            email = email,
                            password = password,
                            dob = dob,
                            gender = gender,
                            gotra = gotra,
                            pravar = pravar,
                            veda = veda,
                            shakha = shakha,
                            pankti = pankti,
                            sutra = sutra,
                            aadharNumber = aadharNumber,
                            experience = experience,
                            address = address,
                            country = country,
                            state = state,
                            city = city,
                            pincode = pincode,
                            referralCode = referralCode
                        )
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
                        toast("Failed to upload profile image")
                    }
                }
            }
    }

    private fun toast(msg: String) =
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()

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
            openGallery()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkPermissionForCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
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
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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

    private fun registerUserMultipart(
        profileImageUrl: String,
        designation: String,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        dob: String,
        gender: String,
        gotra: String,
        pravar: String,
        veda: String,
        shakha: String,
        pankti: String,
        sutra: String,
        aadharNumber: String,
        experience: String,
        address: String,
        country: String,
        state: String,
        city: String,
        pincode: String,
        referralCode: String?,
    ) {
        fun String.rb() = this.toRequestBody("text/plain".toMediaTypeOrNull())

        val image = profileImageUrl.rb()
        val aboutRB = designation.rb()
        val firstNameRB = firstName.rb()
        val lastNameRB = lastName.rb()
        val phoneRB = phone.rb()
        val emailRB = email.rb()
        val passwordRB = password.rb()
        val dobRB = dob.rb()
        val genderRB = gender.rb()
        val gotraRB = gotra.rb()
        val pravarRB = pravar.rb()
        val vedaRB = veda.rb()
        val shakhaRB = shakha.rb()
        val panktiRB = pankti.rb()
        val sutraRB = sutra.rb()
        val aadharRB = aadharNumber.rb()
        val serviceParts = selectedServices.mapIndexed { _, service ->
            MultipartBody.Part.createFormData("services", service.id)
        }
        val languageParts = selectedLanguages.mapIndexed { _, lang ->
            MultipartBody.Part.createFormData("language", lang)
        }
        val experienceRB = experience.rb()
        val addressRB = address.rb()
        val countryRB = country.rb()
        val stateRB = state.rb()
        val cityRB = city.rb()
        val zipRB = pincode.rb()
        val referralCodeRB = referralCode?.rb()

        val deviceTypeRB = "android".rb()
        val deviceTokenRB = prefs.fcmToken.rb()

        apiVm.registerUserMultipart(
            image = image,
            about = aboutRB,
            firstName = firstNameRB,
            lastName = lastNameRB,
            phone = phoneRB,
            email = emailRB,
            password = passwordRB,
            dateOfBirth = dobRB,
            gender = genderRB,
            gotra = gotraRB,
            pravar = pravarRB,
            veda = vedaRB,
            shakha = shakhaRB,
            pankti = panktiRB,
            sutra = sutraRB,
            aadhar = aadharRB,
            services = serviceParts,
            language = languageParts,
            experience = experienceRB,
            address = addressRB,
            country = countryRB,
            state = stateRB,
            city = cityRB,
            zip = zipRB,
            referralCode = referralCodeRB,
            role = "vendor".rb(),
            deviceType = deviceTypeRB,
            deviceToken = deviceTokenRB,
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()

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
                    progressBar?.dismiss()
                    toast(it.errorBody?.getError()?.errorMessage ?: "Something went wrong")
                }
            }
        }
    }

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

    companion object {
        private const val CAMERA_REQUEST_CODE = 1010
        private const val IMAGE_PICK_CODE = 2020
        private const val CAMERA_PERMISSION_CODE = 1515
    }
}