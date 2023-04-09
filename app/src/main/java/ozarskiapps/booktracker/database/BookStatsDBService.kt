package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class BookStatsDBService(val context: Context): DBService(context) {

    //currentDay is used for testing purposes DO NOT USE PASS THE currentDay PARAMETER OUTSIDE OF TESTS
    fun getBookPredictedReadingTime(book: Book, currentDay: Calendar = Calendar.getInstance()): Int?{
        if(book.bookStatus != BookStatus.Reading || book.currentProgress == 0){
            return null
        }
        val daysSinceStart = book.getDaysSinceStart(currentDay)
        val pagesPerDay = book.currentProgress.toDouble() / daysSinceStart
        return ceil(book.numberOfPages / pagesPerDay).toInt()
    }

    //currentDay is used for testing purposes DO NOT USE PASS THE currentDay PARAMETER OUTSIDE OF TESTS
    fun getBookPredictedFinishDate(book: Book, currentDay: Calendar = Calendar.getInstance()): Long{
        val predictedReadingTime = getBookPredictedReadingTime(book, currentDay)
        return if(predictedReadingTime != null){
            val predictedFinishDate = book.startDate.clone() as Calendar
            predictedFinishDate.add(Calendar.DAY_OF_YEAR, predictedReadingTime - 1)
            predictedFinishDate.timeInMillis
        } else{
            0
        }
    }
}