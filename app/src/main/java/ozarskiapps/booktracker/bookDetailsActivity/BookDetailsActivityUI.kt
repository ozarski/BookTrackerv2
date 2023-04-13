package ozarskiapps.booktracker.bookDetailsActivity

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.TagDBService
import ozarskiapps.booktracker.tag.Tag
import java.util.*

@Composable
fun BookDetailsActivityLayout(book: MutableState<Book>, context: Context) {
    if (book.value.bookStatus == BookStatus.Finished) {
        FinishedBookDetailsUI(book = book, context = context).FinishedBookLayout()
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
fun TagLazyRow(tags: List<Tag>, modifier: Modifier) {
    LazyRow(modifier = modifier) {
        items(tags.size) {
            TagItem(tags[it])
        }
    }
}

@Composable
fun TagListAttribute(context: Context, book: MutableState<Book>) {
    val tagDBService = remember { TagDBService(context = context) }
    val tags = remember { mutableStateOf(tagDBService.getTagsForBookID(book.value.id)) }
    //use only for preview purposes, TODO("REMOVE IN PRODUCTION")
    val mockTags = listOf(
        Tag("tag name 1", color = Color.Red.toString()),
        Tag("tag name 2", color = Color.Green.toString()),
        Tag("tag name 3", color = Color.Blue.toString()),
        Tag("tag name 4", color = Color.Yellow.toString()),
        Tag("tag name 5", color = Color.Magenta.toString()),
        Tag("tag name 6", color = Color.Cyan.toString()),
        Tag("tag name 7", color = Color.Gray.toString()),
        Tag("tag name 8", color = Color.Black.toString()),
        Tag("tag name 9", color = Color.White.toString()),
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(
            text = "Tags: ",
            modifier = Modifier
                .padding(start = 16.dp, top = 10.dp)
                .weight(0.25f),
            fontSize = 20.sp,
        )
        val modifier = Modifier
            .padding(top = 10.dp)
            .weight(1f)
        TagLazyRow(tags = mockTags, modifier)
        Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(0.3f)) {
            Text(text = "Add")
        }
    }
}
