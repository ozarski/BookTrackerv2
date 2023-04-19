package ozarskiapps.booktracker.bookDetailsActivity

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.database.TagDBService
import ozarskiapps.booktracker.tag.Tag

abstract class BookDetailsUI(val book: MutableState<Book>, val context: Context) {

    @Composable
    abstract fun GenerateLayout()

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
    fun ValueTextViewRow(valueText: String) {
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            Text(text = valueText, modifier = Modifier.padding(start = 16.dp), fontSize = 25.sp)
        }
    }

    @Composable
    fun BookAttribute(labelText: String, valueText: String) {
        LabelTextViewRow(labelText = labelText)
        ValueTextViewRow(valueText = valueText)
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
            TagLazyRow(tags = tags.value, modifier)
            Button(onClick = { /*TODO("Add tags to book popup")*/ }, modifier = Modifier.weight(0.3f)) {
                Text(text = "Add")
            }
        }
    }
}