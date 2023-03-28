package ozarskiapps.booktracker.addActivity

import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*

@Composable
fun CreateNumberPicker(
    startVal: Int,
    minVal: Int,
    maxVal: Int,
    onValueChanged: (Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                setOnValueChangedListener { _, _, valueAfterChange ->
                    onValueChanged(
                        valueAfterChange
                    )
                }
                //need to use post to set value, otherwise the value will be set before the view is created and the value will be ignored
                post { value = startVal }
                minValue = minVal
                maxValue = maxVal
                wrapSelectorWheel = false
            }
        }
    )
}

@Composable
fun CustomNumberPicker(
    startVal: Int,
    minVal: Int,
    maxVal: Int,
    displayedValues: Array<String>,
    onValueChanged: (Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                setOnValueChangedListener { _, _, valueAfterChange ->
                    onValueChanged(
                        valueAfterChange
                    )
                }
                post { value = startVal }
                minValue = minVal
                maxValue = maxVal
                wrapSelectorWheel = false
                this.displayedValues = displayedValues
            }
        }
    )
}

@Composable
fun JetpackDatePicker(
    maxDate: MutableState<Calendar> = remember {
        mutableStateOf(Calendar.getInstance()
            .apply {
                set(Calendar.YEAR, 2100)
                set(Calendar.MONTH, 11)
                set(Calendar.DAY_OF_MONTH, 31)
            })
    },
    minDate: MutableState<Calendar> = remember {
        mutableStateOf(Calendar.getInstance()
            .apply {
                set(Calendar.YEAR, 1900)
                set(Calendar.MONTH, 0)
                set(Calendar.DAY_OF_MONTH, 1)
            })
    },
    selectedDate: MutableState<Calendar> = remember { mutableStateOf(Calendar.getInstance()) }
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {


            val maxDayOfMonthVal =
                if (selectedDate.value.get(Calendar.MONTH) != maxDate.value.get(Calendar.MONTH)) {
                    selectedDate.value.getActualMaximum(Calendar.DAY_OF_MONTH)
                } else {
                    maxDate.value.get(Calendar.DAY_OF_MONTH)
                }
            val maxMonthVal =
                if (selectedDate.value.get(Calendar.YEAR) != maxDate.value.get(Calendar.YEAR)) {
                    11
                } else {
                    maxDate.value.get(Calendar.MONTH)
                }

            val minDayOfMonthVal =
                if (selectedDate.value.get(Calendar.MONTH) != minDate.value.get(Calendar.MONTH)) {
                    1
                } else {
                    minDate.value.get(Calendar.DAY_OF_MONTH)
                }
            val minMonthVal =
                if (selectedDate.value.get(Calendar.YEAR) != minDate.value.get(Calendar.YEAR)) {
                    0
                } else {
                    minDate.value.get(Calendar.MONTH)
                }

            //Day of month number picker
            CreateNumberPicker(
                selectedDate.value.get(Calendar.DAY_OF_MONTH),
                minDayOfMonthVal,
                maxDayOfMonthVal
            ) {
                selectedDate.value.set(Calendar.DAY_OF_MONTH, it)
                println(
                    "Selected date: ${selectedDate.value.get(Calendar.DAY_OF_MONTH)}.${
                        selectedDate.value.get(
                            Calendar.MONTH
                        )
                    }.${selectedDate.value.get(Calendar.YEAR)}"
                )
            }
            //Month number picker
            val months = arrayOf(
                "January",
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
                "December"
            )
            val monthsAdjusted = months.copyOfRange(minMonthVal, maxMonthVal + 1)
            CustomNumberPicker(
                selectedDate.value.get(Calendar.MONTH),
                minMonthVal,
                maxMonthVal,
                monthsAdjusted
            ) {
                selectedDate.value.set(Calendar.MONTH, it)
                println(
                    "Selected date: ${selectedDate.value.get(Calendar.DAY_OF_MONTH)}.${
                        selectedDate.value.get(
                            Calendar.MONTH
                        )
                    }.${selectedDate.value.get(Calendar.YEAR)}"
                )
            }
            //Year number picker
            CreateNumberPicker(
                selectedDate.value.get(Calendar.YEAR),
                minDate.value.get(Calendar.YEAR),
                maxDate.value.get(Calendar.YEAR)
            ) {
                selectedDate.value.set(Calendar.YEAR, it)
                println(
                    "Selected date: ${selectedDate.value.get(Calendar.DAY_OF_MONTH)}.${
                        selectedDate.value.get(
                            Calendar.MONTH
                        )
                    }.${selectedDate.value.get(Calendar.YEAR)}"
                )
            }
        }
    }
}