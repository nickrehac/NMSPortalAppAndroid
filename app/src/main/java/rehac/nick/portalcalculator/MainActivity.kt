package rehac.nick.portalcalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PortalCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainMenu()
                }
            }
        }
    }

    @Composable
    fun ImgTextButton(image: Int, text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
        Box(modifier = Modifier
            .padding(Dp(20f))
            .clickable { onClick() }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = image), contentDescription = text, modifier = modifier.size(Dp(150f)))
                Text(text = text, fontSize = TextUnit(25f, TextUnitType.Sp))
            }
        }
    }

    @Composable
    fun MainMenu(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Coordinate Conversion") {
                val coordinateConversionActivity = Intent(context, CoordinateConversionActivity::class.java)
                context.startActivity(coordinateConversionActivity)
            }
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Glyph Pinpointer") {
                val pinpointerActivity = Intent(context, GlyphPinpointerActivity::class.java)
                context.startActivity(pinpointerActivity)
            }
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Addresses of Interest") {

            }

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainMenuPreview() {
        MainMenu()
    }
}