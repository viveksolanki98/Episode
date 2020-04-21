package cwm.mobileapps.episode


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.fragment_watch_list.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

class WatchListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_watch_list, container, false)

        val userAccountDetails= GoogleSignIn.getLastSignedInAccount(context)

        val nextEpisodesRV: RecyclerView? = view?.findViewById((R.id.nextEpisodes_rv))
        nextEpisodesRV?.layoutManager = LinearLayoutManager(context)
        val sections = ArrayList<List<String>>()
        //listOf("Section Title", "API path")
        sections.add(listOf("Trending", "trending"))
        sections.add(listOf("Popular", "popular"))
        sections.add(listOf("Most Played","played/daily"))
        sections.add(listOf("Most Saved","collected/weekly"))

        FBDBhandler.query("UserID_EpisodeID", "${userAccountDetails?.id.toString()}_tt1", fun(data : DataSnapshot?){
            var nextEpisodesList = ArrayList<String>()

            val snapLength = data?.childrenCount?.toInt()
            var counter = 0
            for (singleSnapshot in data!!.getChildren()) {
                val userShowID = JSONObject(singleSnapshot?.getValue().toString()).getString("ShowID")

                counter++
                if (userShowID != "tt1") {
                    APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$userShowID/seasons?extended=episodes", fun(data: Response) {
                        var dataString = data.body!!.string()
                        val allShowEpisodesArray = JSONArray(dataString)
                        val numberOfSeasons = allShowEpisodesArray.length()
                        var loopContinue = true
                        var i = 0
                        while (loopContinue and (i < numberOfSeasons)){
                            var j = 0
                            var allEpisodes = allShowEpisodesArray.getJSONObject(i)
                            while (loopContinue and (j < allEpisodes.length())){
                                val singleEpisode = allEpisodes.getJSONArray("episodes").getJSONObject(j)
                                val singleEpisodeID = singleEpisode.getJSONObject("ids").getString("imdb")
                                if (singleEpisode.getInt("season") != 0){
                                    FBDBhandler.query("UserID_EpisodeID", "${userAccountDetails?.id.toString()}_$singleEpisodeID", fun (showData : DataSnapshot?){
                                        println("appdebug: watch list: in fbdb")
                                        if (showData?.getValue() == null){
                                            nextEpisodesList.add("$userShowID _ $singleEpisodeID _ s${singleEpisode.getInt("season")} _ e${singleEpisode.getInt("number")}")
                                            loopContinue = false
                                        }
                                        if (counter == snapLength) {
                                            activity?.runOnUiThread(Runnable { nextEpisodesRV?.adapter = RecyclerAdapterWLNextEpisode(nextEpisodesList)})
                                        }
                                    })
                                }else{
                                    j = allEpisodes.length()
                                }
                                j++
                            }
                            i++
                        }
                    })
                }
            }
        })



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        watchListTitle_txt.text = "My Watch List"
    }
}
