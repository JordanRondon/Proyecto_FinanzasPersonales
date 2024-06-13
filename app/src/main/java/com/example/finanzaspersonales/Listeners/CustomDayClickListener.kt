//package com.example.finanzaspersonales.Listeners
//
//class CustomDayClickListener {
//}
package com.example.finanzaspersonales.Listeners

import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.util.Date

class CustomDayClickListener(
    private val calendarView: CalendarView,
    private val onDateSelected: (Date) -> Unit
) : OnDayClickListener {
    override fun onDayClick(eventDay: EventDay) {
        val selectedDate = eventDay.calendar.time
        onDateSelected(selectedDate)
    }
}