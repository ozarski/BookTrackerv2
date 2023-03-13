package ozarskiapps.booktracker

import ozarskiapps.booktracker.book.Book

fun mockBookList(): List<Book> {
    val list = mutableListOf<Book>()
    for (i in 0..20) {
        list.add(Book("Full book title $i", "Author name $i", i*42, "OL$i"))
    }
    return list
}