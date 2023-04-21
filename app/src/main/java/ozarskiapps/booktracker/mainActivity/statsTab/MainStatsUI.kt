package ozarskiapps.booktracker.mainActivity.statsTab

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ozarskiapps.booktracker.R

class MainStatsUI(context: Context) {

    private val tabs = mutableListOf(
        context.getString(R.string.total_stats_tab_title),
        context.getString(R.string.year_stats_tab_title),
        context.getString(R.string.month_stats_tab_title)
    )

    @Composable
    fun GenerateLayout() {
        StatsTabbedLayout()
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun StatsTabbedLayout() {
        val tabIndex = remember { mutableStateOf(0) }

        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            TabRow(
                selectedTabIndex = tabIndex.value,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = Color.Green,
                    )
                },
                backgroundColor = Color.White
            ) {
                StatsTabs(tabIndex, scope, pagerState)
                StatsTabsPager(pagerState = pagerState)
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun StatsTabs(
        tabIndex: MutableState<Int>,
        scope: CoroutineScope,
        pagerState: PagerState
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(text = { Text(title) },
                selected = tabIndex.value == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    tabIndex.value = index
                }
            )
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun StatsTabsPager(pagerState: PagerState) {

        HorizontalPager(state = pagerState, count = tabs.size) {
            when (it) {
                0 -> TotalStatsUI().GenerateLayout()
                1 -> YearStatsUI().GenerateLayout()
                2 -> MonthStatsUI().GenerateLayout()
            }
        }
    }
}
