package com.catalin.mymedic.utils

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.design.widget.TextInputEditText
import android.widget.TextView
import com.catalin.mymedic.utils.extension.setToDayStart
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author catalinradoiu
 * @since 2/13/2018
 */

private const val TIME_FORMAT_PATTERN = "dd/MM/yyyy HH:mm"
private const val CALENDAR_DATE_PATTERN = "dd/MM/yyyy"
private const val DAY_TIME_PATTERN = "HH:mm"

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
@BindingAdapter("messageTime", requireAll = true)
fun setMessageTime(textView: TextView, time: Long) {
    if (time != 0L) {
        val currentDayStartTimestamp = Calendar.getInstance().setToDayStart().timeInMillis
        val formatter = SimpleDateFormat(if (currentDayStartTimestamp < time) DAY_TIME_PATTERN else CALENDAR_DATE_PATTERN)
        textView.text = formatter.format(Date(time))
    }
}

