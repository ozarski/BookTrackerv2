package ozarskiapps.booktracker.mainActivity.statsTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import ozarskiapps.booktracker.mainActivity.LayoutMainBooks

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StatsTabbedLayout(){
    val tabIndex = remember { mutableStateOf(0) }
    val tabs = listOf("Total", "Year", "Month")

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top){
        TabRow(selectedTabIndex = tabIndex.value,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = Color.Green,
                )
            },
            backgroundColor = Color.White){
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex.value == index,
                    onClick = {
                        scope.launch{
                            pagerState.animateScrollToPage(index)
                        }
                        tabIndex.value = index
                    }
                )
            }
        }
        HorizontalPager(state = pagerState, count = tabs.size) {
            when(it){
                0 -> TotalStatsLayout()
                1 -> YearStatsLayout()
                2 -> MonthStatsLayout()
            }
        }
    }
}
