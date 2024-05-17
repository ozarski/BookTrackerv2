package ozarskiapps.booktracker.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ozarskiapps.booktracker.database.BookDBService
import ozarskiapps.booktracker.database.DBService
import ozarskiapps.booktracker.database.MockData
import ozarskiapps.booktracker.database.TagDBService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DBService(this).dropDatabase()
        //MockData(this).generateMockData()

        setContent {
            MainUI(this).GenerateLayout()
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MainUI(LocalContext.current).GenerateLayout()
    }
}
