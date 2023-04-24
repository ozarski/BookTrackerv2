package ozarskiapps.booktracker.mainActivity.statsTab

import android.content.Context
import android.widget.NumberPicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ozarskiapps.booktracker.database.GlobalStatsDBService
import ozarskiapps.booktracker.database.YearlyStatsDBService
import java.util.*

class YearStatsUI(private val context: Context) : StatsTabUI() {

    @Composable
    override fun GenerateLayout() {
        YearStatsLayout()
    }

    @Composable
    fun YearStatsLayout() {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {


            DateButton(Calendar.getInstance())

            val totalPages = YearlyStatsDBService(context).getTotalNumberOfPages()
            val totalBooks = YearlyStatsDBService(context).getTotalNumberOfBooks()
            val averagePagesPerDay = YearlyStatsDBService(context).getAveragePagesPerDay()
            val averagePagesPerBook = YearlyStatsDBService(context).getAverageNumberOfPagesPerBook()
            val averageDaysPerBook = YearlyStatsDBService(context).getAverageReadingTime()
            val averageBooksPerMonth = YearlyStatsDBService(context).getAverageBooksPerMonth()
            val averageBooksPerWeek = YearlyStatsDBService(context).getAverageBooksPerWeek()
            val monthWithMostBooksRead = YearlyStatsDBService(context).getMonthWithMostBooksRead()

            TotalPagesText(value = totalPages)

            StatRow(
                stat1Name = "books read",
                stat1Value = totalBooks.toString(),
                stat2Name = "pages per day",
                stat2Value = averagePagesPerDay.toString()
            )
            StatRow(
                stat1Name = "pages per book",
                stat1Value = averagePagesPerBook.toString(),
                stat2Name = "days per book",
                stat2Value = averageDaysPerBook.toString()
            )
            StatRow(
                stat1Name = "books per month",
                stat1Value = averageBooksPerMonth.toString(),
                stat2Name = "books per week",
                stat2Value = averageBooksPerWeek.toString()
            )
            StatRow(
                stat1Name = "month with most books",
                stat1Value = monthWithMostBooksRead,
                stat2Name = "",
                stat2Value = ""
            )
        }
    }

    @Composable
    private fun DateButton(calendar: Calendar){
        Button (onClick = {  /*TODO("Open year picker dialog")*/ },
            colors = ButtonDefaults.buttonColors( containerColor = Color.Transparent),
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(12.dp))
        ){
            val year = calendar.get(Calendar.YEAR).toString()
            Text(text = year, fontSize = 25.sp, color = Color.Black)
        }
    }

    @Preview
    @Composable
    fun YearStatsLayoutPreview() {
        YearStatsLayout()
    }
}