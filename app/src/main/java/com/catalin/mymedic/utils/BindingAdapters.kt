package com.catalin.mymedic.utils

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.design.widget.TextInputEditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author catalinradoiu
 * @since 2/13/2018
 */

@BindingAdapter("drawableTint")
fun setIconTint(textView: TextView, color: Int) {
    textView.compoundDrawables.forEach {
        it?.let { drawable ->
            drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("longDate")
fun setDateFromLong(textInputEditText: TextInputEditText, date: Long){
    textInputEditText.setText(SimpleDateFormat("dd/MM/yyyy").format(Date(date)))
}