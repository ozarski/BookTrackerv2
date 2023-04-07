package ozarskiapps.booktracker

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import ozarskiapps.booktracker.database.DBService
import ozarskiapps.booktracker.database.DatabaseConstants

class DatabaseTests {
    private lateinit var appContext: Context
    private lateinit var dbService: DBService

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        dbService = DBService(appContext)
    }

    @After
    fun tearDown() {
        dbService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun testDatabaseCreation() {
        TestCase.assertNotNull(dbService.writableDatabase)
    }


    @Test
    fun testBookTableCreation() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' AND name='${DatabaseConstants.BookTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            TestCase.assertTrue(cursor.moveToFirst())
            TestCase.assertEquals(DatabaseConstants.BookTable.TABLE_NAME, cursor.getString(0))
        }

    }

    @Test
    fun testReadingTimeTableCreation() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' AND name='${DatabaseConstants.ReadingTimeTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            TestCase.assertTrue(cursor.moveToFirst())
            TestCase.assertEquals(DatabaseConstants.ReadingTimeTable.TABLE_NAME, cursor.getString(0))
        }
    }

    @Test
    fun testTagTableCreation() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' AND name='${DatabaseConstants.TagTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            TestCase.assertTrue(cursor.moveToFirst())
            TestCase.assertEquals(DatabaseConstants.TagTable.TABLE_NAME, cursor.getString(0))
        }
    }

    @Test
    fun testBookTagTableCreation() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' AND name='${DatabaseConstants.BookTagTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            TestCase.assertTrue(cursor.moveToFirst())
            TestCase.assertEquals(DatabaseConstants.BookTagTable.TABLE_NAME, cursor.getString(0))
        }
    }
}