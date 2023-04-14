package ozarskiapps.booktracker.bookDetailsActivity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import java.util.*

class WantToReadBookDetailsUI(val book: MutableState<Book>, val context: Context, val bookStatus: MutableState<BookStatus>) {

    @Composable
    fun GenerateLayout() {

        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            BookAttribute(labelText = "Book title", text = book.value.title)
            BookAttribute(labelText = "Book author", text = book.value.author)
            BookAttribute(labelText = "Number of pages", text = book.value.numberOfPages.toString())
            TagListAttribute(context = context, book = book)
            val rowModifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            val buttonsModifier = Modifier
                .padding(start = 7.dp, bottom = 10.dp, end = 7.dp)
                .weight(0.3f)
            WantToReadBookUIButtons(rowModifier = rowModifier, buttonsModifier = buttonsModifier)
        }
    }

    @Composable
    fun WantToReadBookUIButtons(rowModifier: Modifier, buttonsModifier: Modifier){
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = rowModifier
        ) {
            Button(
                onClick = {
                    bookStatus.value = BookStatus.Reading
                    BookDBService(context).startReadingBookToday(book.value)
                },
                modifier = buttonsModifier
            ) {
                Text(text = "Start today", fontSize = 15.sp)
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = buttonsModifier
            ) {
                Text(text = "Edit", fontSize = 15.sp)
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = buttonsModifier
            ) {
                Text(text = "Delete", fontSize = 15.sp)
            }
        }
    }
}


@Composable
@Preview
fun WantToReadBookLayoutPreview() {
    val book = remember {
        mutableStateOf(
            Book(
                "The Lord of the Rings",
                "J.R.R. Tolkien",
                1000,
                0f,
                BookStatus.WantToRead,
                Calendar.getInstance(),
                Calendar.getInstance(),
            )
        )
    }

    val bookStatus = remember {
        mutableStateOf(BookStatus.WantToRead)
    }

    WantToReadBookDetailsUI(book, androidx.compose.ui.platform.LocalContext.current, bookStatus).GenerateLayout()
}