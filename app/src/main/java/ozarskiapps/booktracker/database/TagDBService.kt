package ozarskiapps.booktracker.database

import android.content.Context
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.tag.Tag

class TagDBService(val context: Context): DBService(context){

    fun addTag(tag: Tag): Long{
        //TODO("Not implemented yet")
        return -1
    }

    fun updateTag(tag: Tag){
        //TODO("Not implemented yet")
    }

    fun deleteTagByID(id: Long){
        //TODO("Not implemented yet")
    }

    fun getTagByID(id: Long): Tag?{
        //TODO("Not implemented yet")
        return null
    }

    fun assignTagToBook(tagID: Long, bookID: Long){
        //TODO("Not implemented yet")
    }

    fun removeTagFromBook(tagID: Long, bookID: Long){
        //TODO("Not implemented yet")
    }

    fun getAllTags(): List<Tag>{
        //TODO("Not implemented yet")
        return emptyList()
    }

    fun getNumberOfBooksUnderTag(tagID: Long): Int{
        //TODO("Not implemented yet")
        return -1
    }

    fun getBooksForTagIDs(tagIDs: List<Long>): List<Long>{
        //TODO("Not implemented yet")
        return emptyList()
    }
}