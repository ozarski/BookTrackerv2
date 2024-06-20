package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.book.BookStatus
import java.util.Calendar
import java.util.Locale

class YearlyStatsDBService(
    val context: Context,
    private var year: Calendar = Calendar.getInstance()
) : DBService(context) {


    fun setYear(year: Calendar) {
        this.year.timeInMillis = year.timeInMillis
    }

    fun getTotalNumberOfBooks(): Int {
        val db = this.readableDatabase
        val resultColumn = "totalBooks"

        val projection = arrayOf(
            "COUNT(*) as $resultColumn"
        )

        val selection =
            "${DatabaseConstants.BookTable.END_DATE_COLUMN} >= ? " +
                    "AND ${DatabaseConstants.BookTable.END_DATE_COLUMN} <= ? " +
                    "AND ${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(
            getCalendarYearStart().timeInMillis.toString(),
            getCalendarYearEnd().timeInMillis.toString(),
            BookStatus.Finished.toString()
        )

        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            if (moveToFirst()) {
                return getInt(getColumnIndexOrThrow(resultColumn)).also {
                    close()
                }
            }
        }
        return 0
    }

    fun getTotalNumberOfPages(): Long {
        val db = this.readableDatabase
        val resultColumn = "totalPages"

        val projection = arrayOf(
            "SUM(${DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN}) as $resultColumn"
        )

        val selection =
            "${DatabaseConstants.BookTable.END_DATE_COLUMN} >= ? " +
                    "AND ${DatabaseConstants.BookTable.END_DATE_COLUMN} <= ? " +
                    "AND ${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(
            getCalendarYearStart().timeInMillis.toString(),
            getCalendarYearEnd().timeInMillis.toString(),
            BookStatus.Finished.toString()
        )

        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            if (moveToFirst()) {
                return getLong(getColumnIndexOrThrow(resultColumn)).also {
                    close()
                }
            }
        }
        return 0
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        val totalNumberOfBooks = getTotalNumberOfBooks()
        if (totalNumberOfBooks == 0) return 0.0
        return getTotalNumberOfPages() / totalNumberOfBooks.toDouble()
    }

    fun getAverageReadingTime(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val startTime = getCalendarYearStart()
        val endTime = getCalendarYearEnd()
        val readingTimeTotal =
            readingTimeDBService.getTotalReadingTimeForTimePeriod(startTime, endTime)
        val totalBooks = getTotalNumberOfBooks()

        return if (totalBooks != 0) readingTimeTotal.toDouble() / getTotalNumberOfBooks().toDouble() else 0.0
    }

    fun getAveragePagesPerDay(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val startTime = getCalendarYearStart()
        val endTime = getCalendarYearEnd()
        val readingTime = readingTimeDBService.getTotalReadingTimeForTimePeriod(startTime, endTime)

        return if (readingTime == 0) 0.0
        else getTotalNumberOfPages() / readingTime.toDouble()
    }

    fun getAverageBooksPerMonth(): Double {
        return getTotalNumberOfBooks() / 12.0
    }

    fun getAverageBooksPerWeek(): Double {
        return getTotalNumberOfBooks() / 52.0
    }

    fun getMonthWithMostBooksRead(): String {
        if (getTotalNumberOfBooks() == 0) return "-"
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, 0)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val monthlyStatsService = MonthlyStatsDBService(context, calendar)
        var maxBooksPerMonth = monthlyStatsService.getTotalNumberOfBooks()
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)

        for (i in 1..Calendar.getInstance().get(Calendar.MONTH)) {
            monthlyStatsService.setMonth(calendar.apply { set(Calendar.MONTH, i) })
            val booksForMonth = monthlyStatsService.getTotalNumberOfBooks()
            if (booksForMonth > maxBooksPerMonth) {
                maxBooksPerMonth = booksForMonth
                month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
            }
        }

        return month ?: "-"
    }

    fun getYearProgress(): Double {
        if (Calendar.getInstance().get(Calendar.YEAR) > year.get(Calendar.YEAR)) return 100.0
        val maxDays = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return currentDay.toDouble() / maxDays.toDouble()
    }

    private fun getCalendarYearStart(): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = year.timeInMillis
        }
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal
    }

    private fun getCalendarYearEnd(): Calendar {
        val cal = Calendar.getInstance().apply {
            timeInMillis = year.timeInMillis
        }
        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal
    }
}