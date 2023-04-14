package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.DatabaseConstants
import ozarskiapps.booktracker.database.ReadingTimeDBService
import java.util.*

class ReadingTimeTests {

    private lateinit var bookDBService: BookDBService
    private lateinit var readingTimeService: ReadingTimeDBService
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        bookDBService = BookDBService(appContext)
        readingTimeService = ReadingTimeDBService(appContext)
    }

    @After
    fun tearDown() {
        bookDBService.close()
        readingTimeService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun readingTimeDataInsertTest(){
        val book = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        bookDBService.addBook(book)

        val cursor = readingTimeService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.ReadingTimeTable.TABLE_NAME}",
            null
        )

        with(cursor){
            assertTrue(moveToFirst())
            while(moveToNext()){
                val id = getLong(getColumnIndexOrThrow(DatabaseConstants.ReadingTimeTable.BOOK_ID_COLUMN))
                val date = getLong(getColumnIndexOrThrow(DatabaseConstants.ReadingTimeTable.DATE_COLUMN))
                assertEquals(book.id, id)
                assertTrue(book.startDate.timeInMillis <= date)
                assertTrue(book.endDate.timeInMillis >= date)
            }
        }
    }

    @Test
    fun testReadingTimeTableCreation(){
        val cursor = readingTimeService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' AND name='${DatabaseConstants.ReadingTimeTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(DatabaseConstants.ReadingTimeTable.TABLE_NAME, cursor.getString(0))
        }
    }

    @Test
    fun singleBookReadingTimeAutomaticallyAddedToDatabase(){
        val book = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        bookDBService.addBook(book)

        val count = getReadingTimeTableRowCount()

        assertEquals(5, count)
    }

    @Test
    fun twoOverlappingBooksReadingTimeAutomaticallyAddedToDatabase(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val count = getReadingTimeTableRowCount()

        assertEquals(6, count)
    }

    @Test
    fun singleBookReadingTimeAutomaticallyUpdatedOnBookUpdate(){
        val book = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        book.id = bookDBService.addBook(book)
        book.startDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }

        bookDBService.updateBook(book)

        val count = getReadingTimeTableRowCount()

        assertEquals(4, count)
    }

    @Test
    fun twoOverlappingBooksReadingTimeAutomaticallyUpdated(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)

        val countAfterAdd = getReadingTimeTableRowCount()
        assertEquals(6, countAfterAdd)


        book1.startDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }

        bookDBService.updateBook(book1)

        val count = getReadingTimeTableRowCount()

        assertEquals(5, count)
    }

    @Test
    fun singleBookReadingTimeAutomaticallyDeletedOnBookDelete(){
        val book = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        book.id = bookDBService.addBook(book)
        val countAfterAdd = getReadingTimeTableRowCount()
        assertEquals(5, countAfterAdd)

        bookDBService.deleteBookByID(book.id)

        val count = getReadingTimeTableRowCount()

        assertTrue(countAfterAdd > count)
        assertEquals(0, count)
    }

    @Test
    fun twoOverlappingBooksReadingTimeAutomaticallyDeletedOnBookDelete(){
        val db = readingTimeService.readableDatabase

        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val countAfterAdd = getReadingTimeTableRowCount()
        assertEquals(6, countAfterAdd)

        bookDBService.deleteBookByID(book1.id)

        val count = getReadingTimeTableRowCount()

        assertTrue(countAfterAdd > count)
        assertEquals(3, count)
    }

    @Test
    fun booksReadInNotOverlappingOrAdjacentTimePeriods() {
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) }
        )

        println(book1.startDate.time)
        println(book2.startDate.time)

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(2, readingTime)
    }

    @Test
    fun booksReadTimeInNotOverlappingAdjacentTimePeriods() {
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(2, readingTime)
    }

    @Test
    fun bookReadInPartiallyOverlappingTimePeriods() {
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(4, readingTime)
    }

    @Test
    fun bookReadInExactlyTheSameTimePeriod(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }

    @Test
    fun bookReadingTimeCoversWholeReadingTimeOfAnotherBook(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }

    @Test
    fun threeBooksReadInPartiallyOverlappingTimePeriods(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }

    @Test
    fun threeBookReadInExactlyTheSameTimePeriod(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }

    @Test
    fun book1ReadInThePeriodCoveringOverlappingReadingTimeOfBook2AndBook3(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }

    @Test
    fun book1ReadInTimePeriodCoveringNotOverlappingReadingTimeOfBook2AndBook3(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }

    @Test
    fun book1ReadInTimePeriodCoveringReadingTimeOfBook2CoveringReadingTimeOfBook3(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val readingTime = readingTimeService.getTotalReadingTime()
        assertEquals(5, readingTime)
    }


    @Test
    fun readingTimeInCustomTimePeriodFor2Books(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)

        val startDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        val endDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) }

        val readingTime = readingTimeService.getTotalReadingTimeForTimePeriod(startDate, endDate)
        assertEquals(3, readingTime)
    }

    @Test
    fun readingTimeInCustomTimePeriodFor3Books(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 1) },
            Calendar.getInstance()
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val startDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        val endDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }

        val readingTime = readingTimeService.getTotalReadingTimeForTimePeriod(startDate, endDate)
        assertEquals(4, readingTime)
    }

    @Test
    fun testNumberOfBooksReadInTimePeriod(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        )

        bookDBService.addBook(book1)
        bookDBService.addBook(book2)
        bookDBService.addBook(book3)

        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }

        val numberOfBooksRead = readingTimeService.getNumberOfBooksReadInTimePeriod(startDate, endDate)
        assertEquals(2, numberOfBooksRead)
    }

    @Test
    fun getBookIDsForBooksReadInTimePeriod(){
        val book1 = Book(
            "Full book title 1",
            "Author name 1",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance(),
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }
        )

        val book2 = Book(
            "Full book title 2",
            "Author name 2",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) },
            Calendar.getInstance()
        )

        val book3 = Book(
            "Full book title 3",
            "Author name 3",
            42,
            0f,
            BookStatus.Finished,
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 2) },
            Calendar.getInstance()
                .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) - 1) }
        )

        book1.id = bookDBService.addBook(book1)
        book2.id = bookDBService.addBook(book2)
        book3.id = bookDBService.addBook(book3)

        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
            .apply { set(Calendar.DAY_OF_YEAR, get(Calendar.DAY_OF_YEAR) + 2) }

        val numberOfBooksRead = readingTimeService.getBookIdsReadInTimePeriod(startDate, endDate)
        assertEquals(listOf(book1.id, book2.id), numberOfBooksRead)
    }

    private fun getReadingTimeTableRowCount(): Int{
        val db = readingTimeService.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseConstants.ReadingTimeTable.TABLE_NAME}", null)
        val count = cursor.count
        cursor.close()
        return count
    }
}