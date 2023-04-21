package ozarskiapps.booktracker.mainActivity.statsTab

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
import java.util.*

class YearStatsUI : StatsTabUI() {

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
            TotalPagesText(23456)

            StatRow(
                stat1Name = "books read",
                stat1Value = "8",
                stat2Name = "pages per day",
                stat2Value = "42"
            )
            StatRow(
                stat1Name = "pages per book",
                stat1Value = "315.1",
                stat2Name = "days per book",
                stat2Value = "7.5"
            )
            StatRow(
                stat1Name = "books will be read with current pace",
                stat1Value = "40",
                stat2Name = "books per month",
                stat2Value = "0.7"
            )
            StatRow(
                stat1Name = "books per week",
                stat1Value = "0.9",
                stat2Name = "year progress",
                stat2Value = "19.8%"
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