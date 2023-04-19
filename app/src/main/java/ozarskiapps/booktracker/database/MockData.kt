package ozarskiapps.booktracker.database

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.tag.Tag
import java.util.*

class MockData(val context: Context): DBService(context) {

    fun generateMockData(){
        addMockBooks()
        addMockTags()
    }

    private fun addMockBooks(){
        val bookDBService = BookDBService(context)


        for (i in 1..10){
            val book = Book(
                "Book $i",
                "Author $i",
                100,
                i*5f,
                BookStatus.values()[i%3],
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i-10) },
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, i-3) },
            )
            bookDBService.addBook(book)
        }

    }

    private fun addMockTags(){
        val tagColors = listOf(
            Color(0xFFE57373),
            Color(0xFFF06292),
            Color(0xFFBA68C8),
            Color(0xFF9575CD),
            Color(0xFF7986CB),
            Color(0xFF64B5F6),
            Color(0xFF4FC3F7),
            Color(0xFF4DD0E1),
            Color(0xFF4DB6AC),
            Color(0xFF81C784),
            Color(0xFFAED581),
            Color(0xFFDCE775),
            Color(0xFFFFD54F),
            Color(0xFFFFB74D),
            Color(0xFFFF8A65),
            Color(0xFFA1887F),
            Color(0xFFE0E0E0),
            Color(0xFF90A4AE),
        )
        val tagDBService = TagDBService(context)
        for (i in 1..5){
            val argbcolor = tagColors.random().toArgb()
            val hexString = "#${Integer.toHexString(argbcolor).substring(2)}"
            val tag = Tag("Tag $i", hexString)
            tagDBService.addTag(tag)
        }

        val tags =  tagDBService.getAllTags()
        val books = BookDBService(context).getAllBooks()
        for (book in books){
            val randomTags = tags.shuffled().take(2)
            for (tag in randomTags){
                tagDBService.assignTagToBook(tag.id, book.id)
            }
        }
    }
}