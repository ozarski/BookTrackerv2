package ozarskiapps.booktracker

import org.junit.Test

import org.junit.Assert.*
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BookTests {
    @Test
    fun getBookReadingTimeSameDayFinish() {
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        val readingTime = book.getBookReadingTimeInDays()

        assertEquals(1, readingTime)
    }

    @Test
    fun getBookReadingTimeDays(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
        )
        val readingTime = book.getBookReadingTimeInDays()

        assertEquals(3, readingTime)
    }

    @Test
    fun getBookReadingTimeFailUnfinished(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
        )
        val readingTime = book.getBookReadingTimeInDays()

        assertEquals(-1, readingTime)
    }

    @Test
    fun getBookDaysSinceStart(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.Reading,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) },
            Calendar.getInstance()
        )
        val daysSinceStart = book.getDaysSinceStart()

        assertEquals(3, daysSinceStart)
    }

    @Test
    fun getBookAveragePagesPerDay(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.Finished,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) },
            Calendar.getInstance()
        )
        val averagePagesPerDay = book.getAveragePagesPerDay()

        assertEquals(25.0, averagePagesPerDay, 0.01)
    }

    @Test
    fun getBookAveragePagesPerDayFailUnfinished(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0f,
            BookStatus.Reading,
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) },
            Calendar.getInstance()
        )
        val averagePagesPerDay = book.getAveragePagesPerDay()

        assertEquals(-1.0, averagePagesPerDay, 0.01)
    }
}