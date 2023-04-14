package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
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
    private val calendar  = Calendar.getInstance().apply { set(Calendar.MONTH, 0) }

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(appContext)
        monthlyStatsService = MonthlyStatsDBService(appContext)
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
        monthlyStatsService.setMonth(calendar)
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
        monthlyStatsService.setMonth(calendar)
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
        monthlyStatsService.setMonth(calendar)
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
        monthlyStatsService.setMonth(calendar)
        val averageReadingTime = monthlyStatsService.getAverageReadingTime()
        assertEquals(0.5, averageReadingTime)
    }

    @Test
    fun getAverageReadingTimeNoBooksInTheDatabase() {
        val averageReadingTime = monthlyStatsService.getAverageReadingTime()
        assertEquals(0.0, averageReadingTime, 0.01)
    }

    @Test
    fun getAverageNumberOfPagesPerDay() {
        addBooks()
        monthlyStatsService.setMonth(calendar)
        val averageNumberOfPagesPerDay = monthlyStatsService.getAveragePagesPerDay()
        assertEquals(300.0, averageNumberOfPagesPerDay, 0.01)
    }

    @Test
    fun getAverageNumberOfPagesPerDayNoBooksInTheDatabase() {
        val averageNumberOfPagesPerDay = monthlyStatsService.getAveragePagesPerDay()
        assertEquals(0.0, averageNumberOfPagesPerDay)
    }

    @Test
    fun getAverageBooksPerWeek() {
        addBooks()
        monthlyStatsService.setMonth(calendar)
        val averageBooksPerWeek = monthlyStatsService.getAverageBooksPerWeek()
        Calendar.WEEK_OF_YEAR

        assertEquals(0.5, averageBooksPerWeek, 0.01)
    }

    @Test
    fun getAverageBooksPerWeekNoBooksInTheDatabase() {
        val averageBooksPerWeek = monthlyStatsService.getAverageBooksPerWeek()
        assertEquals(0.0, averageBooksPerWeek)
    }

    private fun addBooks() {
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0f,
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
            0f,
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
            0f,
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