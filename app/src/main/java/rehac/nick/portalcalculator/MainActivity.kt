package rehac.nick.portalcalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
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
    fun MainMenu(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column(
            modifier = modifier.fillMaxSize()
                .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints.copy(
                            minWidth = 100.dp.roundToPx(),
                            maxWidth = 800.dp.roundToPx()
                        ))
                    layout(placeable.width, placeable.height) {
                        placeable.place(0,0)
                    }
                }.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImgTextButton(
                image = R.drawable.noun_exchange,
                text = "Coordinate Conversion",
                modifier = Modifier.fillMaxWidth()
            ) {
                val coordinateConversionActivity = Intent(context, CoordinateConversionActivity::class.java)
                context.startActivity(coordinateConversionActivity)
            }
            ImgTextButton(
                image = R.drawable.noun_galaxy,
                text = "Glyph Crafter",
                modifier = Modifier.fillMaxWidth()
            ) {
                val pinpointerActivity = Intent(context, GlyphPinpointerActivity::class.java)
                context.startActivity(pinpointerActivity)
            }
            ImgTextButton(
                image = R.drawable.noun_location,
                text = "Addresses of Interest",
                modifier = Modifier.fillMaxWidth()
            ) {
                val addressesOfInterestActivity = Intent(context, AddressesOfInterestActivity::class.java)
                context.startActivity(addressesOfInterestActivity)
            }

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainMenuPreview() {
        MainMenu()
    }
}

@Composable
fun ImgTextButton(image: Int, text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(modifier = Modifier
        .padding(Dp(20f))
        .clickable { onClick() }
        .background(
            MaterialTheme.colorScheme.primary,
            RoundedCornerShape(10.dp)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.padding(15.dp)) {
            Image(
                painter = painterResource(id = image),
                contentDescription = text,
                modifier = modifier.size(Dp(150f)),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary, BlendMode.SrcIn)
            )
            Text(
                text = text,
                fontSize = TextUnit(25f, TextUnitType.Sp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}