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
class DatabaseTests {

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
    fun testDatabaseCreation() {
        assertNotNull(bookDBService.writableDatabase)
    }


    @Test
    fun testBookTableCreation() {
        val cursor = bookDBService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' AND name='${DatabaseConstants.BookTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(DatabaseConstants.BookTable.TABLE_NAME, cursor.getString(0))
        }

    }

    @Test
    fun testAddingBookToDB() {
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0,
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
            assertEquals(book.title, cursor.getString(1))
            assertEquals(book.author, cursor.getString(2))
            assertEquals(book.numberOfPages, cursor.getInt(3))
            assertEquals(book.currentProgress, cursor.getInt(4))
            assertEquals(book.bookStatus.toString(), cursor.getString(5))
            assertEquals(book.startDate.timeInMillis, cursor.getLong(6))
            assertEquals(book.endDate.timeInMillis, cursor.getLong(7))
        }
    }

    @Test
    fun testModifyingBook(){
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        book.author = "New author"
        book.bookStatus = BookStatus.Reading
        book.currentProgress = 10
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
            assertEquals(book.currentProgress, cursor.getInt(4))
            assertEquals(book.bookStatus.toString(), cursor.getString(5))
            assertEquals(book.startDate.timeInMillis, cursor.getLong(6))
            assertEquals(book.endDate.timeInMillis, cursor.getLong(7))
        }
    }

    @Test
    fun testDeletingBook(){
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0,
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
    fun testGettingBookByID(){
        val book = Book(
            "Full book title",
            "Author name",
            42,
            0,
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

}