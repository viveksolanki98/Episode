package cwm.mobileapps.episode


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DatabaseReference


class DiscoverAndSearchFragment : Fragment() {
    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater?.inflate(R.layout.fragment_discover_and_search, container, false)



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


}
