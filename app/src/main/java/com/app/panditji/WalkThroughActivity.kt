package com.app.panditji
import IntroSlideData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.ActivityWalkThroughBinding
import com.app.panditji.ui.adapter.IntroAdapter
import com.app.panditji.ui.login.SignInActivity
import com.app.panditji.utils.AppUtils
import org.koin.android.ext.android.inject
import kotlin.getValue

class WalkThroughActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalkThroughBinding
    private val prefs by inject<PrefsHelper>()


    private val introPages = listOf(
            IntroSlideData("", "Pandit ji App", R.drawable.om_image_icon),
            IntroSlideData("", "Digital Partner for Puja & Bookings", R.drawable.intro_two),
            IntroSlideData("", "Tradition in Your Hands", R.drawable.intro_one)
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
           binding = DataBindingUtil.setContentView(this@WalkThroughActivity, R.layout.activity_walk_through)
           window.statusBarColor = ContextCompat.getColor(this,R.color.white)
           WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

            initViews()

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
        private fun initViews() {
            val adapter = IntroAdapter(introPages, this)
            binding.viewPager.adapter = adapter
            // Setup TabLayout with ViewPager2
            binding.dotsIndicator.setViewPager2(binding.viewPager)
            binding.btnNext.setOnClickListener {
                if (binding.viewPager.currentItem < introPages.lastIndex) {
                    binding.viewPager.currentItem += 1
                    binding.btnPrevious.visibility = View.VISIBLE
                } else {
                    launchMain()
                }
            }


            binding.btnPrevious.setOnClickListener {
                if (binding.viewPager.currentItem > 0) {
                    binding.viewPager.currentItem -= 1
                }
            }


            binding.btnSkip.setOnClickListener {
                Log.d("TAG", "initVieddws: click")
                launchMain()
            }

            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateButtonVisibility()
                }
            })

        }

    private fun updateButtonVisibility() {
        if (binding.viewPager.currentItem == 0) {
            binding.btnPrevious.visibility = View.INVISIBLE
        } else {
            binding.btnPrevious.visibility = View.VISIBLE
        }
        if (binding.viewPager.currentItem == introPages.lastIndex) {
            binding.btnNext.text = "Done"
        } else {
            binding.btnNext.text = "Next"
        }
    }

        private fun launchMain() {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
