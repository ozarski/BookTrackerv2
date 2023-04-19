package ozarskiapps.booktracker.mainActivity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.TagDBService
import ozarskiapps.booktracker.tag.Tag

@Composable
fun LayoutMainBooks(context: Context){
    val text = remember { mutableStateOf("Reading") }
    val tagList = TagDBService(context).getAllTags().toMutableList()
    tagList.add(Tag(name = "${BookStatus.Reading}", color = "#000000"))
    tagList.add(Tag(name = "${BookStatus.Finished}", color = "#000000"))
    tagList.add(Tag(name = "${BookStatus.WantToRead}", color = "#000000"))
    val bookList = remember { mutableStateListOf(BookDBService(context).getAllBooks()) }

    val isOpen = remember {
        mutableStateOf(false)
    }
    val openCloseDropdown: (Boolean) -> Unit = {
        isOpen.value = it
    }
    val userSelectedString: (Tag) -> Unit = {
        text.value = it.name
        bookList.clear()
        val bookIDs = TagDBService(context).getBooksForTagIDs(listOf(it.id))

        //bookList.addAll()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)){
        Row(modifier = Modifier.fillMaxWidth()){
            Text(text = text.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp)
                    .clickable { isOpen.value = true },
                fontSize = 20.sp,
                textAlign = TextAlign.Start)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            DropdownTagList(list = tagList,
                request = openCloseDropdown,
                selectedString = userSelectedString,
                requestToOpen = isOpen.value)
            BookListLazyColumn(bookList = bookList)
        }
    }
}

@Composable
private fun DropdownTagList(
    requestToOpen: Boolean = false,
    list: List<Tag>,
    request: (Boolean) -> Unit,
    selectedString: (Tag) -> Unit
){
    DropdownMenu(expanded = requestToOpen, onDismissRequest = { request(false) }, modifier = Modifier.fillMaxWidth()){
        list.forEach {
            DropdownMenuItem(modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Gray), onClick = {
                selectedString(it)
                request(false)
                println("Selected: $it")
            }){
                Text(it.name)
            }
        }
    }
}