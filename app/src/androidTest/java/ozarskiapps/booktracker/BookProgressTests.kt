package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.DatabaseConstants
import java.util.*

class BookProgressTests {

    private lateinit var bookDBService: BookDBService
    private lateinit var applicationContext: Context

    @Before
    fun setup() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(applicationContext)
    }

    @After
    fun tearDown() {
        bookDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun updateBookProgress() {
        val book = Book(
            "The Lord of the Rings",
            "J.R.R. Tolkien",
            1000,
            0,
            BookStatus.Reading,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)

        bookDBService.updateBookProgress(book, 100)
        val updatedBook = bookDBService.getBookByID(book.id)
        assertEquals(100, updatedBook?.currentProgress)
    }

    @Test
    fun updateBookProgressFailWrongBookID(){
        val book = Book(
            "The Lord of the Rings",
            "J.R.R. Tolkien",
            1000,
            0,
            BookStatus.Reading,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        val bookID = bookDBService.addBook(book)
        book.id = bookID + 1

        bookDBService.updateBookProgress(book, 100)
        val updatedBook = bookDBService.getBookByID(bookID)
        assertEquals(0, updatedBook?.currentProgress)
    }

    @Test
    fun updateBookProgressFailWrongBookStatus(){
        val book = Book(
            "The Lord of the Rings",
            "J.R.R. Tolkien",
            1000,
            0,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)

        bookDBService.updateBookProgress(book, 100)
        val updatedBook = bookDBService.getBookByID(book.id)
        assertEquals(0, updatedBook?.currentProgress)
    }

}