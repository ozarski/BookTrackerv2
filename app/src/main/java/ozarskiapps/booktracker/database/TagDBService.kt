package ozarskiapps.booktracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import ozarskiapps.booktracker.tag.Tag

class TagDBService(val context: Context) : DBService(context) {

    fun addTag(tag: Tag): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagTable.TAG_NAME_COLUMN, tag.name)
            put(DatabaseConstants.TagTable.TAG_COLOR_COLUMN, tag.color)
        }
        return db.insert(DatabaseConstants.TagTable.TABLE_NAME, null, contentValues)
    }

    fun updateTag(tag: Tag) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagTable.TAG_NAME_COLUMN, tag.name)
            put(DatabaseConstants.TagTable.TAG_COLOR_COLUMN, tag.color)
        }
        db.update(
            DatabaseConstants.TagTable.TABLE_NAME,
            contentValues,
            "${BaseColumns._ID} = ?",
            arrayOf(tag.id.toString())
        )
    }

    fun deleteTagByID(id: Long) {
        val db = this.writableDatabase
        db.delete(
            DatabaseConstants.TagTable.TABLE_NAME,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
        removeTagFromBook(tagID = id)
    }

    fun getTagByID(id: Long): Tag? {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagTable.TAG_NAME_COLUMN,
            DatabaseConstants.TagTable.TAG_COLOR_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val tag = Tag(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_NAME_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_COLOR_COLUMN)),
                cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            )
            cursor.close()
            return tag
        }
        cursor.close()
        return null
    }

    fun assignTagToBook(tagID: Long, bookID: Long) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN, bookID)
            put(DatabaseConstants.BookTagTable.TAG_ID_COLUMN, tagID)
        }
        db.insert(DatabaseConstants.BookTagTable.TABLE_NAME, null, contentValues)
    }

    fun removeTagFromBook(tagID: Long? = null, bookID: Long? = null) {
        val db = this.writableDatabase
        if(tagID == null && bookID == null) {
            return
        }
        if(tagID == null) {
            val selection = "${DatabaseConstants.BookTagTable.BOOK_ID_COLUMN} = ?"
            val selectionArgs = arrayOf(bookID.toString())
            db.delete(DatabaseConstants.BookTagTable.TABLE_NAME, selection, selectionArgs)
            return
        }
        if(bookID == null){
            val selection =
                "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} = ?"
            val selectionArgs = arrayOf(tagID.toString())
            db.delete(DatabaseConstants.BookTagTable.TABLE_NAME, selection, selectionArgs)
            return
        }
        val selection =
            "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} = ? AND ${DatabaseConstants.BookTagTable.BOOK_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString(), bookID.toString())
        db.delete(DatabaseConstants.BookTagTable.TABLE_NAME, selection, selectionArgs)
    }

    fun getAllTags(): List<Tag> {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagTable.TAG_NAME_COLUMN,
            DatabaseConstants.TagTable.TAG_COLOR_COLUMN
        )
        val cursor = db.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val tags = mutableListOf<Tag>()
        while (cursor.moveToNext()) {
            val tag = Tag(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_NAME_COLUMN)),
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_COLOR_COLUMN)),
                cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            )
            tags.add(tag)
        }
        cursor.close()
        return tags
    }

    fun getNumberOfBooksUnderTag(tagID: Long): Int {
        val db = this.readableDatabase
        val projection = arrayOf(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN)
        val selection = "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString())
        val cursor = db.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val numberOfBooks = cursor.count
        cursor.close()
        return numberOfBooks
    }

    fun getBooksForTagIDs(tagIDs: List<Long>): List<Long> {
        val db = this.readableDatabase
        val projection = arrayOf(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN)
        val tagIDsString = tagIDs.joinToString()
        val selection = "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} IN ($tagIDsString)"
        val cursor = db.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            selection,
            null,
            null,
            null,
            null
        )
        val bookIDs = mutableListOf<Long>()
        while (cursor.moveToNext()) {
            bookIDs.add(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN)))
        }
        cursor.close()
        //check if all tagIDs are present for each bookID
        val distinctBookIDs = bookIDs.distinct()
        if(distinctBookIDs.size * tagIDs.size == bookIDs.size) {
            return distinctBookIDs
        }
        return emptyList()
    }
}