package ozarskiapps.booktracker.mainActivity.statsTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TotalStatsLayout(){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {

        Text(
            text = "32862",
            fontSize = 35.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "pages read",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        StatRow(
            stat1Name = "books read",
            stat1Value = "83",
            stat2Name = "pages per day",
            stat2Value = "54"
        )
        StatRow(
            stat1Name = "pages per book",
            stat1Value = "395.9",
            stat2Name = "days per book",
            stat2Value = "10.7"
        )
        StatRow(
            stat1Name = "books per month",
            stat1Value = "2.8",
            stat2Name = "books per week",
            stat2Value = "0.7"
        )
        StatRow(
            stat1Name = "books per year",
            stat1Value = "2.8",
            stat2Name = "max books per year",
            stat2Value = "0.7"
        )
    }
}

@Composable
fun StatRow(stat1Name: String, stat1Value: String, stat2Name: String, stat2Value: String){
    //Row with stat labels
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .weight(0.5f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stat1Value,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stat1Name,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(150.dp)
            )
        }
        //Row with stat values
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .weight(0.5f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stat2Value,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stat2Name,
                modifier = Modifier.width(150.dp),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun StatsUIPreview() {
    TotalStatsLayout()
}
