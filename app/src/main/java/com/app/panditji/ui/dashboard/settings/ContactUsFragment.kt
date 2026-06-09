package com.app.panditji.ui.dashboard.settings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.adhyatmah.domain.model.profile.contact_us.ContactUsResponse
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentContactUsBinding
import kotlin.getValue
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding
    private val apiVm by viewModel<apiVm>()
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private var phoneNumber: String = ""
    private var email: String = ""
    private var instaHandle: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactUsBinding.inflate(layoutInflater, container, false)
        getContactUsData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // set default click listeners (later updated with API response values)
        binding.whatsAppLayoyt.setOnClickListener {
            openWhatsApp(requireContext(), phoneNumber)
        }
        binding.gmaillayout.setOnClickListener {
            openGmail(requireContext(),email)
        }
        binding.instaLayout.setOnClickListener {
            openInstagram(instaHandle)
        }
    }

    private fun getContactUsData() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getContactUs().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = result.data?.payload
                    if (data != null) {
                        phoneNumber = data.phone
                        email = data.email
                        instaHandle = data.domain

                        binding.email.text = "• Email: ${data.email}"
                        binding.instaId.text = "• Instagram: ${data.domain}"
                        binding.phoneId.text = "• Phone Number: ${data.phone}"
                    }
                }

                is Resource.Error -> {
                    progressBar?.dismiss()
                    when (result.exception) {
                        is NoConnectionException -> {
                            requireActivity().toast("No Internet")
                        }

                        else -> {
                            Log.e("ContactUsFragment", "Error Code: ${result.errorBody?.getError()?.errorCode}")
                            Log.e("ContactUsFragment", "Error Message: ${result.errorBody?.getError()?.errorMessage}")
                            Log.e("ContactUsFragment", "Status Code: ${result.errorBody?.getError()?.statusCode}")

                            result.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                requireActivity().toast(errorMessage)
                            }
                        }
                    }
                }

                else -> {
                    // Handle loading or other states if needed
                }
            }
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phoneNumber")
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // WhatsApp not installed or other error
            false
        }
    }

    fun openGmail(
        context: Context,
        emailAddress: String,
        subject: String? = null,
        body: String? = null
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$emailAddress")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                setPackage("com.google.android.gm")
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Gmail not installed or other error
            false
        }
    }


    private fun openInstagram(instagramHandle: String) {
        try {
            val uri = Uri.parse("http://instagram.com/_u/$instagramHandle")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // fallback to browser
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/$instagramHandle"))
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening Instagram: ${e.message}")
            Snackbar.make(requireView(), "Error opening Instagram", Snackbar.LENGTH_SHORT).show()
        }
    }

}
