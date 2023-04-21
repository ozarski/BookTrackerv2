package ozarskiapps.booktracker

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import ozarskiapps.booktracker.database.TagDBService
import android.content.Context
import android.provider.BaseColumns
import androidx.compose.ui.graphics.Color
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Test
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.DatabaseConstants
import ozarskiapps.booktracker.tag.Tag

class TagTests {

    private lateinit var applicationContext: Context
    private lateinit var tagDBService: TagDBService

    @Before
    fun setUp() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        tagDBService = TagDBService(applicationContext)
    }

    @After
    fun tearDown() {
        tagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun addTagToDB() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
    }

    @Test
    fun updateTag() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        tag.name = "Updated tag"
        tagDBService.updateTag(tag)
        val projection = arrayOf(BaseColumns._ID, DatabaseConstants.TagTable.TAG_NAME_COLUMN)
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(tag.id.toString())
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        assert(cursor.moveToFirst())
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val name =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_NAME_COLUMN))
        assertEquals(tag.id, id)
        assertEquals(tag.name, name)
    }

    @Test
    fun removeTag() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        tagDBService.deleteTagByID(tag.id)
        val projection = arrayOf(BaseColumns._ID, DatabaseConstants.TagTable.TAG_NAME_COLUMN)
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        assert(!cursor.moveToFirst())
    }

    @Test
    fun getTagByID() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tagFromDB = tagDBService.getTagByID(tag.id)
        assertEquals(tag, tagFromDB)
    }

    @Test
    fun getTagByIDFail() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tagFromDB = tagDBService.getTagByID(tag.id + 1)
        assertEquals(null, tagFromDB)
    }

    @Test
    fun getAllTags() {
        val tag1 = Tag("Test tag 1", Color.Gray)
        tag1.id = tagDBService.addTag(tag1)
        testTagAddSuccess(tag1)
        val tag2 = Tag("Test tag 2", Color.Gray)
        tag2.id = tagDBService.addTag(tag2)
        testTagAddSuccess(tag2)
        val tag3 = Tag("Test tag 3", Color.Gray)
        tag3.id = tagDBService.addTag(tag3)
        testTagAddSuccess(tag3)

        val tags = tagDBService.getAllTags()
        assertEquals(3, tags.size)
        assertEquals(tag1, tags[0])
        assertEquals(tag2, tags[1])
        assertEquals(tag3, tags[2])
    }

    @Test
    fun getAllTagsFailNoTagInDB() {
        val tags = tagDBService.getAllTags()
        assertEquals(0, tags.size)
        assertEquals(emptyList<Tag>(), tags)
    }

    @Test
    fun addTagToBook() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)

        val projection = arrayOf(
            DatabaseConstants.BookTagTable.BOOK_ID_COLUMN,
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        assert(cursor.moveToFirst())
        val bookIDFromDB =
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTagTable.BOOK_ID_COLUMN))
        val tagIDFromDB =
            cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.BookTagTable.TAG_ID_COLUMN))

        assertEquals(bookID, bookIDFromDB)
        assertEquals(tag.id, tagIDFromDB)
    }

    @Test
    fun removeTagFromBook() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.removeBookTagItems(tag.id, bookID)

        val projection = arrayOf(
            DatabaseConstants.BookTagTable.BOOK_ID_COLUMN,
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        assert(!cursor.moveToFirst())
    }

    @Test
    fun removeTagFromBookFailWrongTagID() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.removeBookTagItems(tag.id + 1, bookID)

        val projection = arrayOf(
            DatabaseConstants.BookTagTable.BOOK_ID_COLUMN,
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        assert(cursor.moveToFirst())
    }

    @Test
    fun removeTagFromBookFailWrongBookID() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.removeBookTagItems(tag.id, bookID + 1)

        val projection = arrayOf(
            DatabaseConstants.BookTagTable.BOOK_ID_COLUMN,
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        assert(cursor.moveToFirst())
    }

    @Test
    fun removeTagFromBookOnTagDelete() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.deleteTagByID(tag.id)

        val projection = arrayOf(
            DatabaseConstants.BookTagTable.BOOK_ID_COLUMN,
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        assert(!cursor.moveToFirst())
    }

    @Test
    fun removeTagBookRecordsOnBookDelete() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tag2 = Tag("Test tag 2", Color.Gray)
        tag2.id = tagDBService.addTag(tag2)
        testTagAddSuccess(tag2)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.assignTagToBook(tag2.id, bookID)
        BookDBService(applicationContext).deleteBookByID(bookID)

        val projection = arrayOf(
            DatabaseConstants.BookTagTable.BOOK_ID_COLUMN,
            DatabaseConstants.BookTagTable.TAG_ID_COLUMN
        )
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.BookTagTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        assert(!cursor.moveToFirst())
    }

    @Test
    fun getNumberOfBooksUnderTag() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        val numberOfBooks = tagDBService.getNumberOfBooksUnderTag(tag.id)
        assertEquals(1, numberOfBooks)
    }

    @Test
    fun getNumberOfBookUnderTagFailNoBooksInDB() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val numberOfBooks = tagDBService.getNumberOfBooksUnderTag(tag.id)
        assertEquals(0, numberOfBooks)
    }

    @Test
    fun getBooksForOneTag() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        val books = tagDBService.getBooksForTagIDs(listOf(tag.id))
        assertEquals(1, books.size)
        assertEquals(bookID, books[0])
    }

    @Test
    fun getBooksForOneTagFailNoBooksAssignedToTag() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val books = tagDBService.getBooksForTagIDs(listOf(tag.id))
        assertEquals(0, books.size)
    }

    @Test
    fun getBooksForTwoTags() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tag2 = Tag("Test tag 2", Color.Gray)
        tag2.id = tagDBService.addTag(tag2)
        testTagAddSuccess(tag2)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.assignTagToBook(tag2.id, bookID)
        val books = tagDBService.getBooksForTagIDs(listOf(tag.id, tag2.id))
        assertEquals(1, books.size)
        assertEquals(bookID, books[0])
    }

    @Test
    fun getBooksForTwoTagsFailNoBooksAssignedToOneTag() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tag2 = Tag("Test tag 2", Color.Gray)
        tag2.id = tagDBService.addTag(tag2)
        testTagAddSuccess(tag2)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        val books = tagDBService.getBooksForTagIDs(listOf(tag.id, tag2.id))
        assertEquals(0, books.size)
    }

    @Test
    fun getBooksForTwoTagsFailNoBooksAssignedToBothTags() {
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tag2 = Tag("Test tag 2", Color.Gray)
        tag2.id = tagDBService.addTag(tag2)
        testTagAddSuccess(tag2)
        val books = tagDBService.getBooksForTagIDs(listOf(tag.id, tag2.id))
        assertEquals(0, books.size)
    }


    @Test
    fun getTagsForBookID(){
        val tag = Tag("Test tag", Color.Gray)
        tag.id = tagDBService.addTag(tag)
        testTagAddSuccess(tag)
        val tag2 = Tag("Test tag 2", Color.Gray)
        tag2.id = tagDBService.addTag(tag2)
        testTagAddSuccess(tag2)
        val bookID = 1L
        tagDBService.assignTagToBook(tag.id, bookID)
        tagDBService.assignTagToBook(tag2.id, bookID)
        val tags = tagDBService.getTagsForBookID(bookID)
        assertEquals(2, tags.size)
        assertEquals(tag.id, tags[0].id)
        assertEquals(tag.name, tags[0].name)
        assertEquals(tag.color, tags[0].color)
        assertEquals(tag2.id, tags[1].id)
        assertEquals(tag2.name, tags[1].name)
        assertEquals(tag2.color, tags[1].color)
    }
    private fun testTagAddSuccess(tag: Tag) {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagTable.TAG_NAME_COLUMN,
            DatabaseConstants.TagTable.TAG_COLOR_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(tag.id.toString())
        val cursor = tagDBService.readableDatabase.query(
            DatabaseConstants.TagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        assert(cursor.moveToFirst())
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val name =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_NAME_COLUMN))
        val colorString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagTable.TAG_COLOR_COLUMN))
        val color = Color(colorString.toULong())
        assertEquals(tag.id, id)
        assertEquals(tag.name, name)
        assertEquals(tag.color, color)
    }
}