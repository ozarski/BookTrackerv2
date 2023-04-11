package ozarskiapps.booktracker.bookDetailsActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus

class BookDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookDetailsLayout()
        }
    }

    @Preview
    @Composable
    private fun BookDetailsLayout() {
        val book = remember {
            mutableStateOf(
                Book(
                    "The Lord of the Rings",
                    "J.R.R. Tolkien",
                    1178,
                    0,
                    BookStatus.WantToRead,
                    java.util.Calendar.getInstance(),
                    java.util.Calendar.getInstance(),
                    0
                )
            )
        }
        BookDetailsActivityLayout(book = book, this)
    }
}