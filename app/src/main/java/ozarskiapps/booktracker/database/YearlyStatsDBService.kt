package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.setCalendar
import java.util.*

class YearlyStatsDBService(val context: Context): DBService(context) {

    fun getTotalNumberOfPages(): Long {
        //TODO("Not implemented yet")
        return -1
    }

    fun getTotalNumberOfBooks(): Int {
        //TODO("Not implemented yet")
        return -1
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        //TODO("Not implemented yet")
        return -1.0
    }

    fun getAverageReadingTime(): Double {
        //TODO("Not implemented yet")
        return -1.0
    }

    fun getAveragePagesPerDay(): Double {
        //TODO("Not implemented yet")
        return -1.0
    }

    fun getAverageBooksPerMonth(): Double {
        //TODO("Not implemented yet")
        return -1.0
    }

    fun getAverageBooksPerWeek(): Double {
        //TODO("Not implemented yet")
        return -1.0
    }

    fun getMonthWithMostBooksRead(): String {
        //TODO("Not implemented yet")
        return ""
    }

    fun getCalendarMonthStart(calendar: Calendar): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
        }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal
    }

    fun getCalendarMonthEnd(calendar: Calendar): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
        }
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal
    }

}