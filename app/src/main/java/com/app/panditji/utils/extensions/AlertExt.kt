package com.app.panditji.utils.extensions

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.app.panditji.R

fun Activity.showPrompt(
    msg: String, @StringRes titleResId: Int? = null,
    positiveAction: () -> Unit = {},
    negativeAction: () -> Unit = {},
    positiveButtonTitle: Int = R.string.yes,
    negativeButtonTitle: Int = R.string.cancel
) {
    println("showInfoDialog() called with: msg = [$msg], titleResId = [$titleResId]")


    if (!isDestroyed && !isFinishing) {
        AlertDialog.Builder(this).apply {
            titleResId?.let { setTitle(it) }
            setMessage(msg)
            setPositiveButton(positiveButtonTitle, { dialogInterface, i ->
                positiveAction()
            })
            setNegativeButton(negativeButtonTitle, { _, _ -> negativeAction() })
        }.show()
    } else
        println("showInfoDialog: error: activity is destroyed")
}
