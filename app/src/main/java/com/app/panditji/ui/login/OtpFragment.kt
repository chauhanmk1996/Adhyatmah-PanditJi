package com.app.panditji.ui.login
import RegistrationModel
import SendOtpModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.panditji.MainActivity
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.AppConstants
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentOtpBinding
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpFragment : Fragment() {
    private lateinit var binding: FragmentOtpBinding
    private val apiVm by viewModel<apiVm>()
    private val prefs by inject<PrefsHelper>()
    private var progresbar: Dialog? = null
    private var countdown: CountDownTimer?=null
    private var isTimerRunning = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(inflater)
        binding.ivBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
        AppUtils.setupHideKeyboardOnTouch(binding.root,requireActivity())
        initViews()
        return binding.root
    }

    private fun initViews() {
        startTimer()

        val otp = arguments?.getString("otp") ?: ""
        if (otp.isNotEmpty()) {
            binding.pinview.setValue(otp)
        }

        binding.pinview.setPinViewEventListener { pinView, fromUser ->
            val otp = pinView.value
            if (otp.length == 4) {
                hideKeyboard(binding.pinview)
            }
        }

        binding.btnVerify.setOnClickListener {
            validation()
        }
    }

    private fun validation() {
        val otp = binding.pinview.value

        /*if (!isTimerRunning) {
            requireActivity().toast("OTP expired. Please request a new OTP.")
            return
        }*/

        if(otp.isEmpty()){
            requireActivity().toast(getString(R.string.please_enter_otp))
        }else{
            val model = RegistrationModel(
                mobile = AppConstants.MOBILE_NUMBER,
                otp = otp,
                deviceType = "android",
                deviceToken = prefs.fcmToken,
//              user_type = ""
            )
            verifyOtp(model)
        }
    }
private fun startTimer() {
    // Cancel existing timer if any
    countdown?.cancel()

    countdown = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            var t1 = ""
            var t2: String
            val min = seconds / 60
            val sec = seconds % 60
            t2 = if (sec < 10) {
                "0${sec}"
            } else {
                "$sec"
            }
            if (min < 10) {
                t1 = "0${min}"
            } else {
                t1 = "$min"
            }

            binding.tvTimer.text = "${getString(R.string.resend_confirmation_code)} ($t1:$t2)"
            binding.tvResend.isEnabled = false
            context?.let {
                binding.tvResend.setTextColor(ContextCompat.getColor(it, R.color.grey))
            }
        }

        override fun onFinish() {
            isTimerRunning = false
            binding.tvTimer.text = "00:00"
            binding.tvResend.isEnabled = true
            context?.let {
                binding.tvResend.setTextColor(ContextCompat.getColor(it, R.color.blue))
            }

            binding.tvResend.setOnClickListener {
                // Prevent multiple requests while timer is running
                if (!isTimerRunning) {
                    val model = SendOtpModel(mobile = AppConstants.MOBILE_NUMBER)
                    reSendOtp(model)
                }
            }
        }
    }.start()
}
    private var isResendInProgress = false

    private fun reSendOtp(model: SendOtpModel?) {
        if (isResendInProgress) return  // Prevent multiple requests
        isTimerRunning=true
        isResendInProgress = true
        progresbar = AppUtils.progressDialog(requireActivity())

        apiVm.reSendOtp(model)
            .observe(requireActivity()) { it ->
                progresbar?.dismiss()
                when (it) {
                    is Resource.Success -> {
                        isResendInProgress = false
                        Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                        startTimer()  // Restart timer after OTP is sent
                    }
                    is Resource.Error -> {
                        isResendInProgress = false
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
                        isResendInProgress = false
                    }
                }
            }
    }

    private fun verifyOtp(model: RegistrationModel?) {
        progresbar = AppUtils.progressDialog(requireActivity())
        apiVm.verifyOtp(model)
            .observe(requireActivity()) { it ->
                progresbar?.dismiss()
                when (it) {
                    is Resource.Success -> {
                        val data = it.data?.payload
                        if (data != null) {
                            if (data.role=="vendor"){
                                prefs.firstName = data.customer?.firstName.toString()
                                prefs.lastName = data.customer?.lastName.toString()
                                prefs.authToken = data.accessToken.toString()
                                prefs.deviceType = "android"
                                prefs.email = data.customer?.email.toString()
                                prefs.phone = data.customer?.phone.toString()
                                prefs.isLoggedIn = true
                                prefs.profileImage = data.customer?.cover?.url.toString()
                                prefs.userId = data.customer?.id.toString()
                                Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT).show()

                                if (it.data.code == 200) {
                                    countdown?.cancel()
                                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                                    requireActivity().finish()
                                }
                            }else{
                                Toast.makeText(requireActivity(), "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    is Resource.Error -> {
                        it.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                            requireActivity().toast(errorMessage)
                        }
                    }
                    else -> {
                        // Handle unexpected case
                    }
                }
            }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}