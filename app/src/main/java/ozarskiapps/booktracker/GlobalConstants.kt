package ozarskiapps.booktracker

const val HOURS_IN_DAY = 24
const val MINUTES_IN_HOUR = 60
const val SECONDS_IN_MINUTE = 60
const val MILLISECONDS_IN_SECOND = 1000
const val MILLISECONDS_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND

const val OPEN_LIBRARY_API_CONNECTION_STRING = "https://openlibrary.org/search.json?q="
const val OPEN_LIBRARY_API_SELECTION_FIELDS = "title,author_name,number_of_pages_median,key"