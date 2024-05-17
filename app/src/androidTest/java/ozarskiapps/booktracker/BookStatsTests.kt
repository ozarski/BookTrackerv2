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
import ozarskiapps.booktracker.database.BookStatsService
import ozarskiapps.booktracker.database.DatabaseConstants
import java.util.*

class BookStatsTests {

    private lateinit var bookStatsService: BookStatsService

    @Before
    fun setUp() {
        bookStatsService = BookStatsService()
    }

    @After
    fun tearDown() {
        bookStatsService
    }

    @Test
    fun getPredictedReadingTimeForBook(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            10f,
            BookStatus.Reading,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) },
            Calendar.getInstance()
        )

        val predictedReadingTime = bookStatsService.getBookPredictedReadingTime(book)
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
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) },
            Calendar.getInstance()
        )

        val predictedReadingTime = bookStatsService.getBookPredictedReadingTime(book)
        val predictedFinishDate = bookStatsService.getBookPredictedFinishDate(book)
        val finishCalendar = (book.startDate.clone() as Calendar).apply{
            if(predictedReadingTime != null){
                add(Calendar.DAY_OF_YEAR, predictedReadingTime - 1)
            }
            else{
                fail()
            }
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
            BookStatus.Finished
        )

        val predictedReadingTime = bookStatsService.getBookPredictedReadingTime(book)
        assertEquals(null, predictedReadingTime)
    }

    @Test
    fun getPredictedReadingTimeForBookFailProgressIsZero(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.Reading
        )

        val predictedReadingTime = bookStatsService.getBookPredictedReadingTime(book)
        assertEquals(null, predictedReadingTime)
    }
}