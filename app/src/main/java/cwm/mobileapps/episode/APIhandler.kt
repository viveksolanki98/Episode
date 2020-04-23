package cwm.mobileapps.episode

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_show_page.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode

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

            @RequiresApi(Build.VERSION_CODES.O)
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

    fun fanartAPISync(urlSTR: String): Response {
        val requestImage = Request.Builder().url(urlSTR).build()
        val clientImage = OkHttpClient()
        val res = clientImage.newCall(requestImage).execute()
        println("appdebug: API Sync: $urlSTR")
        return res
    }
}