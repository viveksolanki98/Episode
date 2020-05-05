package cwm.mobileapps.episode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Response
import org.json.JSONArray


class ShowCommentsSpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_comments_sp, container, false)


        val showCommentsRV : RecyclerView? = view?.findViewById(R.id.showComments_rv)
        showCommentsRV?.layoutManager  = LinearLayoutManager(context)
        populateShowComments(showCommentsRV)

        return view
    }

    private fun populateShowComments(showCommentsRV: RecyclerView?) {
        val showID = activity?.intent?.getStringExtra("show_id")
        APIhandler.trackitAPIAsync("https://api.trakt.tv/shows/$showID/comments/highest?spoiler=false&extended=full", fun(data : Response){
            val apiCommentArray = JSONArray(data.body!!.string())
            println("appdebug: showCommentsSp: in API response ${apiCommentArray.length()}")
            activity?.runOnUiThread{showCommentsRV?.adapter = RecyclerAdapterShowComments(apiCommentArray)}
        })
    }
}
