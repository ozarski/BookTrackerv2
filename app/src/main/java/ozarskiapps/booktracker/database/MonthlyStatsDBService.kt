package ozarskiapps.booktracker.database

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.calendarFromMillis
import java.util.*

class MonthlyStatsDBService(val context: Context, val month: Calendar = Calendar.getInstance()) :
    DBService(context) {


    fun setMonth(month: Calendar) {
        this.month.timeInMillis = month.timeInMillis
    }

    fun getTotalNumberOfBooks(): Int {
        val db = this.readableDatabase

        val resultColumn = "totalBooks"

        val projection = arrayOf(
            "COUNT(*) as $resultColumn"
        )

        val selection = "${DatabaseConstants.BookTable.END_DATE_COLUMN} >= ? " +
                "AND ${DatabaseConstants.BookTable.END_DATE_COLUMN} <= ? " +
                "AND ${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(
            getCalendarMonthStart(month).timeInMillis.toString(),
            getCalendarMonthEnd(month).timeInMillis.toString(),
            BookStatus.Finished.toString()
        )

        return db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            if (moveToFirst()) {
                getInt(getColumnIndexOrThrow(resultColumn)).also{
                    close()
                }
            } else {
                0
            }
        }
    }

    fun getTotalNumberOfPages(): Long {
        val db = this.readableDatabase

        val resultColumn = "totalPages"
        val projection = arrayOf(
            "SUM(${DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN}) as $resultColumn"
        )

        val selection = "${DatabaseConstants.BookTable.END_DATE_COLUMN} >= ? " +
                "AND ${DatabaseConstants.BookTable.END_DATE_COLUMN} <= ? " +
                "AND ${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(
            getCalendarMonthStart(month).timeInMillis.toString(),
            getCalendarMonthEnd(month).timeInMillis.toString(),
            BookStatus.Finished.toString()
        )

        return db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            if (moveToFirst()) {
                getLong(getColumnIndexOrThrow(resultColumn)).also{
                    close()
                }
            } else {
                0
            }
        }
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        val totalNumberOfBooks = getTotalNumberOfBooks()
        if (totalNumberOfBooks == 0) return 0.0
        return getTotalNumberOfPages().toDouble() / totalNumberOfBooks
    }

    fun getAverageReadingTime(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val (monthStart, monthEnd) = getCalendarStartEnd(month)

        readingTimeDBService.getTotalReadingTimeForTimePeriod(
            monthStart,
            monthEnd
        ).apply{
            if (this == 0) return 0.0
            return this.toDouble() / getTotalNumberOfBooks()
        }
    }

    fun getAveragePagesPerDay(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val (monthStart, monthEnd) = getCalendarStartEnd(month)

        readingTimeDBService.getTotalReadingTimeForTimePeriod(
            monthStart,
            monthEnd
        ).apply{
            if (this == 0) return 0.0
            return getTotalNumberOfPages().toDouble() / this
        }
    }

    fun getAverageBooksPerWeek(): Double {
        if (getTotalNumberOfBooks() == 0) return 0.0
        return getTotalNumberOfBooks().toDouble() / 4.0
    }

    //timeNow is used only for testing, DO NOT PASS THIS PARAMETER IN PRODUCTION CODE
    fun getMonthProgress(timeNow: Calendar = Calendar.getInstance()): Double {
        val maxDays = timeNow.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = timeNow.get(Calendar.DAY_OF_MONTH)
        return currentDay.toDouble()/maxDays.toDouble()
    }

    private fun getCalendarMonthStart(calendar: Calendar): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
        }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal
    }

    private fun getCalendarMonthEnd(calendar: Calendar): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
        }
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal
    }

    private fun getCalendarStartEnd(month: Calendar): Pair<Calendar, Calendar>{
        val start = getCalendarMonthStart(month)
        val end = getCalendarMonthEnd(month)

        return Pair(start, end)
    }
}