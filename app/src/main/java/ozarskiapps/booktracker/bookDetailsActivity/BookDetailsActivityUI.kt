package ozarskiapps.booktracker.bookDetailsActivity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.TagDBService
import ozarskiapps.booktracker.tag.Tag
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookDetailsActivityLayout(book: MutableState<Book>, context: Context) {
    if (book.value.bookStatus == BookStatus.Finished) {
        finishedBookLayout(book = book, context = context)
    }

}

@Composable
fun finishedBookLayout(book: MutableState<Book>, context: Context) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        BookAttribute(labelText = "Book title", text = book.value.title)
        BookAttribute(labelText = "Book author", text = book.value.author)
        BookAttribute(labelText = "Number of pages", text = book.value.numberOfPages.toString())
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        BookAttribute(labelText = "Start date", text = sdf.format(book.value.startDate.time))
        BookAttribute(labelText = "End date", text = sdf.format(book.value.endDate.time))
        BookAttribute(
            labelText = "Reading time",
            text = book.value.getBookReadingTimeInDays().toString()
        )
        BookAttribute(
            labelText = "Average pages per day",
            text = book.value.getAveragePagesPerDay().toString()
        )
        TagListAttribute(context = context, book = book)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Button(
                onClick = {
                },
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 10.dp, end = 10.dp)
                    .weight(1f)
            ) {
                Text(text = "Edit")
            }

            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(bottom = 10.dp, end = 16.dp, start = 10.dp)
                    .weight(1f)
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Composable
fun LabelTextViewRow(labelText: String) {
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = labelText,
            modifier = Modifier.padding(start = 16.dp, top = 10.dp),
            fontSize = 20.sp
        )
    }
}

@Composable
fun TextViewRow(text: String) {
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Text(text = text, modifier = Modifier.padding(start = 16.dp), fontSize = 25.sp)
    }
}

@Composable
fun BookAttribute(labelText: String, text: String) {
    LabelTextViewRow(labelText = labelText)
    TextViewRow(text = text)
}

@Composable
fun TagItem(tag: Tag) {
    Text(text = tag.name, modifier = Modifier.padding(5.dp))
}

@Composable
fun TagLazyRow(tags: List<Tag>) {
    LazyRow(modifier = Modifier.padding(10.dp)) {
        items(tags.size) {
            TagItem(tags[it])
        }
    }
}

@Composable
fun TagListAttribute(context: Context, book: MutableState<Book>) {
    val tagDBService = remember { TagDBService(context = context) }
    val tags = remember { mutableStateOf(tagDBService.getTagsForBookID(book.value.id)) }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tags: ",
            modifier = Modifier.padding(start = 16.dp, top = 10.dp, end = 5.dp),
            fontSize = 25.sp
        )
        TagLazyRow(tags = tags.value)
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Add tag")
        }
    }
}

@Composable
@Preview
fun FinishedBookPreview() {
    val book = remember {
        mutableStateOf(
            Book(
                "title",
                "author",
                100,
                0,
                BookStatus.Finished,
                Calendar.getInstance(),
                Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            )
        )
    }

    finishedBookLayout(book = book, context = androidx.compose.ui.platform.LocalContext.current)
}

@Composable
@Preview
fun TagLazyRowPreview() {
    val tags = listOf(
        Tag("tag name 1", color = Color.Red.toString()),
        Tag("tag name 2", color = Color.Green.toString()),
        Tag("tag name 3", color = Color.Blue.toString()),
        Tag("tag name 4", color = Color.Yellow.toString()),
        Tag("tag name 5", color = Color.Magenta.toString()),
        Tag("tag name 6", color = Color.Cyan.toString()),
        Tag("tag name 7", color = Color.Gray.toString()),
        Tag("tag name 8", color = Color.White.toString()),
        Tag("tag name 9", color = Color.Black.toString())
    )
    TagLazyRow(tags = tags)
}