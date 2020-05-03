package cwm.mobileapps.episode

import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_show_tracking_sp.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.math.RoundingMode

/**
 * A simple [Fragment] subclass.
 */
class ShowTrackingSPFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_tracking_sp, container, false)

        val showID = activity?.intent?.extras?.getString("show_id")


        val IMDbShowPageBTN : Button? = view?.findViewById(R.id.IMDbShowPage_btn)
        IMDbShowPageBTN?.setOnClickListener {
            //https://developer.android.com/training/basics/intents/sending#kotlin
            // Build the intent
            val imdbPage = Uri.parse("imdb:///title/$showID")
            val imdbIntent = Intent(Intent.ACTION_VIEW, imdbPage)

            // Verify it resolves
            val activities: List<ResolveInfo>? = activity?.packageManager?.queryIntentActivities(imdbIntent, 0)
            // Start an activity if it's safe
            if (activities?.isNotEmpty()!!) {
                startActivity(imdbIntent)
            }
        }

        APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$showID?extended=full", fun(response : Response){
            val showDataObj = JSONObject(response.body!!.string())
            val showStartYear = showDataObj.getString("first_aired").split("-")[0]

            val showRating = showDataObj.getDouble("rating").toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
            val showStatus = showDataObj.getString("status").split(" ").joinToString(" ") { it.capitalize() }.trimEnd()

            val showDetails = "$showStartYear, ${showDataObj.getString("network")}, $showStatus, $showRating"
            activity?.runOnUiThread{
                showDescription_txt.text = showDataObj.getString("overview")
                showDetails_txt.text = showDetails

            }
        })

        val episodeListRV: RecyclerView? = view.findViewById((R.id.episodeList_rv))
        episodeListRV?.layoutManager = LinearLayoutManager(activity)

        APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$showID/seasons?extended=episodes", fun(data: Response) {
            val dataString = data.body!!.string()
            val allShowEpisodesArray = JSONArray(dataString)
            val numberOfSeasons = allShowEpisodesArray.length()
            val allEpisodesArr = ArrayList<String>()

            for (i in 0 until numberOfSeasons) {
                val singleSeasonData = allShowEpisodesArray.getJSONObject(i)
                val allEpisodesInSeason = singleSeasonData.getJSONArray("episodes")
                if (singleSeasonData.getInt("number") != 0) {
                    for (j in 0 until allEpisodesInSeason.length()) {
                        val singleEpisodeData = allEpisodesInSeason.getJSONObject(j)
                        /*
                        val episodeID = singleEpisodeData.getJSONObject("ids").getString("imdb")
                        if (episodeID.matches("tt\\d{7,8}".toRegex())){
                            allEpisodesArr.add(episodeID)
                        }
                        */
                        val episodeID = singleEpisodeData.getJSONObject("ids").getString("trakt")
                        if (episodeID.matches("\\d+".toRegex())){
                            allEpisodesArr.add(episodeID)
                        }

                    }
                }
            }
            activity?.runOnUiThread{ episodeListRV?.adapter = RecyclerAdapterEpisodeCard(allEpisodesArr)}
        })



        return view
    }

}
