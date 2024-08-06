package rehac.nick.portalcalculator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import android.util.JsonReader
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.select.NodeFilter
import org.w3c.dom.DOMImplementation
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import rehac.nick.portalcalculator.ImgTextButton

class AddressesOfInterestActivity : ComponentActivity() {

    var isLoading = true

    val basesOfInterest = ArrayList<LocationOfInterest>()
    val coloniesOfInterest = ArrayList<LocationOfInterest>()
    val hubsOfInterest = ArrayList<LocationOfInterest>()

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Bases") {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java)
                    .putExtra(KEY_PAGE_TO_USE, "Bases")
                    .putExtra(KEY_PAGE_TITLE, "BASES"))
            }
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Colonies") {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java)
                    .putExtra(KEY_PAGE_TO_USE, "Colony_Catalogue")
                    .putExtra(KEY_PAGE_TITLE, "COLONIES"))
            }
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Official Bodies") {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java)
                    .putExtra(KEY_PAGE_TO_USE, "Celestial_Bodies")
                    .putExtra(KEY_PAGE_TITLE, "OFFICIAL BODIES"))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainContentPreview() {
        MainContent()
    }
}