package rehac.nick.portalcalculator

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme

const val KEY_LOCATION_INFO = "KEY_LOCATION_INFO"

class LocationListingDetailsActivity : ComponentActivity() {
    lateinit var locationInfo: LocationOfInterest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationInfo = intent.getParcelableExtra(KEY_LOCATION_INFO, LocationOfInterest::class.java)!!
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
        CoroutineScope(Dispatchers.IO).launch {
            locationInfo = finalizeLocationData(locationInfo)
        }
    }

    @Composable
    fun MainContent() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                locationInfo.name,
                fontSize = 50.sp,
                lineHeight = 60.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if(locationInfo.thumbnail != null) {
                Image(
                    locationInfo.thumbnail!!.asImageBitmap(),
                    null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f)
                )
            } else {
                Image(
                    painterResource(id = R.drawable.noun_picture_rectangle),
                    null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.primary,
                        BlendMode.SrcIn
                    ),
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f)
                )
            }
            Text(
                locationInfo.description,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                "Galaxy Address: ${locationInfo.galaxyAddress?:"Unknown"}"
            )
            Text(
                "Portal Address: ${locationInfo.portalAddress?:"Unknown"}"
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        locationInfo = LocationOfInterest(
            null,
            "",
            "NAME",
            "PAGE",
            "Description this is a description hell yeah wwooooooo",
            "",
            ""
        )

        PortalCalculatorTheme {
            MainContent()
        }
    }
}

