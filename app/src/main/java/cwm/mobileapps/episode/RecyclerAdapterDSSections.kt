package cwm.mobileapps.episode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class RecyclerAdapterDSSections(val sections : ArrayList<List<String>>) : RecyclerView.Adapter<RecyclerAdapterDSSections.ViewHolder>() {

    override fun getItemCount() = sections.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.sectionTitleTXT.text = sections[position][0].split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
        holder.sectionRV?.layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
        launchDiscoverSection(holder, sections[position][1])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.discover_sections_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val sectionRV : RecyclerView = itemView.findViewById(R.id.section_rv)
        val sectionTitleTXT : TextView = itemView.findViewById(R.id.sectionTitle_txt)
    }

    fun launchDiscoverSection(holder : ViewHolder?, section : String?) {
        //image URL: https://image.tmdb.org/t/p/w500/lbIMe94gXNGBzlFACqbrUyEXpyN.jpg
        //var urlSTR = "https://api.themoviedb.org/3/tv/$section?api_key=9b05770b260d801f3b9e84fd281f2064&language=en-US&page=1"
        var urlSTR = "https://api.trakt.tv/shows/$section"

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
                println("FAIL API")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val result = JSONArray(body)
                println("API Search Success")

                val showNames = ArrayList<String>()
                val showIDs = ArrayList<String>()
                val showImageLocations = ArrayList<String>()
                for (i in 0 until result.length()) {
                    val item = result.getJSONObject(i)
                    var showData = if(section == "popular") item else item.getJSONObject("show")

                    val tvdbID = showData.getJSONObject("ids").getString("tvdb")
                    val imdbID = showData.getJSONObject("ids").getString("imdb")

                    var urlSTRImage = "http://webservice.fanart.tv/v3/tv/$tvdbID?api_key=cc52af8ac688a6c7a9a83e293624fe35"
                    val requestImage = Request.Builder().url(urlSTRImage).build()
                    val clientImage = OkHttpClient()
                    val responseImage = clientImage.newCall(requestImage).execute()
                    val bodyImage = responseImage.body!!.string()
                    val imageObj = JSONObject(bodyImage)
                    var imageLocation = try {
                        imageObj.getJSONArray("tvposter").getJSONObject(0).getString("url")
                    }catch (e : Exception){
                        "https://clipartart.com/images/vintage-movie-poster-clipart-2.jpg"
                    }

                    println("appdebug: imageLocation(tvdb): " + imageLocation)

                    showNames.add(showData.getString("title"))
                    showIDs.add(imdbID)
                    showImageLocations.add(imageLocation)
                }

                (holder?.itemView?.context as UserHomeActivity)?.runOnUiThread(Runnable { holder?.sectionRV?.adapter = RecyclerAdapterDSLists(showNames, showIDs, showImageLocations) })
            }
        })
    }
}