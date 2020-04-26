package cwm.mobileapps.episode




import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class DiscoverAndSearchFragment : androidx.fragment.app.Fragment() {

    var sections :MutableList<List<String>> = ArrayList()
    val discoverSectionsRV: RecyclerView? = view?.findViewById((R.id.discoverSections_rv))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_discover_and_search, container, false)

        setHasOptionsMenu(true)
        val discoverSectionsRV: RecyclerView? = view?.findViewById((R.id.discoverSections_rv))
        discoverSectionsRV?.layoutManager = LinearLayoutManager(context)

        //listOf("Section Title", "API path")
        sections.add(listOf("Trending", "trending"))
        sections.add(listOf("Popular", "popular"))
        sections.add(listOf("Most Played","played/daily"))
        sections.add(listOf("Most Saved","collected/weekly"))
        discoverSectionsRV?.adapter = RecyclerAdapterDSSections(sections as ArrayList<List<String>>)

        /*
        val homeLaunchBTN: Button? = view?.findViewById(R.id.homeLaunch_btn)
        homeLaunchBTN?.setOnClickListener {
            val intentToMainActivity = Intent(activity, MainActivity::class.java)
            startActivity(intentToMainActivity)
        }
        */

        val searchLaunchBTN : Button? = view?.findViewById(R.id.searchLauncher_btn)
        searchLaunchBTN?.setOnClickListener {
            val intentToSearchActivity = Intent(context, SearchActivity::class.java)
            startActivity(intentToSearchActivity)
        }



        val discoverSearchTitleTXT: TextView? = view?.findViewById(R.id.discoverSearchTitle_txt)
        discoverSearchTitleTXT?.text = "Discover & Search"

        return view
    }



}