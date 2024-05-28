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

        val (monthStart, monthEnd) = getCalendarStartEnd(month)

        val selection =
            "${DatabaseConstants.BookTable.END_DATE_COLUMN} >= ? " +
                    "AND ${DatabaseConstants.BookTable.END_DATE_COLUMN} <= ? " +
                    "AND ${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(
            monthStart.timeInMillis.toString(),
            monthEnd.timeInMillis.toString(),
            BookStatus.Finished.toString()
        )

        val booksForMonth = mutableListOf<Book>()
        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).apply{
            while (moveToNext()) {
                val book = getBookFromCursor(this)
                booksForMonth.add(book)
            }
        }
        return booksForMonth

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

    private fun getBookFromCursor(cursor: Cursor): Book {
        with(cursor){
            val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            val title =
                getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.TITLE_COLUMN))
            val author =
                getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN))
            val numberOfPages =
                getInt(getColumnIndexOrThrow(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN))
            val currentProgress =
                getFloat(getColumnIndexOrThrow(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN))
            val bookStatus =
                BookStatus.valueOf(getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN)))
            val startDateMillis =
                getLong(getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
            val endDateMillis =
                getLong(getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))

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