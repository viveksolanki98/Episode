package cwm.mobileapps.episode


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_watch_list.*

class WatchListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_watch_list, container, false)

        val nextEpisodesRV: RecyclerView? = view?.findViewById((R.id.nextEpisodes_rv))
        nextEpisodesRV?.layoutManager = LinearLayoutManager(context)
        val sections = ArrayList<List<String>>()
        //listOf("Section Title", "API path")
        sections.add(listOf("Trending", "trending"))
        sections.add(listOf("Popular", "popular"))
        sections.add(listOf("Most Played","played/daily"))
        sections.add(listOf("Most Saved","collected/weekly"))
        nextEpisodesRV?.adapter = RecyclerAdapterWLNextEpisode(sections)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        watchListTitle_txt.text = "My Watch List"
    }
}
