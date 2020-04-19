package cwm.mobileapps.episode




import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*


class DiscoverAndSearchFragment : androidx.fragment.app.Fragment() {
    private lateinit var database: DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_discover_and_search, container, false)

        val trendingShowsRV: RecyclerView? = view?.findViewById((R.id.popularShows_rv))
        trendingShowsRV?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        displayDiscoverSection(trendingShowsRV,"popular")

        val topRatedShowsRV: RecyclerView? = view?.findViewById((R.id.topRatedShows_rv))
        topRatedShowsRV?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        displayDiscoverSection(topRatedShowsRV,"top_rated")


        val latestShowsRV: RecyclerView? = view?.findViewById((R.id.airingToday_rv))
        latestShowsRV?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        displayDiscoverSection(latestShowsRV,"airing_today")


        /*
        val homeLaunchBTN: Button? = view?.findViewById(R.id.homeLaunch_btn)
        homeLaunchBTN?.setOnClickListener {
            val intentToMainActivity = Intent(activity, MainActivity::class.java)
            startActivity(intentToMainActivity)
        }
        */

        val discoverSearchTitleTXT: TextView? = view?.findViewById(R.id.discoverSearchTitle_txt)
        discoverSearchTitleTXT?.text = "Discover & Search."

        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        inflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.searchInDandS_m)
        if (searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu, inflater)

    }

    fun displayDiscoverSection(rView : RecyclerView?, section : String?) {
        //image URL: https://image.tmdb.org/t/p/w500/lbIMe94gXNGBzlFACqbrUyEXpyN.jpg
        //var urlSTR = "https://api.themoviedb.org/3/tv/$section?api_key=9b05770b260d801f3b9e84fd281f2064&language=en-US&page=1"
        var urlSTR = "https://api.trakt.tv/shows/trending"

        val request = Request
            .Builder()
            .url(urlSTR)
            .addHeader("Content-Type","application/json")
            .addHeader("trakt-api-version","2")
            .addHeader("trakt-api-key","60208da48cb89f83f54f9686b0027df865b8aa8e51d2af64e7e4429b2cac7b28")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                println("FAIL API")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()
                val result = JSONArray(body)
                println("API Search Success")

                val showNames = ArrayList<String>()
                val showIDs = ArrayList<String>()
                val showImageLocations = ArrayList<String>()
                for (i in 0 until result.length()) {
                    val item = result.getJSONObject(i)
                    val showData = item.getJSONObject("show")


                    val tvdbID = showData.getJSONObject("ids").getString("tvdb")
                    var urlSTRImage = "http://webservice.fanart.tv/v3/tv/$tvdbID?api_key=cc52af8ac688a6c7a9a83e293624fe35"
                    val requestImage = Request.Builder().url(urlSTRImage).build()
                    val clientImage = OkHttpClient()
                    val responseImage = clientImage.newCall(requestImage).execute()
                    val bodyImage = responseImage.body!!.string()
                    val imageObj = JSONObject(bodyImage)

                    val imageLocation = imageObj.getJSONArray("tvposter").getJSONObject(0).getString("url")
                    println("appdebug: imageLocation(tvdb): " + imageLocation)

                    showNames.add(showData.getString("title"))
                    showIDs.add(showData.getJSONObject("ids").getString("tvdb"))
                    showImageLocations.add(imageLocation)
                }

                activity?.runOnUiThread(Runnable { rView?.adapter = PostsAdapter(showNames, showIDs, showImageLocations) })

            }
        })
    }
}