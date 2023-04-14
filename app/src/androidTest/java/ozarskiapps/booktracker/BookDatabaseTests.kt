package ozarskiapps.booktracker

import android.content.Context
import android.provider.BaseColumns
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.DatabaseConstants
import java.util.*

@RunWith(AndroidJUnit4::class)
class BookDatabaseTests {

    private lateinit var bookDBService: BookDBService
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(appContext)
    }

    @After
    fun tearDown() {
        bookDBService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }


    @Test
    fun testAddingBookToDB() {
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        bookDBService.addBook(book)
        val cursor = bookDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.BookTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(
                book.title,
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.TITLE_COLUMN))
            )
            assertEquals(
                book.author,
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.AUTHOR_COLUMN))
            )
            assertEquals(
                book.numberOfPages,
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.NUMBER_OF_PAGES_COLUMN))
            )
            assertEquals(
                book.currentProgress,
                cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.CURRENT_PROGRESS_COLUMN))
            )
            assertEquals(
                book.bookStatus.toString(),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.BOOK_STATUS_COLUMN))
            )
            assertEquals(
                book.startDate.timeInMillis,
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.START_DATE_COLUMN))
            )
            assertEquals(
                book.endDate.timeInMillis,
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTable.END_DATE_COLUMN))
            )
        }
    }

    @Test
    fun testAddBookWrongEndDate() {
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        )
        bookDBService.addBook(book)
        testGettingBookByID()

        val bookWithCorrectedDate = bookDBService.getBookByID(book.id)
        assertNotNull(bookWithCorrectedDate)
        assertEquals(book.endDate.timeInMillis, bookWithCorrectedDate?.startDate?.timeInMillis)

    }

    @Test
    fun testModifyingBook() {
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        book.author = "New author"
        book.bookStatus = BookStatus.Reading
        book.currentProgress = 10f
        bookDBService.updateBook(book)
        val cursor = bookDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.BookTable.TABLE_NAME} WHERE ${BaseColumns._ID} = ${book.id}",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(book.title, cursor.getString(1))
            assertEquals(book.author, cursor.getString(2))
            assertEquals(book.numberOfPages, cursor.getInt(3))
            assertEquals(book.currentProgress, cursor.getFloat(4))
            assertEquals(book.bookStatus.toString(), cursor.getString(5))
            assertEquals(book.startDate.timeInMillis, cursor.getLong(6))
            assertEquals(book.endDate.timeInMillis, cursor.getLong(7))
        }
    }

    @Test
    fun testDeletingBook() {
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        bookDBService.deleteBookByID(book.id)
        val cursor = bookDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.BookTable.TABLE_NAME} WHERE ${BaseColumns._ID} = ${book.id}",
            null
        )
        cursor.use {
            assertFalse(cursor.moveToFirst())
        }
    }

    @Test
    fun testGettingBookByID() {
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        val bookFromDB = bookDBService.getBookByID(book.id)
        assertNotNull(bookFromDB)
        assertEquals(book.title, bookFromDB?.title)
        assertEquals(book.author, bookFromDB?.author)
        assertEquals(book.numberOfPages, bookFromDB?.numberOfPages)
        assertEquals(book.currentProgress, bookFromDB?.currentProgress)
        assertEquals(book.bookStatus, bookFromDB?.bookStatus)
        assertEquals(book.startDate.timeInMillis, bookFromDB?.startDate?.timeInMillis)
        assertEquals(book.endDate.timeInMillis, bookFromDB?.endDate?.timeInMillis)
    }

    @Test
    fun testGetNonexistentBookByID() {
        val bookFromDB = bookDBService.getBookByID(1)
        assertNull(bookFromDB)
    }

}