package cwm.mobileapps.episode

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.Response
import org.json.JSONArray


class SearchActivity : AppCompatActivity() {
    var apiResultsArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //val searchResultRV: RecyclerView? = findViewById((R.id.searchResult_rv))
        searchResult_rv.layoutManager = LinearLayoutManager(this)
        searchResult_rv.adapter = RecyclerAdapterSearchResultCard(apiResultsArray)

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflater.inflate(R.menu.main, menu);
        //super.onCreateOptionsMenu(menu,inflater);
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main,menu)

        val searchItem = menu.findItem(R.id.menu_search)
        searchItem.expandActionView()
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                finish()
                return true
            }

        })

        if (searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()){
                        /*
                        resultsArr.clear()
                        var epiID = "tt1480055"
                        resultsArr.add(epiID)
                         */

                        APIhandler.trackitAPIAsync("https://api.trakt.tv/search/show?query=$newText&extended=full", fun(response : Response){
                            apiResultsArray = JSONArray(response.body!!.string())
                            println("appdebug: search activity: API get search results: $apiResultsArray")
                            runOnUiThread(Runnable {searchResult_rv.adapter = RecyclerAdapterSearchResultCard(apiResultsArray)})
                            //runOnUiThread(Runnable {searchResult_rv.adapter?.notifyDataSetChanged()})
                        })


                    }else {
                        apiResultsArray = JSONArray()
                        //searchResult_rv.adapter?.notifyDataSetChanged()
                        searchResult_rv.adapter = RecyclerAdapterSearchResultCard(apiResultsArray)

                    }
                    //searchResult_rv.adapter?.notifyDataSetChanged()
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
        //return super.onCreateOptionsMenu(menu, inflater)

    }

    private fun runSearchQuery(searchTerm : String){

    }
}
