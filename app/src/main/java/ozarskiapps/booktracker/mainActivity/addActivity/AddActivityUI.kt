package ozarskiapps.booktracker.mainActivity.addActivity

import android.content.Context
import android.widget.NumberPicker
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*

@Composable
fun BookAddActivityLayout(title: MutableState<TextFieldValue>,
                          author: MutableState<TextFieldValue>,
                          numberOfPages: MutableState<TextFieldValue>,
                          context: Context,
                          startDay: MutableState<Int>,
                          startMonth: MutableState<Int>,
                          startYear: MutableState<Int>,
                          endDay: MutableState<Int>,
                          endMonth: MutableState<Int>,
                          endYear: MutableState<Int>){

    Column(modifier = Modifier
        .background(Color.White)
        .padding(top = 10.dp)
        .fillMaxSize()){

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)){

            TextFieldRow(placeholder = "Book title", label = "Title", text = title)
            TextFieldRow(placeholder = "Book author", label = "Author", text = author)
            NumberTextFieldRow(placeholder = "Number of pages", label = "Number of pages", text = numberOfPages)
            StatusRadioButtons()
            Text(text = "Start date", modifier = Modifier.padding(start = 16.dp, top = 10.dp))
            StartDatePicker(context = context, month = startMonth, day = startDay, year = startYear)
            Text(text = "End date", modifier = Modifier.padding(start = 16.dp, top = 10.dp))
            EndDatePicker(context = context, month = endMonth, day = endDay, year = endYear)
        }
        AddButton()
    }

}

@Composable
fun TextFieldRow(placeholder: String, label: String, text: MutableState<TextFieldValue>){
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()){
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
                unfocusedBorderColor = Color.Gray),
            modifier = Modifier
                .padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun NumberTextFieldRow(placeholder: String, label: String, text: MutableState<TextFieldValue>){
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()){
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
                unfocusedBorderColor = Color.Gray),
            modifier = Modifier
                .padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
                .fillMaxWidth()
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusRadioButtons() {
    val radioOptions = listOf("Reading", "Finished ", "Want to read")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    val option = remember { mutableStateOf(radioOptions[0]) }
    Row (horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()){
        // below line is use to set data to
        // each radio button in columns.
        radioOptions.forEach { text ->
            StatusRadioButton(text = text, selectedOption = selectedOption, onOptionSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusRadioButton(text: String,
                      selectedOption: String,
                      onOptionSelected: (String) -> Unit){
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
        Row(verticalAlignment = Alignment.CenterVertically){
            RadioButton(
                selected = (text == selectedOption),
                onClick = {
                    // inside on click method we are setting a
                    // selected option of our radio buttons.
                    onOptionSelected(text)
                }
            )
            Text(
                text = text,
            )
        }
    }
}

@Composable
fun CreateNumberPicker(context: Context,
                       changedValue: MutableState<Int>,
                       minVal: Int,
                       maxVal: MutableState<Int>){
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                setOnValueChangedListener { numberPicker, i, i2 ->  changedValue.value = i2}
                minValue = minVal
                maxValue = maxVal.value
                wrapSelectorWheel = true
            }
        }
    )
}

@Composable
fun CustomNumberPicker(context: Context, 
                       changedValue: MutableState<Int>,
                       minVal: Int,
                       maxVal: Int,
                       displayedValues: Array<String>){
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                setOnValueChangedListener { numberPicker, i, i2 ->  changedValue.value = i2}
                minValue = minVal
                maxValue = maxVal
                wrapSelectorWheel = true
                this.displayedValues = displayedValues
            }
        }
    )
}

@Composable
fun MonthNumberPicker(context: Context, month: MutableState<Int>){
    val displayedValues = arrayOf("January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December")
    CustomNumberPicker(context = context, changedValue = month, minVal = 0, maxVal = 11, displayedValues = displayedValues)
}

@Composable
fun DayOfMonthNumberPicker(context: Context, month: MutableState<Int>, day: MutableState<Int>){
    val maxVal = remember {mutableStateOf(
            when(month.value){
                0, 2, 4, 6, 7, 9, 11 -> 31
                1 -> 28
                else -> 30
            }
        )
    }
    CreateNumberPicker(context = context, changedValue = day, minVal = 1, maxVal = maxVal)
}

@Composable
fun YearNumberPicker(context: Context, year: MutableState<Int>){
    CreateNumberPicker(context = context, changedValue = year, minVal = 1900, maxVal = remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) })
}

@Composable
fun StartDatePicker(context: Context, month: MutableState<Int>, day: MutableState<Int>, year: MutableState<Int>){
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)){
        DayOfMonthNumberPicker(context, month, day)
        MonthNumberPicker(context, month)
        YearNumberPicker(context, year)
    }
}

@Composable
fun EndDatePicker(context: Context, month: MutableState<Int>, day: MutableState<Int>, year: MutableState<Int>){
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)){
        DayOfMonthNumberPicker(context, month, day)
        MonthNumberPicker(context, month)
        YearNumberPicker(context, year)
    }
}

@Composable
fun AddButton(){
    Button(onClick = { /*TODO*/ }, modifier = Modifier
        .padding(start = 16.dp, bottom = 10.dp, end = 16.dp)
        .fillMaxWidth()) {
        Text(text = "Add")
    }
}


@Preview
@Composable
fun BookAddActivityLayoutPreview() {
    val bookTitle = remember { mutableStateOf(TextFieldValue("")) }
    val bookAuthor = remember { mutableStateOf(TextFieldValue("")) }
    val bookNumberOfPages = remember { mutableStateOf(TextFieldValue("")) }
    BookAddActivityLayout(
        bookTitle,
        bookAuthor,
        bookNumberOfPages,
        context = LocalContext.current,
        remember{mutableStateOf(3)},
        remember{mutableStateOf(20)},
        remember{mutableStateOf(2023)},
        remember{mutableStateOf(4)},
        remember{mutableStateOf(1)},
        remember{mutableStateOf(2023)}
    )
}