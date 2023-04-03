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
import ozarskiapps.booktracker.database.YearlyStatsDBService
import java.util.*

class YearlyStatsTests {

    private lateinit var appContext: Context
    private lateinit var bookDBService: BookDBService
    private lateinit var yearlyStatsService: YearlyStatsDBService

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(appContext)
        yearlyStatsService = YearlyStatsDBService(appContext, Calendar.getInstance())

    }

    @After
    fun tearDown() {
        bookDBService.close()
        yearlyStatsService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun getTotalNumberOfPages() {
        addBooks()
        val totalNumberOfPages = yearlyStatsService.getTotalNumberOfPages()
        assertEquals(700, totalNumberOfPages)
    }

    @Test
    fun getTotalNumberOfPagesNoBooksInTheDatabase(){
        val totalNumberOfPages = yearlyStatsService.getTotalNumberOfPages()
        assertEquals(0, totalNumberOfPages)
    }

    @Test
    fun getTotalBooks() {
        addBooks()
        val totalNumberOfBooks = yearlyStatsService.getTotalNumberOfBooks()
        assertEquals(3, totalNumberOfBooks)
    }

    @Test
    fun getTotalBooksNoBooksInTheDatabase(){
        val totalNumberOfBooks = yearlyStatsService.getTotalNumberOfBooks()
        assertEquals(0, totalNumberOfBooks)
    }

    @Test
    fun getAverageNumberOfPagesPerBook() {
        addBooks()
        val averageNumberOfPagesPerBook = yearlyStatsService.getAverageNumberOfPagesPerBook()
        assertEquals(233.33, averageNumberOfPagesPerBook)
    }

    @Test
    fun getAverageNumberOfPagesPerBookNoBooksInTheDatabase(){
        val averageNumberOfPagesPerBook = yearlyStatsService.getAverageNumberOfPagesPerBook()
        assertEquals(0.0, averageNumberOfPagesPerBook)
    }

    @Test
    fun getAverageReadingTime() {
        addBooks()
        val averageReadingTime = yearlyStatsService.getAverageReadingTime()
        assertEquals(1, averageReadingTime)
    }

    @Test
    fun getAverageReadingTimeNoBooksInTheDatabase(){
        val averageReadingTime = yearlyStatsService.getAverageReadingTime()
        assertEquals(0, averageReadingTime)
    }

    @Test
    fun getAverageNumberOfPagesPerDay() {
        addBooks()
        val averageNumberOfPagesPerDay = yearlyStatsService.getAveragePagesPerDay()
        assertEquals(233.33, averageNumberOfPagesPerDay, 0.01)
    }

    @Test
    fun getAverageNumberOfPagesPerDayNoBooksInTheDatabase(){
        val averageNumberOfPagesPerDay = yearlyStatsService.getAveragePagesPerDay()
        assertEquals(0.0, averageNumberOfPagesPerDay)
    }

    @Test
    fun getAverageBooksPerMonth()
    {
        addBooks()
        val averageBooksPerMonth = yearlyStatsService.getAverageBooksPerMonth()
        assertEquals(0.25, averageBooksPerMonth, 0.01)
    }

    @Test
    fun getAverageBooksPerMonthNoBooksInTheDatabase(){
        val averageBooksPerMonth = yearlyStatsService.getAverageBooksPerMonth()
        assertEquals(0.0, averageBooksPerMonth)
    }

    @Test
    fun getAverageBooksPerWeek(){
        addBooks()
        val averageBooksPerWeek = yearlyStatsService.getAverageBooksPerWeek()
        Calendar.WEEK_OF_YEAR

        assertEquals(17.66, averageBooksPerWeek, 0.01)
    }

    @Test
    fun getAverageBooksPerWeekNoBooksInTheDatabase(){
        val averageBooksPerWeek = yearlyStatsService.getAverageBooksPerWeek()
        assertEquals(0.0, averageBooksPerWeek)
    }

    @Test
    fun getMonthWithMostBooksRead(){
        addBooks()
        val monthWithMostBooksRead = yearlyStatsService.getMonthWithMostBooksRead()
        assertEquals("February", monthWithMostBooksRead)
    }

    @Test
    fun getMonthWithMostBooksReadNoBooksInTheDatabase(){
        val monthWithMostBooksRead = yearlyStatsService.getMonthWithMostBooksRead()
        assertEquals("-", monthWithMostBooksRead)
    }


    private fun addBooks() {
        val book1 = Book(
            "Book 1",
            "Author 1",
            100,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 1)},
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 1)}
        )

        val book2 = Book(
            "Book 2",
            "Author 2",
            200,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 1)},
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 1)}
        )

        val book3 = Book(
            "Book 3",
            "Author 3",
            300,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.YEAR, get(Calendar.YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.YEAR, get(Calendar.YEAR) - 1) }
        )

        val book4 = Book(
            "Book 4",
            "Author 4",
            400,
            0,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 0)},
            Calendar.getInstance()
                .apply { set(Calendar.MONTH, 0)}
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)
        book4.id = bookDBService.addBook(book4)
    }
}