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
private const val CALENDAR_DATE_PATTERN = "dd/MM/yyyy"

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

@SuppressLint("SimpleDateFormat")
@BindingAdapter("longDate", requireAll = true)
fun setDateFromLong(textView: TextView, date: Long) {
    if (date != 0L) {
        textView.text = SimpleDateFormat(TIME_FORMAT_PATTERN).format(Date(date))
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("calendarDateLong", requireAll = true)
fun setCalendarDateFromLong(textView: TextView, date: Long) {
    if (date != 0L) {
        textView.text = SimpleDateFormat(CALENDAR_DATE_PATTERN).format(Date(date))
    }
}

