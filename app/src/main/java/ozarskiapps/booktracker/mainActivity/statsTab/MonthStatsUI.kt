package ozarskiapps.booktracker.mainActivity.statsTab

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.database.MonthlyStatsDBService
import java.text.SimpleDateFormat
import java.util.*

class MonthStatsUI(private val context: Context): StatsTabUI(){

    @Composable
    override fun GenerateLayout(){
        MonthStatsLayout()
    }
    @Composable
    private fun MonthStatsLayout() {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {

            DateButton(Calendar.getInstance())
            val totalPages = MonthlyStatsDBService(context).getTotalNumberOfPages()
            val totalBooks = MonthlyStatsDBService(context).getTotalNumberOfBooks()
            val averagePagesPerDay = MonthlyStatsDBService(context).getAveragePagesPerDay()
            val averagePagesPerBook = MonthlyStatsDBService(context).getAverageNumberOfPagesPerBook()
            val averageDaysPerBook = MonthlyStatsDBService(context).getAverageReadingTime()
            val averageBooksPerWeek = MonthlyStatsDBService(context).getAverageBooksPerWeek()

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
                stat2Name = "books per week",
                stat2Value = averageBooksPerWeek.toString(),
                stat1Name = "",
                stat1Value = ""
            )

        }
    }

    @Composable
    private fun DateButton(date: Calendar){
        Button (onClick = {  /*TODO("Open month picker dialog")*/ },
            colors = ButtonDefaults.buttonColors( containerColor = Color.Transparent),
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(12.dp))
        ){
            val sdf = SimpleDateFormat("MM.yyyy", Locale.ROOT)
            val dateText = sdf.format(date.time)
            Text(text = dateText, fontSize = 25.sp, color = Color.Black)
        }
    }

    @Composable
    @Preview
    fun MonthStatsLayoutPreview() {
        MonthStatsLayout()
    }
}