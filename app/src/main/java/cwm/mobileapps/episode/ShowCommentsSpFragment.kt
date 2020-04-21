package cwm.mobileapps.episode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 */
class ShowCommentsSpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_comments_sp, container, false)

        val fragTitleTXT : TextView? = view?.findViewById(R.id.fragTitle_txt)
        fragTitleTXT?.text = "Show Comments"

        return view
    }

}
