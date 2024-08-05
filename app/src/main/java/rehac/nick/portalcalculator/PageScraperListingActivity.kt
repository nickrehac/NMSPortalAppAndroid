package rehac.nick.portalcalculator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import rehac.nick.portalcalculator.ui.theme.PortalCalculatorTheme
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

const val KEY_PAGE_TO_USE = "KEY_PAGE_TO_USE"

class BasesOfInterestActivity : ComponentActivity() {
    var basesTablesList: ArrayList<LocationTable>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContentLoading()
        }
        val page = intent.getStringExtra("KEY_PAGE_TO_USE")!!
        CoroutineScope(Dispatchers.IO).launch {
            retrievePageLocationTables(page).onSuccess {
                basesTablesList = it
                withContext(Dispatchers.Main) {
                    setContent {
                        MainContent(wikiData = basesTablesList!!)
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
        PortalCalculatorTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(30.dp))
                    Text(
                        "BASES",
                        fontSize = 50.sp
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
                                    onClick = {},
                                    shape = MaterialTheme.shapes.extraSmall,
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if(entry.thumbnail == null) {
                                            Image(
                                                painterResource(R.drawable.ic_launcher_background),
                                                null
                                            )
                                        } else {
                                            Image(
                                                entry.thumbnail.asImageBitmap(),
                                                null,
                                                modifier = Modifier.size(120.dp),
                                                contentScale = ContentScale.FillHeight
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
                        error.message!!,
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
                            "<-Back"
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
                        Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888),
                        "Name 1",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888),
                        "Name 2",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888),
                        "Name 3",
                        "",
                        ""
                    )
                )
            ),
            LocationTable(
                "Section Name 2",
                arrayListOf(
                    LocationOfInterest(
                        Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888),
                        "Name 1",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888),
                        "Name 2",
                        "",
                        ""
                    ),
                    LocationOfInterest(
                        Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888),
                        "Name 3",
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


class LocationOfInterest(
    val thumbnail: Bitmap?,
    val name: String,
    val page: String,
    val description: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Bitmap::class.java.classLoader,Bitmap::class.java)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(thumbnail, 0)
        parcel.writeString(name)
        parcel.writeString(page)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationOfInterest> {
        override fun createFromParcel(parcel: Parcel): LocationOfInterest {
            return LocationOfInterest(parcel)
        }

        override fun newArray(size: Int): Array<LocationOfInterest?> {
            return arrayOfNulls(size)
        }
    }

}

class LocationTable(val tableTitle: String, val entries: ArrayList<LocationOfInterest>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readArrayList(null, LocationOfInterest::class.java) ?: ArrayList<LocationOfInterest>()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tableTitle)
        parcel.writeList(entries)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationTable> {
        override fun createFromParcel(parcel: Parcel): LocationTable {
            return LocationTable(parcel)
        }

        override fun newArray(size: Int): Array<LocationTable?> {
            return arrayOfNulls(size)
        }
    }
}

fun retrievePageLocationTables(pageName: String) : Result<ArrayList<LocationTable>> {
    val locationTableList = ArrayList<LocationTable>()
    var retval = Result.success(locationTableList)
    val urlConnection = URL("https://nmsgalactichub.miraheze.org/w/api.php?action=parse&page=$pageName&format=json").openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("User-Agent", APP_USER_AGENT)
    try {
        val istream = BufferedInputStream(urlConnection.inputStream)
        val pageJSON = JSONObject(InputStreamReader(istream).readText())
        urlConnection.disconnect()
        val wikiPageDocument = Jsoup.parse(pageJSON.getJSONObject("parse").getJSONObject("text").getString("*"))
        wikiPageDocument.getElementsByClass("wikitable").forEach {table ->
            val tableTitle = table.previousElementSibling()!!.child(0).text()
            val entries = ArrayList<LocationOfInterest>()
            table.child(0).children().forEach {row ->
                row.children().forEach {column ->
                    column.child(0).let {

                        val entryBitmap = loadBitmapFromURL("https:${it.child(0).child(0).child(0).attr("src")}")
                        val entryTitle = it.child(2).text()
                        val entryPage = it.child(2).attr("href").substring(6)
                        val description = it.text().substring(1+entryTitle.length)

                        if(entryBitmap == null) {
                            Log.d("","")
                        }

                        entries.add(LocationOfInterest(
                            entryBitmap,
                            entryTitle,
                            entryPage,
                            description
                        ))

                    }
                }
            }
            locationTableList.add(LocationTable(tableTitle, entries))
        }
    } catch(e: Exception) {
        urlConnection.disconnect()
        retval = Result.failure(e)
    }
    return retval
}

fun loadBitmapFromURL(url: String) : Bitmap? {
    var retval: Bitmap? = null
    val urlConnection = URL(url).openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("User-Agent", APP_USER_AGENT)
    try {
        retval = BitmapFactory.decodeStream(BufferedInputStream(urlConnection.inputStream))
    } catch (e: Exception) {
        Log.w("loadBitmapFromURL", e.toString())
    } finally {
        urlConnection.disconnect()
    }
    return retval
}

fun retrieveBitmap(imageName: String) : Bitmap? {
    var retval : Bitmap? = null

    val urlConnectionImageMetadata = URL("https://nmsgalactichub.miraheze.org/w/api.php?action=query&prop=imageinfo&titles=$imageName&iiprop=url&format=json").openConnection() as HttpsURLConnection
    urlConnectionImageMetadata.setRequestProperty("User-Agent", APP_USER_AGENT)
    try {
        var istream = BufferedInputStream(urlConnectionImageMetadata.inputStream)
        val imageDescriptor = JSONObject(InputStreamReader(istream).readText())
        urlConnectionImageMetadata.disconnect()
        val urlConnectionImage = URL(imageDescriptor.getJSONObject("query")
            .getJSONObject("pages")
            .run{ getJSONObject(keys().next())}
            .getJSONArray("imageinfo")
            .getJSONObject(0)
            .getString("url")).openConnection() as HttpURLConnection
        urlConnectionImage.setRequestProperty("User-Agent", APP_USER_AGENT)
        try {
            istream = BufferedInputStream(urlConnectionImage.inputStream)
            retval = BitmapFactory.decodeStream(istream)
        } catch(e: Exception) {
            Log.e("retrieveImage", e.toString())
        } finally {
            urlConnectionImage.disconnect()
        }
    } catch(e: Exception) {
        Log.e("retrieveImage", e.toString())
        Log.d("retrieveImage", "response code: ${urlConnectionImageMetadata.responseCode}")
        Log.d("retrieveImage", "response message: ${urlConnectionImageMetadata.responseMessage}")
    }

    return retval
}