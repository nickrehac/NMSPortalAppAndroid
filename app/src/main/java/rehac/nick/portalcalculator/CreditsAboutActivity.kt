package rehac.nick.portalcalculator

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme

class CreditsAboutActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }

    @Composable
    fun MainContent() {
        PortalCalculatorTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column (
                    Modifier.padding(30.dp)
                ) {
                    Text(
                        "CREDITS // ABOUT",
                        fontSize = 35.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(
                        Modifier.height(50.dp)
                    )
                    Text(
                        "Icons From The Noun Project\nwww.thenounproject.com\n(CC Attribution 3.0):",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    arrayOf(
                        "\"Planet\" by Cahya Kurniawan (unicon)",
                        "\"Space Base\" by Bacontaco",
                        "\"Space Colony\" by Karyative",
                        "\"Picture Rectangle\" by Brad (pixelpusher)",
                        "\"Exchange\" by Gregor Cresnar (grega.cresnar)",
                        "\"Galaxy\" by Shashank Singh (rshashank19)",
                        "\"Government Building\" by Uma (musapiih12)",
                        "\"Map Marker\" by Noun Project"

                    ).forEach {
                        Text(
                            it,
                            Modifier.padding(start = 20.dp, top = 15.dp)
                        )
                    }
                    Spacer(
                        Modifier.height(50.dp)
                    )
                    Text(
                        "Location information taken from the No Man's Sky Galactic Hub Wiki page at www.nmsgalactichub.miraheze.org\n\nThanks to Miraheze support for enabling this project",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    @Preview(
        showBackground = true,
        uiMode = Configuration.UI_MODE_NIGHT_YES
    )
    @Composable
    fun MainContentPreview() = MainContent()
}