package com.app.panditji.ui.dashboard.settings

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentSettingsBinding
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.deleteAccountLayout.setOnClickListener {
            showLogoutDialog()
        }

//        binding.NotificationLayout.setOnClickListener {
//            findNavController().navigate(R.id.notificationFragment)
//        }


        binding.termsAndConditionLayout.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("type","1")
            bundle.putString("headerName","Terms and conditions")
            bundle.putString("headerBody", getString(R.string.termandconditiondata))
//            findNavController().navigate(R.id.termsConditionsFragment,bundle)
        }

        binding.privacyPolicyLayout.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("type","2")
            bundle.putString("headerName","Privacy Policy")
            bundle.putString("headerBody", getString(R.string.privacy_data))
//            findNavController().navigate(R.id.termsConditionsFragment, bundle)
        }

        binding.aboutUSLayout.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("type","3")
            bundle.putString("headerName","About Us")
            bundle.putString("headerBody", "About Us")
//            findNavController().navigate(R.id.termsConditionsFragment, bundle)
        }

    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to delete Account?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun deleteAccount() {
        Log.e("TAG", "getVenueDetailById: ${prefs.authToken}")
        progressBar = AppUtils.progressDialog(requireActivity())
//        apiVm.getDeleteAccount(prefs.authToken)
//            .observe(requireActivity()) { it ->
//                println("PankajSingh:$it")
//                when (it) {
//                    is Resource.Success -> {
//                        progressBar?.dismiss()
//                        Toast.makeText(requireActivity(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                    is Resource.Error ->{
//                        progressBar?.dismiss()
//                        when (it.exception) {
//                            is NoConnectionException -> {
//                                Toast.makeText(requireActivity(), "No internet", Toast.LENGTH_SHORT).show()
//                            }
//                            else -> {
//                                it.errorBody?.getError()?.errorMessage?.let { errorMessage ->
//                                    requireActivity().toast(errorMessage)
//                                }
//                            }
//                        }
//                    }
//                    else -> {
//
//                    }
//                }
//            }

     }

}