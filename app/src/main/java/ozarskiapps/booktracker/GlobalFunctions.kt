package ozarskiapps.booktracker

import java.util.*
import kotlin.math.roundToInt

fun setCalendar(calendar: Calendar, dayStart: Boolean = true): Calendar {
    if(dayStart){
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    } else {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }
    return calendar
}

fun calendarFromMillis(millis: Long): Calendar{
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return calendar
}

fun roundDouble(value: Double, multiplier: Int): Double{
    return (value * multiplier).roundToInt() / multiplier.toDouble()
}