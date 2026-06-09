package com.app.panditji.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

inline fun <reified T : Any> Context.intentFor(vararg params: Pair<String, Any?>) =
    Intent(this, T::class.java).apply {
        putExtras(
            bundleOf(*params)
        )
    }


inline fun <reified T : Activity> Fragment.startActivity(
    vararg params: Pair<String, Any?>,
    isNewTask: Boolean = false,
    isAnimate: Boolean = true
) {
    activity?.startActivity<T>(
        params = *params,
        isNewTask = isNewTask,
        enterAnim = null,
        exitAnim = null,
        isAnimate = isAnimate
    )
}

inline fun <reified T : Activity> Activity.startActivity(
    vararg params: Pair<String, Any?>,
    isNewTask: Boolean = false,
    enterAnim: Int? = null,
    exitAnim: Int? = null,
    bundle: Bundle? = null,
    isAnimate: Boolean = true
) {
    intentFor<T>(*params).apply {
        if (isNewTask) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (!isAnimate) addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(this, bundle)
    }
    enterAnim?.let { enter ->
        exitAnim?.let { exit ->
            overridePendingTransition(enter, exit)
        }
    }
}

