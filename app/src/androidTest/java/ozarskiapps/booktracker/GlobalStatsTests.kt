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
import ozarskiapps.booktracker.database.GlobalStatsDBService
import java.util.*

class GlobalStatsTests {

    private lateinit var appContext: Context
    private lateinit var bookDBService: BookDBService
    private lateinit var globalStatsService: GlobalStatsDBService

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(appContext)
        globalStatsService = GlobalStatsDBService(appContext)

    }

    @After
    fun tearDown() {
        bookDBService.close()
        globalStatsService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun getTotalNumberOfPages() {
        addBooks()
        val totalNumberOfPages = globalStatsService.getTotalNumberOfPages()
        assertEquals(600, totalNumberOfPages)
    }

    @Test
    fun getTotalNumberOfPagesNegativeNumberOfPagesFail(){
        addBooksNegativeNumberOfPages()
        val totalNumberOfPages = globalStatsService.getTotalNumberOfPages()
        assertEquals(0, totalNumberOfPages)
    }

    @Test
    fun getTotalNumberOfBooks(){
        addBooks()
        val totalNumberOfBooks = globalStatsService.getTotalNumberOfBooks()
        assertEquals(3, totalNumberOfBooks)
    }

    @Test
    fun getAverageNumberOfPages(){
        addBooks()
        val averageNumberOfPages = globalStatsService.getAverageNumberOfPagesPerBook()
        assertEquals(200.0, averageNumberOfPages)
    }

    @Test
    fun getAverageNumberOfPagesNegativeNumberOfPagesFail(){
        addBooksNegativeNumberOfPages()
        val averageNumberOfPages = globalStatsService.getAverageNumberOfPagesPerBook()
        assertEquals(0.0, averageNumberOfPages)
    }

    @Test
    fun getAverageBookReadingTime(){
        addBooks()
        val bookReadingTime = globalStatsService.getAverageReadingTime()
        assertEquals(3.66, bookReadingTime, 0.01)
    }

    @Test
    fun getAverageBookReadingTimeNoFinishedBooksFail(){
        val bookReadingTime = globalStatsService.getAverageReadingTime()
        assertEquals(0.0, bookReadingTime, 0.01)
    }

    @Test
    fun getAverageBooksMonthly(){
        addBooksMonthlyTest()
        val averageBooksMonthly = globalStatsService.getAverageBooksPerMonth()
        assertEquals(1.5, averageBooksMonthly, 0.01)
    }

    @Test
    fun getAverageBooksMonthlyNoFinishedBooksFail(){
        val averageBooksMonthly = globalStatsService.getAverageBooksPerMonth()
        assertEquals(0.0, averageBooksMonthly, 0.01)
    }

    @Test
    fun getAverageBookWeekly(){
        addBooksWeeklyTest()
        val averageBooksWeekly = globalStatsService.getAverageBooksPerWeek()
        assertEquals(1.5, averageBooksWeekly, 0.01)
    }

    @Test
    fun getAverageBookWeeklyNoFinishedBooksFail(){
        val averageBooksWeekly = globalStatsService.getAverageBooksPerWeek()
        assertEquals(0.0, averageBooksWeekly, 0.01)
    }

    @Test
    fun getAveragePagesPerDay(){
        addBooks()
        val averagePagesPerDay = globalStatsService.getAveragePagesPerDay()
        assertEquals(54.54, averagePagesPerDay, 0.01)
    }

    @Test
    fun getAveragePagesPerDayNoFinishedBooksFail(){
        val averagePagesPerDay = globalStatsService.getAveragePagesPerDay()
        assertEquals(0.0, averagePagesPerDay, 0.01)
    }

    @Test
    fun getMonthWithMostBooksRead(){
        addBooksMonthlyTest()
        val monthWithMostBooksRead = globalStatsService.getMonthWithMostBooksRead()
        val monthName = Calendar.getInstance().apply { set(Calendar.MONTH, get(Calendar.MONTH) - 1) }.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG,
            Locale.ENGLISH
        )
        assertEquals("$monthName ${Calendar.getInstance().get(Calendar.YEAR)}", monthWithMostBooksRead)
    }

    @Test
    fun getMonthWithMostBooksReadNoFinishedBooksFail(){
        val monthWithMostBooksRead = globalStatsService.getMonthWithMostBooksRead()
        assertEquals("-", monthWithMostBooksRead)
    }

    @Test
    fun getMostReadAuthor(){
        addBooksMostReadAuthorTest()
        val mostReadAuthor = globalStatsService.getMostReadAuthor()
        assertEquals("Author 3", mostReadAuthor)
    }

    @Test
    fun getMostReadAuthorNoFinishedBooksFail(){
        val mostReadAuthor = globalStatsService.getMostReadAuthor()
        assertEquals("-", mostReadAuthor)
    }

    @Test
    fun numberOfMonthsBetweenDatesTest(){
        val start = Calendar.getInstance().apply { set(Calendar.MONTH, get(Calendar.MONTH) - 11) }
        val end = Calendar.getInstance()
        val result = globalStatsService.getNumberOfMonthsBetweenDates(start, end)
        assertEquals(12, result)
    }

    @Test
    fun numberOfWeeksBetweenDates(){
        val start = Calendar.getInstance().apply { set(Calendar.WEEK_OF_YEAR, get(Calendar.WEEK_OF_YEAR) - 7) }
        val end = Calendar.getInstance()
        val result = globalStatsService.getNumberOfWeeksBetweenDates(start, end)
        assertEquals(8, result)
    }

    private fun addBooksMostReadAuthorTest() {
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )

        val book2 = Book(
            "Book 2",
            "Author 3",
            200,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) }
        )

        val book3 = Book(
            "Book 3",
            "Author 3",
            -400,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)
    }

    private fun addBooksWeeklyTest() {
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )

        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) }
        )

        val book3 = Book(
            "Book 3",
            "Author 3",
            -400,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 7) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)
    }

    fun addBooks(){
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 4) },
            Calendar.getInstance()
        )

        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 3) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        val book3 = Book(
            "Book 3",
            "Author 3",
            300,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 3) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 7) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)
    }

    fun addBooksNegativeNumberOfPages(){
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 4) },
            Calendar.getInstance()
        )

        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 3) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        val book3 = Book(
            "Book 3",
            "Author 3",
            -400,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 3) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 7) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)
    }

    fun addBooksMonthlyTest(){
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )

        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, get(Calendar.MONTH) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, get(Calendar.MONTH) - 1) }
        )

        val book3 = Book(
            "Book 3",
            "Author 3",
            -400,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, get(Calendar.MONTH) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, get(Calendar.MONTH) - 1) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)
    }


}