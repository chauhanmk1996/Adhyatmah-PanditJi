package com.app.panditji

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.ActivitySplashBinding
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity(){
        private lateinit var binding: ActivitySplashBinding
        private val prefs by inject<PrefsHelper>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)

//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//                insets
//            }

            checkForLogin()
        }

    override fun attachBaseContext(newBase: Context) {

        // Get saved language code directly
        var localeCode = prefs.selectedLanguageCode

        Log.i("TAG", "attachBaseContext code: $localeCode")

        // If not selected yet, default to English
        if (localeCode.isEmpty() || localeCode == "null") {
            localeCode = "en"
            prefs.selectedLanguageCode = "en"
        }

        super.attachBaseContext(AppUtils.setLocale(newBase, localeCode))
    }
        private fun checkForLogin() {
            val isLoggedIn =
                AppUtils.getPrefBoolean(AppConstants.USER_PREF.IS_LOGIN, this)
            Handler(mainLooper).postDelayed({
                if (prefs.isLoggedIn) {
                    intent = Intent(this, MainActivity::class.java)
                } else {
                    intent = Intent(this, WalkThroughActivity::class.java)
                }
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 3000)
        }

    }
