package ozarskiapps.booktracker.bookDetailsActivity

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus


class BookDetailsActivityUI(book: MutableState<Book>, context: Context) :
    BookDetailsUI(book, context) {
    @Composable
    override fun GenerateLayout() {
        val bookStatus = remember { mutableStateOf(book.value.bookStatus) }
        when (bookStatus.value) {
            BookStatus.Finished -> {
                FinishedBookDetailsLayout(book = book, context = context).GenerateLayout()
            }
            BookStatus.WantToRead -> {
                WantToReadBookDetailsLayout(
                    book = book,
                    context = context,
                    bookStatus = bookStatus
                ).GenerateLayout()
            }
            else -> {
                ReadingBookDetailsLayout(
                    book = book,
                    context = context,
                    bookStatus = bookStatus
                ).GenerateLayout()
            }
        }
    }
}