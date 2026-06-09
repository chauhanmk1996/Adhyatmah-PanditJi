package com.app.panditji.ui.login

import LoginRequest
import LoginWithMobileRequest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.panditji.MainActivity
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.AppConstants
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentEnterNumberBinding
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.prefs.Preferences

class EnterNumberFragment : Fragment() {
    private lateinit var binding: FragmentEnterNumberBinding
    private val apiVm by viewModel<apiVm>()
    private var progresbar: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterNumberBinding.inflate(layoutInflater)
        initViews()
        setCountryPicker()
        return binding.root
    }

    private fun setCountryPicker() {
        binding.countryPicker.apply {
            setCountryForNameCode("IN")
            isClickable = false
            isEnabled = false
        }
        binding.countryPicker.setOnClickListener(null)
        binding.countryPicker.setCcpClickable(false)

    }

    private fun initViews() {
        alreadyHaveAccount()
        binding.loginBtn.setOnClickListener {
            if (validation()) {
                sendOtp();
            }
        }
        binding.loginPage.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
        AppUtils.setupHideKeyboardOnTouch(binding.root, requireActivity())
    }

    private fun validation(): Boolean {
        if (binding.phonenumberInput.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the number!", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.phonenumberInput.text.toString().length < 10) {
            Toast.makeText(requireContext(), "Please enter a valid number!", Toast.LENGTH_SHORT)
                .show()
            return false
        } else {
            return true
        }
    }

    private fun alreadyHaveAccount() {
        val fullText = getString(R.string.don_t_have_an_account_sign_up)
        val spannableString = SpannableString(fullText)
        val signUpClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().popBackStack()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorPrimary
                ) // your link color
                ds.isUnderlineText = true
            }
        }

        val signUpStart = fullText.indexOf(getString(R.string.sign_up))
        val signUpEnd = signUpStart + getString(R.string.sign_up).length

        spannableString.setSpan(
            signUpClick,
            signUpStart,
            signUpEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.alreadyHaveAccountId.text = spannableString
        binding.alreadyHaveAccountId.movementMethod = LinkMovementMethod.getInstance()
        binding.alreadyHaveAccountId.highlightColor = Color.TRANSPARENT
    }

    private fun sendOtp() {
        progresbar = AppUtils.progressDialog(requireActivity())
        apiVm.loginWithMobile(LoginWithMobileRequest(mobile = binding.phonenumberInput.text.toString()))
            .observe(
                requireActivity()
            ) { it ->
                println("Ujjwal:$it")
                when (it) {
                    is Resource.Success -> {
                        val data = it.data?.payload
                        progresbar?.dismiss()

                        if (it.data?.code == 200) {
                            AppConstants.MOBILE_NUMBER = binding.phonenumberInput.text.toString()
                            findNavController().navigate(R.id.otpFragment)
                        } else if (it.data?.message == "Account not active.") {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.your_account_is_not_active_redirecting_to_registration),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(requireActivity(), it.data?.message, Toast.LENGTH_LONG)
                                .show()
                        }

                    }

                    is Resource.Error -> {
                        progresbar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {

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


}