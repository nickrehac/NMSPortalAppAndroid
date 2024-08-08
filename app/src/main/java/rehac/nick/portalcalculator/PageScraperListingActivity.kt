package rehac.nick.portalcalculator

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme
import java.net.UnknownHostException

const val KEY_PAGE_TO_USE = "KEY_PAGE_TO_USE"
const val KEY_PAGE_TITLE = "KEY_PAGE_TITLE"

class PageScraperListingActivity : ComponentActivity() {

    private lateinit var viewModel: PageScraperListingActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[PageScraperListingActivityViewModel::class.java]
        super.onCreate(savedInstanceState)
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        if(viewModel.basesTablesList != null) {
            setContent { MainContent(viewModel.basesTablesList!!) }
            return
        }

        setContent {
            MainContentLoading()
        }

        val page = intent.getStringExtra(KEY_PAGE_TO_USE)!!
        viewModel.pageTitle = intent.getStringExtra(KEY_PAGE_TITLE)!!
        CoroutineScope(Dispatchers.IO).launch {
            if(page == "Colony_Catalogue") {
                retrievePageLocationTablesType2(page)
            } else {
                retrievePageLocationTablesType1(page)
            }.onSuccess {
                viewModel.basesTablesList = it
                withContext(Dispatchers.Main) {
                    setContent {
                        MainContent(wikiData = viewModel.basesTablesList!!)
                    }
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    setContent {
                        MainContentFailed(error = it)
                    }
                }
            }
        }
    }

    @Composable
    fun MainContentLoading() {
        PortalCalculatorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Loading", fontSize = 40f.sp)
                    Spacer(Modifier.height(Dp(100f)))
                    LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(Dp(100f)))
                }
            }
        }

    }

    @Composable
    fun MainContent(wikiData: ArrayList<LocationTable>) {
        val context = LocalContext.current
        PortalCalculatorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(30.dp))
                    Text(
                        viewModel.pageTitle,
                        fontSize = 50.sp,
                        lineHeight = 50.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState(0)),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (table in wikiData) {
                            Text(
                                table.tableTitle,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                            for (entry in table.entries) {
                                Button(
                                    onClick = {
                                              context.startActivity(Intent(context, LocationListingDetailsActivity::class.java)
                                                  .putExtra(KEY_LOCATION_INFO, entry))
                                    },
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        if(entry.thumbnail == null) {
                                            Image(
                                                painterResource(R.drawable.noun_picture_rectangle),
                                                null,
                                                modifier = Modifier
                                                    .height(80.dp)
                                                    .width(140.dp)
                                                    .padding(top = 20.dp)
                                                    .scale(2.3f),
                                                contentScale = ContentScale.Fit
                                            )
                                        } else {
                                            Image(
                                                entry.thumbnail.asImageBitmap(),
                                                null,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .height(80.dp)
                                                    .width(140.dp),
                                                contentScale = ContentScale.Fit
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(20.dp))
                                        Text(
                                            text = entry.name,
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun MainContentFailed(error: Throwable) {
        PortalCalculatorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "ERROR", color = androidx.compose.ui.graphics.Color.Red,
                        fontSize = 40f.sp
                    )
                    Text(
                        if(error is UnknownHostException)
                            "Not Connected to Internet.\nPlease Check Your Connection."
                        else error.message!!,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(Dp(30f)),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Button(
                        onClick = { finish() },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "<-Back",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun MainContentPreview() {
        MainContent(arrayListOf(
            LocationTable(
                "Section Name 1",
                arrayListOf(
                    LocationOfInterest(
                        null,
                        null,
                        "Name 1",
                        "",
                        "",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        null,
                        null,
                        "Name 2",
                        "",
                        "",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        null,
                        null,
                        "Name 3",
                        "",
                        "",
                        "",
                        ""
                    )
                )
            ),
            LocationTable(
                "Section Name 2",
                arrayListOf(
                    LocationOfInterest(
                        null,
                        null,
                        "Name 1",
                        "",
                        "",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        null,
                        null,
                        "Name 2",
                        "",
                        "",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        null,
                        null,
                        "Name 3",
                        "",
                        "",
                        "",
                        ""
                    )
                )
            )
        ))
    }

    @Preview
    @Composable
    fun MainContentLoadingPreview() {
        MainContentLoading()
    }

    @Preview
    @Composable
    fun MainContentFailedPreview() {
        MainContentFailed(error = java.lang.Exception("Sample Error Message very very bad blah blah lorem ipsum etc etc"))
    }
}

data class PageScraperListingActivityViewModel(
    var basesTablesList: ArrayList<LocationTable>? = null,
    var pageTitle: String = ""
) : ViewModel()


