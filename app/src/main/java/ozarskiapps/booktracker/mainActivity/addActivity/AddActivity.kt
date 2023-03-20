package ozarskiapps.booktracker.mainActivity.addActivity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import ozarskiapps.booktracker.mainActivity.LayoutMain
import java.util.*

class AddActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            AddLayout(context = this)
        }
    }
}

@Composable
private fun AddLayout(context: Context){
    val bookTitle = remember { mutableStateOf(TextFieldValue("")) }
    val bookAuthor = remember { mutableStateOf(TextFieldValue("")) }
    val bookNumberOfPages = remember { mutableStateOf(TextFieldValue("")) }
    val startDay = remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    val startMonth = remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    val startYear = remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    val endDay = remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    val endMonth = remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    val endYear = remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    BookAddActivityLayout(title = bookTitle, author = bookAuthor, numberOfPages = bookNumberOfPages, context, startDay, startMonth, startYear, endDay, endMonth, endYear)
}
