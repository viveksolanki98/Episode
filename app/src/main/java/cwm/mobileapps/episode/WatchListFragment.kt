package cwm.mobileapps.episode


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.activity_user_home2.*
import kotlinx.android.synthetic.main.fragment_watch_list.*
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import rm.com.longpresspopup.LongPressPopupBuilder
import kotlin.collections.ArrayList

class WatchListFragment : Fragment() {
    var userID : String? = ""
    var nextEpisodesList = ArrayList<String>()
    var currentlyWatchingShows = ArrayList<String>()
    lateinit var viewAdapter : RecyclerAdapterEpisodeCard
    var watchListNextEpisodeRefreshLayoutSRL : SwipeRefreshLayout? = null
    var completedShows = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_watch_list, container, false)

        //Prepare recycler view for next episode list
        val nextEpisodesRV: RecyclerView? = view?.findViewById((R.id.nextEpisodes_rv))
        nextEpisodesRV?.layoutManager = LinearLayoutManager(context)
        viewAdapter = RecyclerAdapterEpisodeCard(nextEpisodesList)
        nextEpisodesRV?.adapter = viewAdapter

        //Pull down to refresh listener
        watchListNextEpisodeRefreshLayoutSRL = view?.findViewById(R.id.watchListNextEpisodeRefreshLayout_SRL)
        watchListNextEpisodeRefreshLayoutSRL?.setOnRefreshListener {
            println("appdebug: watchList: in refresh listener")
            populateNextEpisodeRV()
        }

        //Initialise list
        populateNextEpisodeRV()

        //Swipe to mark as watched
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                //If swiped mark as watched and remove from recycler view
                viewAdapter.removeItem(viewHolder)
            }

        }
        //Add listener to recycler view
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(nextEpisodesRV)

        return view
    }

    fun populateNextEpisodeRV() {
        //This function gets all the episodes that are next to watch
        //get user id
        val myPref: SharedPreferences = context!!.getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        userID = myPref.getString("user_id_google", "")

        completedShows = 0
        //Get all the shows the user has added
        FBDBhandler.queryListener("UserID_EpisodeID", "${userID}_tt1", fun(data : DataSnapshot?){
            watchListNextEpisodeRefreshLayoutSRL?.isRefreshing = true
            nextEpisodesList.clear()
            currentlyWatchingShows.clear()
            val snapLength = data?.childrenCount?.toInt()
            //For each show in the FBDB they are watching...
            for ((counter, singleSnapshot) in data!!.children.withIndex()) {
                val userShowID = JSONObject(singleSnapshot?.getValue().toString()).getString("ShowID")
                //Skip acount initializer
                if (userShowID != "tt1") {
                    currentlyWatchingShows.add(userShowID)
                    updateNextEpisodeToWatch(userShowID,snapLength!!.minus(1))
                }

            }
            //If no shows then stop the refresh spinner
            if(snapLength!! <= 1){
                viewAdapter.notifyDataSetChanged()
                watchListNextEpisodeRefreshLayoutSRL?.isRefreshing = false
            }
        })
    }

    private fun updateNextEpisodeToWatch(showID : String, numberOfShows : Int) {
        //This function works out which episode is next to watch given a show id
        //get all the episodes for a show
        APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$showID/seasons?extended=episodes", fun(data: Response) {
            val dataString = data.body!!.string()
            val allShowEpisodesArray = JSONArray(dataString)
            val numberOfSeasons = allShowEpisodesArray.length()
            val allEpisodesArr = ArrayList<String>()

            //Add the latest aired show to the SQL database
            val apiRes = APIhandler.trackitAPISync("https://api.trakt.tv/shows/${showID}/last_episode")
            val latestEpisodeID = JSONObject(apiRes.body!!.string()).getJSONObject("ids").getString("trakt")
            ContentProviderHandler().safeInsert(activity!!.contentResolver, showID, latestEpisodeID)

            //For each season...
            for (i in 0 until numberOfSeasons) {
                val singleSeasonData = allShowEpisodesArray.getJSONObject(i)
                val allEpisodesInSeason = singleSeasonData.getJSONArray("episodes")
                if(singleSeasonData.getInt("number") != 0) {
                    //For each episode in season
                    for (j in 0 until allEpisodesInSeason.length()) {
                        val singleEpisodeData = allEpisodesInSeason.getJSONObject(j)
                        allEpisodesArr.add(singleEpisodeData.getJSONObject("ids").getString("trakt"))
                    }
                }
            }
            //Get all the episodes the user has watched from the FBDB
            FBDBhandler.query("UserID_ShowID", "${userID}_$showID", fun(showData : DataSnapshot?){
                //For each episode
                for(singleShowSnapshot in showData!!.children){
                    val userEpisodeID = JSONObject(singleShowSnapshot?.getValue().toString()).getString("EpisodeID")
                    //Remove from the array since it has been watched
                    allEpisodesArr.remove(userEpisodeID)
                }
                //Now allEpisodesArr[0] is the next one to watch since allEpisodesArr contains all the episodes
                //that haven't been watched and is in air date order

                // The remove line prevents the episode being added multiple times into the list
                if (allEpisodesArr.size > 0) {
                    nextEpisodesList.remove(allEpisodesArr[0])
                    nextEpisodesList.add(allEpisodesArr[0])
                    nextEpisodesList.sort()
                }else{
                    completedShows++
                }
                if((nextEpisodesList.size + completedShows) == numberOfShows){
                    //Once all the episodes have been found, notify change
                    viewAdapter.notifyDataSetChanged()
                    watchListNextEpisodeRefreshLayoutSRL?.isRefreshing = false
                }
            })
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        watchListTitle_txt.text = "My Watch List"
    }
}
