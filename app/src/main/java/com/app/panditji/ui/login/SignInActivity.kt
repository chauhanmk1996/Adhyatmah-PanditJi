package com.app.panditji.ui.login
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.app.panditji.R
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.ActivitySigninBinding
import com.app.panditji.utils.AppUtils
import org.koin.android.ext.android.inject
import kotlin.getValue

class SignInActivity : AppCompatActivity() {
//class SignInActivity : UserBaseActivity<ActivitySigninBinding>() {
    private lateinit var binding: ActivitySigninBinding
    private val prefs by inject<PrefsHelper>()


    /* override fun getLayoutId(): Int {
        return R.layout.activity_signin
    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SignInActivity, R.layout.activity_signin)
        window.statusBarColor = ContextCompat.getColor(this,R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

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
}