package rehac.nick.portalcalculator

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme

class AddressesOfInterestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
        setContent {
            PortalCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }



    @Composable
    fun MainContent(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column(
            modifier = modifier
                .height(Dp(100f))
                .verticalScroll(rememberScrollState())
                .layout{measurable, constraints ->
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
            ImgTextButton(image = R.drawable.noun_space_base, text = "Bases", modifier = modifier.fillMaxWidth()) {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java)
                    .putExtra(KEY_PAGE_TO_USE, "Bases")
                    .putExtra(KEY_PAGE_TITLE, "BASES"))
            }
            ImgTextButton(image = R.drawable.noun_space_colony, text = "Colonies", modifier = modifier.fillMaxWidth()) {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java)
                    .putExtra(KEY_PAGE_TO_USE, "Colony_Catalogue")
                    .putExtra(KEY_PAGE_TITLE, "COLONIES"))
            }
            ImgTextButton(image = R.drawable.noun_government_building, text = "Official Bodies", modifier = modifier.fillMaxWidth()) {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java)
                    .putExtra(KEY_PAGE_TO_USE, "Celestial_Bodies")
                    .putExtra(KEY_PAGE_TITLE, "OFFICIAL BODIES"))
            }
        }
    }

    @Preview(
        showBackground = true,
        showSystemUi = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES
        )
    @Composable
    fun MainContentPreview() {
        PortalCalculatorTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainContent()
            }
        }
    }
}