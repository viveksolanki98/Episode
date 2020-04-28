package cwm.mobileapps.episode


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.fragment_watch_list.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class WatchListFragment : Fragment() {
    var userID : String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_watch_list, container, false)

        val nextEpisodesRV: RecyclerView? = view?.findViewById((R.id.nextEpisodes_rv))
        nextEpisodesRV?.layoutManager = LinearLayoutManager(context)

        val watchListNextEpisodeRefreshLayoutSRL : SwipeRefreshLayout? = view?.findViewById(R.id.watchListNextEpisodeRefreshLayout_SRL)
        watchListNextEpisodeRefreshLayoutSRL?.setOnRefreshListener {
            println("appdebug: watchList: in refresh listener")
            populateNextEpisodeRV(watchListNextEpisodeRefreshLayoutSRL, nextEpisodesRV)
        }

        populateNextEpisodeRV(watchListNextEpisodeRefreshLayoutSRL, nextEpisodesRV)


        return view
    }

    private fun populateNextEpisodeRV(refreshLayer: SwipeRefreshLayout?, nextEpisodesRV: RecyclerView? ) {
        //val userAccountDetails = GoogleSignIn.getLastSignedInAccount(context)
        val myPref: SharedPreferences = context!!.getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        userID = myPref?.getString("user_id_google", "")

        //userAccountDetails?.id.toString()
        refreshLayer?.isRefreshing = true
        FBDBhandler.queryListener("UserID_EpisodeID", "${userID}_tt1", fun(data : DataSnapshot?){
            var nextEpisodesList = ArrayList<String>()
            val snapLength = data?.childrenCount?.toInt()
            for ((counter, singleSnapshot) in data!!.children.withIndex()) {
                val userShowID = JSONObject(singleSnapshot?.getValue().toString()).getString("ShowID")

                if (userShowID != "tt1") {
                    APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$userShowID/seasons?extended=episodes", fun(data: Response) {
                        var dataString = data.body!!.string()
                        val allShowEpisodesArray = JSONArray(dataString)
                        val numberOfSeasons = allShowEpisodesArray.length()
                        var allEpisodesArr = ArrayList<String>()

                        for (i in 0 until numberOfSeasons) {
                            var singleSeasonData = allShowEpisodesArray.getJSONObject(i)
                            var allEpisodesInSeason = singleSeasonData.getJSONArray("episodes")
                            if(singleSeasonData.getInt("number") != 0) {
                                for (j in 0 until allEpisodesInSeason.length()) {
                                    var singleEpisodeData = allEpisodesInSeason.getJSONObject(j)
                                    allEpisodesArr.add(singleEpisodeData.getJSONObject("ids").getString("imdb"))
                                }
                            }
                        }
                        FBDBhandler.query("UserID_ShowID", "${userID}_$userShowID", fun(showData : DataSnapshot?){
                            for(singleShowSnapshot in showData!!.children){
                                val userEpisodeID = JSONObject(singleShowSnapshot?.getValue().toString()).getString("EpisodeID")
                                allEpisodesArr.remove(userEpisodeID)
                            }

                            nextEpisodesList.add(allEpisodesArr[0])
                            if(counter == snapLength?.minus(1)){
                                nextEpisodesList.sort()
                                activity?.runOnUiThread(Runnable { nextEpisodesRV?.adapter = RecyclerAdapterEpisodeCard(nextEpisodesList)})
                                refreshLayer?.isRefreshing = false
                            }

                        })
                    })
                }
                if(counter == snapLength?.minus(1)){
                    activity?.runOnUiThread(Runnable { nextEpisodesRV?.adapter = RecyclerAdapterEpisodeCard(nextEpisodesList)})
                    refreshLayer?.isRefreshing = false
                }
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        watchListTitle_txt.text = "My Watch List"
    }
}
