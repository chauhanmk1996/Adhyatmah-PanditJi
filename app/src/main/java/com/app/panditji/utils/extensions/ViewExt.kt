package com.app.panditji.utils.extensions

import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible

fun Context?.toast(text: String, shortTime: Boolean = true) {
    Toast.makeText(this, text, if (shortTime) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>,isUnderLine:Boolean=true) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor
                textPaint.isUnderlineText = isUnderLine
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun View.makeVisible(show: Boolean = true) {
    isVisible = if (show)
        show
    else
        false

}

fun View.toggleVisibility(){
    isVisible=!isVisible
}

fun View.makeVisible(show: Boolean = true, gone: Boolean = true) {
    this.visibility = if (show) View.VISIBLE else if (gone) View.GONE else View.INVISIBLE

}

fun View.makeEnable(enable:Boolean=true){
    this.isEnabled=enable
}


fun Context.convertDpToPixels(dp: Int) =/*TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,dp.toFloat(),resources.displayMetrics)*/
    (dp * resources.displayMetrics.scaledDensity).toInt()


fun String?.getColoredString( colorId: Int,startPos:Int=0,endPos:Int=1): Spannable? {
    val spannable: Spannable = SpannableString(this)
    spannable.setSpan(
        ForegroundColorSpan(colorId), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    // Log.d(TAG, spannable.toString())
    return spannable
}


