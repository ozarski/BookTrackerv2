package ozarskiapps.booktracker.bookDetailsActivity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import ozarskiapps.booktracker.database.BookStatsDBService
import ozarskiapps.booktracker.roundDouble
import java.text.SimpleDateFormat
import java.util.*

class ReadingBookDetailsLayout(
    book: MutableState<Book>,
    context: Context,
    val bookStatus: MutableState<BookStatus>
) : BookDetailsUI(book, context) {

    @Composable
    override fun GenerateLayout() {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {

            BookAttribute(labelText = "Book title", valueText = book.value.title)
            BookAttribute(labelText = "Book author", valueText = book.value.author)
            BookAttribute(
                labelText = "Number of pages",
                valueText = book.value.numberOfPages.toString()
            )

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            BookAttribute(
                labelText = "Start date",
                valueText = sdf.format(book.value.startDate.time)
            )

            BookProgressSection()

            TagListAttribute(context = context, book = book)

            val rowModifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            val buttonsModifier = Modifier
                .padding(start = 7.dp, bottom = 10.dp, end = 7.dp)
                .weight(0.3f)
            ReadingBookUIButtons(rowModifier = rowModifier, buttonsModifier = buttonsModifier)
        }
    }

    @Composable
    fun ReadingBookUIButtons(rowModifier: Modifier, buttonsModifier: Modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = rowModifier
        ) {
            Button(
                onClick = {
                    bookStatus.value = BookStatus.Finished
                    BookDBService(context).finishReadingBookToday(book.value)
                },
                modifier = buttonsModifier
            ) {
                Text(text = "Finish today", fontSize = 14.sp)
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = buttonsModifier
            ) {
                Text(text = "Edit", fontSize = 14.sp)
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = buttonsModifier
            ) {
                Text(text = "Delete", fontSize = 14.sp)
            }
        }
    }

    @Composable
    fun BookProgressSection() {
        val progressValue = remember { mutableStateOf(book.value.currentProgress) }
        BookProgressIndicator(progressValue)
        BookProgressSlider(progressValue)
        BookProgressStats(progressValue)
    }

    @Composable
    fun BookProgressIndicator(progressValue: MutableState<Float>) {
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Progress",
                modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                fontSize = 20.sp
            )
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${progressValue.value.toInt()} pages",
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 25.sp
            )
        }
    }

    @Composable
    fun BookProgressSlider(progressValue: MutableState<Float>) {
        Row {
            Slider(
                value = progressValue.value,
                onValueChange = {
                    book.value.currentProgress = roundDouble(it.toDouble(), 1).toFloat()
                    progressValue.value = book.value.currentProgress
                },
                onValueChangeFinished = {
                    BookDBService(context).updateBook(book.value)
                },
                valueRange = 0f..book.value.numberOfPages.toFloat(),
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    inactiveTickColor = Color.Gray,
                    activeTickColor = Color.Black,
                )
            )
        }
    }

    @Composable
    fun ProgressValueTextRow(text: String) {

        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp
            )
        }
    }

    @Composable
    fun BookProgressStats(progressValue: MutableState<Float>) {
        val percentage = (progressValue.value / book.value.numberOfPages.toFloat()) * 100
        val percentageRounded = roundDouble(percentage.toDouble(), 10)
        ProgressValueTextRow(text = "Completion: $percentageRounded%")

        val estimatedReadingTime =
            BookStatsDBService(context).getBookPredictedReadingTime(book.value)
        val daysLeft =
            if (estimatedReadingTime != null) estimatedReadingTime - book.value.getDaysSinceStart() else null
        ProgressValueTextRow(
            text = if (estimatedReadingTime != null)
                "Estimated reading time: $estimatedReadingTime days ($daysLeft left)"
            else
                "Estimated reading time: -"
        )

        val estimatedFinishDate = BookStatsDBService(context).getBookPredictedFinishDate(book.value)
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        val estimatedFinishDateText = if (estimatedFinishDate != 0L) {
            sdf.format(estimatedFinishDate)
        } else {
            "-"
        }
        ProgressValueTextRow(
            text = if (estimatedFinishDate != 0L)
                "Estimated finish date: $estimatedFinishDateText"
            else
                "Estimated finish date: -"
        )
    }
}

@Preview
@Composable
fun ReadingBookLayoutPreview() {
    val book = remember {
        mutableStateOf(
            Book(
                "The Lord of the Rings",
                "J.R.R. Tolkien",
                1000,
                500f,
                BookStatus.Reading,
                Calendar.getInstance(),
                Calendar.getInstance(),
            )
        )
    }

    val bookStatus = remember { mutableStateOf(BookStatus.Reading) }
    ReadingBookDetailsLayout(
        book = book,
        context = androidx.compose.ui.platform.LocalContext.current,
        bookStatus = bookStatus
    ).GenerateLayout()
}