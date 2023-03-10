package ozarskiapps.booktracker.mainActivity

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

@Composable
fun LayoutMain(){
    LayoutMainTabs()
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LayoutMainTabs(){
    val tabIndex = remember { mutableStateOf(0) }
    val tabs = listOf("Books", "Stats")

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
                Tab(text = { Text(title)},
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
                0 -> LayoutMainBooks()
                1 -> LayoutMainStats()
            }
        }
    }
}