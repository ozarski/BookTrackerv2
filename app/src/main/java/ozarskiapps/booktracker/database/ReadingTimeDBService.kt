package ozarskiapps.booktracker.database

import android.content.ContentValues
import android.content.Context
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.readingTime.ReadingDay
import java.util.*

class ReadingTimeDBService(context: Context) : DBService(context) {

    fun getDaysByBookID(id: Long): List<ReadingDay> {
        //TODO("Not implemented yet")
        return mutableListOf()
    }

    fun getBookIDsForTimePeriod(start: Calendar, end: Calendar): List<Long> {
        //TODO("Not implemented yet")
        return mutableListOf()
    }

    fun addBookReadingTime(book: Book) {
        val days = getReadingDaysForBook(book)
        val db = this.writableDatabase
        days.forEach {
            val contentValues = ContentValues().apply {
                put(DatabaseConstants.ReadingTimeTable.DATE_COLUMN, it.date.timeInMillis)
                put(DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN, it.bookID)
            }
            db.insert(DatabaseConstants.ReadingTimeTable.TABLE_NAME, null, contentValues)
        }
    }

    fun updateBookReadingTime(book: Book) {
        deleteBookReadingTimeByBookID(book.id)
        addBookReadingTime(book)
    }

    fun deleteBookReadingTimeByBookID(id: Long) {
        val db = this.writableDatabase
        val selection = "${DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(DatabaseConstants.ReadingTimeTable.TABLE_NAME, selection, selectionArgs)
    }

    fun getTotalReadingTime(): Int {
        //TODO("Not implemented yet")
        return -1
    }

    fun getTotalReadingTimeForTimePeriod(start: Calendar, end: Calendar): Int {
        //TODO("Not implemented yet")
        return -1
    }

    private fun getReadingDaysForBook(book: Book): List<ReadingDay> {
        val readingDayList = mutableListOf<ReadingDay>()
        val start = Calendar.getInstance().apply { timeInMillis = book.startDate.timeInMillis }
        val end = Calendar.getInstance().apply { timeInMillis = book.endDate.timeInMillis }

        while (start <= end) {
            val readingDay = ReadingDay(
                Calendar.getInstance().apply { timeInMillis = start.timeInMillis },
                book.id
            )
            readingDayList.add(readingDay)
            start.add(Calendar.DAY_OF_YEAR, 1)
        }
        return readingDayList
    }
}