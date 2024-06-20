package ozarskiapps.booktracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.calendarFromMillis
import java.util.*

class BookDBService(private val context: Context) : DBService(context) {

    val fullBookProjection = arrayOf(
        BaseColumns._ID,
        DatabaseConstants.BookTable.TITLE_COLUMN,
        DatabaseConstants.BookTable.AUTHOR_COLUMN,
        DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN,
        DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN,
        DatabaseConstants.BookTable.BOOK_STATUS_COLUMN,
        DatabaseConstants.BookTable.START_DATE_COLUMN,
        DatabaseConstants.BookTable.END_DATE_COLUMN
    )

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

        return db.insert(DatabaseConstants.BookTable.TABLE_NAME, null, contentValues).also {
            book.id = it
            if(book.bookStatus == BookStatus.Finished){
                ReadingTimeDBService(context).addBookReadingTime(book)
            }
        }
    }

    fun getBookByID(id: Long): Book? {
        val db = this.readableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf(id.toString())

        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            fullBookProjection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            if (moveToFirst()) {
                return getBookFromCursor(this)
            }
        }
        return null
    }

    fun updateBook(book: Book) {
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
        ReadingTimeDBService(context).updateBookReadingTime(book)
    }

    fun deleteBookByID(id: Long) {
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        db.delete(DatabaseConstants.BookTable.TABLE_NAME, selection, selectionArgs)
        ReadingTimeDBService(context).deleteBookReadingTimeByBookID(id)
        TagDBService(context).removeBookTagItems(bookID = id)
    }

    fun getAllWantToReadBooks(): List<Book> {
        val db = this.readableDatabase
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.WantToRead.toString())

        val bookList = mutableListOf<Book>()
        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            fullBookProjection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run {
            while (moveToNext()) {
                bookList.add(getBookFromCursor(this))
            }
        }
        return bookList
    }

    fun startReadingBookToday(book: Book) {
        if (book.bookStatus == BookStatus.WantToRead) {
            book.bookStatus = BookStatus.Reading
            book.startDate = Calendar.getInstance()
            updateBook(book)
        }
    }

    fun finishReadingBookToday(book: Book) {
        if(book.bookStatus == BookStatus.Finished) return
        book.startDate =
            if (book.bookStatus == BookStatus.WantToRead) Calendar.getInstance() else book.startDate
        book.endDate = Calendar.getInstance()
        book.bookStatus = BookStatus.Finished
        updateBook(book)
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
                BookStatus.valueOf(
                    getString(getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN))
                )

            val startDate = calendarFromMillis(
                getLong(getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
            )
            val endDate = calendarFromMillis(
                getLong(getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))
            )
            return Book(
                title,
                author,
                numberOfPages,
                currentProgress,
                bookStatus,
                startDate,
                endDate,
                id
            )
        }
    }

    fun updateBookProgress(book: Book, progress: Float) {
        if (book.bookStatus == BookStatus.Reading) {
            book.currentProgress = progress
            updateBook(book)
        }
    }

    fun getAllBooks(): List<Book> {
        val db = this.readableDatabase

        val bookList = mutableListOf<Book>()
        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            fullBookProjection,
            null,
            null,
            null,
            null,
            null
        ).run{
            while (moveToNext()) {
                bookList.add(getBookFromCursor(this))
            }
        }
        return bookList
    }

    fun getBooksWithIDs(ids: List<Long>): List<Book> {
        val db = this.readableDatabase

        val selection = "${BaseColumns._ID} IN (${ids.joinToString(",")})"
        val bookList = mutableListOf<Book>()
        db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            fullBookProjection,
            selection,
            null,
            null,
            null,
            null
        ).run{
            while (moveToNext()) {
                bookList.add(getBookFromCursor(this))
            }
        }
        return bookList
    }
}