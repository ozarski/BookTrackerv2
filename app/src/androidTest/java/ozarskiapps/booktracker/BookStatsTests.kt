package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
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
            10f,
            BookStatus.Reading,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val fakeCurrentDay = book.startDate.clone() as Calendar
        fakeCurrentDay.add(Calendar.DAY_OF_YEAR, 1)
        val predictedReadingTime = BookStatsDBService.getBookPredictedReadingTime(book, fakeCurrentDay)
        assertEquals(20, predictedReadingTime)
    }

    @Test
    fun getPredictedFinishDateForBook(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            10f,
            BookStatus.Reading,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val fakeCurrentDay = book.startDate.clone() as Calendar
        fakeCurrentDay.add(Calendar.DAY_OF_YEAR, 1)
        val predictedReadingTime = BookStatsDBService.getBookPredictedReadingTime(book, fakeCurrentDay)
        val predictedFinishDate = BookStatsDBService.getBookPredictedFinishDate(book, fakeCurrentDay)
        val finishCalendar = book.startDate.clone() as Calendar
        if(predictedReadingTime != null){
            finishCalendar.add(Calendar.DAY_OF_YEAR, predictedReadingTime - 1)
        }
        else{
            fail()
        }
        assertEquals(finishCalendar.timeInMillis, predictedFinishDate)
    }

    @Test
    fun getPredictedReadingTimeForBookFailWrongBookStatus(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            10f,
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
            0f,
            BookStatus.Reading,
            Calendar.getInstance().apply { set(2023, 0, 1) },
            Calendar.getInstance()
        )

        val predictedReadingTime = BookStatsDBService.getBookPredictedReadingTime(book)
        assertEquals(null, predictedReadingTime)
    }
}