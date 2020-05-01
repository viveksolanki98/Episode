package cwm.mobileapps.episode

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
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
                val showData = if(section == "popular") item else item.getJSONObject("show")

                val imageLocation = APIhandler.imageFromID(showData.getJSONObject("ids"))

                println("appdebug: imageLocation(tvdb): " + imageLocation)

                val imdbID = showData.getJSONObject("ids").getString("imdb")
                showNames.add(showData.getString("title"))
                showIDs.add(imdbID)
                showImageLocations.add(imageLocation!!)
            }

            (holder?.itemView?.context as Activity?)?.runOnUiThread(Runnable {
                holder?.sectionRV?.adapter = RecyclerAdapterDSLists(showNames, showIDs, showImageLocations)
            })
        })
    }
}