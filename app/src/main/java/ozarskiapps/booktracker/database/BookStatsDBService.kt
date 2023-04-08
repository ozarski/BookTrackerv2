package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.book.Book

class BookStatsDBService(val context: Context): DBService(context) {

    fun getBookPredictedReadingTime(book: Book): Int?{
        //TODO("Not implemented yet")
        return -1
    }

    fun getBookPredictedFinishDate(book: Book): Long{
        //TODO("Not implemented yet")
        return -1
    }
}