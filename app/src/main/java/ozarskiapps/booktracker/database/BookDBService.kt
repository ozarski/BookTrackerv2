package ozarskiapps.booktracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import java.util.*

class BookDBService(context: Context) : DBService(context) {

    fun addBook(book: Book): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(DatabaseConstants.BookTable.TITLE_COLUMN, book.title)
            put(DatabaseConstants.BookTable.AUTHOR_COLUMN, book.author)
            put(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN, book.numberOfPages)
            put(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN, book.currentProgress)
            put(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN, book.bookStatus.toString())
            put(DatabaseConstants.BookTable.START_DATE_COLUMN, book.startDate.timeInMillis)
            put(DatabaseConstants.BookTable.END_DATE_COLUMN, book.endDate.timeInMillis)
        }

        return db.insert(DatabaseConstants.BookTable.TABLE_NAME, null, contentValues)
    }

    fun getBookByID(id: Long): Book? {
        val db = this.readableDatabase

        val projection = arrayOf(
            DatabaseConstants.BookTable.TITLE_COLUMN,
            DatabaseConstants.BookTable.AUTHOR_COLUMN,
            DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN,
            DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN,
            DatabaseConstants.BookTable.BOOK_STATUS_COLUMN,
            DatabaseConstants.BookTable.START_DATE_COLUMN,
            DatabaseConstants.BookTable.END_DATE_COLUMN
        )

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor) {
            if (moveToFirst()) {
                val title =
                    getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.TITLE_COLUMN))
                val author =
                    getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN))
                val numberOfPages =
                    getInt(getColumnIndexOrThrow(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN))
                val currentProgress =
                    getInt(getColumnIndexOrThrow(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN))
                val bookStatus =
                    getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN))
                val startDate =
                    getLong(getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
                val endDate =
                    getLong(getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))

                val bookStatusEnum = BookStatus.valueOf(bookStatus)
                val startDateCalendar = Calendar.getInstance().apply{ timeInMillis = startDate }
                val endDateCalendar = Calendar.getInstance().apply { timeInMillis = endDate }

                return Book(
                    title,
                    author,
                    numberOfPages,
                    currentProgress,
                    bookStatusEnum,
                    startDateCalendar,
                    endDateCalendar,
                    id
                )
            }
        }
        return null
    }

    fun updateBook(book: Book){
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(DatabaseConstants.BookTable.TITLE_COLUMN, book.title)
            put(DatabaseConstants.BookTable.AUTHOR_COLUMN, book.author)
            put(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN, book.numberOfPages)
            put(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN, book.currentProgress)
            put(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN, book.bookStatus.toString())
            put(DatabaseConstants.BookTable.START_DATE_COLUMN, book.startDate.timeInMillis)
            put(DatabaseConstants.BookTable.END_DATE_COLUMN, book.endDate.timeInMillis)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(book.id.toString())

        db.update(DatabaseConstants.BookTable.TABLE_NAME, contentValues, selection, selectionArgs)
    }

    fun deleteBookByID(id: Long){
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        db.delete(DatabaseConstants.BookTable.TABLE_NAME, selection, selectionArgs)
    }
}