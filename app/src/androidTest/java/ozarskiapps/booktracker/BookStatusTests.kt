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

class BookStatusTests {

    private lateinit var appContext: Context
    private lateinit var bookDBService: BookDBService

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
    fun getAllWantToReadBooks() {
        addBooks()
        val books = bookDBService.getAllWantToReadBooks()
        assertEquals(books.size, 2)
    }

    @Test
    fun testAddingWantToReadBook(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        val addedBook = bookDBService.getBookByID(book.id)
        assert(addedBook != null)
        assertEquals(addedBook?.bookStatus, BookStatus.WantToRead)
        assertEquals(addedBook?.startDate?.timeInMillis, 0L)
        assertEquals(addedBook?.endDate?.timeInMillis, 0L)
    }

    @Test
    fun testAddingReadingBook(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Reading,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        val addedBook = bookDBService.getBookByID(book.id)
        assert(addedBook != null)
        assertEquals(addedBook?.bookStatus, BookStatus.Reading)
        assertEquals(addedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assertEquals(addedBook?.endDate?.timeInMillis, 0L)
    }

    @Test
    fun testAddingFinishedBook(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        val addedBook = bookDBService.getBookByID(book.id)
        assert(addedBook != null)
        assertEquals(addedBook?.bookStatus, BookStatus.Finished)
        assertEquals(addedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assertEquals(addedBook?.endDate?.timeInMillis, book.endDate.timeInMillis)
    }

    @Test
    fun startReadingBookToday(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        bookDBService.startReadingBookToday(book)
        val modifiedBook = bookDBService.getBookByID(book.id)
        assert(modifiedBook != null)
        assertEquals(modifiedBook?.bookStatus, BookStatus.Reading)
        assertEquals(modifiedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assertEquals(modifiedBook?.endDate?.timeInMillis, 0L)
    }

    @Test
    fun finishReadingBookToday(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Reading,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) },
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        bookDBService.finishReadingBookToday(book)
        val modifiedBook = bookDBService.getBookByID(book.id)
        assert(modifiedBook != null)
        assertEquals(modifiedBook?.bookStatus, BookStatus.Finished)
        assertEquals(modifiedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assert(modifiedBook?.endDate?.timeInMillis != 0L)
    }

    @Test
    fun finishReadingBookTodayWantToReadBook(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        bookDBService.finishReadingBookToday(book)
        val modifiedBook = bookDBService.getBookByID(book.id)
        assert(modifiedBook != null)
        assertEquals(modifiedBook?.bookStatus, BookStatus.Finished)
        assert(modifiedBook?.startDate?.timeInMillis != 0L)
        assert(modifiedBook?.endDate?.timeInMillis != 0L)
    }

    @Test
    fun startReadingBookTodayFailWrongBookStatusReading(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Reading,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) },
            Calendar.getInstance()
        )
        book.id = bookDBService.addBook(book)
        bookDBService.startReadingBookToday(book)
        val modifiedBook = bookDBService.getBookByID(book.id)
        assert(modifiedBook != null)
        assertEquals(modifiedBook?.bookStatus, BookStatus.Reading)
        assertEquals(modifiedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assertEquals(modifiedBook?.endDate?.timeInMillis, 0L)
    }

    @Test
    fun startReadingBookTodayFailWrongBookStatusFinished(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) },
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }
        )
        book.id = bookDBService.addBook(book)
        bookDBService.startReadingBookToday(book)
        val modifiedBook = bookDBService.getBookByID(book.id)
        assert(modifiedBook != null)
        assertEquals(modifiedBook?.bookStatus, BookStatus.Finished)
        assertEquals(modifiedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assertEquals(modifiedBook?.endDate?.timeInMillis, book.endDate.timeInMillis)
    }

    @Test
    fun finishReadingBookTodayFailWrongBookStatusFinished(){
        val book = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) },
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }
        )
        book.id = bookDBService.addBook(book)
        bookDBService.finishReadingBookToday(book)
        val modifiedBook = bookDBService.getBookByID(book.id)
        assert(modifiedBook != null)
        assertEquals(modifiedBook?.bookStatus, BookStatus.Finished)
        assertEquals(modifiedBook?.startDate?.timeInMillis, book.startDate.timeInMillis)
        assertEquals(modifiedBook?.endDate?.timeInMillis,  book.endDate.timeInMillis)
    }


    private fun addBooks() {
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0f,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        val book3 = Book(
            "Book 3",
            "Author 3",
            300,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        val book4 = Book(
            "Book 4",
            "Author 4",
            400,
            0f,
            BookStatus.Reading,
            Calendar.getInstance(),
            Calendar.getInstance()
        )


        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)
        bookDBService.addBook(book4)
    }
}