package ozarskiapps.booktracker.mainActivity.statsTab

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonthStatsLayout() {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {

        Button (onClick = {  /*TODO("Open month picker dialog")*/ },
            colors = ButtonDefaults.buttonColors( containerColor = Color.Transparent),
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(12.dp))
        ){
            Text(text = "03.2023", fontSize = 25.sp, color = Color.Black)
        }

        Text(
            text = "800",
            fontSize = 35.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = "pages read",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        StatRow(
            stat1Name = "books read",
            stat1Value = "1",
            stat2Name = "pages per day",
            stat2Value = "27"
        )
        StatRow(
            stat1Name = "pages per book",
            stat1Value = "800",
            stat2Name = "days per book",
            stat2Value = "29.0"
        )
        StatRow(
            stat1Name = "books per week",
            stat1Value = "0.7",
            stat2Name = "year progress",
            stat2Value = "19.7%"
        )

    }
}

@Composable
@Preview
fun MonthStatsLayoutPreview() {
    MonthStatsLayout()
}