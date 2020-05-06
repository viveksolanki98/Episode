package cwm.mobileapps.episode




import android.app.SearchManager
import android.content.Context
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

        //listOf("Section Title", "API path")
        sections.add(listOf("Trending", "trending"))
        sections.add(listOf("Popular", "popular"))
        sections.add(listOf("Most Played","played/daily"))
        sections.add(listOf("Most Saved","collected/weekly"))
        discoverSectionsRV?.adapter = RecyclerAdapterDSSections(sections as ArrayList<List<String>>)
        //discoverSectionsRV?.scrollToPosition(1)


        /*
        val homeLaunchBTN: Button? = view?.findViewById(R.id.homeLaunch_btn)
        homeLaunchBTN?.setOnClickListener {
            val intentToMainActivity = Intent(activity, MainActivity::class.java)
            startActivity(intentToMainActivity)
        }
        */

        val searchLaunchBTN : Button? = view?.findViewById(R.id.searchLauncher_btn)
        val intentToSearchActivity = Intent(context, SearchActivity::class.java)
        searchLaunchBTN?.setOnClickListener {
            intentToSearchActivity.putExtra("search_term", "")
            startActivity(intentToSearchActivity)
        }

        val searchTextBarSV : SearchView? = view?.findViewById(R.id.searchTextBar_sv)
        searchTextBarSV?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                println("appdebug: discoverAndSearch: inside of submit listener: $query")
                intentToSearchActivity.putExtra("search_term", query)
                startActivity(intentToSearchActivity)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println("appdebug: discoverAndSearch: inside of change listener: $newText")
                return true
            }
        })



        val discoverSearchTitleTXT: TextView? = view?.findViewById(R.id.discoverSearchTitle_txt)
        discoverSearchTitleTXT?.text = "Discover & Search"

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        println("appdebug: discover and search: scroll position: ${discoverSectionsRV?.verticalScrollbarPosition}")

        discoverSectionsRV?.verticalScrollbarPosition?.let {
            outState.putInt("scroll_position_sections",
                it
            )
        }

    }
}