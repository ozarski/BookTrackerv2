package ozarskiapps.booktracker.mainActivity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.TagDBService
import ozarskiapps.booktracker.tag.Tag
import java.util.*

class MainBooksUI(val context: Context) {

    private lateinit var text: MutableState<String>
    private lateinit var tagList: MutableList<Tag>
    private lateinit var bookList: SnapshotStateList<List<Book>>

    @Composable
    fun GenerateLayout(){
        LayoutMainBooks()
    }

    @Composable
    private fun LayoutMainBooks() {

        tagList = mutableListOf()
        text = remember { mutableStateOf("All") }
        tagList.add(Tag(name = "All", color = Color.Red))
        tagList.add(Tag(name = "${BookStatus.Reading}", color = Color.Gray))
        tagList.add(Tag(name = "${BookStatus.Finished}", color = Color.Green))
        tagList.add(Tag(name = "${BookStatus.WantToRead}", color = Color.Blue))
        tagList.addAll(TagDBService(context).getAllTags().toMutableList())

        bookList = remember { mutableStateListOf(BookDBService(context).getAllBooks()) }

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
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            TagDropdownTextRow(isOpen)

            Row(modifier = Modifier.fillMaxWidth()) {
                DropdownTagList(
                    list = tagList,
                    request = openCloseDropdown,
                    selectedString = userSelectedString,
                    requestToOpen = isOpen.value
                )
                BookListUI(bookList, context).GenerateLayout()
            }
        }
    }

    @Composable
    private fun TagDropdownTextRow(isOpen: MutableState<Boolean>) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 10.dp)
                    .clickable { isOpen.value = true },
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
        }
    }


    @Composable
    private fun DropdownTagList(
        requestToOpen: Boolean = false,
        list: List<Tag>,
        request: (Boolean) -> Unit,
        selectedString: (Tag) -> Unit
    ) {
        DropdownMenu(
            expanded = requestToOpen,
            onDismissRequest = { request(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            list.forEach {
                DropdownMenuItem(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray), onClick = {
                    selectedString(it)
                    request(false)
                    reloadBookList(it)
                }) {
                    Text(it.name)
                }
            }
        }
    }

    private fun reloadBookList(tag: Tag){
        if(tag.name != "Reading" && tag.name != "Finished" && tag.name != "WantToRead" && tag.name != "All"){
            val bookIDs = TagDBService(context).getBooksForTagIDs(listOf(tag.id))
            val newBookList = BookDBService(context).getBooksWithIDs(bookIDs)
            bookList.clear()
            bookList.addAll(Collections.singleton(newBookList))
        }
        else{
            if(tag.name == "All"){
                bookList.clear()
                bookList.addAll(Collections.singleton(BookDBService(context).getAllBooks()))
                return
            }
            val newBookList = BookDBService(context).getAllBooks()
            bookList.clear()
            val bookFilteredByStatus = newBookList.filter { it.bookStatus.name == tag.name }
            bookList.addAll(Collections.singleton(bookFilteredByStatus))
        }
    }
}