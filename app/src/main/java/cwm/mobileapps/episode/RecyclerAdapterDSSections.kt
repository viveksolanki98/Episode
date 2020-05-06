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
import org.json.JSONObject
import java.lang.Exception

class RecyclerAdapterDSSections(val sections : ArrayList<List<String>>, val showIDs: ArrayList<String> =  ArrayList<String>()) : RecyclerView.Adapter<RecyclerAdapterDSSections.ViewHolder>() {
    var listDataSet = JSONArray()
    var nestedFlag = true
    lateinit var viewAdapter : RecyclerAdapterDSLists

    override fun getItemCount() = sections.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.sectionTitleTXT.text = sections[position][0].split(" ").joinToString(" ") { it.capitalize() }.trimEnd()
        holder.sectionRV.layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
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
        if (section == "notStarted"){
            var counter = 0
            val objectDataSet = JSONObject()
            listDataSet = JSONArray()
            showIDs.forEach {
                APIhandler.trackitAPIAsync("https://api.trakt.tv/search/trakt/$it?type=show", fun(response: Response) {
                    counter++
                    val showData = JSONArray(response.body!!.string()).getJSONObject(0)
                    objectDataSet.put(it.toString(), showData)
                    //listDataSet.put(showData)
                    if (counter == showIDs.size){
                        listDataSet = JSONArray()
                        objectDataSet.keys().forEach {objKey ->
                            listDataSet.put(objectDataSet.getJSONObject(objKey))
                        }
                        nestedFlag = true
                        (holder?.itemView?.context as Activity?)?.runOnUiThread {
                            viewAdapter = RecyclerAdapterDSLists(listDataSet, nestedFlag)
                            viewAdapter.notifyDataSetChanged()
                            holder?.sectionRV?.adapter = viewAdapter
                        }
                    }
                })
            }

        }else {
            APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$section?extended=full", fun(response: Response) {
                val body = response.body!!.string()
                try {
                    listDataSet = JSONArray(body)
                } catch (e: Exception) {
                }
                nestedFlag = section != "popular"
                (holder?.itemView?.context as Activity?)?.runOnUiThread {
                    viewAdapter = RecyclerAdapterDSLists(listDataSet, nestedFlag)
                    viewAdapter.notifyDataSetChanged()
                    holder?.sectionRV?.adapter = viewAdapter
                }
            })
        }
    }
}