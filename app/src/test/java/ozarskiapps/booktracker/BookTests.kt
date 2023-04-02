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
            0,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )
        val readingTime = book.getBookReadingTimeInDays()

        assertEquals(1, readingTime)
    }

    @Test
    fun getBookReadingTime3Days(){
        val book = Book(
            "Full book title",
            "Author name",
            100,
            0,
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
            0,
            BookStatus.WantToRead,
            Calendar.getInstance(),
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
        )
        val readingTime = book.getBookReadingTimeInDays()

        assertEquals(-1, readingTime)
    }
}