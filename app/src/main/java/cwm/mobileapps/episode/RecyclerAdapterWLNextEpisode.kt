package cwm.mobileapps.episode

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapterWLNextEpisode(val episodeIDs : ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapterWLNextEpisode.ViewHolder>() {

    override fun getItemCount() = episodeIDs.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        APIhandler.trackitAPIAsync("https://api.trakt.tv/search/imdb/${episodeIDs[position]}?extended=full", fun (data : Response){
            var dataObj = JSONArray(data.body!!.string()).getJSONObject(0)
            val showTitle = dataObj.getJSONObject("show").getString("title")
            val episodeTitle = dataObj.getJSONObject("episode").getString("title")
            val season = dataObj.getJSONObject("episode").getInt("season")
            val episode = dataObj.getJSONObject("episode").getInt("number")
            val airDateString =  dataObj.getJSONObject("episode").getString("first_aired")

            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val outputFormatter = DateTimeFormatter.ofPattern("d LLL yyyy", Locale.ENGLISH)
            val date = LocalDate.parse(airDateString, inputFormatter)
            val airDateFormatted: String = outputFormatter.format(date)


            val tvdbID = dataObj.getJSONObject("show").getJSONObject("ids").getInt("tvdb")
            var urlSTRImage = "http://webservice.fanart.tv/v3/tv/$tvdbID?api_key=cc52af8ac688a6c7a9a83e293624fe35"
            val imageObj = JSONObject(APIhandler.fanartAPISync(urlSTRImage).body!!.string())
            var imageLocation = try {
                imageObj.getJSONArray("tvposter").getJSONObject(0).getString("url")
            }catch (e : Exception){
                "https://clipartart.com/images/vintage-movie-poster-clipart-2.jpg"
            }

            (holder?.itemView?.context as UserHomeActivity)?.runOnUiThread(Runnable {
                holder.showTitleTXT.text = showTitle
                holder.episodeTitleTXT.text = episodeTitle
                holder.episodeDetailsTXT.text ="Season $season, Episode $episode, $airDateFormatted"
                Glide.with(holder?.itemView?.context as UserHomeActivity).load(imageLocation).into(holder.showPosterIV)

                holder.showPosterIV.setOnClickListener {
                    val intentToShowPageActivity = Intent(holder.itemView.context, ShowPageActivity::class.java)
                    intentToShowPageActivity.putExtra("show_title", showTitle)
                    intentToShowPageActivity.putExtra("show_id", dataObj.getJSONObject("show").getJSONObject("ids").getString("imdb"))
                    intentToShowPageActivity.putExtra("show_poster", imageLocation)

                    holder.itemView.context.startActivity(intentToShowPageActivity)
                }
            })
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.watch_list_next_episode_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT : TextView = itemView.findViewById(R.id.showTitleInCard_txt)
        val episodeTitleTXT : TextView = itemView.findViewById(R.id.episodeTitle_txt)
        val episodeDetailsTXT : TextView = itemView.findViewById(R.id.episodeDetails_txt)
        val showPosterIV : ImageView = itemView.findViewById(R.id.showPoster_iv)
    }


}