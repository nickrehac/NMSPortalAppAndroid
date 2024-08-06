package rehac.nick.portalcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme

class CoordinateConversionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        setContent {
            PortalCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainInteraction()
                }
            }
        }
    }

    @Composable
    fun MainInteraction(modifier: Modifier = Modifier) {
        Text("HEO")
    }

    @Preview(showBackground = true)
    @Composable
    fun MainInteractionPreview() {
        MainInteraction()
    }
}