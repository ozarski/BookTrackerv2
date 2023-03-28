package ozarskiapps.booktracker.book

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
