package rehac.nick.portalcalculator

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme

const val KEY_LOCATION_INFO = "KEY_LOCATION_INFO"

class LocationListingDetailsActivity : ComponentActivity() {
    lateinit var viewModel: LocationListingDetailsActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LocationListingDetailsActivityViewModel::class.java]

        if(viewModel.locationInfo == null) viewModel.locationInfo = intent.getParcelableExtra(KEY_LOCATION_INFO, LocationOfInterest::class.java)!!

        setContent {
            PortalCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(viewModel.locationInfo!!, viewModel.finalizing)
                }
            }
        }

        if(viewModel.finalizing)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.locationInfo = finalizeLocationData(viewModel.locationInfo!!)
                viewModel.finalizing = false

                withContext(Dispatchers.Main) {
                    setContent {
                        PortalCalculatorTheme {
                            // A surface container using the 'background' color from the theme
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                MainContent(viewModel.locationInfo!!, viewModel.finalizing)
                            }
                        }
                    }
                }
            }
    }

    @Composable
    fun MainContent(location: LocationOfInterest, finalizing: Boolean) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                location.name,
                fontSize = 50.sp,
                lineHeight = 60.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(20.dp))
            if(location.thumbnail != null) {
                Image(
                    location.thumbnail.asImageBitmap(),
                    null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            } else {
                Image(
                    painterResource(id = R.drawable.noun_picture_rectangle),
                    null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.primary,
                        BlendMode.SrcIn
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                location.description,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(50.dp))
            val unknownOrLoading = if(finalizing) "LOADING" else "UNKNOWN"
            Text(
                "Galaxy Address: ${location.galaxyAddress?:unknownOrLoading}"
            )
            Text(
                "Portal Address: ${location.portalAddress?:unknownOrLoading}"
            )
            if(finalizing) {
                LinearProgressIndicator()
            }
            Spacer(Modifier.height(20.dp))
            location.portalAddress?.let{PortalAddressBox(portalAddress = it)}
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Preview(showBackground = true)
    @Composable
    fun MainContentPreview() {
        val locationInfo = LocationOfInterest(
            null,
            "",
            "NAME",
            "PAGE",
            "Description this is a description hell yeah wwooooooo",
            "0000:0000:0000:0000",
            "0123456789ef"
        )

        PortalCalculatorTheme {
            MainContent(locationInfo, true)
        }
    }
}

data class LocationListingDetailsActivityViewModel(
    var locationInfo: LocationOfInterest? = null,
    var finalizing: Boolean = true
) : ViewModel()
