package cwm.mobileapps.episode

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.episode_rv_card.view.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class RecyclerAdapterEpisodeCard(val episodeIDs : ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapterEpisodeCard.ViewHolder>() {
    var userID : String? = ""

    override fun getItemCount() = episodeIDs.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user ID from shared preferences
        val myPref: SharedPreferences = holder.itemView.context!!.getSharedPreferences("Episode_pref",
            Context.MODE_PRIVATE
        )
        userID = myPref?.getString("user_id_google", "")

        (holder.itemView.context as Activity?)?.runOnUiThread{
            FBDBhandler.query("UserID_EpisodeID", "${userID}_${episodeIDs[position]}", fun(episodeCheckData :DataSnapshot?){
                if (episodeCheckData?.getValue() != null){
                    holder.watchedToggleSWT.isChecked = true
                }
            })
        }

        if (episodeIDs[position] != "null") {
            APIhandler.trackitAPIAsync(
                "https://api.trakt.tv/search/imdb/${episodeIDs[position]}?extended=full",
                fun(data: Response) {
                    val dataObj = JSONArray(data.body!!.string()).getJSONObject(0)
                    val showID = dataObj.getJSONObject("show").getJSONObject("ids").getString("imdb")
                    val showTitle = dataObj.getJSONObject("show").getString("title")
                    val episodeTitle = dataObj.getJSONObject("episode").getString("title")
                    val season = dataObj.getJSONObject("episode").getInt("season")
                    val episode = dataObj.getJSONObject("episode").getInt("number")
                    val airDateString = dataObj.getJSONObject("episode").getString("first_aired")

                    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                    val outputFormatter = DateTimeFormatter.ofPattern("d LLL yyyy", Locale.ENGLISH)
                    val date = LocalDate.parse(airDateString, inputFormatter)
                    val airDateFormatted: String = outputFormatter.format(date)

                    val imageLocation = APIhandler.imageFromID(dataObj.getJSONObject("show").getJSONObject("ids"))


                    //println("appdebug: recyclerAdapterEpisodeCard: image location: $imageLocation")
                    (holder.itemView.context as Activity?)?.runOnUiThread(Runnable {
                        holder.showTitleTXT.text = showTitle
                        holder.episodeTitleTXT.text = episodeTitle
                        holder.episodeDetailsTXT.text = "Season $season, Episode $episode, $airDateFormatted"
                        if (!(holder.itemView.context as Activity).isFinishing) {
                            Glide.with(holder.itemView.context as Activity).load(imageLocation).into(holder.showPosterIV)
                        }

                        holder.showPosterIV.setOnClickListener {
                            val intentToShowPageActivity =
                                Intent(holder.itemView.context, ShowPageActivity::class.java)
                            intentToShowPageActivity.putExtra("show_title", showTitle)
                            intentToShowPageActivity.putExtra("show_id", showID)
                            intentToShowPageActivity.putExtra("show_poster", imageLocation)

                            holder.itemView.context.startActivity(intentToShowPageActivity)
                        }

                        holder.watchedToggleSWT.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                //FBDBhandler.addRecord(episodeIDs[position], showID,userID!!)
                                //Toast.makeText(holder.itemView.context,"Show Marked Watched!", Toast.LENGTH_SHORT).show()
                                markEpisodeAsWatched(showID, episodeIDs[position], holder.itemView.context)
                            } else {
                                FBDBhandler.deleteRecord("UserID_EpisodeID", "${userID}_${episodeIDs[position]}",
                                    fun() {
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Show Marked Not Watched",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    fun() {})
                            }
                        }

                    })
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.episode_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT : TextView = itemView.findViewById(R.id.showTitleInCard_txt)
        val episodeTitleTXT : TextView = itemView.findViewById(R.id.episodeTitle_txt)
        val episodeDetailsTXT : TextView = itemView.findViewById(R.id.episodeDetails_txt)
        val showPosterIV : ImageView = itemView.findViewById(R.id.showPoster_iv)
        val watchedToggleSWT : Switch = itemView.findViewById(R.id.watchedToggle_swt)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder){

        APIhandler.trackitAPIAsync("https://api.trakt.tv/search/imdb/${episodeIDs[viewHolder.adapterPosition]}",
            fun(apiData : Response){
                val dataObj = JSONArray(apiData.body!!.string()).getJSONObject(0)
                val showID = dataObj.getJSONObject("show").getJSONObject("ids").getString("imdb")

                markEpisodeAsWatched(showID, episodeIDs[viewHolder.adapterPosition], viewHolder.itemView.context)
                episodeIDs.removeAt(viewHolder.adapterPosition)
                (viewHolder.itemView.context as Activity?)?.runOnUiThread {
                    notifyItemRemoved(viewHolder.adapterPosition)
                }
            })
    }

    private fun markEpisodeAsWatched(showID : String, episodeID : String, context : Context){
        FBDBhandler.addRecord(episodeID, showID, userID!!)
        (context as Activity?)?.runOnUiThread {
            Toast.makeText(context, "Show Marked Watched!", Toast.LENGTH_SHORT).show()
        }
    }


}