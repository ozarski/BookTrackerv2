package ozarskiapps.booktracker.mainActivity.statsTab

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

abstract class StatsTabUI {

    @Composable
    abstract fun GenerateLayout()

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

    @Composable
    fun TotalPagesText(value: Long){

        Text(
            text = value.toString(),
            fontSize = 35.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "pages read",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}