package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.setCalendar
import java.time.Month
import java.util.*

class TotalStatsDBService(val context: Context) : DBService(context) {

    fun getTotalNumberOfPages(): Long {
        val db = this.readableDatabase
        val resultColumn = "totalNumberOfPages"
        val projection =
            arrayOf("SUM(${DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN}) as $resultColumn")
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())

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
                val numberOfPages = getLong(getColumnIndexOrThrow(resultColumn))
                close()
                return if (numberOfPages > 0) numberOfPages else 0
            }
        }
        return 0
    }

    fun getTotalNumberOfBooks(): Int {
        val db = this.readableDatabase
        val resultColumn = "totalNumberOfBooks"
        val projection = arrayOf("COUNT(*) as $resultColumn")
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())

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
                return getInt(getColumnIndexOrThrow(resultColumn)).also { close() }
            }
        }
        return 0
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        val db = this.readableDatabase
        val resultColumn = "averageNumberOfPagesPerBook"
        val projection =
            arrayOf("AVG(${DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN}) as $resultColumn")
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())

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
                val averageNumberOfPagesPerBook = getDouble(getColumnIndexOrThrow(resultColumn))
                close()
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
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())

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
                val minDate = getLong(getColumnIndexOrThrow(resultColumn))
                val numberOfMonths = getNumberOfMonthsBetweenDates(
                    Calendar.getInstance().apply {
                        timeInMillis = minDate
                    },
                    Calendar.getInstance()
                )
                close()
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
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())

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
                val minDate = getLong(getColumnIndexOrThrow(resultColumn))
                val numberOfWeeks = getNumberOfWeeksBetweenDates(
                    Calendar.getInstance().apply {
                        timeInMillis = minDate
                    },
                    Calendar.getInstance()
                )
                close()
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
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())

        val minDateCal = Calendar.getInstance()

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
                getLong(getColumnIndexOrThrow(resultColumn)).let {
                    if (it > 0) {
                        minDateCal.timeInMillis = it
                    } else {
                        return "-"
                    }
                }
                close()
            }
        }


        var maxMonth = getMonthNameAndYearEnglish(minDateCal)
        var maxNumberOfBooks = 0
        val monthStatsService = MonthlyStatsDBService(context, minDateCal)

        while (minDateCal.before(setCalendar(Calendar.getInstance(), false))) {
            val numberOfBooks =
                monthStatsService.getTotalNumberOfBooks()

            if (numberOfBooks > maxNumberOfBooks) {
                maxNumberOfBooks = numberOfBooks
                maxMonth = getMonthNameAndYearEnglish(minDateCal)
            }

            minDateCal.add(Calendar.MONTH, 1)
            monthStatsService.setMonth(minDateCal)
        }

        return maxMonth
    }

    fun getMostReadAuthor(): String {
        val db = this.readableDatabase
        val resultColumn = "author"
        val projection =
            arrayOf("COUNT(${DatabaseConstants.BookTable.AUTHOR_COLUMN}) as $resultColumn, ${DatabaseConstants.BookTable.AUTHOR_COLUMN}")
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.Finished.toString())
        val groupBy = DatabaseConstants.BookTable.AUTHOR_COLUMN
        val orderBy = "$resultColumn DESC"

        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            groupBy,
            null,
            orderBy
        ).run {
            if (moveToFirst()) {
                return getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN)).also {
                    close()
                }
            }
        }
        return "-"
    }

    fun getNumberOfMonthsBetweenDates(start: Calendar, end: Calendar): Int {
        return ((end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12
        + end.get(Calendar.MONTH) - start.get(Calendar.MONTH) + 1)
    }

    fun getNumberOfWeeksBetweenDates(start: Calendar, end: Calendar): Int {
        var numberOfWeeks = 0
        val iteratorDate = start.clone() as Calendar
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
}