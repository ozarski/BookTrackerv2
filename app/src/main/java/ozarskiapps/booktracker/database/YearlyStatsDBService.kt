package ozarskiapps.booktracker.database

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.calendarFromMillis
import ozarskiapps.booktracker.setCalendar
import java.util.*

class YearlyStatsDBService(
    val context: Context,
    private var year: Calendar = Calendar.getInstance()
) : DBService(context) {

    private var books = getBooksForYear(year)

    fun setYear(year: Calendar) {
        books = getBooksForYear(year)
        this.year.timeInMillis = year.timeInMillis
    }

    private fun getBooksForYear(year: Calendar): List<Book> {
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
        val startCal = getCalendarYearStart()
        val endCal = getCalendarYearEnd()

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

        val books = mutableListOf<Book>()
        with(cursor) {
            while (cursor.moveToNext()) {
                val book = getBookFromCursor(cursor)
                books.add(book)
            }
        }
        return books
    }

    fun getTotalNumberOfPages(): Long {
        return if (books.isEmpty()) 0 else books.sumOf { it.numberOfPages }.toLong()
    }

    fun getTotalNumberOfBooks(): Int {
        return books.size
    }

    fun getAverageNumberOfPagesPerBook(): Double {
        if (getTotalNumberOfPages() == 0L || getTotalNumberOfBooks() == 0) return 0.0
        return getTotalNumberOfPages() / getTotalNumberOfBooks().toDouble()
    }

    fun getAverageReadingTime(): Double {
        if (getTotalNumberOfBooks() == 0) return 0.0
        var readingTimeTotal = 0
        books.forEach { book ->
            if (book.bookStatus == BookStatus.Finished) {
                readingTimeTotal += book.getBookReadingTimeInDays()
            }
        }
        return readingTimeTotal.toDouble() / getTotalNumberOfBooks().toDouble()
    }

    fun getAveragePagesPerDay(): Double {
        if(getTotalNumberOfBooks() == 0) return 0.0
        val readingTimeDBService = ReadingTimeDBService(context)
        val startTime = getCalendarYearStart()
        val endTime = getCalendarYearEnd()
        val readingTime = readingTimeDBService.getTotalReadingTimeForTimePeriod(startTime, endTime)
        return if (readingTime == 0) 0.0
        else getTotalNumberOfPages() / readingTime.toDouble()
    }

    fun getAverageBooksPerMonth(): Double {
        if(getTotalNumberOfBooks() == 0) return 0.0

        return getTotalNumberOfBooks() / 12.0
    }

    fun getAverageBooksPerWeek(): Double {
        if(getTotalNumberOfBooks() == 0) return 0.0

        return getTotalNumberOfBooks() / 52.0
    }

    fun getMonthWithMostBooksRead(): String {
        if(getTotalNumberOfBooks() == 0) return "-"
        val calendar = Calendar.getInstance().apply { set(Calendar.MONTH, 0) }
        var maxBooksPerMonth = getNumberOfBooksForMonth(calendar)
        var month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)

        for(i in 1..11){
            calendar.set(Calendar.MONTH, i)
            val booksForMonth = getNumberOfBooksForMonth(calendar)
            if(booksForMonth > maxBooksPerMonth){
                maxBooksPerMonth = booksForMonth
                month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
            }
        }

        return month ?: "-"
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

    fun getBookFromCursor(cursor: Cursor): Book {
        val title =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.TITLE_COLUMN))
        val author =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN))
        val numberOfPages =
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN))
        val bookStatus =
            BookStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN)))
        val currentProgress =
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN))
        val startDateMillis =
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
        val endDateMillis =
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val book = Book(
            title,
            author,
            numberOfPages,
            currentProgress,
            bookStatus,
            calendarFromMillis(startDateMillis),
            calendarFromMillis(endDateMillis),
            id
        )
        return book
    }

    fun getCalendarYearStart(): Calendar{
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

    fun getCalendarYearEnd(): Calendar{
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

    fun getNumberOfBooksForMonth(month: Calendar): Int{
        val start = getCalendarMonthStart(month)
        val end = getCalendarMonthEnd(month)
        val booksThisMonth = books.filter{ book ->
            book.endDate.timeInMillis >= start.timeInMillis && book.endDate.timeInMillis <= end.timeInMillis
        }
        return booksThisMonth.size
    }

}