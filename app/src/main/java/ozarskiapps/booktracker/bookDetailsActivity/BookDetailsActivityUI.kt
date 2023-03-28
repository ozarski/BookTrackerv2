package ozarskiapps.booktracker.bookDetailsActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book

@Composable
fun BookDetailsActivityLayout(book: MutableState<Book>) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(top = 10.dp)
            .fillMaxSize()
    ){
        BookAttribute(labelText = "Book title", text = book.value.title)
        BookAttribute(labelText = "Book author", text = book.value.author)
        BookAttribute(labelText = "Number of pages", text = book.value.numberOfPages.toString())
    }
}

@Composable
fun LabelTextViewRow(labelText: String){
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Text(text = labelText, modifier = Modifier.padding(start = 16.dp, top = 10.dp), fontSize = 15.sp)
    }
}

@Composable
fun TextViewRow(text: String){
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        Text(text = text, modifier = Modifier.padding(start = 16.dp), fontSize = 20.sp)
    }
}

@Composable
fun BookAttribute(labelText: String, text: String){
    LabelTextViewRow(labelText = labelText)
    TextViewRow(text = text)
}