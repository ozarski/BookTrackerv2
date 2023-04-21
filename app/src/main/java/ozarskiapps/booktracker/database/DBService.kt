package ozarskiapps.booktracker.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class DBService(private val appContext: Context) : SQLiteOpenHelper(
    appContext,
    DatabaseConstants.DATABASE_NAME,
    null,
    DatabaseConstants.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        if(db!=null){
            createBookTable(db)
            createReadingTimeTable(db)
            createTagTable(db)
            createBookTagTable(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO("Not yet implemented")
    }

    private fun createBookTable(db: SQLiteDatabase){
        db.execSQL(DatabaseConstants.BOOK_TABLE_CREATE_QUERY)
    }

    private fun createReadingTimeTable(db: SQLiteDatabase){
        db.execSQL(DatabaseConstants.READING_TIME_TABLE_CREATE_QUERY)
    }

    private fun createTagTable(db: SQLiteDatabase){
        db.execSQL(DatabaseConstants.TAG_TABLE_CREATE_QUERY)
    }

    private fun createBookTagTable(db: SQLiteDatabase){
        db.execSQL(DatabaseConstants.BOOK_TAG_TABLE_CREATE_QUERY)
    }

    fun dropDatabase(){
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }
}