package ozarskiapps.booktracker.database

import android.provider.BaseColumns

object DatabaseConstants{
    const val DATABASE_NAME = "books.db"

    const val DATABASE_VERSION = 1

    const val BOOK_TABLE_CREATE_QUERY = "CREATE TABLE ${BookTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${BookTable.TITLE_COLUMN} TEXT NOT NULL," +
            "${BookTable.AUTHOR_COLUMN} TEXT NOT NULL," +
            "${BookTable.NUMBER_OF_PAGES_COLUMN} INTEGER NOT NULL," +
            "${BookTable.CURRENT_PROGRESS_COLUMN} INTEGER," +
            "${BookTable.BOOK_STATUS_COLUMN} TEXT NOT NULL," +
            "${BookTable.START_DATE_COLUMN} LONG," +
            "${BookTable.END_DATE_COLUMN} LONG)"

    const val READING_TIME_TABLE_CREATE_QUERY = "CREATE TABLE ${ReadingTimeTable.TABLE_NAME} (" +
            "${ReadingTimeTable.BOOK_ID_COLUMN} TEXT NOT NULL," +
            "${ReadingTimeTable.DATE_COLUMN} LONG NOT NULL)"

    const val TAG_TABLE_CREATE_QUERY = "CREATE TABLE ${TagTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${TagTable.TAG_NAME_COLUMN} TEXT NOT NULL)"

    const val BOOK_TAG_TABLE_CREATE_QUERY = "CREATE TABLE ${BookTagTable.TABLE_NAME} (" +
            "${BookTagTable.BOOK_ID_COLUMN} INTEGER NOT NULL," +
            "${BookTagTable.TAG_ID_COLUMN} INTEGER NOT NULL)"

    object BookTable: BaseColumns {
        const val TABLE_NAME = "books"
        const val TITLE_COLUMN = "title"
        const val AUTHOR_COLUMN = "author"
        const val NUMBER_OF_PAGES_COLUMN = "number_of_pages"
        const val CURRENT_PROGRESS_COLUMN = "current_progress"
        const val BOOK_STATUS_COLUMN = "book_status"
        const val START_DATE_COLUMN = "start_date"
        const val END_DATE_COLUMN = "end_date"
    }

    object ReadingTimeTable: BaseColumns{
        const val TABLE_NAME = "reading_time"
        const val BOOK_ID_COLUMN = "book_id"
        const val DATE_COLUMN = "date"
    }

    object TagTable: BaseColumns{
        const val TABLE_NAME = "tags"
        const val TAG_NAME_COLUMN = "tag_name"
    }

    object BookTagTable{
        const val TABLE_NAME = "book_tags"
        const val BOOK_ID_COLUMN = "book_id"
        const val TAG_ID_COLUMN = "tag_id"
    }
}