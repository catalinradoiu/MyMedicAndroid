package com.catalin.mymedic.feature.shared

import android.text.Editable
import android.text.TextWatcher

/**
 * @author catalinradoiu
 * @since 5/28/2018
 */
abstract class OnTextChangedListener : TextWatcher {
    override fun afterTextChanged(s: Editable?) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}