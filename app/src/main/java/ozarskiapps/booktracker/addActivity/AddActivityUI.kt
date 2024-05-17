package ozarskiapps.booktracker.addActivity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ozarskiapps.booktracker.book.Book
import ozarskiapps.booktracker.book.BookStatus
import ozarskiapps.booktracker.bookDetailsActivity.BookDetailsActivity
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.mainActivity.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class AddActivityUI(val context: Context) {
    private lateinit var title: MutableState<TextFieldValue>
    private lateinit var author: MutableState<TextFieldValue>
    private lateinit var numberOfPages: MutableState<TextFieldValue>
    private lateinit var startDate: MutableState<Calendar>
    private lateinit var endDate: MutableState<Calendar>
    private lateinit var selectedOption: MutableState<String>

    @Composable
    fun GenerateLayout() {

        selectedOption = remember { mutableStateOf("Reading") }
        title = remember { mutableStateOf(TextFieldValue("")) }
        author = remember { mutableStateOf(TextFieldValue("")) }
        numberOfPages = remember { mutableStateOf(TextFieldValue("")) }
        startDate = remember { mutableStateOf(Calendar.getInstance()) }
        endDate = remember { mutableStateOf(Calendar.getInstance()) }

        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 10.dp)
                .fillMaxSize()
        ) {
            BookFieldsColumn()
            AddButton(context)
        }

    }


    @Composable
    fun BookFieldsColumn() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
        ) {

            TextFieldRow(placeholder = "Book title", label = "Title", text = title)
            TextFieldRow(placeholder = "Book author", label = "Author", text = author)

            NumberTextFieldRow(
                placeholder = "Number of pages",
                label = "Number of pages",
                text = numberOfPages
            )

            StatusRadioButtons(selectedOption.value) { option -> selectedOption.value = option }
            if (selectedOption.value == "Finished") {
                Text(text = "Start date", modifier = Modifier.padding(start = 16.dp, top = 10.dp))
                DateRow(
                    date = startDate,
                    context = LocalContext.current,
                    maxDate = endDate.value,
                    text = "Start date: "
                )
                Text(text = "End date", modifier = Modifier.padding(start = 16.dp, top = 10.dp))
                DateRow(
                    date = endDate,
                    context = LocalContext.current,
                    minDate = startDate.value,
                    text = "End date: "
                )
            } else if (selectedOption.value == "Reading") {
                Text(text = "Start date", modifier = Modifier.padding(start = 16.dp, top = 10.dp))
                DateRow(date = startDate, context = LocalContext.current)
            }
        }
    }

    @Composable
    fun DateRow(
        date: MutableState<Calendar>,
        context: Context,
        maxDate: Calendar = Calendar.getInstance().apply { add(Calendar.YEAR, 100) },
        minDate: Calendar = Calendar.getInstance().apply { add(Calendar.YEAR, -100) },
        text: String = "Date: "
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
            val formattedDate = sdf.format(date.value.time)
            Text(
                text = "$text $formattedDate",
                modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                textAlign = TextAlign.Start,
                fontSize = 25.sp
            )
            Button(onClick = {
                showDatePickerDialog(date, context, maxDate, minDate)
            }) {
                Text(text = "Change")
            }
        }
    }

    private fun showDatePickerDialog(
        date: MutableState<Calendar>,
        context: Context,
        maxDate: Calendar,
        minDate: Calendar
    ) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                date.value = Calendar
                    .getInstance()
                    .apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
            },
            date.value.get(Calendar.YEAR),
            date.value.get(Calendar.MONTH),
            date.value.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        datePickerDialog.show()
    }

    @Composable
    fun TextFieldRow(placeholder: String, label: String, text: MutableState<TextFieldValue>) {

        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = text.value,
                onValueChange = { newText ->
                    text.value = newText
                },
                label = { Text(text = label) },
                placeholder = { Text(text = placeholder) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
                    .fillMaxWidth()
            )
        }
    }

    @Composable
    fun NumberTextFieldRow(placeholder: String, label: String, text: MutableState<TextFieldValue>) {
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = text.value,
                onValueChange = { newText ->
                    text.value = newText
                },
                label = { Text(text = label) },
                placeholder = { Text(text = placeholder) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
                    .fillMaxWidth()
            )
        }

    }

    @Composable
    fun StatusRadioButtons(selectedOption: String, onOptionSelected: (String) -> Unit) {
        val radioOptions = listOf("Reading", "Finished", "Want to read")

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            // below line is used to create each radio button
            radioOptions.forEach { text ->
                StatusRadioButtonColumn(
                    text = text,
                    selectedOption = selectedOption,
                    onOptionSelected
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StatusRadioButtonColumn(
        text: String,
        selectedOption: String,
        onOptionSelected: (String) -> Unit
    ) {
        Column(
            Modifier
                .selectable(
                    // this method is called when
                    // radio button is selected.
                    selected = (text == selectedOption),
                    // below method is called on
                    // clicking of radio button.
                    onClick = {
                        onOptionSelected(text)
                    }
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = {
                        onOptionSelected(text)
                    }
                )
                Text(
                    text = text,
                )
            }
        }
    }

    private fun checkFields(): Boolean {
        if (title.value.text == "" || author.value.text == "" || numberOfPages.value.text == "")
            return false
        else if (startDate.value.timeInMillis > endDate.value.timeInMillis && selectedOption.value == "Finished")
            return false
        return true
    }

    @Composable
    fun AddButton(context: Context) {
        val coroutineScope = rememberCoroutineScope()
        Button(
            onClick = {
                if (!checkFields()) {
                    Toast.makeText(context, "Invalid values", Toast.LENGTH_SHORT).show()
                }
                else{
                    val book = Book(
                        title = title.value.text,
                        author = author.value.text,
                        numberOfPages = numberOfPages.value.text.toInt(),
                        currentProgress = 0f,
                        bookStatus = BookStatus.valueOf(selectedOption.value),
                        startDate = startDate.value,
                        endDate = endDate.value,
                    )
                    coroutineScope.launch {
                        val bookID = BookDBService(context).addBook(book)
                        val intent = Intent(context, BookDetailsActivity::class.java)
                        intent.putExtra("BOOK_ID", bookID)
                        context.startActivity(intent)
                    }
                }
            },
            modifier = Modifier
                .padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Add")
        }
    }
}

@Preview
@Composable
fun AddBookPreview() {
    AddActivityUI(LocalContext.current).GenerateLayout()
}
