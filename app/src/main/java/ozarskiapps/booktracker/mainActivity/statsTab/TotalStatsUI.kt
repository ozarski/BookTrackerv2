package ozarskiapps.booktracker.mainActivity.statsTab

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ozarskiapps.booktracker.database.TotalStatsDBService
import ozarskiapps.booktracker.roundDouble

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

            val totalPages = TotalStatsDBService(context).getTotalNumberOfPages()
            val totalBooks = TotalStatsDBService(context).getTotalNumberOfBooks()
            val averagePagesPerDay = TotalStatsDBService(context).getAveragePagesPerDay()
            val averagePagesPerBook = TotalStatsDBService(context).getAverageNumberOfPagesPerBook()
            val averageDaysPerBook = TotalStatsDBService(context).getAverageReadingTime()
            val averageBooksPerMonth = TotalStatsDBService(context).getAverageBooksPerMonth()
            val averageBooksPerWeek = TotalStatsDBService(context).getAverageBooksPerWeek()
            val monthWithMostBooksRead = TotalStatsDBService(context).getMonthWithMostBooksRead()
            val mostReadAuthor = TotalStatsDBService(context).getMostReadAuthor()

            TotalPagesText(value = totalPages)

            StatRow(
                stat1Name = "books read",
                stat1Value = totalBooks.toString(),
                stat2Name = "pages per day",
                stat2Value = roundDouble(averagePagesPerDay, 10).toString()
            )
            StatRow(
                stat1Name = "pages per book",
                stat1Value = roundDouble(averagePagesPerBook, 10).toString(),
                stat2Name = "days per book",
                stat2Value = roundDouble(averageDaysPerBook, 10).toString()
            )
            StatRow(
                stat1Name = "books per month",
                stat1Value = roundDouble(averageBooksPerMonth, 10).toString(),
                stat2Name = "books per week",
                stat2Value = roundDouble(averageBooksPerWeek, 10).toString()
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
