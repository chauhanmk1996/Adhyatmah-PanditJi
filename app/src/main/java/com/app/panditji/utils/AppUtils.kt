package com.app.panditji.utils
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.app.panditji.MainActivity
import com.app.panditji.data.model.app_language.AppLanguage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern


object AppUtils {
//    private const val PREF_LANGUAGE = "selected_language"
    val languageList = listOf(
        AppLanguage("English", "en"),
        AppLanguage("Hindi", "hi"),
        AppLanguage("Marathi", "mr"),
        AppLanguage("Bengali", "bn"),
        AppLanguage("Gujarati", "gu"),
        AppLanguage("Odia", "or"),
        AppLanguage("Tamil", "ta"),
        AppLanguage("Telugu", "te"),
        AppLanguage("Kannada", "kn"),
        AppLanguage("Malayalam", "ml")
    )

    fun setLocale(context: Context, languageCode: String): Context {
        // Save it
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
//        prefs.edit().putString(PREF_LANGUAGE, languageCode).apply()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToYyyyMmDd(dateString: String): String {
        return Instant.parse(dateString)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate()
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun formatDate(inputDate: String?): String {
        if (inputDate.isNullOrEmpty()) return ""
        return try {
            // Input format (with 'Z' indicating UTC timezone)
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            // Output format (e.g. "17 Oct, 2025")
            val outputFormat = SimpleDateFormat("dd MMM, yyyy HH:mm a", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun isValidEmailId(email: String): Boolean {

        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
    /*  fun setCurrentFragment(
          pContext: FragmentActivity,
          pContainerId: Int, pFragment: Fragment, pTag: String
      ) {
          val fragmentManager = pContext.supportFragmentManager
          val fragmentTransaction = fragmentManager.beginTransaction()
          //  .setCustomAnimations( R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)

          *//*if (pFragment is ProfileFragment ||
                pFragment is LoginFragment ||
                pFragment is FeedFragment ||
                pFragment is HomeFragment ||
                pFragment is SearchFragment

        )*//*if (pFragment is LoginFragment) {
            fragmentTransaction.replace(pContainerId, pFragment, pTag)
        }*//*else if(pFragment is IncidentCagetoriesFragment){
            fragmentTransaction.add(pContainerId, pFragment, pTag)

        }*//* else {
            fragmentTransaction.add(pContainerId, pFragment, pTag)
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }*/
    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun progressDialog(context: Context?): Dialog {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_progress)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    fun progressDialogOnlyInitialize(context: Context?): Dialog {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
    fun getPref(key: String, context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(key, null)
    }

    fun getPrefBoolean(key: String, context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(key, false)
    }

    fun putPref(key: String, value: String, context: Context?) {
        if (context == null)
            return
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putPrefUserLoginId(key: String, value: String, context: Context?) {
        if (context == null)
            return
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getPrefUserLoginId(key: String, context: Context): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(key, null)
    }

    fun putPrefBoolean(key: String, value: Boolean, context: Context?) {
        if (context == null)
            return
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun toastMessgae(context: Context, message: String, time: Int) {
        Toast.makeText(context, message, time).show()

    }

    fun goToMainActivity(mContext: AppCompatActivity) {
        val intent = Intent(mContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        mContext.startActivity(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mContext.finishAffinity()
        }
        mContext.finish()
    }


    fun getProcessedLikeCount(number: Long): String? {
        if (number < 1000) return "" + number
        val exp = (Math.log(number.toDouble()) / Math.log(1000.0)).toInt()
        return String.format(
            "%.1f %c",
            number / Math.pow(1000.0, exp.toDouble()),
            "kMGTPE"[exp - 1]
        )
    }

    fun getColoredString(mString: String?, colorId: Int): Spannable? {
        val spannable: Spannable = SpannableString(mString)
        spannable.setSpan(
            ForegroundColorSpan(colorId),
            0,
            1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Log.d(TAG, spannable.toString())
        return spannable
    }



    fun getLocalDate(value: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val df1 = SimpleDateFormat("HH:mm", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        df1.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(value)
        df.timeZone = TimeZone.getDefault()
        df1.timeZone = TimeZone.getDefault()
        val formattedDate = df1.format(date)

        return formattedDate
    }

    fun getLocalReel(value: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val df1 = SimpleDateFormat("HH:mm", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        df1.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(value)
        df.timeZone = TimeZone.getDefault()
        df1.timeZone = TimeZone.getDefault()
        val formattedDate = df1.format(date)

        return formattedDate    }

    /*  fun getFbImage(object: O){
          var url=""
          try {
              url = response.getJSONObject()
                  .getJSONObject(FACEBOOK_FIELD_PICTURE)
                  .getJSONObject(FACEBOOK_FIELD_DATA)
                  .getString(FACEBOOK_FIELD_URL);
          } catch ( e:Exception) {
              e.printStackTrace();
          }
          return url;
      }*/

    fun showHideFoodTooltip(mContext: Context, tvTooltip: TextView){
        tvTooltip.visibility=View.VISIBLE
        Handler(mContext.mainLooper).postDelayed({
            tvTooltip.visibility = View.INVISIBLE

        }, 4500)
    }

    fun isJSONValid(test: String?): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }

    fun formatTimeToAmPm(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format
        return sdf.format(calendar.time)
    }

    fun isTimeAfter(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ): Boolean {
        val start = startHour * 60 + startMinute
        val end = endHour * 60 + endMinute
        return end > start
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupHideKeyboardOnTouch(view: View, activity: Activity) {
        if (view !is EditText) {
            view.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                false
            }
        }
        if (view is ViewGroup) {
            for (child in view.children) {
                setupHideKeyboardOnTouch(child, activity)
            }
        }
    }


    fun convertTo12HourFormat(time24: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(time24)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            time24 // fallback in case of error
        }
    }

    fun convertTo24Hour(time12: String): String {
        return try {
            val inputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(time12)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            time12
        }
    }

}
