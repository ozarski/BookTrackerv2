package ozarskiapps.booktracker.book

import ozarskiapps.booktracker.setCalendar
import java.util.*

class Book(
    var title: String,
    var author: String,
    var numberOfPages: Int,
    var currentProgress: Int,
    var bookStatus: BookStatus,
    var startDate: Calendar,
    var endDate: Calendar,
    var id: Long = -1
) {
    constructor(
        title: String,
        author: String,
        numberOfPages: Int,
        currentProgress: Int,
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
    )

    fun getBookReadingTimeInDays(): Int{
        return if(bookStatus == BookStatus.Finished){
            val startDate = setCalendar(this.startDate)
            val endDate = setCalendar(this.endDate, false)
            ((endDate.timeInMillis - startDate.timeInMillis) / (24 * 60 * 60 * 1000)).toInt() + 1
        } else{
            -1
        }
    }

    override fun toString(): String {
        return "Title: $title \t" +
                "Author: $author\t" +
                "Number of pages: $numberOfPages\t" +
                "Current progress: $currentProgress\t" +
                "Book status: $bookStatus\t" +
                "Start date: $startDate\t" +
                "End date: $endDate\t" +
                "ID: $id"
    }
}
