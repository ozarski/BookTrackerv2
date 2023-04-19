package ozarskiapps.booktracker.book

import ozarskiapps.booktracker.*
import ozarskiapps.booktracker.OpenLibraryAPI.OpenLibraryAPIBook
import java.util.*

class Book(
    var title: String,
    var author: String,
    var numberOfPages: Int,
    var currentProgress: Float,
    var bookStatus: BookStatus,
    var startDate: Calendar,
    var endDate: Calendar,
    var id: Long = -1
) {
    constructor(
        title: String,
        author: String,
        numberOfPages: Int,
        currentProgress: Float,
        bookStatus: BookStatus,
        startDate: Calendar,
        endDate: Calendar,
    ) : this(
        title,
        author,
        numberOfPages,
        currentProgress,
        bookStatus,
        setCalendar(startDate),
        setCalendar(endDate, false),
        -1
    ) {
        if (bookStatus == BookStatus.WantToRead) {
            this.startDate.timeInMillis = 0
            this.endDate.timeInMillis = 0
        } else if (bookStatus == BookStatus.Reading) {
            this.endDate.timeInMillis = 0
        } else if (bookStatus == BookStatus.Finished) {
            if (this.startDate.timeInMillis > this.endDate.timeInMillis) {
                this.endDate.timeInMillis = this.startDate.timeInMillis
            }
        }
    }

    constructor(bookData: OpenLibraryAPIBook) : this(
        bookData.title,
        bookData.authorsToString(),
        bookData.numberOfPagesMedian,
        0f,
        BookStatus.WantToRead,
        Calendar.getInstance().apply { timeInMillis = 0L },
        Calendar.getInstance().apply { timeInMillis = 0L }
    )

    fun getBookReadingTimeInDays(): Int {
        return if (bookStatus == BookStatus.Finished) {
            val startDate = setCalendar(this.startDate)
            val endDate = setCalendar(this.endDate, false)
            daysBetweenDates(startDate, endDate)
        } else {
            -1
        }
    }

    //now parameter is for testing purposes, DO NOT PASS now PARAMETER OUTSIDE OF TESTS
    fun getDaysSinceStart(now: Calendar = Calendar.getInstance()): Int {
        return if (bookStatus == BookStatus.Reading) {
            val startDate = setCalendar(this.startDate)
            val dateNow = setCalendar(now, false)
            daysBetweenDates(startDate, dateNow)
        } else {
            -1
        }
    }

    fun getAveragePagesPerDay(): Double {
        return if (bookStatus == BookStatus.Finished) {
            val readingTime = getBookReadingTimeInDays()
            if (readingTime != -1) {
                numberOfPages.toDouble() / readingTime
            } else {
                -1.0
            }
        } else {
            -1.0
        }
    }

    override fun toString(): String {
        return "Title: $title \t" +
                "Author: $author\t" +
                "Number of pages: $numberOfPages\t" +
                "Current progress: $currentProgress\t" +
                "Book status: $bookStatus\t" +
                "Start date: ${startDate.time}\t" +
                "End date: ${endDate.time}\t" +
                "ID: $id"
    }
}
