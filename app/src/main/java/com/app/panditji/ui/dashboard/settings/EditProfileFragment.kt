package com.app.panditji.ui.dashboard.settings

import UpdateProfile
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.model.SignUpData
import com.app.panditji.data.model.get_profile.GetProfileResponse
import com.app.panditji.data.model.get_services.GetAllServicesResponse
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentEditProfileBinding
import com.app.panditji.ui.dashboard.LanguageAdapter
import com.app.panditji.ui.dashboard.ServicesAdapter
import com.app.panditji.ui.login.DesignationAdapter
import com.app.panditji.ui.login.ExperienceAdapter
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.AppUtils.convertToYyyyMmDd
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.getString
import com.app.panditji.utils.extensions.toast
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
import kotlin.getValue

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()
    var selectedGender = ""
    var selectedDate = ""
    private lateinit var imageUri: Uri
    private var file: File? = null
    private var mimeType: String = ""
    private var isImageUpdated = false
    private var aadhaarFile: File? = null
    private var aadhaarMimeType: String = ""
    private var isAadhaarImageUpdated = false
    private var currentImageForAadhaar = false

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

    companion object {
        const val CAMERA_REQUEST_CODE = 1001
        const val IMAGE_PICK_CODE = 1002
        const val CAMERA_PERMISSION_CODE = 1003
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserDetail()
        getSignUpData()
        AppUtils.setupHideKeyboardOnTouch(binding.root, requireActivity())

        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            frImage.setOnClickListener {
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

            btnUpdateProfile.setOnClickListener {
                checkValidations()
            }
        }
    }

    private fun getUserDetail() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.userProfile(prefs.userId)
            .observe(
                requireActivity()
            ) {
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

    private fun setUserData(data: GetProfileResponse.Payload?) {
        with(binding) {
            Glide.with(requireActivity())
                .load(data?.vendor?.image?.url)
                .placeholder(R.drawable.pandit_ji_img)
                .error(R.drawable.pandit_ji_img)
                .into(binding.profileImage)

            etDesignation.text = Editable.Factory.getInstance().newEditable(data?.vendor?.about)
            etFirstName.text = Editable.Factory.getInstance().newEditable(data?.vendor?.firstName)
            etLastName.text = Editable.Factory.getInstance().newEditable(data?.vendor?.lastName)
            etPhoneNumber.text = Editable.Factory.getInstance().newEditable(data?.vendor?.phone)
            etEmail.text = Editable.Factory.getInstance().newEditable(data?.vendor?.email)
            data?.vendor?.dateOfBirth?.let {
                etDob.text = convertToYyyyMmDd(it)
                selectedDate = it
            }
            etGender.text = Editable.Factory.getInstance().newEditable(data?.vendor?.gender)
            etGotra.text = Editable.Factory.getInstance().newEditable(data?.vendor?.gotra ?: "")
            etPrawar.text = Editable.Factory.getInstance().newEditable(data?.vendor?.pravar ?: "")
            etVeda.text = Editable.Factory.getInstance().newEditable(data?.vendor?.veda ?: "")
            etShakha.text = Editable.Factory.getInstance().newEditable(data?.vendor?.shakha ?: "")
            etPankti.text = Editable.Factory.getInstance().newEditable(data?.vendor?.pankti ?: "")
            etSutra.text = Editable.Factory.getInstance().newEditable(data?.vendor?.sutra ?: "")
            etAadharNumber.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.aadhar ?: "")
            data?.vendor?.services?.let { services ->
                selectedServices = services.toMutableList()
                val serviceName = services.map { lang ->
                    lang.poojaType
                }.toMutableList()

                val selected = serviceName.joinToString(", ")
                etServices.text = selected
            }
            data?.vendor?.language?.let { languages ->
                selectedLanguages = languages.map { lang ->
                    lang.replaceFirstChar { it.uppercaseChar() }
                }.toMutableList()

                val selected = selectedLanguages.joinToString(", ")
                etLanguage.text = selected
            }
            etExperience.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.experience ?: "")
            etAddress.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.street ?: "")
            etCountry.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.country ?: "")
            etState.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.state ?: "")
            etCity.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.city ?: "")
            etPincode.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.address?.zip ?: "")
            etReferralCode.text =
                Editable.Factory.getInstance().newEditable(data?.vendor?.referral_code ?: "")
        }
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

    private fun genderPopUpMenu() {
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

    private fun checkValidations() {
        val designation = binding.etDesignation.getString()
        val firstName = binding.etFirstName.getString()
        val lastName = binding.etLastName.getString()
        val phone = binding.etPhoneNumber.getString()
        val email = binding.etEmail.getString()
        val dob = selectedDate
        val gender = binding.etGender.getString()
        val gotra = binding.etGotra.getString()
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

        when {
            designation.isEmpty() -> toast(getString(R.string.error_select_designation))
            firstName.isEmpty() -> toast(getString(R.string.please_enter_first_name))
            lastName.isEmpty() -> toast(getString(R.string.please_enter_last_name))
            phone.isEmpty() -> toast(getString(R.string.please_enter_phone_number))
            email.isEmpty() -> toast(getString(R.string.please_enter_email))
            !AppUtils.isValidEmailId(email) -> toast(getString(R.string.please_enter_valid_email))
            dob.isEmpty() -> toast(getString(R.string.error_select_dob))
            gender.isEmpty() -> toast(getString(R.string.error_select_gender))
            gotra.isEmpty() -> toast(getString(R.string.error_enter_gotra))
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
                val model = UpdateProfile(
                    about = binding.etDesignation.getString(),
                    firstName = binding.etFirstName.getString(),
                    lastName = binding.etLastName.getString(),
                    phone = binding.etPhoneNumber.getString(),
                    email = binding.etEmail.getString(),
                    dateOfBirth = selectedDate.ifEmpty { null },
                    gender = selectedGender.toLowerCase(Locale.ROOT),
                    gotra = binding.etGotra.getString(),
                    pravar = binding.etPrawar.getString(),
                    veda = binding.etVeda.getString(),
                    shakha = binding.etShakha.getString(),
                    pankti = binding.etPankti.getString(),
                    sutra = binding.etSutra.getString(),
                    aadhar = binding.etAadharNumber.getString(),
                    services = selectedServices.map { it.id },
                    language = selectedLanguages.map { it.lowercase() }.toMutableList(),
                    experience = binding.etExperience.getString(),
                    address = binding.etAddress.getString(),
                    country = binding.etCountry.getString(),
                    state = binding.etState.getString(),
                    city = binding.etCity.getString(),
                    zip = binding.etPincode.getString()
                )
                when {
                    isImageUpdated -> uploadMedia(model)
                    else -> updateProfile(model)
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()

    private fun updateProfile(model: UpdateProfile?) {
        model?.accessToken = prefs.authToken
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.userUpdateProfile(prefs.authToken, model)
            .observe(
                requireActivity()
            ) {
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        prefs.firstName = model?.firstName ?: ""
                        prefs.lastName = model?.lastName ?: ""
                        prefs.email = model?.email ?: ""
                        prefs.phone = model?.phone ?: ""
                        if (model?.image?.isNotEmpty() == true && model.image != null) prefs.profileImage =
                            model.image ?: ""
                        toast(it.data?.message ?: "")
                        findNavController().popBackStack()
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
        uploadGenericImage(file) { imageUrl ->
            request.image = imageUrl
            updateProfile(request)
        }
    }

    private fun uploadGenericImage(imageFile: File?, onSuccess: (String) -> Unit) {
        if (imageFile == null) {
            Toast.makeText(requireContext(), "No profile image selected", Toast.LENGTH_SHORT).show()
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
                    requireActivity().toast("Failed to upload profile image")
                }

                else -> {}
            }
        }
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
        }
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

    private fun ifUserPermanentDe(permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), permission
        )
    }
}