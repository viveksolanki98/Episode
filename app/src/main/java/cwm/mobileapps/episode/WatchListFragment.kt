package cwm.mobileapps.episode


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.fragment_watch_list.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

class WatchListFragment : Fragment() {
    var userID : String? = ""
    var nextEpisodesList = ArrayList<String>()
    var currentlyWatchingShows = ArrayList<String>()
    lateinit var viewAdapter : RecyclerAdapterEpisodeCard
    var watchListNextEpisodeRefreshLayoutSRL : SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_watch_list, container, false)

        val nextEpisodesRV: RecyclerView? = view?.findViewById((R.id.nextEpisodes_rv))
        nextEpisodesRV?.layoutManager = LinearLayoutManager(context)
        viewAdapter = RecyclerAdapterEpisodeCard(nextEpisodesList)
        nextEpisodesRV?.adapter = viewAdapter

        watchListNextEpisodeRefreshLayoutSRL = view?.findViewById(R.id.watchListNextEpisodeRefreshLayout_SRL)
        watchListNextEpisodeRefreshLayoutSRL?.setOnRefreshListener {
            println("appdebug: watchList: in refresh listener")
            populateNextEpisodeRV()
        }

        populateNextEpisodeRV()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                viewAdapter.removeItem(viewHolder)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(nextEpisodesRV)



        return view
    }

    fun populateNextEpisodeRV() {
        val myPref: SharedPreferences = context!!.getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        userID = myPref.getString("user_id_google", "")

        watchListNextEpisodeRefreshLayoutSRL?.isRefreshing = true
        FBDBhandler.query("UserID_EpisodeID", "${userID}_tt1", fun(data : DataSnapshot?){
            nextEpisodesList.clear()
            currentlyWatchingShows.clear()
            val snapLength = data?.childrenCount?.toInt()
            for ((counter, singleSnapshot) in data!!.children.withIndex()) {
                val userShowID = JSONObject(singleSnapshot?.getValue().toString()).getString("ShowID")

                if (userShowID != "tt1") {
                    currentlyWatchingShows.add(userShowID)
                    updateNextEpisodeToWatch(userShowID)
                }
                if(counter == snapLength?.minus(1)){
                    viewAdapter.notifyDataSetChanged()
                    watchListNextEpisodeRefreshLayoutSRL?.isRefreshing = false
                }
            }
        })
    }

    private fun updateNextEpisodeInSQLDb(showID : String, episodeID : String){

        val cpResultQuery = ContentProviderHandler().query(activity!!.contentResolver, showID)
        if (cpResultQuery == null){
            println("appdebug: watchList: updateNextEpisodeInSQLDb: QUERY: NO RECORD EXISTS")
            ContentProviderHandler().safeInsert(activity!!.contentResolver, showID, episodeID)
            println("appdebug: watchList: updateNextEpisodeInSQLDb: INSERT: done $showID $episodeID")

        }else {
            println("appdebug: watchList: updateNextEpisodeInSQLDb: QUERY: ${cpResultQuery.get(0).showID} ${cpResultQuery.get(0).episodeID}")
            val updateRes = ContentProviderHandler().update(activity!!.contentResolver, showID, episodeID)
            println("appdebug: watchList: updateNextEpisodeInSQLDb: UPDATE: $updateRes $showID $episodeID")
        }
    }

    fun updateNextEpisodeToWatch(showID : String) {
        APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$showID/seasons?extended=episodes", fun(data: Response) {
            val dataString = data.body!!.string()
            val allShowEpisodesArray = JSONArray(dataString)
            val numberOfSeasons = allShowEpisodesArray.length()
            val allEpisodesArr = ArrayList<String>()

            val lastSeasonEpisodes = allShowEpisodesArray.getJSONObject(numberOfSeasons-1).getJSONArray("episodes")
            val lastEpisodeID = lastSeasonEpisodes.getJSONObject(lastSeasonEpisodes.length()-1).getJSONObject("ids").getString("imdb")
            //updateNextEpisodeInSQLDb(userShowID, lastEpisodeID)
            //ContentProviderHandler().safeInsert(activity!!.contentResolver, showID, lastEpisodeID)

            for (i in 0 until numberOfSeasons) {
                val singleSeasonData = allShowEpisodesArray.getJSONObject(i)
                val allEpisodesInSeason = singleSeasonData.getJSONArray("episodes")
                if(singleSeasonData.getInt("number") != 0) {
                    for (j in 0 until allEpisodesInSeason.length()) {
                        val singleEpisodeData = allEpisodesInSeason.getJSONObject(j)
                        allEpisodesArr.add(singleEpisodeData.getJSONObject("ids").getString("imdb"))
                    }
                }
            }
            FBDBhandler.query("UserID_ShowID", "${userID}_$showID", fun(showData : DataSnapshot?){
                for(singleShowSnapshot in showData!!.children){
                    val userEpisodeID = JSONObject(singleShowSnapshot?.getValue().toString()).getString("EpisodeID")
                    allEpisodesArr.remove(userEpisodeID)
                }
                //this remove line prevent the episode being added multiple times into the list
                nextEpisodesList.add(allEpisodesArr[0])
                nextEpisodesList.distinct()
                viewAdapter.notifyDataSetChanged()
            })
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        watchListTitle_txt.text = "My Watch List"
    }
}
