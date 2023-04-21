package ozarskiapps.booktracker.addActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class AddActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            AddActivityUI(this).GenerateLayout()
        }
    }
}