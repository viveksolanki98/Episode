package cwm.mobileapps.episode

import android.os.Build
import androidx.annotation.RequiresApi
import com.uwetrottmann.thetvdb.TheTvdb
import com.uwetrottmann.thetvdb.entities.Series
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object APIhandler {
    //API Documentation
    //https://trakt.docs.apiary.io/ -> https://trakt.tv/
    //https://fanarttv.docs.apiary.io/ -> https://fanart.tv/

    fun trackitAPIAsync(urlSTR: String, callback : (Response) -> Unit){

        val request = Request
            .Builder()
            .url(urlSTR)
            .addHeader("Content-Type","application/json")
            .addHeader("trakt-api-version","2")
            .addHeader("trakt-api-key","60208da48cb89f83f54f9686b0027df865b8aa8e51d2af64e7e4429b2cac7b28")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("appdebug: API Fail: $urlSTR")
            }

            //@RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                println("appdebug: API Success: $urlSTR")
                //var dataString = data.body!!.string()
                callback(response)
            }
        })
    }

    fun trackitAPISync(urlSTR: String): Response {
        val requestImage = Request.Builder().url(urlSTR)
            .addHeader("Content-Type","application/json")
            .addHeader("trakt-api-version","2")
            .addHeader("trakt-api-key","60208da48cb89f83f54f9686b0027df865b8aa8e51d2af64e7e4429b2cac7b28")
            .build()
        val clientImage = OkHttpClient()
        val res = clientImage.newCall(requestImage).execute()
        println("appdebug: API Sync: $urlSTR")
        return res
    }

    fun async(urlSTR: String, callback : (Response) -> Unit){

        val request = Request.Builder().url(urlSTR).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("appdebug: API Fail: $urlSTR")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                println("appdebug: API Success: $urlSTR")
                //var dataString = data.body!!.string()
                callback(response)
            }
        })
    }

    fun sync(urlSTR: String): Response? {
        val requestImage = Request.Builder().url(urlSTR).build()
        val clientImage = OkHttpClient()
        val res = clientImage.newCall(requestImage).execute()
        println("appdebug: API Sync: response code ${res.code}, URL $urlSTR")
        return if(res.code == 200) res else null
    }

    fun imageFromID(IDs: JSONObject): String? {
        val imageLocation: String?
        val defaultImage = "https://clipartart.com/images/vintage-movie-poster-clipart-2.jpg"

        val tmdbAPIResponse = sync("https://api.themoviedb.org/3/tv/${IDs.getString("tmdb")}/images?api_key=9b05770b260d801f3b9e84fd281f2064")
        if(tmdbAPIResponse?.code == 200) {
            imageLocation = try {
                "https://image.tmdb.org/t/p/w500/" + JSONObject(tmdbAPIResponse.body!!.string()).getJSONArray("posters").getJSONObject(0).getString("file_path")
            } catch (e: Exception) {
                defaultImage
            }
        }else{
            val fanartAPIResponse = sync("http://webservice.fanart.tv/v3/tv/${IDs.getString("tvdb")}?api_key=cc52af8ac688a6c7a9a83e293624fe35")
            imageLocation = if(fanartAPIResponse?.code == 200) {
                try {
                    JSONObject(fanartAPIResponse.body!!.string()).getJSONArray("tvposter").getJSONObject(0).getString("url")
                } catch (e: Exception) {
                    defaultImage
                }
            }else{
                defaultImage
            }
        }

        return imageLocation
    }

    fun theTVDBAPI(id : Int): Series? {
        //https://github.com/UweTrottmann/thetvdb-java
        val apiCall = TheTvdb("10ca5db52b5fce407118c1b70a16e0ec")
        val apiResponse = apiCall.series()
            .series(id, "en")
            .execute()
        val apiData : Series?
        if (apiResponse.code() == 200){
            println("appdebug: APIhandler: theTVDBAPI: api SUCCESS $id")
            apiData = apiResponse.body()?.data
            apiData?.banner = "https://artworks.thetvdb.com/banners/" + apiData?.banner
            apiData?.fanart = "https://artworks.thetvdb.com/banners/" + apiData?.fanart
            apiData?.poster = "https://artworks.thetvdb.com/banners/" + apiData?.poster
        }else{
            println("appdebug: APIhandler: theTVDBAPI: api FAIL $id")
            apiData = null
        }
        return apiData
    }
}