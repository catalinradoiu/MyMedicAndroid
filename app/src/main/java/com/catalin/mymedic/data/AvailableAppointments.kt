package com.catalin.mymedic.data

import com.wdullaer.materialdatetimepicker.time.Timepoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author catalinradoiu
 * @since 6/1/2018
 */
data class AvailableAppointments(
    var unselectableDays: ArrayList<Calendar>,
    var unselectableTimesForDays: HashMap<Long, ArrayList<Timepoint>>,
    var medicSchedule: List<Schedule>,
    var appointmentDuration: Int,
    var defaultSelectableTimeForWeek: HashMap<Int, ArrayList<Timepoint>>
) {
    constructor() : this(ArrayList<Calendar>(), HashMap<Long, ArrayList<Timepoint>>(0), ArrayList<Schedule>(), 0, HashMap())
}