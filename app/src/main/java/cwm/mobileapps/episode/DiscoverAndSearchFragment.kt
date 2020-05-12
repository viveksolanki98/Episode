package cwm.mobileapps.episode



import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DiscoverAndSearchFragment : androidx.fragment.app.Fragment() {

    var sections :MutableList<List<String>> = ArrayList()
    val discoverSectionsRV: RecyclerView? = view?.findViewById((R.id.discoverSections_rv))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_discover_and_search, container, false)

        setHasOptionsMenu(true)
        val discoverSectionsRV: RecyclerView? = view?.findViewById((R.id.discoverSections_rv))
        discoverSectionsRV?.layoutManager = LinearLayoutManager(context)

        //What each element means: listOf("Section Title", "API path")
        sections.add(listOf("Trending", "trending"))
        sections.add(listOf("Popular", "popular"))
        sections.add(listOf("Most Played","played/daily"))
        sections.add(listOf("Most Saved","collected/weekly"))
        //create recycler view
        discoverSectionsRV?.adapter = RecyclerAdapterDSSections(sections as ArrayList<List<String>>)

        //This handles the search bar in the fragment
        val intentToSearchActivity = Intent(context, SearchActivity::class.java)
        val searchTextBarSV : SearchView? = view?.findViewById(R.id.searchTextBar_sv)
        //create the listener
        searchTextBarSV?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //On submit move to the search activity
                println("appdebug: discoverAndSearch: inside of submit listener: $query")
                intentToSearchActivity.putExtra("search_term", query)
                startActivity(intentToSearchActivity)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        //Assign the fragment title
        val discoverSearchTitleTXT: TextView? = view?.findViewById(R.id.discoverSearchTitle_txt)
        discoverSearchTitleTXT?.text = "Discover & Search"

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //this is stored to keep scroll position of the recycler view consistent when rotating
        discoverSectionsRV?.verticalScrollbarPosition?.let {
            outState.putInt("scroll_position_sections",
                it
            )
        }

    }
}