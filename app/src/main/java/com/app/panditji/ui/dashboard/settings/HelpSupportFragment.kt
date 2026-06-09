package com.app.panditji.ui.dashboard.settings

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.panditji.data.model.faq.Payload
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.databinding.FragmentHelpSupportBinding
import com.app.panditji.ui.adapter.AdapterFaq
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class HelpSupportFragment : Fragment() {

    private lateinit var binding: FragmentHelpSupportBinding
    private val apiVm by viewModel<apiVm>()
    private var progressBar: Dialog? = null
    private lateinit var adapter: AdapterFaq

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpSupportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFAQList()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getFAQList() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getFaqs("vendor").observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = result.data?.payload
                    if (!data.isNullOrEmpty()) {
                        setAdapter(data)
                        Log.d("HelpSupportFragment", "FAQ List Loaded: $data")
                    }
                }

                is Resource.Error -> {
                    progressBar?.dismiss()
                    when (result.exception) {
                        is NoConnectionException -> {
                            requireActivity().toast("No Internet")
                        }

                        else -> {
                            Log.e("HelpSupportFragment", "Error Code: ${result.errorBody?.getError()?.errorCode}")
                            Log.e("HelpSupportFragment", "Error Msg: ${result.errorBody?.getError()?.errorMessage}")
                            Log.e("HelpSupportFragment", "Status: ${result.errorBody?.getError()?.statusCode}")

                            result.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                requireActivity().toast(errorMessage)
                            }
                        }
                    }
                }

                else -> {
                    // loading handled by progressDialog
                }
            }
        }
    }

    private fun setAdapter(data: List<Payload>) {
        adapter = AdapterFaq(data)
        binding.faqRecycler.adapter = adapter
    }
}
