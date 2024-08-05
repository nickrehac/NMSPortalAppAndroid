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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            //retrieveBitmap("Galactic_Hub_Main_Emblem.png")
            retrievePageLocationTables("Bases")
        }
    }



    @Composable
    fun MainContent(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column(
            modifier = modifier
                .height(Dp(100f))
                .verticalScroll(rememberScrollState())
        ) {
            ImgTextButton(image = R.drawable.ic_launcher_background, text = "Bases") {
                context.startActivity(Intent(context, BasesOfInterestActivity::class.java))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainContentPreview() {
        MainContent()
    }
}

class LocationOfInterest(
    val thumbnail: Bitmap,
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

class LocationTable(val tableTitle: String, val entries: ArrayList<LocationOfInterest>) : Parcelable {
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

fun retrievePageLocationTables(pageName: String) : ArrayList<LocationTable> {
    var retval = ArrayList<LocationTable>()
    val urlConnection = URL("https://nmsgalactichub.miraheze.org/w/api.php?action=parse&page=$pageName&format=json").openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("User-Agent", APP_USER_AGENT)
    try {
        val istream = BufferedInputStream(urlConnection.inputStream)
        val pageJSON = JSONObject(InputStreamReader(istream).readText())
        val wikiPageDocument = Jsoup.parse(pageJSON.getJSONObject("parse").getJSONObject("text").getString("*"))
        wikiPageDocument.getElementsByClass("wikitable").forEach {
            val tableTitle = it.previousElementSibling()!!.child(0).text()
            val entries = ArrayList<LocationOfInterest>()
            it.child(0).children().forEach {
                it.children().forEach {
                    it.child(0).let {

                        val entryBitmap = loadBitmapFromURL("https:${it.child(0).child(0).child(0).attr("src")}")
                        val entryTitle = it.child(2).text()
                        val entryPage = it.child(2).attr("href").substring(6)
                        val description = it.text().substring(1+entryTitle.length)

                        entries.add(LocationOfInterest(
                            entryBitmap!!,
                            entryTitle,
                            entryPage,
                            description
                        ))

                    }
                }
            }
            retval.add(LocationTable(tableTitle, entries))
        }
    } finally {
        urlConnection.disconnect()
    }
    return retval
}

fun loadBitmapFromURL(url: String) : Bitmap? {
    var retval: Bitmap? = null
    val urlConnection = URL(url).openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("User-Agent", APP_USER_AGENT)
    try {
        retval = BitmapFactory.decodeStream(BufferedInputStream((urlConnection.inputStream)))
    } catch (e: Exception) {
        Log.d("loadBitmapFromURL", e.toString())
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
    }finally {
        urlConnectionImageMetadata.disconnect()
    }

    return retval
}