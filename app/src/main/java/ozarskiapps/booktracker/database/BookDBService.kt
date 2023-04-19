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

        val id = db.insert(DatabaseConstants.BookTable.TABLE_NAME, null, contentValues)
        book.id = id
        ReadingTimeDBService(context).addBookReadingTime(book)
        return id
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
            DatabaseConstants.BookTable.END_DATE_COLUMN,
            BaseColumns._ID
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
                return getBookFromCursor(cursor)
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
        TagDBService(context).removeTagFromBook(bookID = id)
    }

    fun getAllWantToReadBooks(): List<Book> {
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.BookTable.TITLE_COLUMN,
            DatabaseConstants.BookTable.AUTHOR_COLUMN,
            DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN,
            DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN,
            DatabaseConstants.BookTable.BOOK_STATUS_COLUMN,
            DatabaseConstants.BookTable.START_DATE_COLUMN,
            DatabaseConstants.BookTable.END_DATE_COLUMN,
            BaseColumns._ID
        )
        val selection = "${DatabaseConstants.BookTable.BOOK_STATUS_COLUMN} = ?"
        val selectionArgs = arrayOf(BookStatus.WantToRead.toString())

        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val bookList = mutableListOf<Book>()
        while (cursor.moveToNext()) {
            bookList.add(getBookFromCursor(cursor))
        }
        return bookList
    }

    fun startReadingBookToday(book: Book) {
        if(book.bookStatus == BookStatus.WantToRead){
            book.bookStatus = BookStatus.Reading
            book.startDate = Calendar.getInstance()
            updateBook(book)
        }
    }

    fun finishReadingBookToday(book: Book) {
        if(book.bookStatus == BookStatus.Reading){
            book.bookStatus = BookStatus.Finished
            book.endDate = Calendar.getInstance()
            updateBook(book)
        }
        else if(book.bookStatus == BookStatus.WantToRead){
            book.bookStatus = BookStatus.Finished
            book.startDate = Calendar.getInstance()
            book.endDate = Calendar.getInstance()
            updateBook(book)
        }
    }

    private fun getBookFromCursor(cursor: Cursor): Book{

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
            BookStatus.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN))
            )

        val startDate = calendarFromMillis(
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
        )
        val endDate = calendarFromMillis(
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))
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

    fun updateBookProgress(book: Book, progress: Float){
        if(book.bookStatus == BookStatus.Reading){
            book.currentProgress = progress
            updateBook(book)
        }
    }

    fun getAllBooks(): List<Book>{
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.BookTable.TITLE_COLUMN,
            DatabaseConstants.BookTable.AUTHOR_COLUMN,
            DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN,
            DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN,
            DatabaseConstants.BookTable.BOOK_STATUS_COLUMN,
            DatabaseConstants.BookTable.START_DATE_COLUMN,
            DatabaseConstants.BookTable.END_DATE_COLUMN,
            BaseColumns._ID
        )

        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val bookList = mutableListOf<Book>()
        while (cursor.moveToNext()) {
            bookList.add(getBookFromCursor(cursor))
        }
        return bookList
    }

    fun getBooksWithIDs(ids: List<Long>): List<Book>{
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.BookTable.TITLE_COLUMN,
            DatabaseConstants.BookTable.AUTHOR_COLUMN,
            DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN,
            DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN,
            DatabaseConstants.BookTable.BOOK_STATUS_COLUMN,
            DatabaseConstants.BookTable.START_DATE_COLUMN,
            DatabaseConstants.BookTable.END_DATE_COLUMN,
            BaseColumns._ID
        )
        val selection = "${BaseColumns._ID} IN (${ids.joinToString(",")})"
        val cursor = db.query(
            DatabaseConstants.BookTable.TABLE_NAME,
            projection,
            selection,
            null,
            null,
            null,
            null
        )
        val bookList = mutableListOf<Book>()
        while(cursor.moveToNext()){
            bookList.add(getBookFromCursor(cursor))
        }
        return bookList
    }
}