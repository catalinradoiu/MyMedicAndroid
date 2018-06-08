package com.catalin.mymedic.utils.extension

import java.util.*

/**
 * @author catalinradoiu
 * @since 6/8/2018
 */

fun Calendar.setToDayStart(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

fun Calendar.setToDayStartWithTimestamp(timestamp: Long): Calendar {
    timeInMillis = timestamp
    setToDayStart()
    return this
}