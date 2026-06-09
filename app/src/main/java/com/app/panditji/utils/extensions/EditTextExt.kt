package com.app.panditji.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged


var EditText.value
    get() = this.text.toString()
    set(value) = setText(value)

/*
val CountryCodePicker.selectedCountryCodeWithPlus
    get() = "+" + this.selectedCountryCode
*/


fun watchInputFields(vararg editTexts: EditText, action: EditText.(String?) -> Unit) {
    editTexts.iterator().forEach { et ->
        et.doAfterTextChanged {
            et.action(it.toString())
        }
    }
}


fun EditText.afterTextChanged(action: (String?) -> Unit): TextWatcher {
    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(text: Editable?) {
            //  removeTextChangedListener(this)
            action(text?.toString())
            // addTextChangedListener(this)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }
    this.addTextChangedListener(textWatcher)
    return textWatcher
}
