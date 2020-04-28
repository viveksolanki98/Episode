package cwm.mobileapps.episode

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DataSnapshot

class RecyclerAdapterDSLists(val showTitle : ArrayList<String>, val showIDs : ArrayList<String>, val showImageLocations : ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapterDSLists.ViewHolder>() {
    var userID : String? = ""

    override fun getItemCount() = showTitle.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myPref: SharedPreferences = holder.itemView.context!!.getSharedPreferences("Episode_pref", Context.MODE_PRIVATE)
        userID = myPref?.getString("user_id_google", "")

        holder.showTitleTXT.text = showTitle[position]
        //"https://image.tmdb.org/t/p/w500/"
        Glide.with(holder.itemView.context).load(showImageLocations[position]).into(holder.showPosterIV)

        FBDBhandler.queryListener("UserID_ShowID", "${userID}_${showIDs[position]}", fun(data : DataSnapshot?) {
            println("appdebug: showExistsNEW: " + data!!.getValue())
            holder.addShowBTN.visibility = if (data.getValue() != null) View.GONE else View.VISIBLE
        })

        holder.showPosterIV.setOnClickListener {
            val intentToShowPageActivity = Intent(holder.itemView.context, ShowPageActivity::class.java)
            intentToShowPageActivity.putExtra("show_title", showTitle[position])
            intentToShowPageActivity.putExtra("show_id", showIDs[position])
            intentToShowPageActivity.putExtra("show_poster", showImageLocations[position])

            holder.itemView.context.startActivity(intentToShowPageActivity)
        }

        holder.addShowBTN.setOnClickListener {
            FBDBhandler.addRecord("tt1", showIDs[position], userID!!)
            holder.addShowBTN.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.discover_lists_rv_card, parent, false)
        return ViewHolder(view)

    }

    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val showTitleTXT: TextView = itemView.findViewById(R.id.showTitle_txt)
        val showPosterIV: ImageView = itemView.findViewById(R.id.showPoster_iv)
        val addShowBTN : Button = itemView.findViewById(R.id.addShow_btn)
    }
}
