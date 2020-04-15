package cwm.mobileapps.episode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 */
class MyAccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.fragment_my_account, container, false)

        val myAccountHeaddingTXT: TextView? = view?.findViewById(R.id.myAccountHeadding_txt)
        myAccountHeaddingTXT?.text = "My Account"

        val signOutBTN: Button? = view?.findViewById(R.id.signOut_btn)
        signOutBTN?.setOnClickListener{
            (activity as UserHomeActivity).signOut()
        }

        return view
    }

}
