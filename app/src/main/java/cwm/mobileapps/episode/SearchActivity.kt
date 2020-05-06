package cwm.mobileapps.episode

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.Response
import org.json.JSONArray


class SearchActivity : AppCompatActivity() {
    var apiResultsArray = JSONArray()
    var mQuery : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //val searchResultRV: RecyclerView? = findViewById((R.id.searchResult_rv))
        searchResult_rv.layoutManager = LinearLayoutManager(this)
        searchResult_rv.adapter = RecyclerAdapterSearchResultCard(apiResultsArray)

        mQuery = savedInstanceState?.getString("search_query")

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                applySearch(query)
            }
        }else if(intent?.getStringExtra("search_term") != null){
            mQuery = intent.getStringExtra("search_term")
            applySearch(mQuery)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main,menu)

        // Get the SearchView and set the searchable configuration:
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        }

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
            searchView.setQuery(mQuery,true)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.setQuery(query,true)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText!!.isNotEmpty()){
                        mQuery = newText
                        applySearch(newText)
                    }else {
                        apiResultsArray = JSONArray()
                        searchResult_rv.adapter = RecyclerAdapterSearchResultCard(apiResultsArray)

                    }
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
        //return super.onCreateOptionsMenu(menu, inflater)

    }

    private fun applySearch(searchTerm : String?){
        APIhandler.trackitAPIAsync("https://api.trakt.tv/search/show?query=$searchTerm&extended=full", fun(response : Response){
            apiResultsArray = JSONArray(response.body!!.string())
            println("appdebug: search activity: API get search results: $apiResultsArray")
            runOnUiThread{searchResult_rv.adapter = RecyclerAdapterSearchResultCard(apiResultsArray)}
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mQuery = savedInstanceState.getString("search_query")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_query", mQuery)
    }
}
