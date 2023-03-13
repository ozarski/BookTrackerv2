package ozarskiapps.booktracker.book

class Book(val title: String, val author: String, val numberOfPages: Int, val openLibraryKey: String, val id: Long = -1L){
    override fun toString(): String {
        return "Title: $title \t" +
                "Author: $author\t" +
                "Number of pages: $numberOfPages"
    }
}
