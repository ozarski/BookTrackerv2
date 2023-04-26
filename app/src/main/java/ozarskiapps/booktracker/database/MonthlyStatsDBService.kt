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

    private var books = getBooksForMonth(month)

    fun setMonth(month: Calendar) {
        books = getBooksForMonth(month)
        this.month.timeInMillis = month.timeInMillis
    }

    private fun getBooksForMonth(month: Calendar): List<Book> {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.BookTable.TITLE_COLUMN,
            DatabaseConstants.BookTable.AUTHOR_COLUMN,
            DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN,
            DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN,
            DatabaseConstants.BookTable.BOOK_STATUS_COLUMN,
            DatabaseConstants.BookTable.START_DATE_COLUMN,
            DatabaseConstants.BookTable.END_DATE_COLUMN,
        )
        val selection =
            "${DatabaseConstants.BookTable.END_DATE_COLUMN} >= ? " +
                    "AND ${DatabaseConstants.BookTable.END_DATE_COLUMN} <= ? " +
                    "AND ${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val startCal = getCalendarMonthStart(month)
        val endCal = getCalendarMonthEnd(month)
        val selectionArgs = arrayOf(
            startCal.timeInMillis.toString(),
            endCal.timeInMillis.toString(),
            BookStatus.Finished.toString()
        )
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val booksForMonth = mutableListOf<Book>()
        while (cursor.moveToNext()) {
            val book = getBookFromCursor(cursor)
            booksForMonth.add(book)
        }
        return booksForMonth

    }

    private fun getBookFromCursor(cursor: Cursor): Book {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val title =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.TITLE_COLUMN))
        val author =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN))
        val numberOfPages =
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN))
        val currentProgress =
            cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN))
        val bookStatus =
            BookStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN)))
        val startDateMillis =
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
        val endDateMillis =
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))
        return Book(
            title,
            author,
            numberOfPages,
            currentProgress,
            bookStatus,
            calendarFromMillis(startDateMillis),
            calendarFromMillis(endDateMillis),
            id
        )
    }

    fun getTotalNumberOfBooks(): Int {
        return books.size
    }

    fun getTotalNumberOfPages(): Long {
        if (getTotalNumberOfBooks() == 0) return 0

        return books.sumOf { it.numberOfPages }.toLong()
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        if (getTotalNumberOfBooks() == 0) return 0.0
        return getTotalNumberOfPages().toDouble() / getTotalNumberOfBooks()
    }

    fun getAverageReadingTime(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val monthStart = getCalendarMonthStart(month)
        val monthEnd = getCalendarMonthEnd(month)
        val readingTime = readingTimeDBService.getTotalReadingTimeForTimePeriod(
            monthStart,
            monthEnd
        )
        if (readingTime == 0) return 0.0
        return readingTime.toDouble() / getTotalNumberOfBooks()
    }

    fun getAveragePagesPerDay(): Double {
        val readingTimeDBService = ReadingTimeDBService(context)
        val monthStart = getCalendarMonthStart(month)
        val monthEnd = getCalendarMonthEnd(month)
        val readingTime = readingTimeDBService.getTotalReadingTimeForTimePeriod(
            monthStart,
            monthEnd
        )
        if (readingTime == 0) return 0.0
        return getTotalNumberOfPages().toDouble() / readingTime
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


}