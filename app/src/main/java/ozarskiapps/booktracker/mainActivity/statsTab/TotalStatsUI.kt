package ozarskiapps.booktracker.mainActivity.statsTab

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ozarskiapps.booktracker.database.GlobalStatsDBService

class TotalStatsUI(private val context: Context): StatsTabUI(){

    @Composable
    override fun GenerateLayout(){
        TotalStatsLayout()
    }
    @Composable
    fun TotalStatsLayout(){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {

            val totalPages = GlobalStatsDBService(context).getTotalNumberOfPages()
            val totalBooks = GlobalStatsDBService(context).getTotalNumberOfBooks()
            val averagePagesPerDay = GlobalStatsDBService(context).getAveragePagesPerDay()
            val averagePagesPerBook = GlobalStatsDBService(context).getAverageNumberOfPagesPerBook()
            val averageDaysPerBook = GlobalStatsDBService(context).getAverageReadingTime()
            val averageBooksPerMonth = GlobalStatsDBService(context).getAverageBooksPerMonth()
            val averageBooksPerWeek = GlobalStatsDBService(context).getAverageBooksPerWeek()
            val monthWithMostBooksRead = GlobalStatsDBService(context).getMonthWithMostBooksRead()
            val mostReadAuthor = GlobalStatsDBService(context).getMostReadAuthor()

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
                stat2Name = "most read author",
                stat2Value = mostReadAuthor
            )
        }
    }
}
@Preview
@Composable
private fun StatsUIPreview() {
    TotalStatsUI(LocalContext.current).GenerateLayout()
}
