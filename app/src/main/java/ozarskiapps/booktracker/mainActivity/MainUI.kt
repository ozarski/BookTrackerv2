package ozarskiapps.booktracker.mainActivity

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
import ozarskiapps.booktracker.mainActivity.statsTab.MainStatsUI


class MainUI(val context: Context) {

    private val tabs = listOf(
        context.getString(R.string.main_tabs_book_list_title),
        context.getString(R.string.main_tabs_stats_title)
    )

    @Composable
    fun GenerateLayout() {
        LayoutMain()
    }
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun LayoutMain() {
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
                MainTabs(tabIndex, scope, pagerState)
            }
            MainTabsPager(pagerState = pagerState)
        }


    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun MainTabs(tabIndex: MutableState<Int>, scope: CoroutineScope, pagerState: PagerState) {
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
    private fun MainTabsPager(pagerState: PagerState){
        HorizontalPager(state = pagerState, count = tabs.size) {
            when (it) {
                0 -> MainBooksUI(context).GenerateLayout()
                1 -> MainStatsUI(context).GenerateLayout()
            }
        }
    }
}
