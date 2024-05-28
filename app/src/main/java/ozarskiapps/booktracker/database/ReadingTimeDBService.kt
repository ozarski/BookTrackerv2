package ozarskiapps.booktracker.database

import android.content.ContentValues
import android.content.Context
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.readingTime.ReadingDay
import ozarskiapps.booktracker.setCalendar
import java.util.*

class ReadingTimeDBService(context: Context) : DBService(context) {

    fun getNumberOfBooksReadInTimePeriod(start: Calendar, end: Calendar): Int {
        val db = this.readableDatabase
        val startCal = setCalendar(start)
        val endCal = setCalendar(end, false)
        val resultColumn = "numberOfBooks"
        val projection =
            arrayOf("COUNT(DISTINCT ${DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN}) as $resultColumn")

        val selection = "${DatabaseConstants.ReadingTimeTable.DATE_COLUMN} BETWEEN ? AND ?"
        val selectionArgs =
            arrayOf(startCal.timeInMillis.toString(), endCal.timeInMillis.toString())

        db.query(
            DatabaseConstants.ReadingTimeTable.TABLE_NAME,
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

    fun getBookIdsReadInTimePeriod(start: Calendar, end: Calendar): List<Long> {
        val db = this.readableDatabase
        val startCal = setCalendar(start)
        val endCal = setCalendar(end, false)
        val resultColumn = "bookID"
        val projection =
            arrayOf("DISTINCT ${DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN} as $resultColumn")

        val selection = "${DatabaseConstants.ReadingTimeTable.DATE_COLUMN} BETWEEN ? AND ?"
        val selectionArgs =
            arrayOf(startCal.timeInMillis.toString(), endCal.timeInMillis.toString())

        val bookIds = mutableListOf<Long>()
        db.query(
            DatabaseConstants.ReadingTimeTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            while (moveToNext()) {
                bookIds.add(getLong(getColumnIndexOrThrow(resultColumn)))
            }
            close()
        }
        return bookIds
    }

    fun addBookReadingTime(book: Book) {
        val days = getReadingDaysForBook(book)
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            days.forEach {
                val contentValues = ContentValues().apply {
                    put(DatabaseConstants.ReadingTimeTable.DATE_COLUMN, it.date.timeInMillis)
                    put(DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN, it.bookID)
                }
                db.insert(DatabaseConstants.ReadingTimeTable.TABLE_NAME, null, contentValues)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateBookReadingTime(book: Book) {
        deleteBookReadingTimeByBookID(book.id)
        addBookReadingTime(book)
    }

    fun deleteBookReadingTimeByBookID(id: Long) {
        val selection = "${DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(id.toString())
        this.writableDatabase.delete(
            DatabaseConstants.ReadingTimeTable.TABLE_NAME,
            selection,
            selectionArgs
        )
    }

    fun getTotalReadingTime(): Int {
        val db = this.readableDatabase
        val resultColumn = "numberOfDays"
        val projection =
            arrayOf("COUNT(DISTINCT ${DatabaseConstants.ReadingTimeTable.DATE_COLUMN}) as $resultColumn")

        db.query(
            DatabaseConstants.ReadingTimeTable.TABLE_NAME,
            projection,
            null,
            null,
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

    fun getTotalReadingTimeForTimePeriod(start: Calendar, end: Calendar): Int {
        val db = this.readableDatabase
        val startCal = setCalendar(start)
        val endCal = setCalendar(end, false)
        val resultColumn = "numberOfDays"
        val projection =
            arrayOf("COUNT(DISTINCT ${DatabaseConstants.ReadingTimeTable.DATE_COLUMN}) as $resultColumn")
        val selection = "${DatabaseConstants.ReadingTimeTable.DATE_COLUMN} BETWEEN ? AND ?"
        val selectionArgs =
            arrayOf(startCal.timeInMillis.toString(), endCal.timeInMillis.toString())

        db.query(
            DatabaseConstants.ReadingTimeTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run{
            if (moveToFirst()) {
                return getInt(getColumnIndexOrThrow(resultColumn)).also{
                    close()
                }
            }
        }

        return 0
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