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
        APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$section", fun(response : Response){
            val body = response.body!!.string()
            var result = JSONArray()
            try {
                result = JSONArray(body)
            }catch (e :Exception){}
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
                val imageObj = JSONObject(APIhandler.fanartAPISync(urlSTRImage).body!!.string())
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
        })
    }
}