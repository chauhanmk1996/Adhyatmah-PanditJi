package com.app.panditji.data.sharedPrefs

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefsBooleanDelegate(private val key: String) : ReadWriteProperty<PrefsHelper, Boolean> {

    override fun getValue(thisRef: PrefsHelper, property: KProperty<*>): Boolean {
        return thisRef.sharedPref.getBoolean(key, false)
    }

    override fun setValue(thisRef: PrefsHelper, property: KProperty<*>, value: Boolean) {
        thisRef.sharedPref.edit().putBoolean(key, value).apply()
    }

}

class PrefsStringDelegate(private val key: String) : ReadWriteProperty<PrefsHelper, String> {

    override fun getValue(thisRef: PrefsHelper, property: KProperty<*>): String {
        return thisRef.sharedPref.getString(key, "") ?: ""
    }

    override fun setValue(thisRef: PrefsHelper, property: KProperty<*>, value: String) {
        thisRef.sharedPref.edit().putString(key, value).apply()
    }
}

class PrefsIntDelegate(private val key: String) : ReadWriteProperty<PrefsHelper, Int> {

    override fun getValue(thisRef: PrefsHelper, property: KProperty<*>): Int {
        return thisRef.sharedPref.getInt(key, -1) ?: -1
    }

    override fun setValue(thisRef: PrefsHelper, property: KProperty<*>, value: Int) {
        thisRef.sharedPref.edit().putInt(key, value).apply()
    }
}

