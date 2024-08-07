package rehac.nick.portalcalculator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LocationOfInterest(
    val thumbnail: Bitmap?,
    val fullImageName: String?,
    val name: String,
    val page: String?,
    val description: String,
    val galaxyAddress: String?,
    val portalAddress: String?
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Bitmap::class.java.classLoader, Bitmap::class.java),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(thumbnail, 0)
        parcel.writeString(fullImageName)
        parcel.writeString(name)
        parcel.writeString(page)
        parcel.writeString(description)
        parcel.writeString(galaxyAddress)
        parcel.writeString(portalAddress)
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

fun finalizeLocationData(location: LocationOfInterest): LocationOfInterest {

    if(location.page == null) return location

    val pageJSON = getPageJSON(location.page).getOrElse { return location }
        .getJSONObject("parse")

    var thumbnail: Bitmap? = location.fullImageName?.let { retrieveBitmap(it) }

    val wikiData = Jsoup.parse(pageJSON.getJSONObject("text").getString("*"))



    return LocationOfInterest(
        thumbnail ?: location.thumbnail,
        location.fullImageName,
        location.name,
        location.page,
        location.description,
        location.galaxyAddress,
        location.portalAddress
    )
}

class LocationTable(val tableTitle: String, val entries: ArrayList<LocationOfInterest>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readArrayList(null, LocationOfInterest::class.java) ?: ArrayList<LocationOfInterest>()
    )

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

fun getPageJSON(pageName: String) : Result<JSONObject> {
    var retval: Result<JSONObject>
    val urlConnection = URL("https://nmsgalactichub.miraheze.org/w/api.php?action=parse&page=$pageName&format=json").openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("User-Agent", APP_USER_AGENT)
    try {
        val istream = BufferedInputStream(urlConnection.inputStream)
        retval = Result.success(JSONObject(InputStreamReader(istream).readText()))
    } catch (e: Exception) {
        retval = Result.failure(e)
        urlConnection.disconnect()
    }
    return retval
}

fun retrievePageLocationTablesType1(pageName: String) : Result<ArrayList<LocationTable>> {
    val locationTableList = ArrayList<LocationTable>()
    var retval = Result.success(locationTableList)
    val pageJSON = getPageJSON(pageName).getOrElse { return Result.failure(it) }
    try {
        val wikiPageDocument = Jsoup.parse(pageJSON.getJSONObject("parse").getJSONObject("text").getString("*"))
        wikiPageDocument.getElementsByClass("wikitable").forEach {table ->
            var tableTitleCursor = table.previousElementSibling()!!
            while(true) {
                if(tableTitleCursor.firstElementChild()?.hasClass("mw-headline") == true) {
                    tableTitleCursor = tableTitleCursor.firstElementChild()!!
                    break
                }
                if(tableTitleCursor.firstElementChild()?.nextElementSibling()?.hasClass("mw-headline") == true){
                    tableTitleCursor = tableTitleCursor.firstElementChild()!!.nextElementSibling()!!
                    break
                }
                tableTitleCursor = tableTitleCursor.previousElementSibling()!!
            }

            val tableTitle = tableTitleCursor.text()

            val entries = ArrayList<LocationOfInterest>()
            table.child(0).children().forEach {row ->
                row.children().forEach {column ->
                    column.child(0).let {

                        val entryBitmap = loadBitmapFromURL("https:${it.firstElementChild()?.firstElementChild()?.firstElementChild()?.attr("src")}")
                        val entryFullImageName = it.firstElementChild()?.firstElementChild()?.attr("href")?.substring(6)
                        val entryTitle = it.child(2).text()
                        val entryPage = it.child(2).attr("href").substring(6)
                        val description = it.text().substring(1+entryTitle.length)

                        entries.add(LocationOfInterest(
                            entryBitmap,
                            entryFullImageName,
                            entryTitle,
                            entryPage,
                            description,
                            null,
                            null
                        ))

                    }
                }
            }
            locationTableList.add(LocationTable(tableTitle, entries))
        }
    } catch(e: Exception) {
        retval = Result.failure(e)
    }
    return retval
}

fun retrievePageLocationTablesType2(pageName: String) : Result<ArrayList<LocationTable>> {
    val locationTableList = ArrayList<LocationTable>()
    var retval = Result.success(locationTableList)
    val pageJSON = getPageJSON(pageName).getOrElse { return Result.failure(it) }
    try {
        val wikiPageDocument = Jsoup.parse(pageJSON.getJSONObject("parse").getJSONObject("text").getString("*"))
        wikiPageDocument.getElementsByClass("wikitable").forEach {table ->
            var tableTitleCursor = table.previousElementSibling()!!
            while(tableTitleCursor.tagName() != "h2" && tableTitleCursor.tagName() != "h4") tableTitleCursor = tableTitleCursor.previousElementSibling()!!

            val tableTitle = (tableTitleCursor.firstElementChild()?:tableTitleCursor).text()

            val entries = ArrayList<LocationOfInterest>()
            table.child(0).children().drop(1).forEach {row ->
                val entryBitmap = loadBitmapFromURL("https:${row.firstElementChild()?.firstElementChild()?.firstElementChild()?.firstElementChild()?.attr("src")}")
                val fullImageName = row.firstElementChild()?.firstElementChild()?.firstElementChild()?.attr("href")?.substring(6)
                val entryTitle = row.child(1).child(0).text()
                val entryPage = row.child(1).firstElementChild()?.attr("href")?.ifEmpty { null }?.substring(6)
                var portalAddress = ""
                var galaxyAddress: String? = null
                var descriptionBuilder = ""
                var descriptionCursor = row.child(3).firstChild()

                while(descriptionCursor != null) {
                    if(descriptionCursor is Element) {
                        if(descriptionCursor.hasClass("glyphfont small-glyph")) {

                            portalAddress = descriptionCursor.text()

                            descriptionCursor = descriptionCursor.previousSibling()!!.previousSibling()!!

                            galaxyAddress = (descriptionCursor as TextNode).text()

                            descriptionBuilder = descriptionBuilder.substring(0, descriptionBuilder.length - galaxyAddress.length)
                            break
                        }
                        descriptionBuilder += descriptionCursor.text()
                    }
                    else if(descriptionCursor is TextNode){
                        descriptionBuilder += descriptionCursor.text()
                    }
                    descriptionCursor = descriptionCursor.nextSibling()
                }

                entries.add(LocationOfInterest(
                    entryBitmap,
                    fullImageName,
                    entryTitle,
                    entryPage,
                    descriptionBuilder,
                    galaxyAddress,
                    portalAddress
                ))
            }
            locationTableList.add(LocationTable(tableTitle, entries))
        }
    } catch(e: Exception) {
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