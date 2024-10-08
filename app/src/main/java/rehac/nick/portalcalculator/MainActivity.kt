package rehac.nick.portalcalculator

import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
            modifier = modifier
                .fillMaxSize()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            minWidth = 100.dp.roundToPx(),
                            maxWidth = 800.dp.roundToPx()
                        )
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
                .width(IntrinsicSize.Max),
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
            Text(
                "ABOUT // CREDITS",
                Modifier.clickable {
                    context.startActivity(Intent(context, CreditsAboutActivity::class.java))
                },
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Composable
    fun MainMenuPreviewDay() {
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

    @Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun MainMenuPreviewNight() {
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

@Composable
fun AddressDisplayPanel(address: String) {
    Row() {
        for(glyph in address) {

        }
    }
}

val GLYPH_RESOURCES = arrayOf(
    R.drawable.glyph0,
    R.drawable.glyph1,
    R.drawable.glyph2,
    R.drawable.glyph3,
    R.drawable.glyph4,
    R.drawable.glyph5,
    R.drawable.glyph6,
    R.drawable.glyph7,
    R.drawable.glyph8,
    R.drawable.glyph9,
    R.drawable.glypha,
    R.drawable.glyphb,
    R.drawable.glyphc,
    R.drawable.glyphd,
    R.drawable.glyphe,
    R.drawable.glyphf,
)
@Composable
fun GlyphImage(glyph: Char, modifier: Modifier = Modifier) {
    val glyphPainter = if(glyph.isLetter() && glyph.uppercaseChar() < 'G') {
        painterResource(id = GLYPH_RESOURCES[10 + glyph.uppercaseChar().code - 'A'.code])
    } else if(glyph.isDigit()) {
        painterResource(id = GLYPH_RESOURCES[glyph.digitToInt()])
    } else {
        painterResource(id = R.drawable.noun_picture_rectangle)
    }
    Image(
        painter = glyphPainter,
        contentDescription = "Glyph $glyph",
        modifier = modifier,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
        contentScale = ContentScale.Fit
    )
}
@Composable
fun PortalAddressBox(portalAddress: String) {
    if (portalAddress.length == 12) {
        Box(
            modifier = Modifier
                .shadow(5.dp, RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                Modifier
                    .padding(5.dp)
                    .fillMaxSize()
            ) {
                for (glyph in portalAddress) {
                    GlyphImage(
                        glyph,
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}