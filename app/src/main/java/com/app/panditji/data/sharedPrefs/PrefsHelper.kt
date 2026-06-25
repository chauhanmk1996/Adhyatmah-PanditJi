package com.app.panditji.data.sharedPrefs

import android.content.Context
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class PrefsHelper(context: Context) {
    /* Init sharedPreferences with injected context*/
    val sharedPref = getDefaultSharedPreferences(context)
    var isLoggedIn by PrefsBooleanDelegate(PrefKeys.IS_LOGIN)
    var isFirstTime by PrefsBooleanDelegate(PrefKeys.IS_FIRST_TIME)
    
    var authToken by PrefsStringDelegate(PrefKeys.AUTH_TOKEN)
    var createdAt by PrefsStringDelegate(PrefKeys.CREATED_AT)
    var fcmToken by PrefsStringDelegate(PrefKeys.FCM_TOKEN)
    var deviceType by PrefsStringDelegate(PrefKeys.DEVICE_TYPE)
    var email by PrefsStringDelegate(PrefKeys.EMAIL)
    var userId by PrefsStringDelegate(PrefKeys.USER_ID)
    var firstName by PrefsStringDelegate(PrefKeys.FIRST_NAME)
    var lastName by PrefsStringDelegate(PrefKeys.LAST_NAME)
    var phone by PrefsStringDelegate(PrefKeys.PHONE)
    var profileImage by PrefsStringDelegate(PrefKeys.PROFILE_IMG)
    var status by PrefsIntDelegate(PrefKeys.STATUS)
    var selectedLanguageName by PrefsStringDelegate(PrefKeys.SELECTED_LANGUAGE_NAME)
    var selectedLanguageCode by PrefsStringDelegate(PrefKeys.SELECTED_LANGUAGE_CODE)
}

object PrefKeys {
    const val IS_LOGIN = "UESR_iS_LOGIN"
    const val IS_FIRST_TIME = "IS_FIRST_TIME"
    const val AUTH_TOKEN = "AUTH_TOKEN"
    const val CREATED_AT = "CREATED_AT"
    const val DEVICE_TOKEN = "DEVICE_TOKEN"
    const val FCM_TOKEN = "FCM_TOKEN"
    const val DEVICE_TYPE = "DEVICE_TYPE"
    const val EMAIL = "EMAIL"
    const val USER_ID = "USER_ID"
    const val FIRST_NAME = "FIRST_NAME"
    const val LAST_NAME = "LAST_NAME"
    const val PHONE = "PHONE"
    const val PROFILE_IMG = "PROFILE_IMG"
    const val STATUS = "STATUS"
    const val SELECTED_LANGUAGE_NAME = "SELECTED_LANGUAGE_NAME"
    const val SELECTED_LANGUAGE_CODE = "SELECTED_LANGUAGE_CODE"



}
