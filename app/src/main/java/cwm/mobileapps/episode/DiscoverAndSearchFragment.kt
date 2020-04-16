package cwm.mobileapps.episode




import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*

class DiscoverAndSearchFragment : androidx.fragment.app.Fragment() {
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_discover_and_search, container, false)

        val shows = ArrayList<String>()
        for (i in 1..100){
            shows.add("Show #$i")
        }

        val trendingShowsRV: RecyclerView? = view?.findViewById((R.id.trendingShows_rv))
        trendingShowsRV?.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        trendingShowsRV?.adapter = PostsAdapter(shows)

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

    fun getSearchResults(searchTerm : String): ArrayList<String> {
        //image URL: https://image.tmdb.org/t/p/w500/lbIMe94gXNGBzlFACqbrUyEXpyN.jpg
        var urlSTR = "https://api.themoviedb.org/3/search/tv?api_key=9b05770b260d801f3b9e84fd281f2064&language=en-US&page=1&query=$searchTerm&include_adult=false"
        val request = Request.Builder().url(urlSTR).build()
        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        val searchResultsObject = JSONObject(response.body?.string())

        val showNames = ArrayList<String>()
        for (i in 0 until searchResultsObject.length()) {
            val item = searchResultsObject.getJSONObject(i.toString())
            showNames.add(item.getString("name"))
        }

        return showNames
        /*
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("FAIL API")
                return null
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()
                val result = JSONObject(body)
                //val showName = result.getJSONArray("results").getJSONObject(0).getString("name")
                println("API Search Success")
                return result
            }
        })

         */
    }
}
