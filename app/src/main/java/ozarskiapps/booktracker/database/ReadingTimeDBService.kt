package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.readingTime.ReadingDay
import java.util.*

class ReadingTimeDBService(context: Context): DBService(context) {

    fun getDaysByBookID(id: Long): List<ReadingDay>{
        return mutableListOf()
    }

    fun getBookIDsForTimePeriod(start: Calendar, end: Calendar): List<Long>{
        return mutableListOf()
    }

    fun addBookReadingTime(book: Book){

    }

    fun updateBookReadingTime(book: Book){

    }

    fun deleteBookReadingTime(book: Book){

    }

    fun getTotalReadingTime(): Int{
        return -1
    }

    fun getTotalReadingTimeForTimePeriod(start: Calendar, end: Calendar): Int{
        return -1
    }
}