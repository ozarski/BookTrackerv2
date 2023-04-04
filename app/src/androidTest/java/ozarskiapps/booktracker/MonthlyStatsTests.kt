package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.DatabaseConstants
import ozarskiapps.booktracker.database.MonthlyStatsDBService
import java.util.*

class MonthlyStatsTests {

    private lateinit var appContext: Context
    private lateinit var bookDBService: BookDBService
    private lateinit var monthlyStatsService: MonthlyStatsDBService

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(appContext)
        monthlyStatsService = MonthlyStatsDBService(
            appContext,
            Calendar.getInstance().apply { set(Calendar.MONTH, 0) })
    }

    @After
    fun tearDown() {
        bookDBService.close()
        monthlyStatsService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun getTotalNumberOfPages() {
        addBooks()
        val totalNumberOfPages = monthlyStatsService.getTotalNumberOfPages()
        TestCase.assertEquals(300, totalNumberOfPages)
    }

    @Test
    fun getTotalNumberOfPagesNoBooksInTheDatabase() {
        val totalNumberOfPages = monthlyStatsService.getTotalNumberOfPages()
        TestCase.assertEquals(0, totalNumberOfPages)
    }

    @Test
    fun getTotalBooks() {
        addBooks()
        val totalNumberOfBooks = monthlyStatsService.getTotalNumberOfBooks()
        TestCase.assertEquals(2, totalNumberOfBooks)
    }

    @Test
    fun getTotalBooksNoBooksInTheDatabase() {
        val totalNumberOfBooks = monthlyStatsService.getTotalNumberOfBooks()
        TestCase.assertEquals(0, totalNumberOfBooks)
    }

    @Test
    fun getAverageNumberOfPagesPerBook() {
        addBooks()
        val averageNumberOfPagesPerBook = monthlyStatsService.getAverageNumberOfPagesPerBook()
        TestCase.assertEquals(150.0, averageNumberOfPagesPerBook, 0.01)
    }

    @Test
    fun getAverageNumberOfPagesPerBookNoBooksInTheDatabase() {
        val averageNumberOfPagesPerBook = monthlyStatsService.getAverageNumberOfPagesPerBook()
        TestCase.assertEquals(0.0, averageNumberOfPagesPerBook)
    }

    @Test
    fun getAverageReadingTime() {
        addBooks()
        val averageReadingTime = monthlyStatsService.getAverageReadingTime()
        TestCase.assertEquals(1, averageReadingTime)
    }

    @Test
    fun getAverageReadingTimeNoBooksInTheDatabase() {
        val averageReadingTime = monthlyStatsService.getAverageReadingTime()
        TestCase.assertEquals(0, averageReadingTime)
    }

    @Test
    fun getAverageNumberOfPagesPerDay() {
        addBooks()
        val averageNumberOfPagesPerDay = monthlyStatsService.getAveragePagesPerDay()
        TestCase.assertEquals(150.0, averageNumberOfPagesPerDay, 0.01)
    }

    @Test
    fun getAverageNumberOfPagesPerDayNoBooksInTheDatabase() {
        val averageNumberOfPagesPerDay = monthlyStatsService.getAveragePagesPerDay()
        TestCase.assertEquals(0.0, averageNumberOfPagesPerDay)
    }

    @Test
    fun getAverageBooksPerWeek() {
        addBooks()
        val averageBooksPerWeek = monthlyStatsService.getAverageBooksPerWeek()
        Calendar.WEEK_OF_YEAR

        TestCase.assertEquals(60.0, averageBooksPerWeek, 0.01)
    }

    @Test
    fun getAverageBooksPerWeekNoBooksInTheDatabase() {
        val averageBooksPerWeek = monthlyStatsService.getAverageBooksPerWeek()
        TestCase.assertEquals(0.0, averageBooksPerWeek)
    }

    private fun addBooks() {
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 0) },
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 0) }
        )

        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 0) },
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 0) }
        )

        val book4 = Book(
            "Book 4",
            "Author 4",
            400,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 1) },
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 1) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book4.id = bookDBService.addBook(book4)
    }
}