package ozarskiapps.booktracker.database

import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import java.util.*
import kotlin.math.ceil

class BookStatsService{

    //currentDay is used for testing purposes DO NOT PASS THE currentDay PARAMETER OUTSIDE OF TESTS
    fun getBookPredictedReadingTime(book: Book): Int?{
        return if(book.bookStatus != BookStatus.Reading || book.currentProgress == 0f){
            null
        }
        else{
            ceil(book.numberOfPages / book.getAveragePagesPerDay()).toInt()
        }
    }

    //currentDay is used for testing purposes DO NOT PASS THE currentDay PARAMETER OUTSIDE OF TESTS
    fun getBookPredictedFinishDate(book: Book): Long{
        val predictedReadingTime = getBookPredictedReadingTime(book)
        return if(predictedReadingTime != null){
            (book.startDate.clone() as Calendar).apply{
                add(Calendar.DAY_OF_YEAR, predictedReadingTime - 1)
            }.timeInMillis
        } else{
            0
        }
    }
}