package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookStatsDBService
import ozarskiapps.booktracker.database.DatabaseConstants
import java.util.*

class BookStatsTests {

    private lateinit var BookStatsDBService: BookStatsDBService
    private lateinit var applicationContext: Context

    @Before
    fun setUp() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        BookStatsDBService = BookStatsDBService(applicationContext)
    }

    @After
    fun tearDown() {
        BookStatsDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun getPredictedReadingTimeForBook(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            10,
            BookStatus.Reading,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val predictedReadingTime = BookStatsDBService.getBookPredictedReadingTime(book)
        assertEquals(10, predictedReadingTime)
    }

    @Test
    fun getPredictedFinishDateForBook(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            10,
            BookStatus.Reading,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val predictedFinishDate = BookStatsDBService.getBookPredictedFinishDate(book)
        val finishCalendar = book.startDate.clone() as Calendar
        finishCalendar.add(Calendar.DAY_OF_YEAR, 9)
        assertEquals(finishCalendar.timeInMillis, predictedFinishDate)
    }

    @Test
    fun getPredictedReadingTimeForBookFailWrongBookStatus(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            10,
            BookStatus.Finished,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val predictedReadingTime = BookStatsDBService.getBookPredictedReadingTime(book)
        assertEquals(null, predictedReadingTime)
    }

    @Test
    fun getPredictedReadingTimeForBookFailProgressIsZero(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0,
            BookStatus.Reading,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val predictedReadingTime = BookStatsDBService.getBookPredictedReadingTime(book)
        assertEquals(null, predictedReadingTime)
    }
}