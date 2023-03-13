package ozarskiapps.booktracker.mainActivity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book

@Composable
fun BookListLazyColumn(bookList: List<Book>){
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxSize()){
        items(bookList.size){
            BookItemRow(bookList[it])
        }
    }
}

@Composable
fun BookItemRow(book: Book){
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(75.dp)
            .background(Color.White)
            .padding(3.dp)
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(5.dp))
            .clickable { println(book.toString()) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Text(
            text = book.title,
            modifier = Modifier
                .weight(0.7f)
                .padding(10.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.author,
            modifier = Modifier
                .weight(0.3f)
                .padding(10.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
