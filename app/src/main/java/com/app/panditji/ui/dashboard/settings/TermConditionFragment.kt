package com.app.panditji.ui.dashboard.settings

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.databinding.FragmentTermConditionBinding
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class TermConditionFragment : Fragment() {

    private lateinit var binding: FragmentTermConditionBinding
    private var progressBar: Dialog? = null
    private var type: String = ""
    private val apiVm by viewModel<apiVm>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermConditionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = arguments?.getString("terms") ?: ""
        when (type) {
            "1", "2" -> binding.title.text = getString(R.string.terms_of_services)
            "3" -> binding.title.text = getString(R.string.about_us)
            else -> binding.title.text = getString(R.string.privacy_policy)
        }

        getPrivacyData()
        setBackHandling()
    }

    private fun getPrivacyData() {
        progressBar = AppUtils.progressDialog(requireActivity())

        apiVm.getPolicies().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = result.data?.payload
                    if (data != null) {
                        val term = data.termsOfService.body
                        val privacy = data.privacyPolicy.body

                        when (type) {
                            "1", "2" -> binding.des.text = Html.fromHtml(term, Html.FROM_HTML_MODE_LEGACY)
                            "3" -> binding.des.text = ""
                            else -> binding.des.text =
                                Html.fromHtml(privacy, Html.FROM_HTML_MODE_LEGACY)
                        }
                    }
                }

                is Resource.Error -> {
                    progressBar?.dismiss()
                    when (result.exception) {
                        is NoConnectionException -> {
                            requireActivity().toast("No Internet")
                        }

                        else -> {
                            Log.e("TermConditionFragment", "Error Code: ${result.errorBody?.getError()?.errorCode}")
                            Log.e("TermConditionFragment", "Error Msg: ${result.errorBody?.getError()?.errorMessage}")
                            Log.e("TermConditionFragment", "Status: ${result.errorBody?.getError()?.statusCode}")

                            result.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                requireActivity().toast(errorMessage)
                            }
                        }
                    }
                }

                else -> {
                    // Loading state handled by progressDialog
                }
            }
        }
    }

    private fun setBackHandling() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }
}



