package com.app.panditji.ui.dashboard.settings

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.panditji.data.model.app_language.AppLanguage
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentContactUsBinding
import com.app.panditji.databinding.FragmentSelectLanguageBinding
import com.app.panditji.utils.AppUtils
import org.koin.android.ext.android.inject
import java.util.Locale
import java.util.prefs.Preferences
import kotlin.getValue
import kotlin.text.isNullOrEmpty

class ChooseLanguageFragment : Fragment() {
    private lateinit var binding: FragmentSelectLanguageBinding
    private val prefs by inject<PrefsHelper>()

    private var selectedLanguage: AppLanguage? = null
    private var adapter: ChooseLanguageAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectLanguageBinding.inflate(layoutInflater, container, false)

        selectedLanguage = AppLanguage(name = prefs.selectedLanguageName, code = prefs.selectedLanguageCode)

        adapter = ChooseLanguageAdapter(
            AppUtils.languageList,
            prefs.selectedLanguageCode
        ) { selectedLang ->
            selectedLanguage = selectedLang
        }

        binding.rvServices.adapter = adapter

        binding.btnNext.setOnClickListener {
            if (selectedLanguage == null) {
                Toast.makeText(requireContext(), "Please select a language", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveLanguageAndApply(selectedLanguage!!)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }
    private fun saveLanguageAndApply(language: AppLanguage) {

        prefs.selectedLanguageName = language.name
        prefs.selectedLanguageCode = language.code

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .edit()
            .putString("selected_language_code", language.code)
            .apply()

        requireActivity().recreate()
        findNavController().popBackStack()
    }
}

