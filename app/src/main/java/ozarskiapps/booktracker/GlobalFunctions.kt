package ozarskiapps.booktracker

import ozarskiapps.booktracker.book.Book
import java.util.*

//fun mockBookList(): List<Book> {
//    val list = mutableListOf<Book>()
//    for (i in 0..20) {
//        list.add(Book("Full book title $i", "Author name $i", i*42))
//    }
//    return list
//}

fun setCalendar(calendar: Calendar, dayStart: Boolean = true): Calendar {
    if(dayStart){
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    } else {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
    }
    return calendar
}