package com.app.panditji
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.app.panditji.databinding.ActivityMainBinding
import com.app.panditji.utils.AppUtils
import com.google.firebase.crashlytics.internal.common.CommonUtils
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var backPressedTime: Long = 0
    private lateinit var toast: Toast
    private val prefs by inject<PrefsHelper>()


    /*override fun getLayoutId(): Int {
        return R.layout.activity_main

    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this,R.color.white)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        handleActivityBackButton()
        navigationMenuClickListener()
        setupBottomNavVisibility()

        //applyBottomInsetToBottomNav()
       //setBottomBarPadding() // ← Add this

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
    private fun handleActivityBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestinationId = navController.currentDestination?.id

                if (currentDestinationId == R.id.homeFragment) {
                    exitFromApp()
                } else if(currentDestinationId == R.id.profileFragment||currentDestinationId == R.id.bookingFragment) {
                    val popped = navController.popBackStack(R.id.homeFragment, false)
                    if (!popped) {
                        // HomeFragment not in back stack — navigate to it
                        navController.navigate(R.id.homeFragment)
                    }
                    binding.bottomNav.selectedItemId = R.id.menuHome
                }else{
                    navController.popBackStack()
                }
            }

        })
    }

    private fun exitFromApp() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            toast.cancel()
            finish()
        } else {
            toast = Toast.makeText(this@MainActivity, "Press again to exit", Toast.LENGTH_SHORT)
            toast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun navigationMenuClickListener() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val currentDestId = navController.currentDestination?.id
            when (item.itemId) {
                R.id.menuHome -> {
                    if (currentDestId != R.id.menuHome) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }

                R.id.menuBooking -> {
                    if (currentDestId != R.id.menuBooking) {
                        navController.navigate(R.id.bookingFragment)
                    }
                    true
                }

                R.id.menuProfile -> {
                    if (currentDestId != R.id.menuProfile) {
                        navController.navigate(R.id.profileFragment)
                    }
                    true
                }
                else -> {
                    false
                }

            }
        }

    }

    private fun setupBottomNavVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.profileFragment,
//                R.id.venueListFragment,
                R.id.bookingFragment -> hideBottomNav(false) // show

                else -> hideBottomNav(true) // hide
            }
        }
    }

    fun selectBottomNavItem(itemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = itemId
    }

    private fun hideBottomNav(hide:Boolean){
        if(hide){
            binding.bottomNav.visibility = View.GONE
//            clearBottomBarPadding()

        }else{
//          setBottomBarPadding()
            binding.bottomNav.visibility = View.VISIBLE
        }
    }



    private fun applyBottomInsetToBottomNav() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val isGesture = isGestureNavigation()

            val bottomPadding = if (isGesture) systemBarsInsets.bottom else 0

            // Only update padding if needed
            if (view.paddingBottom != bottomPadding) {
                view.setPadding(
                    view.paddingLeft,
                    view.paddingTop,
                    view.paddingRight,
                    bottomPadding
                )
            }

            // Let other views handle their own insets
            insets
        }
    }


    fun setBottomBarPaddings() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomPadding = if (isGestureNavigation()) systemBars.bottom else 0
            binding.bottomNav.setPadding(0, 0, 0, bottomPadding)
            WindowInsetsCompat.CONSUMED
        }
    }


    private fun clearBottomBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, null)
        binding.bottomNav.setPadding(0, 0, 0, 0)
        binding.bottomNav.setPadding(0, 0, 0, 0)
    }

    fun setBottomBarPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Remove extra padding from BottomAppBar and its children
            binding.bottomNav.setPadding(0, 0, 0, 0)
            val offset = if (isGestureNavigation()) systemBars.bottom else 0
            binding.bottomNav.setPadding(0, 0, 0, offset)
            WindowInsetsCompat.CONSUMED
        }
    }


    private fun isGestureNavigation(): Boolean {
        val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        val navMode = if (resourceId > 0) resources.getInteger(resourceId) else -1
        return navMode == 2  // 2 = gesture nav, 1 = 3-button, 0 = 2-button
    }


}