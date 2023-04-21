package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.setCalendar
import java.util.*

class GlobalStatsDBService(val context: Context) : DBService(context) {

    fun getTotalNumberOfPages(): Long {
        val db = this.readableDatabase
        val resultColumn = "totalNumberOfPages"
        val projection =
            arrayOf("SUM(${DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            if (moveToFirst()) {
                val numberOfPages = getLong(getColumnIndexOrThrow(resultColumn))
                return if (numberOfPages > 0) numberOfPages else 0
            }
        }
        return 0
    }

    fun getTotalNumberOfBooks(): Int {
        val db = this.readableDatabase
        val resultColumn = "totalNumberOfBooks"
        val projection = arrayOf("COUNT(*) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            if (moveToFirst()) {
                return getInt(getColumnIndexOrThrow(resultColumn))
            }
        }
        return 0
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        val db = this.readableDatabase
        val resultColumn = "averageNumberOfPagesPerBook"
        val projection =
            arrayOf("AVG(${DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            if (moveToFirst()) {
                val averageNumberOfPagesPerBook = getDouble(getColumnIndexOrThrow(resultColumn))
                return if (averageNumberOfPagesPerBook > 0) averageNumberOfPagesPerBook else 0.0
            }
        }
        return 0.0
    }

    fun getAverageReadingTime(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val readingTime = readingTimeDBService.getTotalReadingTime()
        val numberOfBooks = getTotalNumberOfBooks()
        return if (numberOfBooks > 0) readingTime.toDouble() / numberOfBooks.toDouble() else 0.0
    }

    fun getAveragePagesPerDay(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val readingTime = readingTimeDBService.getTotalReadingTime()
        val numberOfPages = getTotalNumberOfPages()
        return if (readingTime > 0) numberOfPages.toDouble() / readingTime.toDouble() else 0.0
    }

    fun getAverageBooksPerMonth(): Double {

        val numberOfBooks = getTotalNumberOfBooks()
        val db = this.readableDatabase
        val resultColumn = "minDate"
        val projection =
            arrayOf("MIN(${DatabaseConstants.BookTable.END_DATE_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            if (moveToFirst()) {
                val minDate = getLong(getColumnIndexOrThrow(resultColumn))
                val numberOfMonths = getNumberOfMonthsBetweenDates(
                    Calendar.getInstance().apply {
                        timeInMillis = minDate
                    },
                    Calendar.getInstance()
                )
                return if (numberOfMonths > 0) numberOfBooks.toDouble() / numberOfMonths.toDouble() else 0.0
            }
        }

        return 0.0
    }

    fun getAverageBooksPerWeek(): Double {
        val numberOfBooks = getTotalNumberOfBooks()
        val db = this.readableDatabase
        val resultColumn = "minDate"
        val projection =
            arrayOf("MIN(${DatabaseConstants.BookTable.END_DATE_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            if (moveToFirst()) {
                val minDate = getLong(getColumnIndexOrThrow(resultColumn))
                val numberOfWeeks = getNumberOfWeeksBetweenDates(
                    Calendar.getInstance().apply {
                        timeInMillis = minDate
                    },
                    Calendar.getInstance()
                )
                return if (numberOfWeeks > 0) numberOfBooks.toDouble() / numberOfWeeks.toDouble() else 0.0
            }
        }

        return 0.0
    }

    fun getMonthWithMostBooksRead(): String {
        val db = this.readableDatabase
        val resultColumn = "minDate"
        val projection =
            arrayOf("MIN(${DatabaseConstants.BookTable.END_DATE_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        var minDate = 0L
        with(cursor) {
            if (moveToFirst()) {
                minDate = getLong(getColumnIndexOrThrow(resultColumn))
            }
        }
        if (minDate == 0L) {
            return "-"
        }

        val minDateCal = Calendar.getInstance().apply {
            timeInMillis = minDate
        }

        var maxMonth = getMonthNameAndYearEnglish(minDateCal)
        var maxNumberOfBooks = 0
        while (minDateCal.before(
                setCalendar(
                    Calendar.getInstance()
                        .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) })
            )
        ) {
            val monthStart = getCalendarMonthStart(minDateCal)
            val monthEnd = getCalendarMonthEnd(minDateCal)

            val numberOfBooks =
                ReadingTimeDBService(context).getNumberOfBooksReadInTimePeriod(monthStart, monthEnd)
            if (numberOfBooks > maxNumberOfBooks) {
                maxNumberOfBooks = numberOfBooks
                maxMonth = getMonthNameAndYearEnglish(minDateCal)
            }
            minDateCal.add(Calendar.MONTH, 1)
        }

        return maxMonth
    }

    fun getMostReadAuthor(): String {
        val db = this.readableDatabase
        val resultColumn = "author"
        val projection =
            arrayOf("COUNT(${DatabaseConstants.BookTable.AUTHOR_COLUMN}) as $resultColumn, ${DatabaseConstants.BookTable.AUTHOR_COLUMN}")
        val groupBy = DatabaseConstants.BookTable.AUTHOR_COLUMN
        val orderBy = "$resultColumn DESC"
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            groupBy,
            null,
            orderBy
        )
        with(cursor) {
            if (moveToFirst()) {
                return getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN))
            }
        }
        return "-"
    }

    fun getNumberOfMonthsBetweenDates(start: Calendar, end: Calendar): Int {
        var numberOfMonths = 0
        val iteratorDate = Calendar.getInstance().apply {
            timeInMillis = start.timeInMillis
        }
        while (iteratorDate.before(end)) {
            numberOfMonths++
            iteratorDate.add(Calendar.MONTH, 1)
        }
        numberOfMonths++
        return numberOfMonths
    }

    fun getNumberOfWeeksBetweenDates(start: Calendar, end: Calendar): Int {
        var numberOfWeeks = 0
        val iteratorDate = Calendar.getInstance().apply {
            timeInMillis = start.timeInMillis
        }
        while (iteratorDate.before(end)) {
            numberOfWeeks++
            iteratorDate.add(Calendar.WEEK_OF_YEAR, 1)
        }
        numberOfWeeks++
        return numberOfWeeks
    }

    private fun getMonthNameAndYearEnglish(date: Calendar): String {
        return "${date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)} ${
            date.get(
                Calendar.YEAR
            )
        }"
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

}