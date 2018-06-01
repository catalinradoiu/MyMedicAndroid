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

private const val TIME_FORMAT_PATTERN = "dd/MM/yyyy HH:mm"

@BindingAdapter("drawableTint")
fun setIconTint(textView: TextView, color: Int) {
    textView.compoundDrawables.forEach {
        it?.let { drawable ->
            drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("longDate", requireAll = true)
fun setDateFromLong(textInputEditText: TextInputEditText, date: Long) {
    if (date != 0L) {
        textInputEditText.setText(SimpleDateFormat(TIME_FORMAT_PATTERN).format(Date(date)))
    }
}

//@SuppressLint("SimpleDateFormat")
//@InverseBindingAdapter(attribute = "app:longDate")
//fun getTimesampFromStringDate(textInputEditText: TextInputEditText, stringDate: String): Long =
//    Calendar.getInstance().apply { time = SimpleDateFormat(TIME_FORMAT_PATTERN).parse(stringDate) }.timeInMillis

