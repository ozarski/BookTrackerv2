package ozarskiapps.booktracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import androidx.compose.ui.graphics.Color
import ozarskiapps.booktracker.tag.Tag

class TagDBService(val context: Context) : DBService(context) {

    fun addTag(tag: Tag): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagTable.TAG_NAME_COLUMN, tag.name)
            put(DatabaseConstants.TagTable.TAG_COLOR_COLUMN, tag.color.value.toString())
        }
        return db.insert(DatabaseConstants.TagTable.TABLE_NAME, null, contentValues)
    }

    fun updateTag(tag: Tag) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagTable.TAG_NAME_COLUMN, tag.name)
            put(DatabaseConstants.TagTable.TAG_COLOR_COLUMN, tag.color.value.toString())
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
        removeBookTagItems(tagID = id)
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
        db.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run{
            if(moveToFirst()){
                return getTagFromCursor(this)
            }
        }
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

    fun removeBookTagItems(tagID: Long? = null, bookID: Long? = null) {
        if(tagID == null && bookID == null) {
            return
        }

        val selection = if(tagID == null) {
            "${DatabaseConstants.BookTagTable.BOOK_ID_COLUMN} = ?"
        } else if (bookID == null) {
            "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} = ?"
        } else {
            "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} = ? AND ${DatabaseConstants.BookTagTable.BOOK_ID_COLUMN} = ?"
        }

        val selectionArgs = if(tagID == null) {
            arrayOf(bookID.toString())
        } else if (bookID == null) {
            arrayOf(tagID.toString())
        } else {
            arrayOf(tagID.toString(), bookID.toString())
        }

        this.writableDatabase.delete(DatabaseConstants.BookTagTable.TABLE_NAME, selection, selectionArgs)
    }

    fun getAllTags(): List<Tag> {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagTable.TAG_NAME_COLUMN,
            DatabaseConstants.TagTable.TAG_COLOR_COLUMN
        )

        val tags = mutableListOf<Tag>()
        db.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        ).run{
            while (moveToNext()) {
                val tag = getTagFromCursor(this)
                tags.add(tag)
            }
        }

        return tags
    }

    fun getNumberOfBooksUnderTag(tagID: Long): Int {
        val db = this.readableDatabase
        val resultColumn = "numberOfBooks"
        val projection = arrayOf("COUNT(DISTINCT ${DatabaseConstants.BookTagTable.BOOK_ID_COLUMN}) as $resultColumn")
        val selection = "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString())
        db.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run{
            if(moveToFirst()){
                return getInt(getColumnIndexOrThrow(resultColumn)).also {
                    close()
                }
            }
        }
        return 0
    }

    fun getBooksForTagIDs(tagIDs: List<Long>): List<Long> {
        val db = this.readableDatabase
        val projection = arrayOf(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN)
        val tagIDsString = tagIDs.joinToString()
        val selection = "${DatabaseConstants.BookTagTable.TAG_ID_COLUMN} IN ($tagIDsString)"

        val bookIDs = mutableListOf<Long>()
        db.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            selection,
            null,
            null,
            null,
            null
        ).run{
            while (moveToNext()) {
                bookIDs.add(getLong(getColumnIndexOrThrow(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN)))
            }
            close()
        }
        //check if all tagIDs are present for each bookID
        return bookIDs.distinct().let{
            if(it.size * tagIDs.size != bookIDs.size) {
                return emptyList()
            }
            it
        }
    }

    fun getTagsForBookID(id: Long): List<Tag>{
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val selection = "${DatabaseConstants.BookTagTable.BOOK_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(id.toString())

        val tags = mutableListOf<Tag>()
        db.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        ).run{
            while (moveToNext()) {
                val tagID = getLong(getColumnIndexOrThrow(DatabaseConstants.BookTagTable.TAG_ID_COLUMN))
                val tag = getTagByID(tagID) ?: continue
                tags.add(tag)
            }
            close()
        }
        return tags
    }

    private fun getTagFromCursor(cursor: Cursor): Tag{
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_NAME_COLUMN))
        val colorString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_COLOR_COLUMN))
        val color = Color(colorString.toULong())

        return Tag(
            name,
            color,
            id
        )
    }
}