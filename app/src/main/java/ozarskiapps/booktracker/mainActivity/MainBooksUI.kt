package ozarskiapps.booktracker.mainActivity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.TagDBService
import ozarskiapps.booktracker.tag.Tag

class MainBooksUI(val context: Context) {

    private lateinit var text: MutableState<String>
    private val tagList = TagDBService(context).getAllTags().toMutableList()

    @Composable
    fun GenerateLayout(){
        LayoutMainBooks()
    }

    @Composable
    private fun LayoutMainBooks() {

        text = remember { mutableStateOf("Reading") }
        tagList.add(Tag(name = "${BookStatus.Reading}", color = Color.Gray))
        tagList.add(Tag(name = "${BookStatus.Finished}", color = Color.Green))
        tagList.add(Tag(name = "${BookStatus.WantToRead}", color = Color.Blue))
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
                BookListUI(bookList).GenerateLayout()
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
                    println("Selected: $it")
                }) {
                    Text(it.name)
                }
            }
        }
    }
}