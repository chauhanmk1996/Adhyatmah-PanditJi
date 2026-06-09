package com.app.panditji.ui.dashboard.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.domain.model.delete_account.delete_request.DeleteRequest
import com.bumptech.glide.Glide
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.PrefKeys
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentProfileBinding
import com.app.panditji.ui.adapter.ProfileAdapter
import com.app.panditji.ui.login.SignInActivity
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }


    private fun initViews() {
        getUserProfile()
        setupRecyclerView()
        setClick()
    }

    private fun setClick() {
        binding.cameraIcon.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }
    }


    private fun setupRecyclerView() {
        val profileItems = mutableListOf(
            /*Pair("About Us", R.drawable.profile_image),*/
            /*Pair("My Address", R.drawable.profile_icon),*/
            Pair(getString(R.string.select_language), R.drawable.language),
            Pair(getString(R.string.terms_amp_conditions), R.drawable.profile_icon),
            Pair(getString(R.string.privacy_policy), R.drawable.profile_icon),
            Pair(getString(R.string.contact_us), R.drawable.profile_icon),
            Pair(getString(R.string.faq_support), R.drawable.profile_icon),
            Pair(getString(R.string.delete_account), R.drawable.profile_icon),
            Pair(getString(R.string.sign_out), R.drawable.ic_logout),
        )
//        if (isLogin == "1") {
//            profileItems.add(Pair("Logout", R.drawable.logout_icon))
//        }
        // Set up adapter
        val adapter = ProfileAdapter(profileItems) { itemTitle ->
            // Handle item click
            when (itemTitle) {
                /*"About Us" -> {
                    val bundle  = Bundle()
                    bundle.putString("terms","3")
                    findNavController().navigate(R.id.termConditionFragment,bundle)
                }

                "My Address" -> {
//                    findNavController().navigate(R.id.)
                }*/
                getString(R.string.select_language) -> {
                    findNavController().navigate(R.id.chooseLanguageFragment)
                }
                getString(R.string.terms_amp_conditions) -> {

                    val bundle  = Bundle()
                    bundle.putString("terms","1")
                    findNavController().navigate(R.id.termConditionFragment,bundle)
                }

                getString(R.string.privacy_policy) -> {
                    findNavController().navigate(R.id.termConditionFragment)

                }

                getString(R.string.contact_us) -> {

                    findNavController().navigate(R.id.contactUsFragment)

                }

                getString(R.string.faq_support) -> {
                    findNavController().navigate(R.id.helpSupportFragment)
                }
                getString(R.string.sign_out) -> {
                    showConfirmationBottomSheet(
                        title = getString(R.string.sign_out_from_app),
                        message = getString(R.string.are_you_sure_you_would_like_to_sign_out_of_your_panditji_app),
                        positiveText = getString(R.string.sign_out)
                    ) {
                        logOutUser()
                    }
                }

                getString(R.string.delete_account) -> {
                    showConfirmationBottomSheet(
                        title = getString(R.string.delete_account),
                        message = getString(R.string.are_you_sure_you_want_to_delete_your_account_this_action_cannot_be_undone),
                        positiveText = getString(R.string.delete)
                    ) {
                        deleteUserAccount() // your delete logic
                    }
                }

            }
        }

        binding.rcvProfileRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    private fun deleteUserAccount() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getDeleteAccount(DeleteRequest(prefs.authToken))
            .observe(requireActivity()) { it ->
                println("Ujjwal:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        logOutUser()
                        Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error ->{
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                Toast.makeText(requireActivity(), "No internet", Toast.LENGTH_SHORT).show()
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


    private fun showConfirmationBottomSheet(
        title: String,
        message: String,
        positiveText: String,
        positiveAction: () -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.logout_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnPositive = dialogView.findViewById<Button>(R.id.btnSignOut)

        // 🔹 Set dynamic content
        tvTitle.text = title
        tvMessage.text = message
        btnPositive.text = positiveText

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        btnPositive.setOnClickListener {
            positiveAction()
            bottomSheetDialog.dismiss()
        }

        // Optional: fully expand it
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior =  BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }

        bottomSheetDialog.show()
    }


    private fun getUserProfile() {
        Log.i("TAG", "getUserProfile: "+prefs.lastName +" "+prefs.firstName)
        binding.userName.text = prefs.firstName +" "+ prefs.lastName
        binding.tvEmail.text = prefs.email
        Glide.with(requireActivity())
            .load(prefs.profileImage)
            .placeholder(R.drawable.pandit_ji_img)
            .error(R.drawable.pandit_ji_img)
            .into(binding.profileImage)
    }

    private fun logOutUser() {
        Log.e("TAG", "getVenueDetailById: ${prefs.authToken}")
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.userLogout(prefs.authToken)
            .observe(requireActivity()) { it ->
                println("PankajSingh:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        logoutClearUser()
                        Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error ->{
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                Toast.makeText(requireActivity(), "No internet", Toast.LENGTH_SHORT).show()
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


    private fun customerSupport() {
        progressBar = AppUtils.progressDialog(requireActivity())

        apiVm.customerSupport()
            .observe(requireActivity())
            { it ->
                println("PankajSingh:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload

                        Log.d("TAG", "dkjdfkjgdlkd $data")

                        Toast.makeText(
                            requireActivity(),
                            "${it.data?.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    is Resource.Error ->{
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                requireActivity().toast("No Internet")
                            }
                            else -> {
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorCode}", )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorMessage}", )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.statusCode}", )
                                it.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                    requireActivity().toast(errorMessage)
                                }
                            }
                        }
                    }
                    else -> {

                        progressBar?.dismiss()

                    }
                }
            }
    }


    private fun logoutClearUser() {
        // 1️⃣ Get the current FCM token before clearing
        val fcmToken = prefs.sharedPref.getString(PrefKeys.FCM_TOKEN, null)
        val languageCode = prefs.sharedPref.getString(PrefKeys.SELECTED_LANGUAGE_CODE, null)
        val languageName = prefs.sharedPref.getString(PrefKeys.SELECTED_LANGUAGE_NAME, null)

        // 2️⃣ Clear all preferences
        prefs.sharedPref.edit().apply {
            clear()
            apply()
        }

        // 3️⃣ Restore the saved FCM token
        if (!fcmToken.isNullOrEmpty()) {
            prefs.sharedPref.edit().apply {
                putString(PrefKeys.FCM_TOKEN, fcmToken)
                apply()
            }
        }
        if (!languageName.isNullOrEmpty()) {
            prefs.sharedPref.edit().apply {
                putString(PrefKeys.SELECTED_LANGUAGE_NAME, languageName)
                putString(PrefKeys.SELECTED_LANGUAGE_CODE, languageCode)
                apply()
            }
        }

        Log.i("TAG", "logoutClearUser fcm: ${prefs.fcmToken}")
        // 4️⃣ Redirect to SignInActivity
        val intent = Intent(requireActivity(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }



}